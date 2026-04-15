package bongz.barbershop.layout.dashboards.modal;

import java.util.List;
import java.util.function.Consumer;

import bongz.barbershop.App;
import bongz.barbershop.loader.ModalLoader;
import bongz.barbershop.model.BarberModel;
import bongz.barbershop.model.PricingCategoryModel;
import bongz.barbershop.model.UserModel;
import bongz.barbershop.service.common.MoneyCalculator;
import bongz.barbershop.service.pricing.PricingCategoryService;
import bongz.barbershop.service.transaction.TransactionService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.util.StringConverter;

public class NewTransactionModalController {

    private final PricingCategoryService pricingCategoryService = new PricingCategoryService();
    private final TransactionService transactionService = new TransactionService();

    private App app;
    private UserModel currentUser;
    private BarberModel selectedBarber;
    private String businessDate;
    private Consumer<String> onTransactionRecorded;

    @FXML
    private Label titleLabel;
    @FXML
    private Label barberIdValueLabel;
    @FXML
    private Label barberNameValueLabel;
    @FXML
    private Label loggedByUserIdValueLabel;
    @FXML
    private Label loggedByUsernameValueLabel;
    @FXML
    private Label businessDateValueLabel;
    @FXML
    private ComboBox<PricingCategoryModel> pricingCategoryComboBox;
    @FXML
    private Label pricingCategoryIdValueLabel;
    @FXML
    private Label chargedAmountValueLabel;
    @FXML
    private Label barberCommissionPercentValueLabel;
    @FXML
    private Label barberEarningAmountValueLabel;
    @FXML
    private Label shopEarningAmountValueLabel;
    @FXML
    private TextArea noteTextArea;
    @FXML
    private Button recordButton;
    @FXML
    private Label statusLabel;

    @FXML
    private void initialize() {
        pricingCategoryComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(PricingCategoryModel category) {
                if (category == null) {
                    return "";
                }

                return category.getName() + " (PHP " + category.getChargedAmountPesos() + ")";
            }

            @Override
            public PricingCategoryModel fromString(String string) {
                return null;
            }
        });

        pricingCategoryComboBox.valueProperty()
                .addListener((observable, oldValue, newValue) -> refreshPricingCategorySummary(newValue));
    }

    public void load(
            App app,
            UserModel currentUser,
            BarberModel selectedBarber,
            String businessDate,
            Consumer<String> onTransactionRecorded) {
        this.app = app;
        this.currentUser = currentUser != null ? currentUser : app.getCurrentUser();
        this.selectedBarber = selectedBarber;
        this.businessDate = businessDate;
        this.onTransactionRecorded = onTransactionRecorded;

        titleLabel.setText(selectedBarber == null
                ? "New Transaction"
                : "New Transaction for " + selectedBarber.getName());

        barberIdValueLabel.setText(selectedBarber == null ? "-" : String.valueOf(selectedBarber.getBarberId()));
        barberNameValueLabel.setText(selectedBarber == null ? "-" : selectedBarber.getName());
        loggedByUserIdValueLabel.setText(this.currentUser == null ? "-" : String.valueOf(this.currentUser.getId()));
        loggedByUsernameValueLabel.setText(this.currentUser == null ? "-" : this.currentUser.getUsername());
        businessDateValueLabel.setText(this.businessDate == null ? "-" : this.businessDate);

        loadPricingCategories();

        if (this.currentUser == null) {
            recordButton.setDisable(true);
            showStatus("No logged-in user found. Close this modal and log in again.");
            return;
        }

        if (selectedBarber == null) {
            recordButton.setDisable(true);
            showStatus("Select a barber first before opening this modal.");
        }
    }

    @FXML
    private void handleRecord() {
        if (selectedBarber == null) {
            showStatus("Select a barber first.");
            return;
        }

        if (currentUser == null) {
            showStatus("No logged-in user found.");
            return;
        }

        PricingCategoryModel selectedCategory = pricingCategoryComboBox.getValue();
        if (selectedCategory == null) {
            showStatus("Select a pricing category first.");
            return;
        }

        var result = transactionService.recordTransaction(
                selectedBarber.getBarberId(),
                selectedCategory.getPricingCategoryId(),
                currentUser.getId(),
                businessDate,
                noteTextArea.getText());

        if (!result.isSuccess()) {
            showStatus(result.getMessage());
            return;
        }

        ModalLoader.modal_close(app);
        if (onTransactionRecorded != null) {
            onTransactionRecorded.accept(
                    "Recorded " + selectedCategory.getName() + " haircut for " + selectedBarber.getName() + ".");
        }
    }

    @FXML
    private void handleCancel() {
        ModalLoader.modal_close(app);
    }

    private void loadPricingCategories() {
        List<PricingCategoryModel> activeCategories = pricingCategoryService.getAllActiveCategories();
        pricingCategoryComboBox.setItems(FXCollections.observableArrayList(activeCategories));

        PricingCategoryModel defaultCategory = pricingCategoryService.getDefaultCategory();
        if (defaultCategory != null) {
            pricingCategoryComboBox.getSelectionModel().select(
                    activeCategories.stream()
                            .filter(category -> category.getPricingCategoryId() == defaultCategory.getPricingCategoryId())
                            .findFirst()
                            .orElse(defaultCategory));
        } else if (!activeCategories.isEmpty()) {
            pricingCategoryComboBox.getSelectionModel().selectFirst();
        }

        refreshPricingCategorySummary(pricingCategoryComboBox.getValue());

        if (activeCategories.isEmpty()) {
            recordButton.setDisable(true);
            showStatus("No active pricing categories are available.");
        }
    }

    private void refreshPricingCategorySummary(PricingCategoryModel category) {
        if (category == null) {
            pricingCategoryIdValueLabel.setText("-");
            chargedAmountValueLabel.setText("PHP 0");
            barberCommissionPercentValueLabel.setText("0%");
            barberEarningAmountValueLabel.setText("PHP 0");
            shopEarningAmountValueLabel.setText("PHP 0");
            return;
        }

        int chargedAmount = category.getChargedAmountPesos();
        int barberEarningAmount = MoneyCalculator.calculateBarberEarningAmount(
                chargedAmount,
                category.getBarberCommissionPercent());
        int shopEarningAmount = MoneyCalculator.calculateShopEarningAmount(chargedAmount, barberEarningAmount);

        pricingCategoryIdValueLabel.setText(String.valueOf(category.getPricingCategoryId()));
        chargedAmountValueLabel.setText("PHP " + chargedAmount);
        barberCommissionPercentValueLabel.setText(category.getBarberCommissionPercent() + "%");
        barberEarningAmountValueLabel.setText("PHP " + barberEarningAmount);
        shopEarningAmountValueLabel.setText("PHP " + shopEarningAmount);
    }

    private void showStatus(String message) {
        statusLabel.setText(message == null ? "" : message);
    }
}
