package com.bpmthanh.ecommercebackend.responseEntity;

import org.springframework.http.ResponseEntity;

public class CustomResponse<T> {

    private int status;
    private T responseBody;
    private String message;

    public CustomResponse(int status, T responseBody, String message) {
        this.status = status;
        this.responseBody = responseBody;
        this.message = message;
    }

    public CustomResponse() {
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public T getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(T responseBody) {
        this.responseBody = responseBody;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResponseEntity<CustomResponse<T>> build() {
        return ResponseEntity.status(status).body(this);
    }
}