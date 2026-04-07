package bongz.barbershop.model;

public class PricingCategoryModel {
    private int pricingCategoryId;
    private String code;
    private String name;
    private String description;
    private int chargedAmountPesos;
    private int barberCommissionPercent;
    private int isDefault;
    private int isActive;
    private int sortOrder;
    private String createdAt;

    public PricingCategoryModel(int pricingCategoryId, String code, String name, String description,
            int chargedAmountPesos, int barberCommissionPercent, int isDefault, int isActive, int sortOrder,
            String createdAt) {
        this.pricingCategoryId = pricingCategoryId;
        this.code = code;
        this.name = name;
        this.description = description;
        this.chargedAmountPesos = chargedAmountPesos;
        this.barberCommissionPercent = barberCommissionPercent;
        this.isDefault = isDefault;
        this.isActive = isActive;
        this.sortOrder = sortOrder;
        this.createdAt = createdAt;
    }

    public int getPricingCategoryId() {
        return pricingCategoryId;
    }

    public void setPricingCategoryId(int pricingCategoryId) {
        this.pricingCategoryId = pricingCategoryId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getChargedAmountPesos() {
        return chargedAmountPesos;
    }

    public void setChargedAmountPesos(int chargedAmountPesos) {
        this.chargedAmountPesos = chargedAmountPesos;
    }

    public int getBarberCommissionPercent() {
        return barberCommissionPercent;
    }

    public void setBarberCommissionPercent(int barberCommissionPercent) {
        this.barberCommissionPercent = barberCommissionPercent;
    }

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
