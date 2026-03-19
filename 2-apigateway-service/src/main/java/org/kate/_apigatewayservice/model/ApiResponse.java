package org.kate._apigatewayservice.model;

public class ApiResponse {
    private boolean success;
    private String message;
    private Object data;

    // Standard Constructor
    public ApiResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Getters and Setters (required for JSON)
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Object getData() { return data; }
}

