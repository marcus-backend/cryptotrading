package com.marcus.controller;

import com.marcus.config.Translator;
import com.marcus.dto.request.UserRequestDTO;
import com.marcus.dto.response.ResponseData;
import com.marcus.dto.response.ResponseError;
import com.marcus.exception.ResourceNotFoundException;
import com.marcus.service.UserService;
import com.marcus.util.UserStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/user")
@Validated
@Slf4j
@Tag(name = "User Controller")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "Add user", description = "API create new user")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<?> addUser(@Valid @RequestBody UserRequestDTO user) {
        log.info("Request add user: {} {}", user.getFirstName(), user.getLastName());
        try {
            return new ResponseData<>(
                    HttpStatus.CREATED.value(),
                    Translator.toLocale("user.add.success"),
                    userService.save(user));
        } catch (Exception e) {
            log.error("Error message: {}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @Operation(summary = "Update user", description = "API update a user by id")
    @PutMapping("/{userId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseData<?> updateUser(@PathVariable @Min(1) long userId, @Valid @RequestBody UserRequestDTO userDTO) {
        try {
            log.info("Request update user userId={}", userId);
            userService.update(userId, userDTO);
            return new ResponseData<>(
                    HttpStatus.ACCEPTED.value(),
                    Translator.toLocale("user.update.success")
            );

        } catch (ResourceNotFoundException e) {
            log.info("Error message = {}", e.getMessage(), e.getCause());
            return new ResponseError(
                    HttpStatus.BAD_REQUEST.value(),
                    Translator.toLocale("user.update.false")
            );
        }

    }

    @Operation(summary = "Update user status", description = "API update status of a user")
    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseData<?> changeStatus(@PathVariable @Min(1) int userId, @RequestParam UserStatus status) {
        try {
            log.info("Request change user status userId={}", userId);
            userService.changeStatus(userId, status);
            return new ResponseData<>(
                    HttpStatus.ACCEPTED.value(),
                    Translator.toLocale("user.update.status.success")
            );
        } catch (ResourceNotFoundException e) {
            log.info("Error message = {}", e.getMessage(), e.getCause());
            return new ResponseError(
                    HttpStatus.BAD_REQUEST.value(),
                    Translator.toLocale("user.update.status.false")
            );
        }
    }

    @Operation(summary = "Delete user", description = "API delete a user")
    @DeleteMapping("{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<?> deleteUser(@Min(1) @PathVariable int userId) {
        try {
            log.info("Request delete user userId={}", userId);
            userService.delete(userId);
            return new ResponseData<>(
                    HttpStatus.OK.value(),
                    Translator.toLocale("user.del.success")
            );
        } catch (ResourceNotFoundException e) {
            log.info("Error message = {}", e.getMessage(), e.getCause());
            return new ResponseError(
                    HttpStatus.BAD_REQUEST.value(),
                    Translator.toLocale("user.del.false")
            );
        }
    }

    @Operation(summary = "Get user", description = "API get a user")
    @GetMapping("{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<?> getUser(@PathVariable long userId) {
        log.info("Request get user detail, userId={}", userId);
        try {
            return new ResponseData<>(
                    HttpStatus.NO_CONTENT.value(),
                    Translator.toLocale("user.detail.success"),
                    userService.getUser(userId)
            );
        } catch (ResourceNotFoundException e) {
            log.info("Error message = {}", e.getMessage(), e.getCause());
            return new ResponseError(
                    HttpStatus.BAD_REQUEST.value(),
                    Translator.toLocale("user.detail.false") + " : " + e.getMessage()
            );
        }

    }

    @Operation(summary = "Get list user", description = "API get list user")
    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<?> getAllUser(
            @RequestParam(required = false, defaultValue = "1") int pageNo,
            @Min(10) @RequestParam int pageSize
    ) {
        log.info("Request get all of users");
        return new ResponseData<>(
                HttpStatus.OK.value(),
                Translator.toLocale("user.all.success"),
                userService.getUsers(pageNo, pageSize)
        );
    }


}
