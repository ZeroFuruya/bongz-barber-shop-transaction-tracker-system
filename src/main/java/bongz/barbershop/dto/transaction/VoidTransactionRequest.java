package bongz.barbershop.dto.transaction;

public class VoidTransactionRequest {
    private int transactionId;
    private String voidReason;
    private int actedByUserId;

    public VoidTransactionRequest(int transactionId, String voidReason, int actedByUserId) {
        this.transactionId = transactionId;
        this.voidReason = voidReason;
        this.actedByUserId = actedByUserId;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public String getVoidReason() {
        return voidReason;
    }

    public int getActedByUserId() {
        return actedByUserId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public void setVoidReason(String voidReason) {
        this.voidReason = voidReason;
    }

    public void setActedByUserId(int actedByUserId) {
        this.actedByUserId = actedByUserId;
    }
}
