package com.ziyi.leetcodereviewsystem;

public class HelloResponse {
    private String message;
    private String status;
    public HelloResponse(String message, String status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
}
