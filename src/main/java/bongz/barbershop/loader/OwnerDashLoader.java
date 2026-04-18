package bongz.barbershop.loader;

import java.io.IOException;

import bongz.barbershop.App;
import bongz.barbershop.model.UserModel;
import bongz.barbershop.layout.dashboards.OwnerDashboardController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class OwnerDashLoader {
    private static FXMLLoader load(String filename) {
        return new FXMLLoader(App.class.getResource("layout/" + filename + ".fxml"));
    }

    public static void load_owner_dashboard_window(App app, Stage mainStage, UserModel currentUser)
            throws IOException {
        mainStage.setTitle("Bongz Barbershop Transaction Tracker System (Owner)");

        FXMLLoader loader = load("dashboards/OwnerDashboardView");
        Pane root = loader.load();
        root.setOnMousePressed(e -> root.requestFocus());

        Scene scene = new Scene(root);
        scene.getStylesheets().add(
                App.class.getResource("styles/global.css").toExternalForm());

        app.setMainScreen(root);

        mainStage.setScene(scene);
        mainStage.setMinHeight(768);
        mainStage.setMinWidth(1024);
        mainStage.setMaximized(false);
        mainStage.setResizable(true);
        mainStage.setFullScreen(false);

        mainStage.show();

        OwnerDashboardController controller = loader.getController();
        controller.load(app, currentUser);
    }
}
