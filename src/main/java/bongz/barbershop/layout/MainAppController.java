package bongz.barbershop.layout;

import java.io.IOException;

import bongz.barbershop.App;
import bongz.barbershop.loader.AuthLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainAppController {

    @FXML
    private Button logInButton;
    @FXML
    private Button exitButton;

    private App app;

    public void load(App app) {
        this.app = app;
    }

    @FXML
    private void handleLogIn() throws IOException {
        AuthLoader.load_login_window(app, this);
    }

    @FXML
    private void handleExit() {
        app.getMainStage().close();
    }
}
