package bongz.barbershop.service.authentication;

import bongz.barbershop.model.UserModel;
import bongz.barbershop.server.dao.UserDAO;
import bongz.barbershop.service.common.ServiceValidation;

public class AuthenticatorService {

    private final UserDAO userDAO = new UserDAO();

    public AuthResult login(String username, String password) {
        String normalizedUsername = ServiceValidation.trimToNull(username);

        if (normalizedUsername == null || ServiceValidation.isBlank(password)) {
            return new AuthResult(false, "Fields cannot be empty", null);
        }

        UserModel user = userDAO.findByUsername(normalizedUsername);

        if (user == null) {
            return new AuthResult(false, "User not found", null);
        }

        if (user.getIsActive() != 1) {
            return new AuthResult(false, "User account is inactive", null);
        }

        if (!password.equals(user.getPassword())) {
            return new AuthResult(false, "Invalid password", null);
        }

        return new AuthResult(true, null, user);
    }
}
