package com.manthan.userservice.stubs;

import com.manthan.userservice.models.Session;
import com.manthan.userservice.models.SessionStatus;
import com.manthan.userservice.models.User;
import com.manthan.userservice.services.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service("AuthServiceStub")
//@Service
public class AuthServiceStub implements IAuthService {

    Map<Long, User> users;
    Map<Long, Session> sessions;
    long rollingUserId = 1;
    long rollingSessionId = 1;

    AuthServiceStub(){
        users = new HashMap<>();
        sessions = new HashMap<>();
        rollingUserId = 1;
        rollingSessionId = 1;
    }

    private User findUserByEmail(String email){
        for (var entry : users.entrySet()) {
            if(entry.getValue().getEmail().equals(email)) return entry.getValue();
        }
        return null;
    }

    private Session findSessionByToken(String token){
        for (var entry : sessions.entrySet()) {
            if(entry.getValue().getToken().equals(token)) return entry.getValue();
        }
        return null;
    }

    @Override
    public User signUp(String email, String password) {
        User user = new User();
        user.setId(rollingUserId);
        user.setEmail(email);
        user.setPassword(password);
        users.put(rollingUserId,user);
        rollingUserId++;
        return user;
    }

    @Override
    public String login(String email, String password) {
        String token = "sometoken";
        User user = findUserByEmail(email);
        if(!user.getPassword().equals(password)) return null;
        Session session = new Session();
        session.setToken(token);
        session.setUser(user);
        session.setSessionStatus(SessionStatus.ACTIVE);
        sessions.put(rollingSessionId, session);
        rollingSessionId++;
        return token;
    }

    @Override
    public boolean validateToken(String token, Long userId) {
        Session session = findSessionByToken(token);
        if(session == null) return false;
        User user = users.get(userId);
        if(user == null) return false;
        return true;
    }
}
