package bongz.barbershop.layout.dashboards.modal;

import java.util.function.Consumer;

import bongz.barbershop.App;
import bongz.barbershop.dto.transaction.TransactionViewDTO;
import bongz.barbershop.loader.ModalLoader;
import bongz.barbershop.model.UserModel;
import bongz.barbershop.service.transaction.TransactionService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class VoidConfirmationModalController {

    private final TransactionService transactionService = new TransactionService();

    private App app;
    private UserModel currentUser;
    private TransactionViewDTO selectedTransaction;
    private Consumer<String> onTransactionVoided;

    @FXML
    private Label titleLabel;
    @FXML
    private Label transactionSummaryLabel;
    @FXML
    private TextArea voidReasonTextArea;
    @FXML
    private Button confirmButton;
    @FXML
    private Label statusLabel;

    @FXML
    private void initialize() {
        confirmButton.setDisable(true);
        voidReasonTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            confirmButton.setDisable(newValue == null || newValue.trim().isEmpty());
        });
    }

    public void load(
            App app,
            UserModel currentUser,
            TransactionViewDTO selectedTransaction,
            Consumer<String> onTransactionVoided) {
        this.app = app;
        this.currentUser = currentUser != null ? currentUser : app.getCurrentUser();
        this.selectedTransaction = selectedTransaction;
        this.onTransactionVoided = onTransactionVoided;

        if (selectedTransaction == null) {
            titleLabel.setText("Void Transaction");
            confirmButton.setDisable(true);
            showStatus("No transaction was provided.");
            return;
        }

        titleLabel.setText("Void Transaction #" + selectedTransaction.getTransactionId());
        transactionSummaryLabel.setText(
                valueOrPlaceholder(selectedTransaction.getBarberName())
                        + " | "
                        + valueOrPlaceholder(selectedTransaction.getPricingCategoryName())
                        + " | "
                        + valueOrPlaceholder(selectedTransaction.getBusinessDate()));
    }

    @FXML
    private void handleConfirm() {
        if (selectedTransaction == null) {
            showStatus("No transaction selected.");
            return;
        }

        if (currentUser == null) {
            showStatus("No logged-in user found.");
            return;
        }

        var result = transactionService.voidTransaction(
                selectedTransaction.getTransactionId(),
                voidReasonTextArea.getText(),
                currentUser.getId());

        if (!result.isSuccess()) {
            showStatus(result.getMessage());
            return;
        }

        ModalLoader.modal_close(app, 2);
        if (onTransactionVoided != null) {
            onTransactionVoided.accept("Voided transaction #" + selectedTransaction.getTransactionId() + ".");
        }
    }

    @FXML
    private void handleCancel() {
        ModalLoader.modal_close(app);
    }

    private String valueOrPlaceholder(String value) {
        return value == null || value.isBlank() ? "(none)" : value;
    }

    private void showStatus(String message) {
        statusLabel.setText(message == null ? "" : message);
    }
}
