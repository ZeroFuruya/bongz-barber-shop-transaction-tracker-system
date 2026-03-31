package bongz.barbershop.service.authentication;

import bongz.barbershop.model.UserModel;
import bongz.barbershop.server.dao.UserDAO;

public class RegistrationService {

    private final UserDAO userDAO = new UserDAO();

    public AuthResult register(String username, String password, String role) {

        if (username.isBlank() || password.isBlank() || role.isBlank()) {
            return new AuthResult(false, "Fields cannot be empty", null);
        }

        if (userDAO.usernameExists(username)) {
            return new AuthResult(false, "Username already exists", null);
        }

        boolean inserted = userDAO.insertUser(username, password, role);

        if (!inserted) {
            return new AuthResult(false, "Registration failed", null);
        }

        UserModel user = userDAO.findByUsername(username);

        return new AuthResult(true, null, user);
    }
}
