package bongz.barbershop.model;

public class TransactionModel {
    private int transactionId;
    private int barberId;
    private int pricingCategoryId;
    private int loggedByUserId;
    private String businessDate;
    private String recordedAt;
    private String pricingCategoryCodeSnapshot;
    private String pricingCategoryNameSnapshot;
    private int chargedAmountPesos;
    private int barberCommissionPercent;
    private int barberEarningAmountPesos;
    private int shopEarningAmountPesos;
    private String status;
    private String voidReason;
    private String note;

    public TransactionModel(int transactionId, int barberId, int pricingCategoryId, int loggedByUserId,
            String businessDate, String recordedAt, String pricingCategoryCodeSnapshot,
            String pricingCategoryNameSnapshot, int chargedAmountPesos, int barberCommissionPercent,
            int barberEarningAmountPesos, int shopEarningAmountPesos, String status, String voidReason,
            String note) {
        this.transactionId = transactionId;
        this.barberId = barberId;
        this.pricingCategoryId = pricingCategoryId;
        this.loggedByUserId = loggedByUserId;
        this.businessDate = businessDate;
        this.recordedAt = recordedAt;
        this.pricingCategoryCodeSnapshot = pricingCategoryCodeSnapshot;
        this.pricingCategoryNameSnapshot = pricingCategoryNameSnapshot;
        this.chargedAmountPesos = chargedAmountPesos;
        this.barberCommissionPercent = barberCommissionPercent;
        this.barberEarningAmountPesos = barberEarningAmountPesos;
        this.shopEarningAmountPesos = shopEarningAmountPesos;
        this.status = status;
        this.voidReason = voidReason;
        this.note = note;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getBarberId() {
        return barberId;
    }

    public void setBarberId(int barberId) {
        this.barberId = barberId;
    }

    public int getPricingCategoryId() {
        return pricingCategoryId;
    }

    public void setPricingCategoryId(int pricingCategoryId) {
        this.pricingCategoryId = pricingCategoryId;
    }

    public int getLoggedByUserId() {
        return loggedByUserId;
    }

    public void setLoggedByUserId(int loggedByUserId) {
        this.loggedByUserId = loggedByUserId;
    }

    public String getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(String businessDate) {
        this.businessDate = businessDate;
    }

    public String getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(String recordedAt) {
        this.recordedAt = recordedAt;
    }

    public String getPricingCategoryCodeSnapshot() {
        return pricingCategoryCodeSnapshot;
    }

    public void setPricingCategoryCodeSnapshot(String pricingCategoryCodeSnapshot) {
        this.pricingCategoryCodeSnapshot = pricingCategoryCodeSnapshot;
    }

    public String getPricingCategoryNameSnapshot() {
        return pricingCategoryNameSnapshot;
    }

    public void setPricingCategoryNameSnapshot(String pricingCategoryNameSnapshot) {
        this.pricingCategoryNameSnapshot = pricingCategoryNameSnapshot;
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

    public int getBarberEarningAmountPesos() {
        return barberEarningAmountPesos;
    }

    public void setBarberEarningAmountPesos(int barberEarningAmountPesos) {
        this.barberEarningAmountPesos = barberEarningAmountPesos;
    }

    public int getShopEarningAmountPesos() {
        return shopEarningAmountPesos;
    }

    public void setShopEarningAmountPesos(int shopEarningAmountPesos) {
        this.shopEarningAmountPesos = shopEarningAmountPesos;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVoidReason() {
        return voidReason;
    }

    public void setVoidReason(String voidReason) {
        this.voidReason = voidReason;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
