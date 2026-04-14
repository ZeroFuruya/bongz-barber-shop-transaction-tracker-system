package bongz.barbershop.service.settings;

import bongz.barbershop.dto.common.ServiceResult;
import bongz.barbershop.model.ShopSettingsModel;
import bongz.barbershop.server.dao.ShopSettingsDAO;
import bongz.barbershop.service.common.ServiceValidation;

public class ShopSettingsService {

    private final ShopSettingsDAO shopSettingsDAO = new ShopSettingsDAO();

    public ShopSettingsModel getSettings() {
        return shopSettingsDAO.getSettings();
    }

    public ServiceResult<ShopSettingsModel> updateShopSettings(String shopName, String currencyCode) {
        String normalizedShopName = ServiceValidation.trimToNull(shopName);
        String normalizedCurrencyCode = ServiceValidation.trimToNull(currencyCode);

        if (normalizedShopName == null) {
            return ServiceResult.fail("Shop name is required");
        }

        if (normalizedCurrencyCode == null) {
            return ServiceResult.fail("Currency code is required");
        }

        ShopSettingsModel existingSettings = shopSettingsDAO.getSettings();
        if (existingSettings == null) {
            return ServiceResult.fail("Shop settings not found");
        }

        ShopSettingsModel updatedSettings = new ShopSettingsModel(
                existingSettings.getSettingsId(),
                normalizedShopName,
                normalizedCurrencyCode.toUpperCase(),
                existingSettings.getUpdatedAt());

        if (!shopSettingsDAO.updateSettings(updatedSettings)) {
            return ServiceResult.fail("Failed to update shop settings");
        }

        return ServiceResult.ok("Shop settings updated", shopSettingsDAO.getSettings());
    }
}
