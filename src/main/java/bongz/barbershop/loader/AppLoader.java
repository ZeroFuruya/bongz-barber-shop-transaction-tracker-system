package bongz.barbershop.loader;

import java.io.IOException;

import bongz.barbershop.App;
import bongz.barbershop.layout.MainAppController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class AppLoader {
    private static FXMLLoader load(String filename) {
        return new FXMLLoader(App.class.getResource("layout/" + filename + ".fxml"));
    }

    static Scene scene;

    public static void load_app_window(App app, Stage mainStage) throws IOException {
        mainStage.setTitle("Bongz Barbershop Transaction Tracker System");

        FXMLLoader loader = load("MainAppView");
        Pane root = loader.load();
        root.setOnMousePressed(e -> root.requestFocus());

        scene = new Scene(root);
        scene.getStylesheets().add(
                App.class.getResource("styles/global.css").toExternalForm());
        app.setMainScreen(root);

        mainStage.setScene(scene);
        mainStage.setMinHeight(768);
        mainStage.setMinWidth(1024);
        mainStage.setMaximized(false);
        mainStage.setResizable(false);
        mainStage.setFullScreen(false);

        mainStage.show();

        MainAppController controller = loader.getController();
        controller.load(app);
    }
}
