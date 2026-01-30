package bongz.barbershop.layout.authentication;

import bongz.barbershop.model.UserModel;
import bongz.barbershop.service.AuthenticatorService;
import bongz.barbershop.service.AuthResult;

import java.io.IOException;

import bongz.barbershop.App;
import bongz.barbershop.loader.OwnerDashLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
// import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class AuthenticationController {

    private App app;
    private Stage mainStage;

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;

    @FXML
    private ImageView redBallImageView;
    @FXML
    private ImageView bongzLogoImageView;

    private final AuthenticatorService authService = new AuthenticatorService();

    // @FXML
    // private void initialize() {
    // redBallImageView.setImage(
    // new Image(getClass().getResourceAsStream(
    // "/bongz/barbershop/assets/images/Redbally.png")));

    // bongzLogoImageView.setImage(
    // new Image(getClass().getResourceAsStream(
    // "/bongz/barbershop/assets/images/Bongzicon.png")));
    // }

    public void load(App app) {
        this.app = app;
    }

    @FXML
    private void handleLogin(ActionEvent event) throws IOException {

        load(app);
        AuthResult result = authService.login(
                usernameField.getText().trim(),
                passwordField.getText());

        if (!result.isSuccess()) {
            showError(result.getMessage());
            return;
        }

        UserModel user = result.getUser();

        System.out.println("LOGIN SUCCESS: " + user.getUsername() + " (" + user.getRole() + ")");

        switch (user.getRole()) {
            case "OWNER" -> OwnerDashLoader.load_owner_dashboard(app);
            // case "MANAGER" -> AuthLoader.load_manager_dashboard();
            default -> showError("Unauthorized role");
        }
    }

    private void showError(String message) {
        System.err.println("LOGIN ERROR: " + message);
    }
}
