package org.kate._4fileservice.controller;

// The <T> makes this class "Generic"
public class ApiResponse<T> {
    private String status;
    private String message;
    private T data; // This can now be anything!

    public ApiResponse(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // Getters
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}