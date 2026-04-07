package bongz.barbershop.model;

public class ShopSettingsModel {
    private int settingsId;
    private String shopName;
    private String currencyCode;
    private String updatedAt;

    public ShopSettingsModel(int settingsId, String shopName, String currencyCode, String updatedAt) {
        this.settingsId = settingsId;
        this.shopName = shopName;
        this.currencyCode = currencyCode;
        this.updatedAt = updatedAt;
    }

    public int getSettingsId() {
        return settingsId;
    }

    public void setSettingsId(int settingsId) {
        this.settingsId = settingsId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
