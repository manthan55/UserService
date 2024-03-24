package com.manthan.userservice.controllers;

import com.manthan.userservice.dtos.UserDTO;
import com.manthan.userservice.models.User;
import com.manthan.userservice.services.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public UserDTO getUser(@PathVariable Long id){
        System.out.println("getUser called with id : "+id);
        User user = userService.getUser(id);
        return getUserDTO(user);
    }

    private UserDTO getUserDTO(User user){
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(user.getEmail());
        userDTO.setRoles(user.getRoles());
        return userDTO;
    }
}
