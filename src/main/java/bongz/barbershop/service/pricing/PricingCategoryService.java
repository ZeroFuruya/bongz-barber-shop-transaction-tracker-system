package bongz.barbershop.service.pricing;

import bongz.barbershop.dto.common.ServiceResult;
import bongz.barbershop.model.PricingCategoryModel;
import bongz.barbershop.server.dao.PricingCategoryDAO;
import bongz.barbershop.service.common.ServiceValidation;

import java.util.List;

public class PricingCategoryService {

    private final PricingCategoryDAO pricingCategoryDAO = new PricingCategoryDAO();

    public List<PricingCategoryModel> getAllCategories() {
        return pricingCategoryDAO.getAllPricingCategories();
    }

    public List<PricingCategoryModel> getAllActiveCategories() {
        return pricingCategoryDAO.getAllActivePricingCategories();
    }

    public PricingCategoryModel getDefaultCategory() {
        return pricingCategoryDAO.getDefaultPricingCategory();
    }

    public PricingCategoryModel getCategoryById(int pricingCategoryId) {
        return pricingCategoryDAO.findById(pricingCategoryId);
    }

    public ServiceResult<PricingCategoryModel> createCategory(String code, String name, String description,
            int chargedAmountPesos, int barberCommissionPercent, int sortOrder) {
        String normalizedCode = ServiceValidation.normalizePricingCode(code);
        String normalizedName = ServiceValidation.trimToNull(name);
        String normalizedDescription = ServiceValidation.trimToNull(description);

        if (normalizedCode == null) {
            return ServiceResult.fail("Pricing category code is required");
        }

        if (normalizedName == null) {
            return ServiceResult.fail("Pricing category name is required");
        }

        if (chargedAmountPesos < 0) {
            return ServiceResult.fail("Charged amount cannot be negative");
        }

        if (barberCommissionPercent < 0 || barberCommissionPercent > 100) {
            return ServiceResult.fail("Commission percent must be between 0 and 100");
        }

        if (pricingCategoryDAO.findByCode(normalizedCode) != null) {
            return ServiceResult.fail("Pricing category code already exists");
        }

        int resolvedSortOrder = sortOrder > 0 ? sortOrder : getNextSortOrder();
        PricingCategoryModel category = new PricingCategoryModel(
                0,
                normalizedCode,
                normalizedName,
                normalizedDescription,
                chargedAmountPesos,
                barberCommissionPercent,
                0,
                1,
                resolvedSortOrder,
                null);

        if (!pricingCategoryDAO.insertPricingCategory(category)) {
            return ServiceResult.fail("Failed to create pricing category");
        }

        return ServiceResult.ok("Pricing category created", pricingCategoryDAO.findByCode(normalizedCode));
    }

    public ServiceResult<PricingCategoryModel> updateCategory(int pricingCategoryId, String code, String name,
            String description, int chargedAmountPesos, int barberCommissionPercent, int isActive, int sortOrder) {
        if (!ServiceValidation.isPositiveId(pricingCategoryId)) {
            return ServiceResult.fail("Invalid pricing category id");
        }

        if (!ServiceValidation.isBinaryFlag(isActive)) {
            return ServiceResult.fail("Active status must be 0 or 1");
        }

        String normalizedCode = ServiceValidation.normalizePricingCode(code);
        String normalizedName = ServiceValidation.trimToNull(name);
        String normalizedDescription = ServiceValidation.trimToNull(description);

        if (normalizedCode == null) {
            return ServiceResult.fail("Pricing category code is required");
        }

        if (normalizedName == null) {
            return ServiceResult.fail("Pricing category name is required");
        }

        if (chargedAmountPesos < 0) {
            return ServiceResult.fail("Charged amount cannot be negative");
        }

        if (barberCommissionPercent < 0 || barberCommissionPercent > 100) {
            return ServiceResult.fail("Commission percent must be between 0 and 100");
        }

        PricingCategoryModel existingCategory = pricingCategoryDAO.findById(pricingCategoryId);
        if (existingCategory == null) {
            return ServiceResult.fail("Pricing category not found");
        }

        PricingCategoryModel categoryWithSameCode = pricingCategoryDAO.findByCode(normalizedCode);
        if (categoryWithSameCode != null && categoryWithSameCode.getPricingCategoryId() != pricingCategoryId) {
            return ServiceResult.fail("Pricing category code already exists");
        }

        if (existingCategory.getIsDefault() == 1 && isActive == 0) {
            return ServiceResult.fail("Default pricing category cannot be inactive");
        }

        int resolvedSortOrder = sortOrder > 0 ? sortOrder : existingCategory.getSortOrder();
        PricingCategoryModel updatedCategory = new PricingCategoryModel(
                pricingCategoryId,
                normalizedCode,
                normalizedName,
                normalizedDescription,
                chargedAmountPesos,
                barberCommissionPercent,
                existingCategory.getIsDefault(),
                isActive,
                resolvedSortOrder,
                existingCategory.getCreatedAt());

        if (!pricingCategoryDAO.updatePricingCategory(updatedCategory)) {
            return ServiceResult.fail("Failed to update pricing category");
        }

        return ServiceResult.ok("Pricing category updated", pricingCategoryDAO.findById(pricingCategoryId));
    }

    public ServiceResult<PricingCategoryModel> setDefaultCategory(int pricingCategoryId) {
        if (!ServiceValidation.isPositiveId(pricingCategoryId)) {
            return ServiceResult.fail("Invalid pricing category id");
        }

        PricingCategoryModel category = pricingCategoryDAO.findById(pricingCategoryId);
        if (category == null) {
            return ServiceResult.fail("Pricing category not found");
        }

        if (!pricingCategoryDAO.setDefaultPricingCategory(pricingCategoryId)) {
            return ServiceResult.fail("Failed to change default pricing category");
        }

        return ServiceResult.ok("Default pricing category updated", pricingCategoryDAO.findById(pricingCategoryId));
    }

    public ServiceResult<PricingCategoryModel> setCategoryActiveStatus(int pricingCategoryId, int isActive) {
        if (!ServiceValidation.isPositiveId(pricingCategoryId)) {
            return ServiceResult.fail("Invalid pricing category id");
        }

        if (!ServiceValidation.isBinaryFlag(isActive)) {
            return ServiceResult.fail("Active status must be 0 or 1");
        }

        PricingCategoryModel existingCategory = pricingCategoryDAO.findById(pricingCategoryId);
        if (existingCategory == null) {
            return ServiceResult.fail("Pricing category not found");
        }

        if (existingCategory.getIsDefault() == 1 && isActive == 0) {
            return ServiceResult.fail("Default pricing category cannot be inactive");
        }

        if (!pricingCategoryDAO.setPricingCategoryActiveStatus(pricingCategoryId, isActive)) {
            return ServiceResult.fail("Failed to update pricing category status");
        }

        return ServiceResult.ok("Pricing category status updated", pricingCategoryDAO.findById(pricingCategoryId));
    }

    private int getNextSortOrder() {
        return pricingCategoryDAO.getAllPricingCategories().stream()
                .mapToInt(PricingCategoryModel::getSortOrder)
                .max()
                .orElse(0) + 1;
    }
}
