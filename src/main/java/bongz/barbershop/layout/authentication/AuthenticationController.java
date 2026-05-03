package bongz.barbershop.layout.authentication;

import bongz.barbershop.model.UserModel;
import bongz.barbershop.service.authentication.AuthResult;
import bongz.barbershop.service.authentication.AuthenticatorService;

import java.io.IOException;

import bongz.barbershop.App;
import bongz.barbershop.loader.ManagerOperationLoader;
import bongz.barbershop.loader.ModalLoader;
import bongz.barbershop.loader.OwnerDashLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AuthenticationController {

    private App app;

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;

    @FXML
    private Label userErrorLabel;
    @FXML
    private Label passErrorLabel;

    private final AuthenticatorService authService = new AuthenticatorService();

    public void load(App app) {
        this.app = app;
    }

    @FXML
    private void handleLogin(ActionEvent event) throws IOException {
        AuthResult result = authService.login(
                usernameField.getText().trim(),
                passwordField.getText());

        if (!result.isSuccess()) {
            showError(result.getMessage());
            return;
        }

        UserModel user = result.getUser();
        app.setCurrentUser(user);

        System.out.println("LOGIN SUCCESS: " + user.getUsername() + " (" + user.getRole() + ")");

        closeModal();

        switch (user.getRole()) {
            case "OWNER" -> OwnerDashLoader.load_owner_dashboard_window(app, app.getMainStage(), user);
            case "MANAGER" -> ManagerOperationLoader.load_manager_dashboard_window(app, app.getMainStage(), user);
            default -> showError("Unauthorized role");
        }
    }

    private void showError(String message) {
        System.err.println("LOGIN ERROR: " + message);
        if (message.toLowerCase().contains("user")) {
            userErrorLabel.setText(message);
            userErrorLabel.setVisible(true);
            passErrorLabel.setVisible(false);
        } else {
            passErrorLabel.setText(message);
            passErrorLabel.setVisible(true);
            userErrorLabel.setVisible(false);
        }

    }

    private void closeModal() {
        ModalLoader.modal_close(app);
    }
}
