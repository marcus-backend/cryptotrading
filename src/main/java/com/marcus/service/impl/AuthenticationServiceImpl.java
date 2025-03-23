package com.marcus.service.impl;

import com.marcus.dto.request.ConfirmResetPasswordDTO;
import com.marcus.dto.request.ForgotPasswordDTO;
import com.marcus.dto.request.ResetPasswordDTO;
import com.marcus.dto.request.SignInRequestDTO;
import com.marcus.dto.response.ConfirmResetPasswordResponse;
import com.marcus.dto.response.ForgotPasswordResponse;
import com.marcus.dto.response.ResetPasswordResponse;
import com.marcus.dto.response.TokenResponse;
import com.marcus.exception.InvalidDataException;
import com.marcus.model.auth.User;
import com.marcus.repository.UserRepository;
import com.marcus.service.AuthenticationService;
import com.marcus.service.JWTService;
import com.marcus.service.UserService;
import com.marcus.util.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Builder
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JWTService jwtService;
    private final ServerProperties serverProperties;
    private final PasswordEncoder passwordEncoder;

    @Override
    public TokenResponse accessToken(SignInRequestDTO signInRequest) {
        log.info("---------- authenticate ----------");
        User user = userService.getByUsername(signInRequest.getUsername());
        List<String> roles = userService.findRolesByUser(user.getId());
        List<SimpleGrantedAuthority> authorities = roles.stream().map(SimpleGrantedAuthority::new).toList();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword(), authorities));

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    @Override
    public TokenResponse refreshToken(HttpServletRequest request) {
        log.info("---------- refreshToken ----------");
        final String refreshToken = request.getHeader(HttpHeaders.REFERER);
        if (StringUtils.isBlank(refreshToken)) {
            throw new InvalidDataException("Token must be not blank");
        }
        final String userName = jwtService.extractUsername(refreshToken, TokenType.REFRESH_TOKEN);
        User user = userService.getByUsername(userName);
        if (!jwtService.isValid(refreshToken, TokenType.REFRESH_TOKEN, user)) {
            throw new InvalidDataException("Not allow access with this token");
        }
        // create new access token
        String accessToken = jwtService.generateToken(user);
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    @Override
    public String removeToken(HttpServletRequest request) {
        log.info("---------- removeToken ----------");
        final String token = request.getHeader(HttpHeaders.REFERER);
        if (StringUtils.isBlank(token)) {
            throw new InvalidDataException("Token must be not blank");
        }
        final String userName = jwtService.extractUsername(token, TokenType.ACCESS_TOKEN);
        return "Deleted!";
    }

    /**
     * Forgot password
     *
     * @param request
     * @return link to resetPassword
     */
    @Override
    public ForgotPasswordResponse forgotPassword(ForgotPasswordDTO request) {
        log.info("---------- forgotPassword ----------");
        // email is exists
        User user = userService.getUserByEmail(request.getEmail());
        // generate reset token
        String resetToken = jwtService.generateResetToken(user);
        // save to redis
        String body = "{\"recretKey\":\"" + resetToken + "\"}";
        String confirmUri = String.format("curl --location 'http://localhost:" + serverProperties.getPort() + "/auth/confirm-reset-password' \\\n" +
                "--header 'accept: */*' \\\n" +
                "--header 'Content-Type: application/json' \\\n" +
                "--data '%s'", body);
        return ForgotPasswordResponse.builder()
                .uri(confirmUri)
                .resetToken(resetToken)
                .build();
    }

    /**
     * Reset password
     *
     * @param request
     * @return reset
     */
    @Override
    public ConfirmResetPasswordResponse resetPassword(ConfirmResetPasswordDTO request) {
        log.info("---------- resetPassword ----------");
        validateToken(request.getResetToken());
        return ConfirmResetPasswordResponse.builder()
                .resetToken(request.getResetToken())
                .build();
    }

    /**
     * Change password
     *
     * @param request
     * @return reset
     */
    @Override
    public ResetPasswordResponse changePassword(ResetPasswordDTO request) {
        log.info("---------- changePassword ----------");
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new InvalidDataException("Password and confirm password do not match");
        }
        // get user by reset token
        User user = validateToken(request.getSecretKey());
        // update password 
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userService.save(user);
        return ResetPasswordResponse.builder().message("Password has been changed").build();
    }

    @Override
    public String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        return null;
    }

    private User validateToken(String token) {
        // validate token
        String username = jwtService.extractUsername(token, TokenType.RESET_TOKEN);
        // validate user is active
        User user = userService.getByUsername(username);
        if (!user.isEnabled()) {
            throw new InvalidDataException("User is not activated");
        }
        return user;
    }
}

