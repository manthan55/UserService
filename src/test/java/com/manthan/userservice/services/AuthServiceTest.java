package com.manthan.userservice.services;

import com.manthan.userservice.models.User;
import com.manthan.userservice.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
    @Captor
    private ArgumentCaptor<String> emailCaptor;

//    AuthServiceTest(AuthService authService, BCryptPasswordEncoder bCryptPasswordEncoder){
//        this.authService = authService;
//        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
//    }

    @Test
    @DisplayName("SignUp successfully")
    public void Test_SignUpWithValidDetails_ReturnsUser() {
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
    public void Test_SignUpWithValidDetailsButUserExists_ReturnsExistingUser() {
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

    @Test
    @DisplayName("Login - user does not exist")
    public void Test_LoginUserDoesNotExist_ReturnsNull() {
        // Arrange
        String email = "test@mail.com";
        String password = "password";

        Optional<User> userOptional = Optional.empty();
        when(userRepository.findByEmail(any(String.class))).thenReturn(userOptional);

        // Act
        User user = authService.login(email,password);

        // Assert
        assertNull(user);
        verify(userRepository,times(1)).findByEmail(any(String.class));
        verify(userRepository).findByEmail(emailCaptor.capture());
        assertEquals(email,emailCaptor.getValue());
    }

    @Test
    @DisplayName("Login with wrong credentials")
    public void Test_LoginWithExistingUserWrongCreds_ReturnsNull() {
        // Arrange
        String email = "test@mail.com";
        String password = "password";
        String correctPassword = "correctPassword";

        User existingUser = new User();
        existingUser.setEmail(email);
        existingUser.setPassword(bCryptPasswordEncoder.encode(correctPassword));
        Optional<User> userOptional = Optional.of(existingUser);
        when(userRepository.findByEmail(any(String.class))).thenReturn(userOptional);

        // Act
        User user = authService.login(email,password);

        // Assert
        assertNull(user);
        verify(userRepository,times(1)).findByEmail(any(String.class));
        verify(userRepository).findByEmail(emailCaptor.capture());
        assertEquals(email,emailCaptor.getValue());
    }

    @Test
    @DisplayName("Login successfully")
    public void Test_LoginWithExistingUserValidCreds_ReturnsUser() {
        // Arrange
        String email = "test@mail.com";
        String password = "password";

        User existingUser = new User();
        existingUser.setEmail(email);
        existingUser.setPassword(bCryptPasswordEncoder.encode(password));
        Optional<User> userOptional = Optional.of(existingUser);
        when(userRepository.findByEmail(any(String.class))).thenReturn(userOptional);

        // Act
        User user = authService.login(email,password);

        // Assert
        assertNotNull(user);
        assertEquals(email,user.getEmail());
        assertTrue(bCryptPasswordEncoder.matches(password, user.getPassword()));
        verify(userRepository,times(1)).findByEmail(any(String.class));
        verify(userRepository).findByEmail(emailCaptor.capture());
        assertEquals(email,emailCaptor.getValue());
    }

}
