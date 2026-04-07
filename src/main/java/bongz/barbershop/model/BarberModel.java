package bongz.barbershop.model;

public class BarberModel {
    private int barberId;
    private String name;
    private String imagePath;
    private int displayOrder;
    private int isActive;
    private String createdAt;

    public BarberModel(int barberId, String name, String imagePath, int displayOrder, int isActive, String createdAt) {
        this.barberId = barberId;
        this.name = name;
        this.imagePath = imagePath;
        this.displayOrder = displayOrder;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    public int getBarberId() {
        return barberId;
    }

    public String getName() {
        return name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public int getIsActive() {
        return isActive;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setBarberId(int barberId) {
        this.barberId = barberId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "BarberModel [barberId=" + barberId + ", name=" + name + ", imagePath=" + imagePath
                + ", displayOrder=" + displayOrder + ", isActive=" + isActive + ", createdAt=" + createdAt + "]";
    }
}
