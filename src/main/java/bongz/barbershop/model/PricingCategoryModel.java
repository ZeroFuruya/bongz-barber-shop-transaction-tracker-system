package bongz.barbershop.model;

public class PricingCategoryModel {
    private int categoryId;
    private String name;

    private int isActive;
    private int sortOrder;
    private String createdAt;

    public PricingCategoryModel(int categoryId, String name, int sortOrder, int isActive, String createdAt) {
        this.categoryId = categoryId;
        this.name = name;
        this.sortOrder = sortOrder;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
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

}
