package com.marcus.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseData <T>{
    private final int status;
    private final String message;
    private T data;

    // PUT, PATCH, DELETE
    public ResponseData(int status, String message) {
        this.status = status;
        this.message = message;
    }

    // GET, POST
    public ResponseData(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
