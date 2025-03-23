package com.marcus.service;

import com.marcus.util.TokenType;
import org.springframework.security.core.userdetails.UserDetails;

public interface JWTService {
    String generateToken(UserDetails user);
    
    String generateRefreshToken(UserDetails user);
    
    String generateResetToken(UserDetails user);
    
    String extractUsername(String token, TokenType type);
    
    boolean isValid(String token, TokenType type, UserDetails user);
}
