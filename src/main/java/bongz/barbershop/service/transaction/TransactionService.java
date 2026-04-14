package bongz.barbershop.service.transaction;

import bongz.barbershop.dto.common.ServiceResult;
import bongz.barbershop.model.BarberModel;
import bongz.barbershop.model.PricingCategoryModel;
import bongz.barbershop.model.TransactionModel;
import bongz.barbershop.model.UserModel;
import bongz.barbershop.model.enums.TransactionStatus;
import bongz.barbershop.server.dao.BarberDAO;
import bongz.barbershop.server.dao.PricingCategoryDAO;
import bongz.barbershop.server.dao.TransactionDAO;
import bongz.barbershop.server.dao.UserDAO;
import bongz.barbershop.service.common.MoneyCalculator;
import bongz.barbershop.service.common.ServiceValidation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TransactionService {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final BarberDAO barberDAO = new BarberDAO();
    private final PricingCategoryDAO pricingCategoryDAO = new PricingCategoryDAO();
    private final UserDAO userDAO = new UserDAO();

    public ServiceResult<TransactionModel> recordTransaction(int barberId, int pricingCategoryId, int loggedByUserId,
            String businessDate, String note) {
        if (!ServiceValidation.isPositiveId(barberId)) {
            return ServiceResult.fail("Invalid barber id");
        }

        if (!ServiceValidation.isPositiveId(pricingCategoryId)) {
            return ServiceResult.fail("Invalid pricing category id");
        }

        if (!ServiceValidation.isPositiveId(loggedByUserId)) {
            return ServiceResult.fail("Invalid logged-by user id");
        }

        String normalizedBusinessDate = ServiceValidation.normalizeBusinessDate(businessDate);
        if (normalizedBusinessDate == null) {
            return ServiceResult.fail("Business date must use YYYY-MM-DD");
        }

        BarberModel barber = barberDAO.findById(barberId);
        if (barber == null) {
            return ServiceResult.fail("Barber not found");
        }
        if (barber.getIsActive() != 1) {
            return ServiceResult.fail("Inactive barbers cannot receive new transactions");
        }

        PricingCategoryModel pricingCategory = pricingCategoryDAO.findById(pricingCategoryId);
        if (pricingCategory == null) {
            return ServiceResult.fail("Pricing category not found");
        }
        if (pricingCategory.getIsActive() != 1) {
            return ServiceResult.fail("Inactive pricing categories cannot be used");
        }

        UserModel loggedByUser = userDAO.findById(loggedByUserId);
        if (loggedByUser == null) {
            return ServiceResult.fail("Logged-by user not found");
        }
        if (loggedByUser.getIsActive() != 1) {
            return ServiceResult.fail("Inactive users cannot log transactions");
        }

        int chargedAmountPesos = pricingCategory.getChargedAmountPesos();
        int barberCommissionPercent = pricingCategory.getBarberCommissionPercent();
        int barberEarningAmountPesos = MoneyCalculator.calculateBarberEarningAmount(
                chargedAmountPesos,
                barberCommissionPercent);
        int shopEarningAmountPesos = MoneyCalculator.calculateShopEarningAmount(
                chargedAmountPesos,
                barberEarningAmountPesos);

        TransactionModel transaction = new TransactionModel(
                0,
                barberId,
                pricingCategoryId,
                loggedByUserId,
                normalizedBusinessDate,
                LocalDateTime.now().format(TIMESTAMP_FORMATTER),
                pricingCategory.getCode(),
                pricingCategory.getName(),
                chargedAmountPesos,
                barberCommissionPercent,
                barberEarningAmountPesos,
                shopEarningAmountPesos,
                TransactionStatus.POSTED.name(),
                null,
                ServiceValidation.trimToNull(note));

        if (!transactionDAO.insertTransaction(transaction)) {
            return ServiceResult.fail("Failed to record transaction");
        }

        TransactionModel savedTransaction = transactionDAO.findById(transaction.getTransactionId());
        return ServiceResult.ok("Transaction recorded", savedTransaction == null ? transaction : savedTransaction);
    }

    public ServiceResult<TransactionModel> voidTransaction(int transactionId, String voidReason, int actedByUserId) {
        if (!ServiceValidation.isPositiveId(transactionId)) {
            return ServiceResult.fail("Invalid transaction id");
        }

        if (!ServiceValidation.isPositiveId(actedByUserId)) {
            return ServiceResult.fail("Invalid acting user id");
        }

        String normalizedVoidReason = ServiceValidation.trimToNull(voidReason);
        if (normalizedVoidReason == null) {
            return ServiceResult.fail("Void reason is required");
        }

        UserModel actingUser = userDAO.findById(actedByUserId);
        if (actingUser == null) {
            return ServiceResult.fail("Acting user not found");
        }
        if (actingUser.getIsActive() != 1) {
            return ServiceResult.fail("Inactive users cannot void transactions");
        }

        TransactionModel transaction = transactionDAO.findById(transactionId);
        if (transaction == null) {
            return ServiceResult.fail("Transaction not found");
        }
        if (TransactionStatus.VOID.name().equalsIgnoreCase(transaction.getStatus())) {
            return ServiceResult.fail("Transaction is already voided");
        }

        if (!transactionDAO.voidTransaction(transactionId, normalizedVoidReason)) {
            return ServiceResult.fail("Failed to void transaction");
        }

        return ServiceResult.ok("Transaction voided", transactionDAO.findById(transactionId));
    }

    public List<TransactionModel> getTransactionsByBusinessDate(String businessDate) {
        String normalizedBusinessDate = ServiceValidation.normalizeBusinessDate(businessDate);
        if (normalizedBusinessDate == null) {
            return List.of();
        }

        return transactionDAO.getTransactionsByBusinessDate(normalizedBusinessDate);
    }

    public List<TransactionModel> getTransactionsByBarberAndBusinessDate(int barberId, String businessDate) {
        if (!ServiceValidation.isPositiveId(barberId)) {
            return List.of();
        }

        String normalizedBusinessDate = ServiceValidation.normalizeBusinessDate(businessDate);
        if (normalizedBusinessDate == null) {
            return List.of();
        }

        return transactionDAO.getTransactionsByBarberAndBusinessDate(barberId, normalizedBusinessDate);
    }
}
