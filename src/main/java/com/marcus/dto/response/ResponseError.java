package com.marcus.dto.response;

public class ResponseError extends ResponseData<Object>{
    public ResponseError(int status, String message) {
        super(status, message);
    }

    public ResponseError(int status, String message, Object data) {
        super(status, message, data);
    }
}
