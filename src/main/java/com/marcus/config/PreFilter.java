package com.marcus.config;

import com.marcus.exception.InvalidTokenException;
import com.marcus.service.JWTService;
import com.marcus.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.SignatureException;
import java.util.Collection;

import static com.marcus.util.TokenType.ACCESS_TOKEN;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@Slf4j
@RequiredArgsConstructor
public class PreFilter extends OncePerRequestFilter {
    
    private final UserService userService;
    private final JWTService jwtService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("--------------------------- PreFilter ---------------------------");
        final String authorization = request.getHeader(AUTHORIZATION);
        log.info("Authorization: {}", authorization);
        if (StringUtils.isBlank(authorization) || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        final String token = authorization.substring("Bearer ".length());
        try {
            final String userName = jwtService.extractUsername(token, ACCESS_TOKEN);
            if (StringUtils.isNotEmpty(userName) && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userService.userDetailsService().loadUserByUsername(userName);

                boolean isAllowed = havePermissions(request.getRequestURI(), userDetails.getAuthorities());
                if (!jwtService.isValid(token, ACCESS_TOKEN, userDetails)) {
                    throw new InvalidTokenException("Token is invalid or expired");
                }

                if (!isAllowed) {
                    throw new InvalidTokenException("Insufficient permissions for URI: " + request.getRequestURI());
                }

                SecurityContext context = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
            }
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token: {}", e.getMessage());
            throw new InvalidTokenException("Token has expired", e);
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            throw new InvalidTokenException("Invalid token format or signature", e);
        } catch (Exception e) {
            log.error("Error processing token: {}", e.getMessage());
            throw new InvalidTokenException("Error processing token", e);
        }
        filterChain.doFilter(request, response);
    }

    boolean havePermissions(String apiUrl, Collection<? extends GrantedAuthority> roles) {
        // TODO: get data from database
//        if (roles.contains("ROLE_ADMIN")) {
//            return true;
//        }
//        return false;
        return true;
    }

}
