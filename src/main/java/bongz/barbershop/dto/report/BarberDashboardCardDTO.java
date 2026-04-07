package bongz.barbershop.dto.report;

public class BarberDashboardCardDTO {
    private int barberId;
    private String barberName;
    private String imagePath;
    private int haircutCountToday;
    private int barberCommissionToday;
    private int shopShareToday;

    public BarberDashboardCardDTO(int barberId, String barberName, String imagePath, int haircutCountToday,
            int barberCommissionToday, int shopShareToday) {
        this.barberId = barberId;
        this.barberName = barberName;
        this.imagePath = imagePath;
        this.haircutCountToday = haircutCountToday;
        this.barberCommissionToday = barberCommissionToday;
        this.shopShareToday = shopShareToday;
    }

    public int getBarberId() {
        return barberId;
    }

    public String getBarberName() {
        return barberName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getHaircutCountToday() {
        return haircutCountToday;
    }

    public int getBarberCommissionToday() {
        return barberCommissionToday;
    }

    public int getShopShareToday() {
        return shopShareToday;
    }

    public void setBarberId(int barberId) {
        this.barberId = barberId;
    }

    public void setBarberName(String barberName) {
        this.barberName = barberName;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setHaircutCountToday(int haircutCountToday) {
        this.haircutCountToday = haircutCountToday;
    }

    public void setBarberCommissionToday(int barberCommissionToday) {
        this.barberCommissionToday = barberCommissionToday;
    }

    public void setShopShareToday(int shopShareToday) {
        this.shopShareToday = shopShareToday;
    }
}
