package bongz.barbershop.layout.dashboards.modal;

import java.util.function.Consumer;

import bongz.barbershop.App;
import bongz.barbershop.loader.ModalLoader;
import bongz.barbershop.model.UserModel;
import bongz.barbershop.model.enums.UserRole;
import bongz.barbershop.service.user.UserService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class OwnerUserFormModalController {

    private final UserService userService = new UserService();

    private App app;
    private UserModel selectedUser;
    private Consumer<String> onUserSaved;

    @FXML
    private Label titleLabel;
    @FXML
    private Label userIdLabel;
    @FXML
    private Label createdAtLabel;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private CheckBox activeCheckBox;
    @FXML
    private Button saveButton;
    @FXML
    private Label statusLabel;

    @FXML
    private void initialize() {
        roleComboBox.setItems(FXCollections.observableArrayList(UserRole.MANAGER.name()));
        roleComboBox.getSelectionModel().selectFirst();
        roleComboBox.setDisable(true);
    }

    public void load(App app, UserModel selectedUser, Consumer<String> onUserSaved) {
        this.app = app;
        this.selectedUser = selectedUser;
        this.onUserSaved = onUserSaved;

        if (selectedUser == null) {
            titleLabel.setText("Add Manager");
            userIdLabel.setText("User ID: New");
            createdAtLabel.setText("Created At: (new)");
            activeCheckBox.setSelected(true);
            return;
        }

        titleLabel.setText("Edit Manager");
        userIdLabel.setText("User ID: " + selectedUser.getId());
        createdAtLabel.setText("Created At: " + valueOrPlaceholder(selectedUser.getCreatedAt()));
        usernameField.setText(selectedUser.getUsername());
        activeCheckBox.setSelected(selectedUser.getIsActive() == 1);
        roleComboBox.getSelectionModel().select(selectedUser.getRole());
    }

    @FXML
    private void handleSave() {
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!valueOrEmpty(password).equals(valueOrEmpty(confirmPassword))) {
            showStatus("Passwords do not match.");
            return;
        }

        if (selectedUser == null) {
            var result = userService.createUser(
                    usernameField.getText(),
                    password,
                    UserRole.MANAGER);

            if (!result.isSuccess()) {
                showStatus(result.getMessage());
                return;
            }

            UserModel savedUser = result.getData();
            if (savedUser != null && !activeCheckBox.isSelected()) {
                var deactivateResult = userService.setUserActiveStatus(savedUser.getId(), 0);
                if (!deactivateResult.isSuccess()) {
                    showStatus(deactivateResult.getMessage());
                    return;
                }
            }

            closeWithMessage("Manager account created.");
            return;
        }

        var result = userService.updateUser(
                selectedUser.getId(),
                usernameField.getText(),
                password,
                UserRole.MANAGER,
                activeCheckBox.isSelected() ? 1 : 0);

        if (!result.isSuccess()) {
            showStatus(result.getMessage());
            return;
        }

        closeWithMessage("Manager account updated.");
    }

    @FXML
    private void handleCancel() {
        ModalLoader.modal_close(app);
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    private String valueOrPlaceholder(String value) {
        return value == null || value.isBlank() ? "(none)" : value;
    }

    private void closeWithMessage(String message) {
        ModalLoader.modal_close(app);
        if (onUserSaved != null) {
            onUserSaved.accept(message);
        }
    }

    private void showStatus(String message) {
        statusLabel.setText(message == null ? "" : message);
    }
}
