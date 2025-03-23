package com.marcus.controller;

import com.marcus.dto.request.ConfirmResetPasswordDTO;
import com.marcus.dto.request.ForgotPasswordDTO;
import com.marcus.dto.request.ResetPasswordDTO;
import com.marcus.dto.request.SignInRequestDTO;
import com.marcus.dto.response.ConfirmResetPasswordResponse;
import com.marcus.dto.response.ForgotPasswordResponse;
import com.marcus.dto.response.ResponseData;
import com.marcus.dto.response.ResponseError;
import com.marcus.dto.response.TokenResponse;
import com.marcus.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("${api.prefix}/auth")
@Validated
@Tag(name = "Authentication Controller")
@RequiredArgsConstructor
public class AuthenticationController {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);
    private final AuthenticationService authenticationService;
    
    @Operation(summary = "Login", description = "API login")
    @PostMapping("/access-token")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TokenResponse> login(@RequestBody SignInRequestDTO request) {
        log.info("Login request: {}", request);
        return new ResponseEntity<>(authenticationService.accessToken(request), HttpStatus.OK);
    }
    
    @Operation(summary = "Refresh token", description = "API refresh token")
    @PostMapping("/refresh-token")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TokenResponse> refreshToken(HttpServletRequest request) {
        return new ResponseEntity<>(authenticationService.refreshToken(request), HttpStatus.OK);
    }
    
    @Operation(summary = "Logout", description = "API logout")
    @PostMapping("/remove-token")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> logout(HttpServletRequest request) {
        return new ResponseEntity<>(authenticationService.removeToken(request), HttpStatus.OK);
    }
    
    @Operation(summary = "Forgot password", description = "API forgot password")
    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<ForgotPasswordResponse> forgotPassword(@RequestBody @Valid ForgotPasswordDTO request) {
        return new ResponseData<>(
                HttpStatus.OK.value(),
                "Forgot password request sent successfully",
                authenticationService.forgotPassword(request)
        );
    }
    
    @Operation(summary = "Reset password", description = "API reset password")
    @PostMapping("/confirm-reset-password")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<ConfirmResetPasswordResponse> resetPassword(@RequestBody ConfirmResetPasswordDTO request) {
        ConfirmResetPasswordResponse response = authenticationService.resetPassword(request);
        return new ResponseData<>(
                HttpStatus.OK.value(),
                "Validate reset token successfully",
                response
        );
    }
    
    @Operation(summary = "Reset password", description = "API reset password")
    @PostMapping("/change-password")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<?> changePassword(@RequestBody @Valid ResetPasswordDTO request) {
        try {
            return new ResponseData<>(
                    HttpStatus.ACCEPTED.value(),
                    authenticationService.changePassword(request).getMessage());
        } catch (Exception e) {
            log.info("Error message = {}", e.getMessage(), e.getCause());
            return new ResponseError(
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage()
            );
        }
    }
}
