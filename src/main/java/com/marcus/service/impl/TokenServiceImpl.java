package com.marcus.service.impl;

import com.marcus.exception.ResourceNotFoundException;
import com.marcus.model.auth.Token;
import com.marcus.repository.TokenRepository;
import com.marcus.service.TokenService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public record TokenServiceImpl(TokenRepository tokenRepository) implements TokenService {
    
    /**
     * Get token by username
     *
     * @param username
     * @return token
     */
    public Token getByUsername(String username) {
        return tokenRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("Not found token"));
    }
    
    /**
     * Save token to DB
     *
     * @param token
     * @return
     */
    public int save(Token token) {
        Optional<Token> optional = tokenRepository.findByUsername(token.getUsername());
        if (optional.isEmpty()) {
            tokenRepository.save(token);
            return token.getId();
        } else {
            Token tokenEntity = optional.get();
            tokenEntity.setAccessToken(token.getAccessToken());
            tokenEntity.setRefreshToken(token.getRefreshToken());
            tokenRepository.save(tokenEntity);
            return tokenEntity.getId();
        }
    }
    
    /**
     * Delete token by username
     *
     * @param username
     */
    public void delete(String username) {
        Token token = getByUsername(username);
        tokenRepository.delete(token);
    }
}