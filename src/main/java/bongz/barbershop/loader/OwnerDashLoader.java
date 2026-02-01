package bongz.barbershop.loader;

import java.io.IOException;

import bongz.barbershop.App;
import bongz.barbershop.layout.MainAppController;
import bongz.barbershop.layout.dashboards.OwnerDashboardController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class OwnerDashLoader {

    // static Scene scene;
    // static Pane modal;

    // public static void load_owner_dashboard_window(App app, MainAppController
    // mainAppController) throws IOException {
    // FXMLLoader loader = load_modal(app, "dashboards/OwnerDashboardView");

    // OwnerDashboardController controller = loader.getController();
    // controller.load(app, mainAppController);
    // }

    // private static FXMLLoader load_modal(App app, String fxml) throws IOException
    // {
    // FXMLLoader loader = new FXMLLoader();
    // loader.setLocation(App.class.getResource("layout/" + fxml + ".fxml"));

    // Pane pane = loader.load();

    // modal = new StackPane(pane);
    // modal.getStyleClass().add("login-bg");
    // modal.setPadding(new Insets(0, 0, 50, 0));

    // app.getMainScreen().getChildren().add(modal);

    // StackPane.setAlignment(pane, Pos.TOP_CENTER);
    // StackPane.setMargin(pane, new Insets(75, 0, 0, 0));

    // modal.setOnMouseClicked(e -> {
    // if (e.getTarget() != modal) {
    // e.consume();
    // } else {
    // modal_close(app);
    // }
    // });

    // return loader;
    // }

    // public static void modal_close(App app) {
    // int lastIdx = app.getMainScreen().getChildren().size() - 1;
    // app.getMainScreen().getChildren().remove(lastIdx);
    // }

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
        mainStage.setResizable(false);
        mainStage.setFullScreen(false);

        mainStage.show();

        OwnerDashboardController controller = loader.getController();
        controller.load(app, mainAppController);
    }
}
