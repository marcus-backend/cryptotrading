package com.marcus.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
@AllArgsConstructor
public class ForgotPasswordResponse implements Serializable {
    private String uri;
    private String resetToken;
}
