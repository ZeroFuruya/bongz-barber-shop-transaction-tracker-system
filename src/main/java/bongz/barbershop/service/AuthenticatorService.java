package bongz.barbershop.service;

import bongz.barbershop.model.UserModel;
import bongz.barbershop.server.dao.UserDAO;

public class AuthenticatorService {

    private final UserDAO userDAO = new UserDAO();

    public AuthResult login(String username, String password) {

        if (username.isBlank() || password.isBlank()) {
            return new AuthResult(false, "Fields cannot be empty", null);
        }

        UserModel user = userDAO.findByUsername(username);

        if (user == null) {
            return new AuthResult(false, "User not found", null);
        }

        if (!password.equals(user.getPassword())) {
            return new AuthResult(false, "Invalid password", null);
        }

        return new AuthResult(true, null, user);
    }
}
