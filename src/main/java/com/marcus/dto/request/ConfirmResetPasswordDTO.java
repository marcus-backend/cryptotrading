package com.marcus.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmResetPasswordDTO {
    @NotBlank(message = "secretKey must be not blank")
    private String resetToken;
}
