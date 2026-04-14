package bongz.barbershop.service.authentication;

import bongz.barbershop.model.UserModel;
import bongz.barbershop.model.enums.UserRole;
import bongz.barbershop.dto.common.ServiceResult;
import bongz.barbershop.service.user.UserService;

public class RegistrationService {

    private final UserService userService = new UserService();

    public AuthResult register(String username, String password, String role) {
        UserRole userRole;

        try {
            userRole = UserRole.fromValue(role);
        } catch (IllegalArgumentException exception) {
            return new AuthResult(false, "Unsupported role", null);
        }

        ServiceResult<UserModel> result = userService.createUser(username, password, userRole);
        if (!result.isSuccess()) {
            return new AuthResult(false, result.getMessage(), null);
        }

        return new AuthResult(true, result.getMessage(), result.getData());
    }
}
