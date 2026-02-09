package bongz.barbershop.loader;

import java.io.IOException;

import bongz.barbershop.App;
import bongz.barbershop.layout.MainAppController;
import bongz.barbershop.layout.dashboards.OwnerDashboardController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ManagerOperationLoader {
    private static FXMLLoader load(String filename) {
        return new FXMLLoader(App.class.getResource("layout/" + filename + ".fxml"));
    }

    static Scene scene;
    private static MainAppController mainAppController;

    public static void load_owner_dashboard_window(App app, Stage mainStage) throws IOException {
        mainStage.setTitle("Bongz Barbershop Transaction Tracker System");

        FXMLLoader loader = load("dashboards/OwnerDashboardView");
        Pane root = loader.load();
        root.setOnMousePressed(e -> root.requestFocus());

        scene = new Scene(root);

        app.setMainScreen(root);

        mainStage.setScene(scene);
        mainStage.setMinHeight(768);
        mainStage.setMinWidth(1024);
        mainStage.setMaximized(false);
        mainStage.setResizable(true);
        mainStage.setFullScreen(false);

        mainStage.show();

        OwnerDashboardController controller = loader.getController();
        controller.load(app, mainAppController);
    }
}
