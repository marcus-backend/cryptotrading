package com.marcus.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
@AllArgsConstructor
public class TokenResponse implements Serializable {
    private String accessToken;
    private String refreshToken;
    private Long userId;
    // more over
}
