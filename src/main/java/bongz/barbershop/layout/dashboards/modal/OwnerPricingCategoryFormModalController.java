package bongz.barbershop.layout.dashboards.modal;

import java.util.function.Consumer;

import bongz.barbershop.App;
import bongz.barbershop.loader.ModalLoader;
import bongz.barbershop.model.PricingCategoryModel;
import bongz.barbershop.service.pricing.PricingCategoryService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class OwnerPricingCategoryFormModalController {

    private final PricingCategoryService pricingCategoryService = new PricingCategoryService();

    private App app;
    private PricingCategoryModel selectedCategory;
    private Consumer<String> onCategorySaved;

    @FXML
    private Label titleLabel;
    @FXML
    private Label pricingIdLabel;
    @FXML
    private Label defaultStatusLabel;
    @FXML
    private Label createdAtLabel;
    @FXML
    private TextField codeField;
    @FXML
    private TextField nameField;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private TextField chargedAmountField;
    @FXML
    private TextField commissionPercentField;
    @FXML
    private TextField sortOrderField;
    @FXML
    private CheckBox activeCheckBox;
    @FXML
    private Button saveButton;
    @FXML
    private Label statusLabel;

    public void load(App app, PricingCategoryModel selectedCategory, Consumer<String> onCategorySaved) {
        this.app = app;
        this.selectedCategory = selectedCategory;
        this.onCategorySaved = onCategorySaved;

        if (selectedCategory == null) {
            titleLabel.setText("Add Pricing Category");
            pricingIdLabel.setText("Pricing ID: New");
            defaultStatusLabel.setText("Default: No");
            createdAtLabel.setText("Created At: (new)");
            activeCheckBox.setSelected(true);
            return;
        }

        titleLabel.setText("Edit Pricing Category");
        pricingIdLabel.setText("Pricing ID: " + selectedCategory.getPricingCategoryId());
        defaultStatusLabel.setText("Default: " + (selectedCategory.getIsDefault() == 1 ? "Yes" : "No"));
        createdAtLabel.setText("Created At: " + valueOrPlaceholder(selectedCategory.getCreatedAt()));
        codeField.setText(selectedCategory.getCode());
        nameField.setText(selectedCategory.getName());
        descriptionTextArea.setText(selectedCategory.getDescription());
        chargedAmountField.setText(String.valueOf(selectedCategory.getChargedAmountPesos()));
        commissionPercentField.setText(String.valueOf(selectedCategory.getBarberCommissionPercent()));
        sortOrderField.setText(String.valueOf(selectedCategory.getSortOrder()));
        activeCheckBox.setSelected(selectedCategory.getIsActive() == 1);
    }

    @FXML
    private void handleSave() {
        int chargedAmount = parseInteger(chargedAmountField.getText());
        int commissionPercent = parseInteger(commissionPercentField.getText());
        int sortOrder = parseInteger(sortOrderField.getText());
        int isActive = activeCheckBox.isSelected() ? 1 : 0;

        if (selectedCategory == null) {
            var result = pricingCategoryService.createCategory(
                    codeField.getText(),
                    nameField.getText(),
                    descriptionTextArea.getText(),
                    chargedAmount,
                    commissionPercent,
                    sortOrder);

            if (!result.isSuccess()) {
                showStatus(result.getMessage());
                return;
            }

            PricingCategoryModel savedCategory = result.getData();
            if (savedCategory != null && isActive == 0) {
                var deactivateResult = pricingCategoryService.setCategoryActiveStatus(
                        savedCategory.getPricingCategoryId(),
                        0);
                if (!deactivateResult.isSuccess()) {
                    showStatus(deactivateResult.getMessage());
                    return;
                }
            }

            closeWithMessage("Pricing category created.");
            return;
        }

        var result = pricingCategoryService.updateCategory(
                selectedCategory.getPricingCategoryId(),
                codeField.getText(),
                nameField.getText(),
                descriptionTextArea.getText(),
                chargedAmount,
                commissionPercent,
                isActive,
                sortOrder);

        if (!result.isSuccess()) {
            showStatus(result.getMessage());
            return;
        }

        closeWithMessage("Pricing category updated.");
    }

    @FXML
    private void handleCancel() {
        ModalLoader.modal_close(app);
    }

    private int parseInteger(String value) {
        try {
            return Integer.parseInt(value == null || value.isBlank() ? "0" : value.trim());
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    private String valueOrPlaceholder(String value) {
        return value == null || value.isBlank() ? "(none)" : value;
    }

    private void closeWithMessage(String message) {
        ModalLoader.modal_close(app);
        if (onCategorySaved != null) {
            onCategorySaved.accept(message);
        }
    }

    private void showStatus(String message) {
        statusLabel.setText(message == null ? "" : message);
    }
}
