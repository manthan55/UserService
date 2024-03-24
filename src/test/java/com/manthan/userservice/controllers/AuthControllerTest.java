package com.manthan.userservice.controllers;

import com.manthan.userservice.dtos.SignUpRequestDTO;
import com.manthan.userservice.dtos.UserDTO;
import com.manthan.userservice.models.User;
import com.manthan.userservice.services.IAuthService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.*;

@SpringBootTest
public class AuthControllerTest {
    @Autowired
    AuthController authController;

    @MockBean
    IAuthService authService;

    @Captor
    private ArgumentCaptor<Long> emailCaptor;
    @Captor
    private ArgumentCaptor<Long> passwordCaptor;

//    @Test
    public void Test_SignUpWithValidDetails_ReturnsUserDTO(){
        String email = "test@mail.com";
        String password = "password";
        SignUpRequestDTO requestDTO = new SignUpRequestDTO();
        requestDTO.setEmail(email);
        requestDTO.setPassword(password);

        User user = new User();
        user.setEmail(email);
//        when(authService.signUp(any(String.class),any(String.class))).thenReturn()
    }
}
