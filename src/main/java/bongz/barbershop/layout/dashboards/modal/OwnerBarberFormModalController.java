package bongz.barbershop.layout.dashboards.modal;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

import bongz.barbershop.App;
import bongz.barbershop.loader.ModalLoader;
import bongz.barbershop.model.BarberModel;
import bongz.barbershop.service.barber.BarberService;
import bongz.barbershop.storage.AppDataPaths;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class OwnerBarberFormModalController {

    private final BarberService barberService = new BarberService();

    private App app;
    private BarberModel selectedBarber;
    private Consumer<String> onBarberSaved;
    private String originalImagePath;
    private boolean removeExistingImage;
    private Path pendingImageSource;
    private String pendingImageRelativePath;

    @FXML
    private Label titleLabel;
    @FXML
    private Label barberIdLabel;
    @FXML
    private Label createdAtLabel;
    @FXML
    private TextField nameField;
    @FXML
    private TextField imagePathField;
    @FXML
    private Label imageHelpLabel;
    @FXML
    private TextField displayOrderField;
    @FXML
    private CheckBox activeCheckBox;
    @FXML
    private Button browseImageButton;
    @FXML
    private Button clearImageButton;
    @FXML
    private Button saveButton;
    @FXML
    private Label statusLabel;

    public void load(App app, BarberModel selectedBarber, Consumer<String> onBarberSaved) {
        this.app = app;
        this.selectedBarber = selectedBarber;
        this.onBarberSaved = onBarberSaved;
        this.originalImagePath = selectedBarber == null ? null : selectedBarber.getImagePath();
        this.removeExistingImage = false;
        this.pendingImageSource = null;
        this.pendingImageRelativePath = null;

        if (selectedBarber == null) {
            titleLabel.setText("Add Barber");
            barberIdLabel.setText("Barber ID: New");
            createdAtLabel.setText("Created At: (new)");
            activeCheckBox.setSelected(true);
            updateImagePresentation();
            return;
        }

        titleLabel.setText("Edit Barber");
        barberIdLabel.setText("Barber ID: " + selectedBarber.getBarberId());
        createdAtLabel.setText("Created At: " + valueOrPlaceholder(selectedBarber.getCreatedAt()));
        nameField.setText(selectedBarber.getName());
        displayOrderField.setText(String.valueOf(selectedBarber.getDisplayOrder()));
        activeCheckBox.setSelected(selectedBarber.getIsActive() == 1);
        updateImagePresentation();
    }

    @FXML
    private void handleBrowseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Barber Image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp", "*.webp"));

        File selectedImage = fileChooser.showOpenDialog(getWindow());
        if (selectedImage == null) {
            return;
        }

        pendingImageSource = selectedImage.toPath();
        pendingImageRelativePath = AppDataPaths.createBarberImageRelativePath(selectedImage.getName());
        removeExistingImage = false;
        updateImagePresentation();
        showStatus("New barber image selected. Save to store it in app data.");
    }

    @FXML
    private void handleClearImage() {
        if (pendingImageRelativePath != null) {
            pendingImageSource = null;
            pendingImageRelativePath = null;
            updateImagePresentation();
            showStatus("Pending barber image selection removed.");
            return;
        }

        if (originalImagePath == null || originalImagePath.isBlank()) {
            return;
        }

        removeExistingImage = !removeExistingImage;
        updateImagePresentation();
        showStatus(removeExistingImage
                ? "Existing barber image will be cleared when you save."
                : "Existing barber image restored.");
    }

    @FXML
    private void handleSave() {
        int displayOrder = parseInteger(displayOrderField.getText());
        int isActive = activeCheckBox.isSelected() ? 1 : 0;
        String resolvedImagePath = resolveImagePathForSave();
        boolean copiedNewImage = false;

        try {
            if (pendingImageSource != null && pendingImageRelativePath != null) {
                AppDataPaths.copyBarberImage(pendingImageSource, pendingImageRelativePath);
                copiedNewImage = true;
            }
        } catch (IOException exception) {
            showStatus("Failed to store the selected barber image.");
            return;
        }

        if (selectedBarber == null) {
            var result = barberService.createBarber(
                    nameField.getText(),
                    resolvedImagePath,
                    displayOrder);

            if (!result.isSuccess()) {
                if (copiedNewImage) {
                    AppDataPaths.deleteQuietly(pendingImageRelativePath);
                }
                showStatus(result.getMessage());
                return;
            }

            BarberModel savedBarber = result.getData();
            if (savedBarber != null && isActive == 0) {
                var deactivateResult = barberService.setBarberActiveStatus(savedBarber.getBarberId(), 0);
                if (!deactivateResult.isSuccess()) {
                    showStatus(deactivateResult.getMessage());
                    return;
                }
            }

            closeWithMessage("Barber created.");
            return;
        }

        var result = barberService.updateBarber(
                selectedBarber.getBarberId(),
                nameField.getText(),
                resolvedImagePath,
                displayOrder,
                isActive);

        if (!result.isSuccess()) {
            if (copiedNewImage) {
                AppDataPaths.deleteQuietly(pendingImageRelativePath);
            }
            showStatus(result.getMessage());
            return;
        }

        closeWithMessage("Barber updated.");
    }

    @FXML
    private void handleCancel() {
        ModalLoader.modal_close(app);
    }

    private String resolveImagePathForSave() {
        if (pendingImageRelativePath != null && pendingImageSource != null) {
            return pendingImageRelativePath;
        }

        if (removeExistingImage) {
            return null;
        }

        return originalImagePath;
    }

    private int parseInteger(String value) {
        try {
            return Integer.parseInt(value == null || value.isBlank() ? "0" : value.trim());
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    private String valueOrPlaceholder(String value) {
        return value == null || value.isBlank() ? "(none)" : value;
    }

    private void updateImagePresentation() {
        if (pendingImageRelativePath != null) {
            imagePathField.setText(pendingImageRelativePath);
            imageHelpLabel.setText("New image selected. It will be copied into the app data folder when you save.");
            clearImageButton.setDisable(false);
            clearImageButton.setText("Remove Selection");
            return;
        }

        if (removeExistingImage) {
            imagePathField.setText("(will be cleared)");
            imageHelpLabel.setText("The current stored image will be removed after you save this barber.");
            clearImageButton.setDisable(false);
            clearImageButton.setText("Restore Image");
            return;
        }

        if (originalImagePath != null && !originalImagePath.isBlank()) {
            imagePathField.setText(originalImagePath);
            imageHelpLabel.setText("Current stored image. Browse a new file if you want to replace it.");
            clearImageButton.setDisable(false);
            clearImageButton.setText("Clear Image");
            return;
        }

        imagePathField.clear();
        imageHelpLabel.setText("No image selected yet. Browse a file and it will be copied into the app data folder when you save.");
        clearImageButton.setDisable(true);
        clearImageButton.setText("Clear Image");
    }

    private Window getWindow() {
        if (saveButton != null && saveButton.getScene() != null) {
            return saveButton.getScene().getWindow();
        }

        return app == null ? null : app.getMainStage();
    }

    private void closeWithMessage(String message) {
        ModalLoader.modal_close(app);
        if (onBarberSaved != null) {
            onBarberSaved.accept(message);
        }
    }

    private void showStatus(String message) {
        statusLabel.setText(message == null ? "" : message);
    }
}
