package bongz.barbershop.layout.dashboards;

import bongz.barbershop.App;
import bongz.barbershop.loader.AppLoader;
import bongz.barbershop.loader.ModalLoader;
import bongz.barbershop.model.UserModel;
import javafx.fxml.FXML;

public class OwnerDashboardController {

    private App app;
    private UserModel currentUser;

    public void load(App app, UserModel currentUser) {
        this.app = app;
        this.currentUser = currentUser;
    }

    @FXML
    private void handleLogout() {
        try {
            currentUser = null;
            app.clearCurrentUser();
            AppLoader.load_app_window(app, app.getMainStage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleNewUser() {
        try {
            ModalLoader.load_registration_window(app);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
