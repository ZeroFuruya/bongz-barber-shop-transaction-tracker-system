package bongz.barbershop;

import javafx.application.Application;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

import bongz.barbershop.loader.AppLoader;
import bongz.barbershop.model.UserModel;
import bongz.barbershop.server.core.DatabaseInitializer;

/**
 * JavaFX App
 */
public class App extends Application {

    private Pane mainScreen;
    private Stage mainStage;
    private UserModel currentUser;

    @Override
    public void start(Stage stage) throws IOException {
        DatabaseInitializer.initialize();
        initialize_main(stage);
    }

    public void initialize_main(Stage mainStage) throws IOException {
        this.mainStage = mainStage;
        AppLoader.load_app_window(this, mainStage);
    }

    public void setMainScreen(Pane screen) {
        mainScreen = screen;
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public Pane getMainScreen() {
        return this.mainScreen;
    }

    public UserModel getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserModel currentUser) {
        this.currentUser = currentUser;
    }

    public void clearCurrentUser() {
        this.currentUser = null;
    }

    public static void main(String[] args) {
        launch();
    }

}
