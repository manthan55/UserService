package com.manthan.userservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manthan.userservice.clients.KafkaProducerClient;
import com.manthan.userservice.dtos.SendEmailMessageDTO;
import com.manthan.userservice.dtos.UserDTO;
import com.manthan.userservice.models.Session;
import com.manthan.userservice.models.SessionStatus;
import com.manthan.userservice.models.User;
import com.manthan.userservice.repositories.SessionRepository;
import com.manthan.userservice.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

//@Service("AuthService")
@Service
public class AuthService implements IAuthService {
    private UserRepository userRepository;
    private SessionRepository sessionRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private SecretKey secretKey;
    private KafkaProducerClient kafkaProducerClient;
    private ObjectMapper objectMapper;

    AuthService(UserRepository userRepository, SessionRepository sessionRepository, BCryptPasswordEncoder bCryptPasswordEncoder, SecretKey secretKey, KafkaProducerClient kafkaProducerClient, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.secretKey = secretKey;
        this.kafkaProducerClient = kafkaProducerClient;
        this.objectMapper = objectMapper;
    }

    public User signUp(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) return userOptional.get();

        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        User savedUser = userRepository.save(user);

        // put message in queue
        SendEmailMessageDTO sendEmailMessageDTO = new SendEmailMessageDTO();
        sendEmailMessageDTO.setTo(email);
        sendEmailMessageDTO.setFrom("from");
        sendEmailMessageDTO.setSubject("some random subject");
        sendEmailMessageDTO.setBody("some random body");
        try {
            kafkaProducerClient.sendMessage("sendEmail",objectMapper.writeValueAsString(sendEmailMessageDTO));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return savedUser;
    }

    public String login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) return null;

        User user = userOptional.get();
//        if (!user.getPassword().equals(password)) return null;
        // here order of params matter
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) return null;

        // Token generation
        String message = "json token";
        message = "{\n  \"roles\": [\n    \"instructor\",\n    \"mentee\"\n  ],\n  \"email\": \"user1@mail.com\",\n  \"expirationDate\": \"2ndApril2024\"\n}";
        byte[] content = message.getBytes(StandardCharsets.UTF_8);


        Map<String, Object> jwtData = new HashMap<>();
        jwtData.put("email", user.getEmail());
        jwtData.put("roles",user.getRoles());
        long nowInMillis = System.currentTimeMillis();
        Date createdAt = new Date(nowInMillis);
        Date expiringAt = new Date(nowInMillis+1000000);
        jwtData.put("createdAt", createdAt);
        jwtData.put("expiryTime", expiringAt);

//        MacAlgorithm algorithm = Jwts.SIG.HS256;
//        SecretKey secret = algorithm.key().build();
        System.out.println("secretKey : "+secretKey.getEncoded());
        String token = Jwts
                .builder()
//                .content(content)
                .claims(jwtData)
                .signWith(secretKey)
                .compact();

//        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
//        headers.add(HttpHeaders.SET_COOKIE, token);


        // ideally resources server will have the key & will decrypt the token to check its validity -- which does not require storage of token
        // the secret will be availble to resource server via KeyVault/SecretManager,etc
        // But, for this project since we don't have KeyVault, we have to store tokens in DB -- then resource servers will make HTTP calls to AuthService to validate the tokens
        Session session = new Session();
        session.setToken(token);
        session.setUser(user);
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setExpiringAt(expiringAt);
        sessionRepository.save(session);

//        return new Pair<User,MultiValueMap<String,String>>(user,headers);
        return token;
    }

    public boolean validateToken(String token, Long userId){
        Optional<Session> optionalSession = sessionRepository.findByTokenAndUser_Id(token, userId);
        if (optionalSession.isEmpty()) return false;

        Session session = optionalSession.get();
        String storedToken = session.getToken();

        JwtParser parser = Jwts
                .parser()
                .verifyWith(secretKey)
                .build();

        Claims claims = parser.parseSignedClaims(storedToken).getPayload();

        long nowInMillis = System.currentTimeMillis();
        long tokenExpiry = (long)claims.get("expiryTime");
        System.out.println("nowInMillis : "+nowInMillis);
        System.out.println("tokenExpiry : "+tokenExpiry);
        if(nowInMillis > tokenExpiry){
            System.out.println("token has expired");
            return false;
        }

        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty()) return false;

        User user = userOptional.get();
        String email = user.getEmail().trim();
        String tokenEmail = ((String)claims.get("email")).trim();
        System.out.println("email : "+email);
        System.out.println("tokenEmail : "+tokenEmail);
        if(!email.equals(tokenEmail))
        {
            System.out.println("email does not match");
            return false;
        }

        return true;
    }
}
