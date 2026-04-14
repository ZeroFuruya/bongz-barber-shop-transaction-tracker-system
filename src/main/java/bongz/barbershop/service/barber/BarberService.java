package bongz.barbershop.service.barber;

import bongz.barbershop.dto.common.ServiceResult;
import bongz.barbershop.model.BarberModel;
import bongz.barbershop.server.dao.BarberDAO;
import bongz.barbershop.service.common.ServiceValidation;

import java.util.List;

public class BarberService {

    private final BarberDAO barberDAO = new BarberDAO();

    public List<BarberModel> getAllBarbers() {
        return barberDAO.getAllBarbers();
    }

    public List<BarberModel> getAllActiveBarbers() {
        return barberDAO.getAllActiveBarbers();
    }

    public BarberModel getBarberById(int barberId) {
        return barberDAO.findById(barberId);
    }

    public ServiceResult<BarberModel> createBarber(String name, String imagePath, int displayOrder) {
        String normalizedName = ServiceValidation.trimToNull(name);
        String normalizedImagePath = ServiceValidation.normalizeOptionalRelativePath(imagePath);

        if (normalizedName == null) {
            return ServiceResult.fail("Barber name is required");
        }

        if (!ServiceValidation.isRelativePath(normalizedImagePath)) {
            return ServiceResult.fail("Barber image path must be relative");
        }

        if (barberDAO.nameExists(normalizedName)) {
            return ServiceResult.fail("Barber name already exists");
        }

        int resolvedDisplayOrder = displayOrder > 0 ? displayOrder : getNextDisplayOrder();
        BarberModel barber = new BarberModel(0, normalizedName, normalizedImagePath, resolvedDisplayOrder, 1, null);

        if (!barberDAO.insertBarber(barber)) {
            return ServiceResult.fail("Failed to create barber");
        }

        return ServiceResult.ok("Barber created", barberDAO.findByName(normalizedName));
    }

    public ServiceResult<BarberModel> updateBarber(int barberId, String name, String imagePath, int displayOrder,
            int isActive) {
        if (!ServiceValidation.isPositiveId(barberId)) {
            return ServiceResult.fail("Invalid barber id");
        }

        if (!ServiceValidation.isBinaryFlag(isActive)) {
            return ServiceResult.fail("Active status must be 0 or 1");
        }

        String normalizedName = ServiceValidation.trimToNull(name);
        String normalizedImagePath = ServiceValidation.normalizeOptionalRelativePath(imagePath);

        if (normalizedName == null) {
            return ServiceResult.fail("Barber name is required");
        }

        if (!ServiceValidation.isRelativePath(normalizedImagePath)) {
            return ServiceResult.fail("Barber image path must be relative");
        }

        BarberModel existingBarber = barberDAO.findById(barberId);
        if (existingBarber == null) {
            return ServiceResult.fail("Barber not found");
        }

        BarberModel barberWithSameName = barberDAO.findByName(normalizedName);
        if (barberWithSameName != null && barberWithSameName.getBarberId() != barberId) {
            return ServiceResult.fail("Barber name already exists");
        }

        int resolvedDisplayOrder = displayOrder > 0 ? displayOrder : existingBarber.getDisplayOrder();
        BarberModel updatedBarber = new BarberModel(
                barberId,
                normalizedName,
                normalizedImagePath,
                resolvedDisplayOrder,
                isActive,
                existingBarber.getCreatedAt());

        if (!barberDAO.updateBarber(updatedBarber)) {
            return ServiceResult.fail("Failed to update barber");
        }

        return ServiceResult.ok("Barber updated", barberDAO.findById(barberId));
    }

    public ServiceResult<BarberModel> setBarberActiveStatus(int barberId, int isActive) {
        if (!ServiceValidation.isPositiveId(barberId)) {
            return ServiceResult.fail("Invalid barber id");
        }

        if (!ServiceValidation.isBinaryFlag(isActive)) {
            return ServiceResult.fail("Active status must be 0 or 1");
        }

        BarberModel existingBarber = barberDAO.findById(barberId);
        if (existingBarber == null) {
            return ServiceResult.fail("Barber not found");
        }

        if (!barberDAO.setBarberActiveStatus(barberId, isActive)) {
            return ServiceResult.fail("Failed to update barber status");
        }

        return ServiceResult.ok("Barber status updated", barberDAO.findById(barberId));
    }

    public ServiceResult<BarberModel> updateDisplayOrder(int barberId, int displayOrder) {
        if (!ServiceValidation.isPositiveId(barberId)) {
            return ServiceResult.fail("Invalid barber id");
        }

        if (displayOrder <= 0) {
            return ServiceResult.fail("Display order must be greater than 0");
        }

        BarberModel existingBarber = barberDAO.findById(barberId);
        if (existingBarber == null) {
            return ServiceResult.fail("Barber not found");
        }

        if (!barberDAO.updateDisplayOrder(barberId, displayOrder)) {
            return ServiceResult.fail("Failed to update barber display order");
        }

        return ServiceResult.ok("Barber display order updated", barberDAO.findById(barberId));
    }

    private int getNextDisplayOrder() {
        return barberDAO.getAllBarbers().stream()
                .mapToInt(BarberModel::getDisplayOrder)
                .max()
                .orElse(0) + 1;
    }
}
