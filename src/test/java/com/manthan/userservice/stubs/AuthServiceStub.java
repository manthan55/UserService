package com.manthan.userservice.stubs;

import com.manthan.userservice.models.Session;
import com.manthan.userservice.models.SessionStatus;
import com.manthan.userservice.models.User;
import com.manthan.userservice.services.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Map;
import java.util.Optional;

@Service("AuthServiceStub")
//@Service
public class AuthServiceStub implements IAuthService {

    Map<String, User> users;
    Map<String, Session> sessions;
    @Autowired
    private SecretKey secretKey;

    @Override
    public User signUp(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        return null;
    }

    @Override
    public String login(String email, String password) {
        String token = "sometoken";
        User user = users.get(email);
        Session session = new Session();
        session.setToken(token);
        session.setUser(user);
        session.setSessionStatus(SessionStatus.ACTIVE);
        sessions.put(token, session);
        return token;
    }

    @Override
    public boolean validateToken(String token, Long userId) {
        Session session = sessions.get(token);
        if(session == null) return false;
        User user = users.get(session.getUser().getEmail());
        if(user == null) return false;
        return true;
    }
}
