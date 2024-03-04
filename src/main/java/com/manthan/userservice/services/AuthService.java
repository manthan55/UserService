package com.manthan.userservice.services;

import com.manthan.userservice.exceptions.SQLException;
import com.manthan.userservice.exceptions.UserAlreadyExistsException;
import com.manthan.userservice.models.User;
import com.manthan.userservice.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    AuthService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User signUp(String email, String password) throws UserAlreadyExistsException, SQLException {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) return userOptional.get();
//        if (userOptional.isPresent()) throw new UserAlreadyExistsException();

        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));

        User savedUser = userRepository.save(user);
        return savedUser;
    }

    public User login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) return null;

        User user = userOptional.get();
        // here order of params matter
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) return null;

        return user;
    }
}
