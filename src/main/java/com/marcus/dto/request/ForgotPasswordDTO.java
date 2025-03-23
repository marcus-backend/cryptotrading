package com.marcus.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordDTO {
    @NotBlank(message = "email must be not blank")
    private String email;
}
