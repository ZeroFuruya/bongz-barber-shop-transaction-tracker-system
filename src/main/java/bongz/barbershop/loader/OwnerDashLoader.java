package bongz.barbershop.loader;

import java.io.IOException;

import bongz.barbershop.App;
import javafx.scene.Scene;

public class OwnerDashLoader {

    static Scene scene;

    public static void load_owner_dashboard(App app) throws IOException {
        App.setRoot("layout/dashboards/OwnerDashboardView");
    }
}
