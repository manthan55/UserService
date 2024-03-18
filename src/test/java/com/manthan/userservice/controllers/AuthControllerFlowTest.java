package com.manthan.userservice.controllers;

import com.manthan.userservice.dtos.LoginRequestDTO;
import com.manthan.userservice.dtos.SignUpRequestDTO;
import com.manthan.userservice.dtos.UserDTO;
import com.manthan.userservice.dtos.ValidateTokenDTO;
import com.manthan.userservice.models.User;
import com.manthan.userservice.services.IAuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;

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

        // signup
        ResponseEntity<UserDTO> signedUpUser = authController.signUp(requestDTO);
        assertEquals(email, signedUpUser.getBody().getEmail());

        // login
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setEmail(email);
        loginRequestDTO.setPassword(password);
        ResponseEntity<String> loginResponse = authController.login(loginRequestDTO);
        assertNotNull(loginResponse.getBody());

        // validate token
        ValidateTokenDTO validateTokenDTO = new ValidateTokenDTO();
        validateTokenDTO.setToken(loginResponse.getBody());
        validateTokenDTO.setUserId(1L);
        ResponseEntity<Boolean> validateTokenResponse = authController.validateToken(validateTokenDTO);
        assertTrue(validateTokenResponse.getBody());
    }
}
