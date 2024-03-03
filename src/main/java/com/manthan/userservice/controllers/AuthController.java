package com.manthan.userservice.controllers;

import com.manthan.userservice.dtos.LoginRequestDTO;
import com.manthan.userservice.dtos.SignUpRequestDTO;
import com.manthan.userservice.dtos.UserDTO;
import com.manthan.userservice.models.User;
import com.manthan.userservice.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// @Controller // when we also want views as resposne along with JSON/XML
@RestController // when we want response to be JSON/XML
public class AuthController {
    // signup
    // login
    // logout
    // forgetPassword

    private AuthService authService;

    AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<UserDTO> signUp(@RequestBody SignUpRequestDTO requestDTO){
        try{
            User user = authService.signUp(requestDTO.getEmail(), requestDTO.getPassword());
            UserDTO userDTO = getUserDTO(user);
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        }
        catch(Exception ex){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity<UserDTO> login(@RequestBody LoginRequestDTO requestDTO){
        try{
            User user = authService.login(requestDTO.getEmail(), requestDTO.getPassword());
            UserDTO userDTO = getUserDTO(user);
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        }
        catch(Exception ex){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    private UserDTO getUserDTO(User user){
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(user.getEmail());
        userDTO.setRoles(user.getRoles());
        return userDTO;
    }
}
