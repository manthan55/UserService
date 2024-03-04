package com.manthan.userservice.services;

import com.manthan.userservice.dtos.SignUpRequestDTO;
import com.manthan.userservice.exceptions.SQLException;
import com.manthan.userservice.exceptions.UserAlreadyExistsException;
import com.manthan.userservice.models.User;
import com.manthan.userservice.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AuthServiceTest {

    @Autowired
    AuthService authService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @MockBean
    UserRepository userRepository;

    @Test
    @DisplayName("SignUp successfully")
    public void Test_SignUpWithValidDetails_ReturnsUser() throws SQLException, UserAlreadyExistsException {
        // Arrange
        String email = "test@mail.com";
        String password = "password";

        User savedUser = new User();
        savedUser.setEmail(email);
        savedUser.setPassword(bCryptPasswordEncoder.encode(password));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        Optional<User> userOptional = Optional.empty();
        when(userRepository.findByEmail(any(String.class))).thenReturn(userOptional);

        // Act
        User user = authService.signUp(email,password);

        // Assert
        assertEquals(email,user.getEmail());
        assertTrue(bCryptPasswordEncoder.matches(password, user.getPassword()));
        verify(userRepository,times(1)).findByEmail(any(String.class));
        verify(userRepository,times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("SignUp fail as user already exists")
    public void Test_SignUpWithValidDetailsButUserExists_ReturnsExistingUser() throws SQLException, UserAlreadyExistsException {
        // Arrange
        String email = "test@mail.com";
        String password = "password";

        User existingUser = new User();
        existingUser.setEmail(email);
        existingUser.setPassword(bCryptPasswordEncoder.encode(password));
        Optional<User> userOptional = Optional.of(existingUser);
        when(userRepository.findByEmail(any(String.class))).thenReturn(userOptional);

        // Act
        User user = authService.signUp(email,password);

        // Assert
        assertEquals(email,user.getEmail());
        assertTrue(bCryptPasswordEncoder.matches(password, user.getPassword()));
        verify(userRepository,times(1)).findByEmail(any(String.class));
        verify(userRepository,times(0)).save(any(User.class));
    }
}
