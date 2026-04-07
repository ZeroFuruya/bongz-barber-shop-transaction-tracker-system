package bongz.barbershop.dto.report;

public class DailyBarberTotalDTO {
    private String businessDate;
    private int barberId;
    private String barberName;
    private int haircutCount;
    private int grossSales;
    private int barberCommissionTotal;
    private int shopShareTotal;

    public DailyBarberTotalDTO(String businessDate, int barberId, String barberName, int haircutCount, int grossSales,
            int barberCommissionTotal, int shopShareTotal) {
        this.businessDate = businessDate;
        this.barberId = barberId;
        this.barberName = barberName;
        this.haircutCount = haircutCount;
        this.grossSales = grossSales;
        this.barberCommissionTotal = barberCommissionTotal;
        this.shopShareTotal = shopShareTotal;
    }

    public String getBusinessDate() {
        return businessDate;
    }

    public int getBarberId() {
        return barberId;
    }

    public String getBarberName() {
        return barberName;
    }

    public int getHaircutCount() {
        return haircutCount;
    }

    public int getGrossSales() {
        return grossSales;
    }

    public int getBarberCommissionTotal() {
        return barberCommissionTotal;
    }

    public int getShopShareTotal() {
        return shopShareTotal;
    }

    public void setBusinessDate(String businessDate) {
        this.businessDate = businessDate;
    }

    public void setBarberId(int barberId) {
        this.barberId = barberId;
    }

    public void setBarberName(String barberName) {
        this.barberName = barberName;
    }

    public void setHaircutCount(int haircutCount) {
        this.haircutCount = haircutCount;
    }

    public void setGrossSales(int grossSales) {
        this.grossSales = grossSales;
    }

    public void setBarberCommissionTotal(int barberCommissionTotal) {
        this.barberCommissionTotal = barberCommissionTotal;
    }

    public void setShopShareTotal(int shopShareTotal) {
        this.shopShareTotal = shopShareTotal;
    }
}
