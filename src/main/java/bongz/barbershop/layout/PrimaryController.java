package bongz.barbershop.layout;

import java.io.IOException;

import bongz.barbershop.App;
import javafx.fxml.FXML;

public class PrimaryController {

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}
