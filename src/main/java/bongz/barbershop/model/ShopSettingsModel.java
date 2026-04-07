package bongz.barbershop.model;

public class ShopSettingsModel {
    private int id;
    private String shopName;
    private String currencyCode;
    private String updatedAt;

    public ShopSettingsModel(int id, String shopName, String currencyCode, String updatedAt) {
        this.id = id;
        this.shopName = shopName;
        this.currencyCode = currencyCode;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public String getShopName() {
        return shopName;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "ShopSettingsModel [id=" + id + ", shopName=" + shopName + ", currencyCode=" + currencyCode
                + ", updatedAt=" + updatedAt + "]";
    }
}
