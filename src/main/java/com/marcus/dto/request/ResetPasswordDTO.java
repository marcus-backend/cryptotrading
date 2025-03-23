package com.marcus.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordDTO {
    @NotBlank(message = "secret key must be not blank")
    private String secretKey;
    
    @NotBlank(message = "password must be not blank")
    private String password;
    
    @NotBlank(message = "confirm password must be not blank")
    private String confirmPassword;
}
