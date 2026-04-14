package bongz.barbershop.service.user;

import bongz.barbershop.dto.common.ServiceResult;
import bongz.barbershop.model.UserModel;
import bongz.barbershop.model.enums.UserRole;
import bongz.barbershop.server.dao.UserDAO;
import bongz.barbershop.service.common.ServiceValidation;

import java.util.List;

public class UserService {

    private final UserDAO userDAO = new UserDAO();

    public UserModel getUserById(int userId) {
        return userDAO.findById(userId);
    }

    public List<UserModel> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public List<UserModel> getAllActiveUsers() {
        return userDAO.getAllActiveUsers();
    }

    public ServiceResult<UserModel> createUser(String username, String password, UserRole role) {
        String normalizedUsername = ServiceValidation.trimToNull(username);

        if (normalizedUsername == null) {
            return ServiceResult.fail("Username is required");
        }

        if (ServiceValidation.isBlank(password)) {
            return ServiceResult.fail("Password is required");
        }

        if (role == null) {
            return ServiceResult.fail("User role is required");
        }

        if (userDAO.usernameExists(normalizedUsername)) {
            return ServiceResult.fail("Username already exists");
        }

        UserModel user = new UserModel(0, normalizedUsername, password, role.name(), 1, null);
        if (!userDAO.insertUser(user)) {
            return ServiceResult.fail("Failed to create user");
        }

        return ServiceResult.ok("User created", userDAO.findByUsername(normalizedUsername));
    }

    public ServiceResult<UserModel> createManager(String username, String password) {
        return createUser(username, password, UserRole.MANAGER);
    }

    public ServiceResult<UserModel> setUserActiveStatus(int userId, int isActive) {
        if (!ServiceValidation.isPositiveId(userId)) {
            return ServiceResult.fail("Invalid user id");
        }

        if (!ServiceValidation.isBinaryFlag(isActive)) {
            return ServiceResult.fail("Active status must be 0 or 1");
        }

        UserModel existingUser = userDAO.findById(userId);
        if (existingUser == null) {
            return ServiceResult.fail("User not found");
        }

        if (!userDAO.setUserActiveStatus(userId, isActive)) {
            return ServiceResult.fail("Failed to update user status");
        }

        return ServiceResult.ok("User status updated", userDAO.findById(userId));
    }
}
