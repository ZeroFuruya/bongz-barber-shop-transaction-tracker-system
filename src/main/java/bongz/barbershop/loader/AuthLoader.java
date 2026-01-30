package bongz.barbershop.loader;

import java.io.IOException;

import bongz.barbershop.App;
import bongz.barbershop.layout.authentication.AuthenticationController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class AuthLoader {

    private static FXMLLoader load(String filename) {
        return new FXMLLoader(App.class.getResource("layout/authentication/" + filename + ".fxml"));
    }

    static Scene scene;

    public static void load_login_window(App app, Stage mainStage) throws IOException {
        mainStage.setTitle("Bongz Barbershop Transaction Tracker System");
        // mainStage.getIcons().add(new
        // Image(App.class.getResourceAsStream("assets/images/ZephyrLMS.png")));

        FXMLLoader loader = load("AuthenticationView");
        Pane root = loader.load();
        root.setOnMousePressed(e -> root.requestFocus());

        scene = new Scene(root);

        app.setMainScreen(root);

        mainStage.setScene(scene);
        mainStage.setMinHeight(567);
        mainStage.setMinWidth(768);
        mainStage.setMaximized(false);

        mainStage.show();

        AuthenticationController controller = loader.getController();
        controller.load(app);
    }

    // TODO : Implement other loaders

    public static void load_manager_dashboard() {
    }

}
