package bongz.barbershop.dto.report;

public class PricingCategorySummaryDTO {
    private int pricingCategoryId;
    private String code;
    private String name;
    private int haircutCount;
    private int grossSales;
    private int barberCommissionTotal;
    private int shopShareTotal;

    public PricingCategorySummaryDTO(int pricingCategoryId, String code, String name, int haircutCount, int grossSales,
            int barberCommissionTotal, int shopShareTotal) {
        this.pricingCategoryId = pricingCategoryId;
        this.code = code;
        this.name = name;
        this.haircutCount = haircutCount;
        this.grossSales = grossSales;
        this.barberCommissionTotal = barberCommissionTotal;
        this.shopShareTotal = shopShareTotal;
    }

    public int getPricingCategoryId() {
        return pricingCategoryId;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
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

    public void setPricingCategoryId(int pricingCategoryId) {
        this.pricingCategoryId = pricingCategoryId;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
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
