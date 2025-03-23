package com.marcus.dto.request;

import com.marcus.util.Platform;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignInRequestDTO implements Serializable {
    @NotBlank(message = "username must be not null")
    private String username;
    @NotBlank(message = "password must be not null")
    private String password;
    private Platform platform;
    private String deviceToken;
    private String versionApp;
}
