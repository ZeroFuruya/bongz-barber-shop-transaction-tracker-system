package bongz.barbershop.service;

import bongz.barbershop.model.UserModel;

public class AuthResult {
    private final boolean success;
    private final String message;
    private final UserModel user;

    public AuthResult(boolean success, String message, UserModel user) {
        this.success = success;
        this.message = message;
        this.user = user;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public UserModel getUser() {
        return user;
    }
}
