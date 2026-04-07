package bongz.barbershop.model;

public class TransactionModel {
    private int id;
    private int barberId;
    private int pricingCategoryId;
    private int loggedByUserId;
    private String businessDate;
    private String recordedAt;
    private String pricingCategoryCodeSnapshot;
    private String pricingCategoryNameSnapshot;
    private int chargedAmount;
    private int barberCommissionPercent;
    private int barberEarningAmount;
    private int shopEarningAmount;
    private String status;
    private String voidReason;
    private String note;

    public TransactionModel(int id, int barberId, int pricingCategoryId, int loggedByUserId, String businessDate,
            String recordedAt, String pricingCategoryCodeSnapshot, String pricingCategoryNameSnapshot,
            int chargedAmount, int barberCommissionPercent, int barberEarningAmount, int shopEarningAmount,
            String status, String voidReason, String note) {
        this.id = id;
        this.barberId = barberId;
        this.pricingCategoryId = pricingCategoryId;
        this.loggedByUserId = loggedByUserId;
        this.businessDate = businessDate;
        this.recordedAt = recordedAt;
        this.pricingCategoryCodeSnapshot = pricingCategoryCodeSnapshot;
        this.pricingCategoryNameSnapshot = pricingCategoryNameSnapshot;
        this.chargedAmount = chargedAmount;
        this.barberCommissionPercent = barberCommissionPercent;
        this.barberEarningAmount = barberEarningAmount;
        this.shopEarningAmount = shopEarningAmount;
        this.status = status;
        this.voidReason = voidReason;
        this.note = note;
    }

    public int getId() {
        return id;
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

    public String getRecordedAt() {
        return recordedAt;
    }

    public String getPricingCategoryCodeSnapshot() {
        return pricingCategoryCodeSnapshot;
    }

    public String getPricingCategoryNameSnapshot() {
        return pricingCategoryNameSnapshot;
    }

    public int getChargedAmount() {
        return chargedAmount;
    }

    public int getBarberCommissionPercent() {
        return barberCommissionPercent;
    }

    public int getBarberEarningAmount() {
        return barberEarningAmount;
    }

    public int getShopEarningAmount() {
        return shopEarningAmount;
    }

    public String getStatus() {
        return status;
    }

    public String getVoidReason() {
        return voidReason;
    }

    public String getNote() {
        return note;
    }

    public void setId(int id) {
        this.id = id;
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

    public void setRecordedAt(String recordedAt) {
        this.recordedAt = recordedAt;
    }

    public void setPricingCategoryCodeSnapshot(String pricingCategoryCodeSnapshot) {
        this.pricingCategoryCodeSnapshot = pricingCategoryCodeSnapshot;
    }

    public void setPricingCategoryNameSnapshot(String pricingCategoryNameSnapshot) {
        this.pricingCategoryNameSnapshot = pricingCategoryNameSnapshot;
    }

    public void setChargedAmount(int chargedAmount) {
        this.chargedAmount = chargedAmount;
    }

    public void setBarberCommissionPercent(int barberCommissionPercent) {
        this.barberCommissionPercent = barberCommissionPercent;
    }

    public void setBarberEarningAmount(int barberEarningAmount) {
        this.barberEarningAmount = barberEarningAmount;
    }

    public void setShopEarningAmount(int shopEarningAmount) {
        this.shopEarningAmount = shopEarningAmount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setVoidReason(String voidReason) {
        this.voidReason = voidReason;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "TransactionModel [id=" + id + ", barberId=" + barberId + ", pricingCategoryId=" + pricingCategoryId
                + ", loggedByUserId=" + loggedByUserId + ", businessDate=" + businessDate + ", recordedAt="
                + recordedAt + ", chargedAmount=" + chargedAmount + ", barberCommissionPercent="
                + barberCommissionPercent + ", barberEarningAmount=" + barberEarningAmount + ", shopEarningAmount="
                + shopEarningAmount + ", status=" + status + ", voidReason=" + voidReason + ", note=" + note + "]";
    }
}
