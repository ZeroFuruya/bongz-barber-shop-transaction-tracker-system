package bongz.barbershop.layout.dashboards;

import bongz.barbershop.App;
import bongz.barbershop.layout.MainAppController;
import bongz.barbershop.loader.AppLoader;
import javafx.fxml.FXML;

public class OwnerDashboardController {

    private App app;
    private MainAppController mainAppController;

    public void load(App app, MainAppController mainAppController) {
        this.app = app;
        this.mainAppController = mainAppController;
    }

    @FXML
    private void handleLogout() {
        try {
            AppLoader.load_app_window(app, app.getMainStage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
