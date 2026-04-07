package bongz.barbershop.dto.transaction;

public class CreateTransactionRequest {
    private int barberId;
    private int pricingCategoryId;
    private int loggedByUserId;
    private String businessDate;
    private String note;

    public CreateTransactionRequest(int barberId, int pricingCategoryId, int loggedByUserId, String businessDate,
            String note) {
        this.barberId = barberId;
        this.pricingCategoryId = pricingCategoryId;
        this.loggedByUserId = loggedByUserId;
        this.businessDate = businessDate;
        this.note = note;
    }

    public int getBarberId() {
        return barberId;
    }

    public int getPricingCategoryId() {
        return pricingCategoryId;
    }

    public int getLoggedByUserId() {
        return loggedByUserId;
    }

    public String getBusinessDate() {
        return businessDate;
    }

    public String getNote() {
        return note;
    }

    public void setBarberId(int barberId) {
        this.barberId = barberId;
    }

    public void setPricingCategoryId(int pricingCategoryId) {
        this.pricingCategoryId = pricingCategoryId;
    }

    public void setLoggedByUserId(int loggedByUserId) {
        this.loggedByUserId = loggedByUserId;
    }

    public void setBusinessDate(String businessDate) {
        this.businessDate = businessDate;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
