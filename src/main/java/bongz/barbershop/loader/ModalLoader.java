package bongz.barbershop.loader;

import java.io.IOException;
import java.util.function.Consumer;

import bongz.barbershop.App;
import bongz.barbershop.dto.transaction.TransactionViewDTO;
import bongz.barbershop.layout.authentication.AuthenticationController;
import bongz.barbershop.layout.authentication.RegistrationController;
import bongz.barbershop.layout.dashboards.modal.NewTransactionModalController;
import bongz.barbershop.layout.dashboards.modal.TransactionDetailModalController;
import bongz.barbershop.layout.dashboards.modal.VoidConfirmationModalController;
import bongz.barbershop.model.BarberModel;
import bongz.barbershop.model.UserModel;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class ModalLoader {

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

    public static void load_login_window(App app) throws IOException {
        FXMLLoader loader = load_modal(app, "authentication/AuthenticationView");

        AuthenticationController controller = loader.getController();
        controller.load(app);
    }

    public static void load_registration_window(App app) throws IOException {
        FXMLLoader loader = load_modal(app, "authentication/RegistrationView");

        RegistrationController controller = loader.getController();
        controller.load(app);
    }

    public static void load_new_transaction_modal(
            App app,
            UserModel currentUser,
            BarberModel selectedBarber,
            String businessDate,
            Consumer<String> onTransactionRecorded) throws IOException {
        FXMLLoader loader = load_modal(app, "dashboards/modal/NewTransactionModal");

        NewTransactionModalController controller = loader.getController();
        controller.load(app, currentUser, selectedBarber, businessDate, onTransactionRecorded);
    }

    public static void load_transaction_detail_modal(
            App app,
            UserModel currentUser,
            TransactionViewDTO selectedTransaction,
            Consumer<String> onTransactionUpdated) throws IOException {
        FXMLLoader loader = load_modal(app, "dashboards/modal/TransactionDetailModal");

        TransactionDetailModalController controller = loader.getController();
        controller.load(app, currentUser, selectedTransaction, onTransactionUpdated);
    }

    public static void load_void_confirmation_modal(
            App app,
            UserModel currentUser,
            TransactionViewDTO selectedTransaction,
            Consumer<String> onTransactionVoided) throws IOException {
        FXMLLoader loader = load_modal(app, "dashboards/modal/VoidConfirmationModal");

        VoidConfirmationModalController controller = loader.getController();
        controller.load(app, currentUser, selectedTransaction, onTransactionVoided);
    }

    public static void modal_close(App app) {
        modal_close(app, 1);
    }

    public static void modal_close(App app, int layers) {
        if (app == null || app.getMainScreen() == null || layers <= 0) {
            return;
        }

        for (int i = 0; i < layers; i++) {
            int childCount = app.getMainScreen().getChildren().size();
            if (childCount <= 1) {
                return;
            }

            app.getMainScreen().getChildren().remove(childCount - 1);
        }
    }
}
