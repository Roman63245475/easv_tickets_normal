package easv.easv_tickets_bar.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private TextField passwordField;

    @FXML
    private void loginClick(){
        String username = this.emailField.getText();
    }
}
