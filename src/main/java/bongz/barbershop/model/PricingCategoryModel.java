package bongz.barbershop.model;

public class PricingCategoryModel {
    private int id;
    private String code;
    private String name;
    private String description;
    private int chargedAmount;
    private int barberCommissionPercent;
    private int isDefault;
    private int isActive;
    private int sortOrder;
    private String createdAt;

    public PricingCategoryModel(int id, String code, String name, String description, int chargedAmount,
            int barberCommissionPercent, int isDefault, int isActive, int sortOrder, String createdAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.chargedAmount = chargedAmount;
        this.barberCommissionPercent = barberCommissionPercent;
        this.isDefault = isDefault;
        this.sortOrder = sortOrder;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getChargedAmount() {
        return chargedAmount;
    }

    public int getBarberCommissionPercent() {
        return barberCommissionPercent;
    }

    public int getIsDefault() {
        return isDefault;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public int getIsActive() {
        return isActive;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setChargedAmount(int chargedAmount) {
        this.chargedAmount = chargedAmount;
    }

    public void setBarberCommissionPercent(int barberCommissionPercent) {
        this.barberCommissionPercent = barberCommissionPercent;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "PricingCategoryModel [id=" + id + ", code=" + code + ", name=" + name + ", chargedAmount="
                + chargedAmount + ", barberCommissionPercent=" + barberCommissionPercent + ", isDefault="
                + isDefault + ", isActive=" + isActive + ", sortOrder=" + sortOrder + ", createdAt=" + createdAt
                + "]";
    }
}
