package com.manthan.userservice.services;

import com.manthan.userservice.models.User;
import com.manthan.userservice.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(Long id){
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isPresent()) return optionalUser.get();

        return null;
    }
}
