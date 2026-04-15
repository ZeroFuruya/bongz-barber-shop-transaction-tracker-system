package bongz.barbershop.layout.dashboards.modal;

import java.io.IOException;
import java.util.function.Consumer;

import bongz.barbershop.App;
import bongz.barbershop.dto.transaction.TransactionViewDTO;
import bongz.barbershop.loader.ModalLoader;
import bongz.barbershop.model.UserModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class TransactionDetailModalController {

    private App app;
    private UserModel currentUser;
    private TransactionViewDTO selectedTransaction;
    private Consumer<String> onTransactionUpdated;

    @FXML
    private Label titleLabel;
    @FXML
    private Label barberIdValueLabel;
    @FXML
    private Label barberNameValueLabel;
    @FXML
    private Label pricingCategoryIdValueLabel;
    @FXML
    private Label pricingCategoryCodeValueLabel;
    @FXML
    private Label pricingCategoryNameValueLabel;
    @FXML
    private Label loggedByUserIdValueLabel;
    @FXML
    private Label loggedByUsernameValueLabel;
    @FXML
    private Label businessDateValueLabel;
    @FXML
    private Label recordedAtValueLabel;
    @FXML
    private Label chargedAmountValueLabel;
    @FXML
    private Label barberCommissionPercentValueLabel;
    @FXML
    private Label barberEarningAmountValueLabel;
    @FXML
    private Label shopEarningAmountValueLabel;
    @FXML
    private Label statusValueLabel;
    @FXML
    private Label noteValueLabel;
    @FXML
    private Label voidReasonValueLabel;
    @FXML
    private Button voidButton;
    @FXML
    private Label statusLabel;

    public void load(
            App app,
            UserModel currentUser,
            TransactionViewDTO selectedTransaction,
            Consumer<String> onTransactionUpdated) {
        this.app = app;
        this.currentUser = currentUser != null ? currentUser : app.getCurrentUser();
        this.selectedTransaction = selectedTransaction;
        this.onTransactionUpdated = onTransactionUpdated;

        if (selectedTransaction == null) {
            titleLabel.setText("Transaction Details");
            voidButton.setDisable(true);
            showStatus("No transaction was provided.");
            return;
        }

        titleLabel.setText("Transaction #" + selectedTransaction.getTransactionId());
        barberIdValueLabel.setText(String.valueOf(selectedTransaction.getBarberId()));
        barberNameValueLabel.setText(valueOrPlaceholder(selectedTransaction.getBarberName()));
        pricingCategoryIdValueLabel.setText(String.valueOf(selectedTransaction.getPricingCategoryId()));
        pricingCategoryCodeValueLabel.setText(valueOrPlaceholder(selectedTransaction.getPricingCategoryCode()));
        pricingCategoryNameValueLabel.setText(valueOrPlaceholder(selectedTransaction.getPricingCategoryName()));
        loggedByUserIdValueLabel.setText(String.valueOf(selectedTransaction.getLoggedByUserId()));
        loggedByUsernameValueLabel.setText(valueOrPlaceholder(selectedTransaction.getLoggedByUsername()));
        businessDateValueLabel.setText(valueOrPlaceholder(selectedTransaction.getBusinessDate()));
        recordedAtValueLabel.setText(valueOrPlaceholder(selectedTransaction.getRecordedAt()));
        chargedAmountValueLabel.setText("PHP " + selectedTransaction.getChargedAmount());
        barberCommissionPercentValueLabel.setText(selectedTransaction.getBarberCommissionPercent() + "%");
        barberEarningAmountValueLabel.setText("PHP " + selectedTransaction.getBarberEarningAmount());
        shopEarningAmountValueLabel.setText("PHP " + selectedTransaction.getShopEarningAmount());
        statusValueLabel.setText(valueOrPlaceholder(selectedTransaction.getStatus()));
        noteValueLabel.setText(valueOrPlaceholder(selectedTransaction.getNote()));
        voidReasonValueLabel.setText(valueOrPlaceholder(selectedTransaction.getVoidReason()));
        voidButton.setDisable("VOID".equalsIgnoreCase(selectedTransaction.getStatus()));
    }

    @FXML
    private void handleVoid() {
        if (selectedTransaction == null) {
            showStatus("No transaction selected.");
            return;
        }

        if ("VOID".equalsIgnoreCase(selectedTransaction.getStatus())) {
            showStatus("This transaction is already voided.");
            return;
        }

        try {
            ModalLoader.load_void_confirmation_modal(app, currentUser, selectedTransaction, onTransactionUpdated);
        } catch (IOException e) {
            showStatus("Failed to open void confirmation modal.");
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
