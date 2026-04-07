package bongz.barbershop.dto.transaction;

public class TransactionViewDTO {
    private int transactionId;
    private int barberId;
    private String barberName;
    private int pricingCategoryId;
    private String pricingCategoryCode;
    private String pricingCategoryName;
    private int loggedByUserId;
    private String loggedByUsername;
    private String businessDate;
    private String recordedAt;
    private int chargedAmount;
    private int barberCommissionPercent;
    private int barberEarningAmount;
    private int shopEarningAmount;
    private String status;
    private String voidReason;
    private String note;

    public TransactionViewDTO(int transactionId, int barberId, String barberName, int pricingCategoryId,
            String pricingCategoryCode, String pricingCategoryName, int loggedByUserId, String loggedByUsername,
            String businessDate, String recordedAt, int chargedAmount, int barberCommissionPercent,
            int barberEarningAmount, int shopEarningAmount, String status, String voidReason, String note) {
        this.transactionId = transactionId;
        this.barberId = barberId;
        this.barberName = barberName;
        this.pricingCategoryId = pricingCategoryId;
        this.pricingCategoryCode = pricingCategoryCode;
        this.pricingCategoryName = pricingCategoryName;
        this.loggedByUserId = loggedByUserId;
        this.loggedByUsername = loggedByUsername;
        this.businessDate = businessDate;
        this.recordedAt = recordedAt;
        this.chargedAmount = chargedAmount;
        this.barberCommissionPercent = barberCommissionPercent;
        this.barberEarningAmount = barberEarningAmount;
        this.shopEarningAmount = shopEarningAmount;
        this.status = status;
        this.voidReason = voidReason;
        this.note = note;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public int getBarberId() {
        return barberId;
    }

    public String getBarberName() {
        return barberName;
    }

    public int getPricingCategoryId() {
        return pricingCategoryId;
    }

    public String getPricingCategoryCode() {
        return pricingCategoryCode;
    }

    public String getPricingCategoryName() {
        return pricingCategoryName;
    }

    public int getLoggedByUserId() {
        return loggedByUserId;
    }

    public String getLoggedByUsername() {
        return loggedByUsername;
    }

    public String getBusinessDate() {
        return businessDate;
    }

    public String getRecordedAt() {
        return recordedAt;
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

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public void setBarberId(int barberId) {
        this.barberId = barberId;
    }

    public void setBarberName(String barberName) {
        this.barberName = barberName;
    }

    public void setPricingCategoryId(int pricingCategoryId) {
        this.pricingCategoryId = pricingCategoryId;
    }

    public void setPricingCategoryCode(String pricingCategoryCode) {
        this.pricingCategoryCode = pricingCategoryCode;
    }

    public void setPricingCategoryName(String pricingCategoryName) {
        this.pricingCategoryName = pricingCategoryName;
    }

    public void setLoggedByUserId(int loggedByUserId) {
        this.loggedByUserId = loggedByUserId;
    }

    public void setLoggedByUsername(String loggedByUsername) {
        this.loggedByUsername = loggedByUsername;
    }

    public void setBusinessDate(String businessDate) {
        this.businessDate = businessDate;
    }

    public void setRecordedAt(String recordedAt) {
        this.recordedAt = recordedAt;
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
}
