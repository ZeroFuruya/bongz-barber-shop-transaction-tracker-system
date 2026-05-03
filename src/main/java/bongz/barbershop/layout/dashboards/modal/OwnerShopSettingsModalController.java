package bongz.barbershop.layout.dashboards.modal;

import java.util.function.Consumer;

import bongz.barbershop.App;
import bongz.barbershop.loader.ModalLoader;
import bongz.barbershop.model.ShopSettingsModel;
import bongz.barbershop.service.settings.ShopSettingsService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class OwnerShopSettingsModalController {

    private final ShopSettingsService shopSettingsService = new ShopSettingsService();

    private App app;
    private ShopSettingsModel settings;
    private Consumer<String> onSettingsSaved;

    @FXML
    private Label titleLabel;
    @FXML
    private Label updatedAtLabel;
    @FXML
    private TextField shopNameField;
    @FXML
    private TextField currencyCodeField;
    @FXML
    private Button saveButton;
    @FXML
    private Label statusLabel;

    public void load(App app, ShopSettingsModel settings, Consumer<String> onSettingsSaved) {
        this.app = app;
        this.settings = settings != null ? settings : shopSettingsService.getSettings();
        this.onSettingsSaved = onSettingsSaved;

        titleLabel.setText("Edit Shop Settings");

        if (this.settings == null) {
            updatedAtLabel.setText("Updated At: (missing)");
            return;
        }

        updatedAtLabel.setText("Updated At: " + valueOrPlaceholder(this.settings.getUpdatedAt()));
        shopNameField.setText(this.settings.getShopName());
        currencyCodeField.setText(this.settings.getCurrencyCode());
    }

    @FXML
    private void handleSave() {
        var result = shopSettingsService.updateShopSettings(
                shopNameField.getText(),
                currencyCodeField.getText(),
                this.settings.getOwnerNotes());

        if (!result.isSuccess()) {
            showStatus(result.getMessage());
            return;
        }

        ModalLoader.modal_close(app);
        if (onSettingsSaved != null) {
            onSettingsSaved.accept("Shop settings updated.");
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
