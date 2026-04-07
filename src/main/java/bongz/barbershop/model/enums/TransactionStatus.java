package bongz.barbershop.model.enums;

public enum TransactionStatus {
    POSTED,
    VOID;

    public static TransactionStatus fromValue(String value) {
        return TransactionStatus.valueOf(value.toUpperCase());
    }
}
