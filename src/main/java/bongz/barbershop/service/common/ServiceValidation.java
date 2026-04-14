package bongz.barbershop.service.common;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public final class ServiceValidation {

    private ServiceValidation() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public static boolean isPositiveId(int value) {
        return value > 0;
    }

    public static boolean isBinaryFlag(int value) {
        return value == 0 || value == 1;
    }

    public static String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public static String normalizeBusinessDate(String businessDate) {
        String trimmed = trimToNull(businessDate);
        if (trimmed == null) {
            return null;
        }

        try {
            return LocalDate.parse(trimmed).toString();
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

    public static String normalizePricingCode(String code) {
        String trimmed = trimToNull(code);
        return trimmed == null ? null : trimmed.toUpperCase();
    }

    public static String normalizeOptionalRelativePath(String path) {
        String trimmed = trimToNull(path);
        if (trimmed == null) {
            return null;
        }

        return trimmed.replace('\\', '/');
    }

    public static boolean isRelativePath(String path) {
        String normalizedPath = normalizeOptionalRelativePath(path);
        if (normalizedPath == null) {
            return true;
        }

        try {
            return !Path.of(normalizedPath).isAbsolute();
        } catch (InvalidPathException exception) {
            return false;
        }
    }
}
