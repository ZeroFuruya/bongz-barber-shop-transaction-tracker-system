package bongz.barbershop.service.reporting;

import bongz.barbershop.dto.report.BarberDashboardCardDTO;
import bongz.barbershop.dto.report.DailyBarberTotalDTO;
import bongz.barbershop.dto.report.DailyShopTotalDTO;
import bongz.barbershop.dto.report.OwnerDashboardDTO;
import bongz.barbershop.dto.report.PricingCategorySummaryDTO;
import bongz.barbershop.dto.transaction.TransactionViewDTO;
import bongz.barbershop.server.dao.ReportDAO;
import bongz.barbershop.service.common.ServiceValidation;

import java.util.List;

public class ReportService {

    private final ReportDAO reportDAO = new ReportDAO();

    public List<BarberDashboardCardDTO> getBarberDashboardCards(String businessDate) {
        String normalizedBusinessDate = ServiceValidation.normalizeBusinessDate(businessDate);
        if (normalizedBusinessDate == null) {
            return List.of();
        }

        return reportDAO.getBarberDashboardCards(normalizedBusinessDate);
    }

    public List<DailyBarberTotalDTO> getDailyBarberTotals(String businessDate) {
        String normalizedBusinessDate = ServiceValidation.normalizeBusinessDate(businessDate);
        if (normalizedBusinessDate == null) {
            return List.of();
        }

        return reportDAO.getDailyBarberTotals(normalizedBusinessDate);
    }

    public DailyShopTotalDTO getDailyShopTotal(String businessDate) {
        String normalizedBusinessDate = ServiceValidation.normalizeBusinessDate(businessDate);
        if (normalizedBusinessDate == null) {
            return new DailyShopTotalDTO(ServiceValidation.trimToNull(businessDate), 0, 0, 0, 0);
        }

        return reportDAO.getDailyShopTotal(normalizedBusinessDate);
    }

    public List<TransactionViewDTO> getTransactionViewsByBusinessDate(String businessDate) {
        String normalizedBusinessDate = ServiceValidation.normalizeBusinessDate(businessDate);
        if (normalizedBusinessDate == null) {
            return List.of();
        }

        return reportDAO.getTransactionViewsByBusinessDate(normalizedBusinessDate);
    }

    public List<TransactionViewDTO> getTransactionViewsByBarberAndBusinessDate(int barberId, String businessDate) {
        if (!ServiceValidation.isPositiveId(barberId)) {
            return List.of();
        }

        String normalizedBusinessDate = ServiceValidation.normalizeBusinessDate(businessDate);
        if (normalizedBusinessDate == null) {
            return List.of();
        }

        return reportDAO.getTransactionViewsByBarberAndBusinessDate(barberId, normalizedBusinessDate);
    }

    public List<DailyBarberTotalDTO> getDateRangeBarberTotals(String fromDate, String toDate) {
        String normalizedFromDate = ServiceValidation.normalizeBusinessDate(fromDate);
        String normalizedToDate = ServiceValidation.normalizeBusinessDate(toDate);

        if (!isValidDateRange(normalizedFromDate, normalizedToDate)) {
            return List.of();
        }

        return reportDAO.getDateRangeBarberTotals(normalizedFromDate, normalizedToDate);
    }

    public List<DailyShopTotalDTO> getDateRangeShopTotals(String fromDate, String toDate) {
        String normalizedFromDate = ServiceValidation.normalizeBusinessDate(fromDate);
        String normalizedToDate = ServiceValidation.normalizeBusinessDate(toDate);

        if (!isValidDateRange(normalizedFromDate, normalizedToDate)) {
            return List.of();
        }

        return reportDAO.getDateRangeShopTotals(normalizedFromDate, normalizedToDate);
    }

    public List<PricingCategorySummaryDTO> getPricingCategorySummary(String fromDate, String toDate) {
        String normalizedFromDate = ServiceValidation.normalizeBusinessDate(fromDate);
        String normalizedToDate = ServiceValidation.normalizeBusinessDate(toDate);

        if (!isValidDateRange(normalizedFromDate, normalizedToDate)) {
            return List.of();
        }

        return reportDAO.getPricingCategorySummary(normalizedFromDate, normalizedToDate);
    }

    public OwnerDashboardDTO getOwnerDashboard(String businessDate) {
        String normalizedBusinessDate = ServiceValidation.normalizeBusinessDate(businessDate);
        if (normalizedBusinessDate == null) {
            return new OwnerDashboardDTO(
                    new DailyShopTotalDTO(ServiceValidation.trimToNull(businessDate), 0, 0, 0, 0),
                    List.of(),
                    List.of());
        }

        return new OwnerDashboardDTO(
                reportDAO.getDailyShopTotal(normalizedBusinessDate),
                reportDAO.getDailyBarberTotals(normalizedBusinessDate),
                reportDAO.getTransactionViewsByBusinessDate(normalizedBusinessDate));
    }

    private boolean isValidDateRange(String fromDate, String toDate) {
        return fromDate != null && toDate != null && fromDate.compareTo(toDate) <= 0;
    }
}
