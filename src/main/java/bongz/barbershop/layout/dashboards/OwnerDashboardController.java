package bongz.barbershop.layout.dashboards;

import bongz.barbershop.App;
import javafx.fxml.FXML;

public class OwnerDashboardController {

    private App app;

    public void load(App app) {
        this.app = app;
    }

    @FXML
    private void handleLogout() {
        try {
            App.setRoot("/bongz/barbershop/layout/authentication/AuthenticationView");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
