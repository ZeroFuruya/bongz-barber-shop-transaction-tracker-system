package bongz.barbershop.dto.report;

import java.util.List;

import bongz.barbershop.dto.transaction.TransactionViewDTO;

public class OwnerDashboardDTO {
    private DailyShopTotalDTO shopTotals;
    private List<DailyBarberTotalDTO> barberTotals;
    private List<TransactionViewDTO> recentTransactions;

    public OwnerDashboardDTO(DailyShopTotalDTO shopTotals, List<DailyBarberTotalDTO> barberTotals,
            List<TransactionViewDTO> recentTransactions) {
        this.shopTotals = shopTotals;
        this.barberTotals = barberTotals;
        this.recentTransactions = recentTransactions;
    }

    public DailyShopTotalDTO getShopTotals() {
        return shopTotals;
    }

    public List<DailyBarberTotalDTO> getBarberTotals() {
        return barberTotals;
    }

    public List<TransactionViewDTO> getRecentTransactions() {
        return recentTransactions;
    }

    public void setShopTotals(DailyShopTotalDTO shopTotals) {
        this.shopTotals = shopTotals;
    }

    public void setBarberTotals(List<DailyBarberTotalDTO> barberTotals) {
        this.barberTotals = barberTotals;
    }

    public void setRecentTransactions(List<TransactionViewDTO> recentTransactions) {
        this.recentTransactions = recentTransactions;
    }
}
