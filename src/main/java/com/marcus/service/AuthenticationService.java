package com.marcus.service;

import com.marcus.dto.request.ConfirmResetPasswordDTO;
import com.marcus.dto.request.ForgotPasswordDTO;
import com.marcus.dto.request.ResetPasswordDTO;
import com.marcus.dto.request.SignInRequestDTO;
import com.marcus.dto.response.ConfirmResetPasswordResponse;
import com.marcus.dto.response.ForgotPasswordResponse;
import com.marcus.dto.response.ResetPasswordResponse;
import com.marcus.dto.response.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {
    TokenResponse accessToken(SignInRequestDTO request);
    
    TokenResponse refreshToken(HttpServletRequest request);
    
    String removeToken(HttpServletRequest request);
    
    ForgotPasswordResponse forgotPassword(ForgotPasswordDTO email);
    
    ConfirmResetPasswordResponse resetPassword(ConfirmResetPasswordDTO request);
    
    ResetPasswordResponse changePassword(ResetPasswordDTO request);

    String getUsername();
}