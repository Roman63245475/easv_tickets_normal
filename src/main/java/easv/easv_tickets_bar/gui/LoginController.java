package easv.easv_tickets_bar.gui;

import easv.easv_tickets_bar.be.Admin;
import easv.easv_tickets_bar.be.EventCoordinator;
import easv.easv_tickets_bar.be.User;
import easv.easv_tickets_bar.bll.Logic;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField usernameField;
    @FXML private TextField revealField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;

    @FXML private Region eyeIcon;

    private Logic logic;
    private OpenWindow openWindow;

    public LoginController(){
        this.logic = new Logic();
        this.openWindow = new OpenWindow();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.errorLabel.setStyle("-fx-text-fill: red");
        this.errorLabel.setOpacity(0.0);
        revealField.textProperty().bindBidirectional(passwordField.textProperty());
    }



    @FXML
    private void loginClick() {
        String username = this.usernameField.getText();
        String password = this.passwordField.getText();
        if (password.isEmpty() || username.isEmpty()){
            this.errorLabel.setText("Both username and password fields need to be filled");
            this.errorLabel.setOpacity(1.0);
            return;
        }
        loginButton.setDisable(true);
        Task<User> loginTask = new Task<User>(){
            @Override
            protected User call() throws Exception {
                return logic.login(username, password);
            }
        };
        loginTask.setOnSucceeded(event -> {
            User user = loginTask.getValue();
            try {
                String fileName = (user instanceof Admin) ? "admin-view.fxml" : "coordinator-view.fxml";
                String title = (user instanceof Admin) ? "Admin panel" : "Event Coordinator panel";
                Object obj = openWindow.openNewWindow(fileName, title, false);
                if (user instanceof EventCoordinator){
                    ((CoordinatorController) obj).setUser((EventCoordinator) user);
                }else{
                    ((AdminController) obj).setUser((Admin) user);
                }
                Stage currentStage = (Stage) this.usernameField.getScene().getWindow();
                currentStage.close();
            }
            catch (IOException e) {
                loginButton.setDisable(false);
                this.errorLabel.setText("Page can't be rendered");
                this.errorLabel.setOpacity(1.0);
            }
        });

        loginTask.setOnFailed(event -> {
            Throwable cause = loginTask.getException();
            loginButton.setDisable(false);
            this.errorLabel.setText(cause.getMessage());
            this.errorLabel.setOpacity(1.0);
        });
        new Thread(loginTask).start();
    }

    @FXML
    private void toggleVisibility() {
        if (passwordField.isVisible()) {
            eyeIcon.setId("open-eye-icon");
            passwordField.setVisible(false);
            revealField.setVisible(true);
        }else{
            eyeIcon.setId("closed-eye-icon");
            passwordField.setVisible(true);
            revealField.setVisible(false);
        }
    }


}
