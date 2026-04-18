package bongz.barbershop.layout.dashboards.modal;

import java.util.function.Consumer;
import java.util.function.Supplier;

import bongz.barbershop.App;
import bongz.barbershop.dto.common.ServiceResult;
import bongz.barbershop.loader.ModalLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class OwnerConfirmActionModalController {

    private App app;
    private Supplier<ServiceResult<?>> confirmAction;
    private Consumer<String> onActionCompleted;

    @FXML
    private Label titleLabel;
    @FXML
    private Label summaryLabel;
    @FXML
    private Button confirmButton;
    @FXML
    private Label statusLabel;

    public void load(
            App app,
            String title,
            String summary,
            String confirmButtonText,
            Supplier<ServiceResult<?>> confirmAction,
            Consumer<String> onActionCompleted) {
        this.app = app;
        this.confirmAction = confirmAction;
        this.onActionCompleted = onActionCompleted;

        titleLabel.setText(title == null || title.isBlank() ? "Confirm Action" : title);
        summaryLabel.setText(summary == null || summary.isBlank() ? "Review this action before continuing." : summary);
        confirmButton.setText(confirmButtonText == null || confirmButtonText.isBlank() ? "Confirm" : confirmButtonText);
    }

    @FXML
    private void handleConfirm() {
        if (confirmAction == null) {
            showStatus("No action was provided.");
            return;
        }

        ServiceResult<?> result = confirmAction.get();
        if (result == null) {
            showStatus("The action did not return a result.");
            return;
        }

        if (!result.isSuccess()) {
            showStatus(result.getMessage());
            return;
        }

        ModalLoader.modal_close(app);
        if (onActionCompleted != null) {
            onActionCompleted.accept(result.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        ModalLoader.modal_close(app);
    }

    private void showStatus(String message) {
        statusLabel.setText(message == null ? "" : message);
    }
}
