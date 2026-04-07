package bongz.barbershop.dto.report;

public class DailyShopTotalDTO {
    private String businessDate;
    private int haircutCount;
    private int grossSales;
    private int barberCommissionTotal;
    private int shopShareTotal;

    public DailyShopTotalDTO(String businessDate, int haircutCount, int grossSales, int barberCommissionTotal,
            int shopShareTotal) {
        this.businessDate = businessDate;
        this.haircutCount = haircutCount;
        this.grossSales = grossSales;
        this.barberCommissionTotal = barberCommissionTotal;
        this.shopShareTotal = shopShareTotal;
    }

    public String getBusinessDate() {
        return businessDate;
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
