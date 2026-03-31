package bongz.barbershop.model;

public class BarberModel {
    private int barberId;
    private String name;
    private int isActive;

    public BarberModel(int barberId, String name, int isActive) {
        this.barberId = barberId;
        this.name = name;
        this.isActive = isActive;
    }

    public int getBarberId() {
        return barberId;
    }

    public String getName() {
        return name;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setBarberId(int barberId) {
        this.barberId = barberId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "BarberModel [barberId=" + barberId + ", name=" + name + ", isActive=" + isActive + "]";
    }
}