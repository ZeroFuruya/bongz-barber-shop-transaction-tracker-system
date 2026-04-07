package bongz.barbershop.model.enums;

public enum UserRole {
    OWNER,
    MANAGER;

    public static UserRole fromValue(String value) {
        return UserRole.valueOf(value.toUpperCase());
    }
}
