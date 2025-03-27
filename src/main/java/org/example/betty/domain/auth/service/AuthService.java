package org.example.betty.domain.auth.service;

public interface AuthService {
    String login(String idToken);

    void logout(String accessToken);

    String checkSession(String accessToken);
}
