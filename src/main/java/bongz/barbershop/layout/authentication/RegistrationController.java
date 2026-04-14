package bongz.barbershop.layout.authentication;

import bongz.barbershop.model.UserModel;
import bongz.barbershop.service.authentication.AuthResult;
import bongz.barbershop.service.authentication.RegistrationService;

import java.io.IOException;

import bongz.barbershop.App;
import bongz.barbershop.loader.ModalLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegistrationController {

    private App app;

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    @FXML
    private Button registerButton;

    private final RegistrationService registrationService = new RegistrationService();

    public void load(App app) {
        this.app = app;
    }

    @FXML
    private void handleRegister(ActionEvent event) throws IOException {
        String defaultRole = "MANAGER";

        AuthResult result = registrationService.register(
                usernameField.getText().trim(),
                passwordField.getText(),
                defaultRole);

        if (!result.isSuccess()) {
            showError(result.getMessage());
            return;
        }

        UserModel user = result.getUser();

        System.out.println("REGISTRATION SUCCESS: " + user.getUsername() + " (" + user.getRole() + ")");

        closeModal();
    }

    private void showError(String message) {
        System.err.println("REGISTRATION ERROR: " + message);
    }

    private void closeModal() {
        ModalLoader.modal_close(app);
    }
}
