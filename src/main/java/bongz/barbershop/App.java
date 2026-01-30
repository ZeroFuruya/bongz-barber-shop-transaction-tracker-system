package bongz.barbershop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

import bongz.barbershop.loader.AuthLoader;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private Pane mainScreen;
    private Stage mainStage;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("/bongz/barbershop/layout/authentication/AuthenticationView"), 768, 576);
        stage.setScene(scene);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public void initialize_main(Stage mainStage) throws IOException {
        this.mainStage = mainStage;
        AuthLoader.load_login_window(this, mainStage);
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

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

    public Object getPrimaryStage() {
        throw new UnsupportedOperationException("Unimplemented method 'getPrimaryStage'");
    }

}