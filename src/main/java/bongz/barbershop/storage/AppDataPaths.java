package bongz.barbershop.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;

public final class AppDataPaths {

    private static final String APP_FOLDER_NAME = "Bongz Barbershop Transaction Tracker System";
    private static final String FALLBACK_FOLDER_NAME = ".bongz-barbershop-transaction-tracker-system";
    private static final String BARBER_IMAGES_FOLDER = "barber-images";

    private AppDataPaths() {
    }

    public static void initializeDirectories() throws IOException {
        Files.createDirectories(getBarberImagesDirectory());
    }

    public static Path getAppDataRoot() {
        String appDataDirectory = System.getenv("APPDATA");
        if (appDataDirectory != null && !appDataDirectory.isBlank()) {
            return Path.of(appDataDirectory, APP_FOLDER_NAME);
        }

        return Path.of(System.getProperty("user.home", "."), FALLBACK_FOLDER_NAME);
    }

    public static Path getBarberImagesDirectory() {
        return getAppDataRoot().resolve(BARBER_IMAGES_FOLDER);
    }

    public static String createBarberImageRelativePath(String originalFileName) {
        String extension = extractExtension(originalFileName);
        String baseName = sanitizeBaseName(originalFileName);
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);
        return BARBER_IMAGES_FOLDER + "/" + baseName + "-" + uniqueSuffix + extension;
    }

    public static Path copyBarberImage(Path sourceFile, String relativePath) throws IOException {
        if (sourceFile == null || !Files.isRegularFile(sourceFile)) {
            throw new IOException("Selected image file could not be found.");
        }

        Path targetPath = resolveInAppData(relativePath);
        Files.createDirectories(targetPath.getParent());
        Files.copy(sourceFile, targetPath, StandardCopyOption.REPLACE_EXISTING);
        return targetPath;
    }

    public static void deleteQuietly(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            return;
        }

        try {
            Files.deleteIfExists(resolveInAppData(relativePath));
        } catch (IOException ignored) {
        }
    }

    public static Path resolveAppDataPath(String relativePath) throws IOException {
        return resolveInAppData(relativePath);
    }

    private static Path resolveInAppData(String relativePath) throws IOException {
        if (relativePath == null || relativePath.isBlank()) {
            throw new IOException("A relative app data path is required.");
        }

        Path rootPath = getAppDataRoot().normalize();
        Path resolvedPath = rootPath.resolve(relativePath.replace('\\', '/')).normalize();

        if (!resolvedPath.startsWith(rootPath)) {
            throw new IOException("Resolved app data path escaped the app data directory.");
        }

        return resolvedPath;
    }

    private static String extractExtension(String fileName) {
        String resolvedName = fileName == null ? "" : Path.of(fileName).getFileName().toString();
        int dotIndex = resolvedName.lastIndexOf('.');
        if (dotIndex <= 0 || dotIndex == resolvedName.length() - 1) {
            return "";
        }

        return resolvedName.substring(dotIndex).toLowerCase(Locale.ROOT);
    }

    private static String sanitizeBaseName(String fileName) {
        String resolvedName = fileName == null ? "" : Path.of(fileName).getFileName().toString();
        int dotIndex = resolvedName.lastIndexOf('.');
        String baseName = dotIndex > 0 ? resolvedName.substring(0, dotIndex) : resolvedName;
        String sanitized = baseName
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");

        return sanitized.isBlank() ? "barber-image" : sanitized;
    }
}
