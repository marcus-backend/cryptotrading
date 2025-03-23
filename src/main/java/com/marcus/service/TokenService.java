package com.marcus.service;

import com.marcus.model.auth.Token;

public interface TokenService {
    Token getByUsername(String username);
    
    int save(Token token);
    
    void delete(String username);
}
