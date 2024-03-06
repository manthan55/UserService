package com.manthan.userservice.services;

import com.manthan.userservice.models.Session;
import com.manthan.userservice.models.SessionStatus;
import com.manthan.userservice.models.User;
import com.manthan.userservice.repositories.SessionRepository;
import com.manthan.userservice.repositories.UserRepository;
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

@Service
public class AuthService {
    private UserRepository userRepository;
    private SessionRepository sessionRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    AuthService(UserRepository userRepository, SessionRepository sessionRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User signUp(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) return userOptional.get();

        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));

        User savedUser = userRepository.save(user);
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
        Date expiringAt = new Date(nowInMillis+10000);
        jwtData.put("createdAt", createdAt);
        jwtData.put("expiryTime", expiringAt);

        MacAlgorithm algorithm = Jwts.SIG.HS256;
        SecretKey secret = algorithm.key().build();
        System.out.println("secret : "+secret.getEncoded());
        String token = Jwts
                .builder()
//                .content(content)
                .claims(jwtData)
                .signWith(secret)
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
}
