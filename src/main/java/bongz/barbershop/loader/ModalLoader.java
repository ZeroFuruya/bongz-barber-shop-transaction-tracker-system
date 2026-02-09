package bongz.barbershop.loader;

import java.io.IOException;

import bongz.barbershop.App;
import bongz.barbershop.layout.MainAppController;
import bongz.barbershop.layout.authentication.AuthenticationController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class ModalLoader {

    static Scene scene;
    static Pane modal;

    private static FXMLLoader load_modal(App app, String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(App.class.getResource("layout/" + fxml + ".fxml"));

        Pane pane = loader.load();

        modal = new StackPane(pane);
        modal.getStyleClass().add("modal-bg");
        modal.setPadding(new Insets(0, 0, 50, 0));

        app.getMainScreen().getChildren().add(modal);

        StackPane.setAlignment(pane, Pos.TOP_CENTER);
        StackPane.setMargin(pane, new Insets(75, 0, 0, 0));

        modal.setOnMouseClicked(e -> {
            if (e.getTarget() != modal) {
                e.consume();
            } else {
                modal_close(app);
            }
        });

        return loader;
    }

    public static void load_login_window(App app, MainAppController mainAppController) throws IOException {
        FXMLLoader loader = load_modal(app, "authentication/AuthenticationView");

        AuthenticationController controller = loader.getController();
        controller.load(app, mainAppController);
    }

    public static void modal_close(App app) {
        int lastIdx = app.getMainScreen().getChildren().size() - 1;
        app.getMainScreen().getChildren().remove(lastIdx);
    }
}
