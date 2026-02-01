package bongz.barbershop.layout.authentication;

import bongz.barbershop.model.UserModel;
import bongz.barbershop.service.AuthResult;
import bongz.barbershop.service.AuthenticatorService;

import java.io.IOException;

import bongz.barbershop.App;
import bongz.barbershop.layout.MainAppController;
import bongz.barbershop.loader.AuthLoader;
import bongz.barbershop.loader.OwnerDashLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class AuthenticationController {

    private App app;
    private MainAppController mainAppController;

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;

    private final AuthenticatorService authService = new AuthenticatorService();

    public void load(App app, MainAppController mainAppController) {
        this.app = app;
        this.mainAppController = mainAppController;
    }

    @FXML
    private void handleLogin(ActionEvent event) throws IOException {

        load(app, mainAppController);
        AuthResult result = authService.login(
                usernameField.getText().trim(),
                passwordField.getText());

        if (!result.isSuccess()) {
            showError(result.getMessage());
            return;
        }

        UserModel user = result.getUser();

        System.out.println("LOGIN SUCCESS: " + user.getUsername() + " (" + user.getRole() + ")");

        closeModal();

        switch (user.getRole()) {
            case "OWNER" -> OwnerDashLoader.load_owner_dashboard_window(app, app.getMainStage());
            // case "MANAGER" -> AuthLoader.load_manager_dashboard();
            default -> showError("Unauthorized role");
        }
    }

    private void showError(String message) {
        System.err.println("LOGIN ERROR: " + message);
    }

    private void closeModal() {
        AuthLoader.modal_close(app);
    }
}
