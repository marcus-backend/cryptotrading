package com.marcus.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
@AllArgsConstructor
public class ConfirmResetPasswordResponse implements Serializable {
    private String resetToken;
}
