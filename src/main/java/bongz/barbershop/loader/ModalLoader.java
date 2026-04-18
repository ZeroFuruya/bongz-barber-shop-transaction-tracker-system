package bongz.barbershop.loader;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import bongz.barbershop.App;
import bongz.barbershop.dto.common.ServiceResult;
import bongz.barbershop.dto.transaction.TransactionViewDTO;
import bongz.barbershop.layout.authentication.AuthenticationController;
import bongz.barbershop.layout.authentication.RegistrationController;
import bongz.barbershop.layout.dashboards.modal.NewTransactionModalController;
import bongz.barbershop.layout.dashboards.modal.OwnerBarberFormModalController;
import bongz.barbershop.layout.dashboards.modal.OwnerConfirmActionModalController;
import bongz.barbershop.layout.dashboards.modal.OwnerPricingCategoryFormModalController;
import bongz.barbershop.layout.dashboards.modal.OwnerShopSettingsModalController;
import bongz.barbershop.layout.dashboards.modal.OwnerTransactionsListModalController;
import bongz.barbershop.layout.dashboards.modal.OwnerUserFormModalController;
import bongz.barbershop.layout.dashboards.modal.TransactionDetailModalController;
import bongz.barbershop.layout.dashboards.modal.VoidConfirmationModalController;
import bongz.barbershop.model.BarberModel;
import bongz.barbershop.model.PricingCategoryModel;
import bongz.barbershop.model.ShopSettingsModel;
import bongz.barbershop.model.UserModel;
import bongz.barbershop.ui.AnimationSupport;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class ModalLoader {

    private static FXMLLoader load_modal(App app, String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(App.class.getResource("layout/" + fxml + ".fxml"));

        Pane pane = loader.load();
        AnimationSupport.installInteractiveControls(pane);

        StackPane modal = new StackPane(pane);
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
        AnimationSupport.playModalEntrance(modal, pane);

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

    public static void load_owner_barber_form_modal(
            App app,
            BarberModel selectedBarber,
            Consumer<String> onBarberSaved) throws IOException {
        FXMLLoader loader = load_modal(app, "dashboards/modal/OwnerBarberFormModal");

        OwnerBarberFormModalController controller = loader.getController();
        controller.load(app, selectedBarber, onBarberSaved);
    }

    public static void load_owner_pricing_category_form_modal(
            App app,
            PricingCategoryModel selectedCategory,
            Consumer<String> onCategorySaved) throws IOException {
        FXMLLoader loader = load_modal(app, "dashboards/modal/OwnerPricingCategoryFormModal");

        OwnerPricingCategoryFormModalController controller = loader.getController();
        controller.load(app, selectedCategory, onCategorySaved);
    }

    public static void load_owner_user_form_modal(
            App app,
            UserModel selectedUser,
            Consumer<String> onUserSaved) throws IOException {
        FXMLLoader loader = load_modal(app, "dashboards/modal/OwnerUserFormModal");

        OwnerUserFormModalController controller = loader.getController();
        controller.load(app, selectedUser, onUserSaved);
    }

    public static void load_owner_shop_settings_modal(
            App app,
            ShopSettingsModel settings,
            Consumer<String> onSettingsSaved) throws IOException {
        FXMLLoader loader = load_modal(app, "dashboards/modal/OwnerShopSettingsModal");

        OwnerShopSettingsModalController controller = loader.getController();
        controller.load(app, settings, onSettingsSaved);
    }

    public static void load_owner_confirm_action_modal(
            App app,
            String title,
            String summary,
            String confirmButtonText,
            Supplier<ServiceResult<?>> confirmAction,
            Consumer<String> onActionCompleted) throws IOException {
        FXMLLoader loader = load_modal(app, "dashboards/modal/OwnerConfirmActionModal");

        OwnerConfirmActionModalController controller = loader.getController();
        controller.load(app, title, summary, confirmButtonText, confirmAction, onActionCompleted);
    }

    public static void load_owner_transactions_list_modal(
            App app,
            UserModel currentUser,
            String title,
            String summary,
            Supplier<List<TransactionViewDTO>> reloadSupplier,
            Consumer<String> onTransactionUpdated) throws IOException {
        FXMLLoader loader = load_modal(app, "dashboards/modal/OwnerTransactionsListModal");

        OwnerTransactionsListModalController controller = loader.getController();
        controller.load(app, currentUser, title, summary, reloadSupplier, onTransactionUpdated);
    }

    public static void modal_close(App app) {
        modal_close(app, 1);
    }

    public static void modal_close(App app, int layers) {
        if (app == null || app.getMainScreen() == null || layers <= 0) {
            return;
        }

        closeTopModalLayers(app, layers);
    }

    private static void closeTopModalLayers(App app, int remainingLayers) {
        if (remainingLayers <= 0) {
            return;
        }

        Pane topModal = findTopModalLayer(app);
        if (topModal == null) {
            return;
        }

        Node modalContent = topModal.getChildren().isEmpty() ? null : topModal.getChildren().get(0);
        AnimationSupport.playModalExit(topModal, modalContent, () -> {
            app.getMainScreen().getChildren().remove(topModal);
            closeTopModalLayers(app, remainingLayers - 1);
        });
    }

    private static Pane findTopModalLayer(App app) {
        if (app == null || app.getMainScreen() == null) {
            return null;
        }

        for (int index = app.getMainScreen().getChildren().size() - 1; index >= 0; index--) {
            Node child = app.getMainScreen().getChildren().get(index);
            if (child instanceof Pane pane && pane.getStyleClass().contains("modal-bg")) {
                return pane;
            }
        }

        return null;
    }
}
