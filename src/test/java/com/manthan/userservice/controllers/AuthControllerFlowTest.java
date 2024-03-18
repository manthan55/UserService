package com.manthan.userservice.controllers;

import com.manthan.userservice.dtos.SignUpRequestDTO;
import com.manthan.userservice.services.IAuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AuthControllerFlowTest {
    @Autowired
    AuthController authController;

    @Test
    public void Test_SignupLoginAndValidateToken(){
        // Arrange
        String email = "dummy";
        String password = "dummy";
        SignUpRequestDTO requestDTO = new SignUpRequestDTO();
        requestDTO.setEmail(email);
        requestDTO.setPassword(password);

        // Act
        authController.signUp(requestDTO);

        // Assert
    }
}
