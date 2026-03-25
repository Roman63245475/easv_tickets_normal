package easv.easv_tickets_bar.gui;

import easv.easv_tickets_bar.CustomExceptions.DataBaseConnectionException;
import easv.easv_tickets_bar.CustomExceptions.LoginException;
import easv.easv_tickets_bar.be.Admin;
import easv.easv_tickets_bar.be.Role;
import easv.easv_tickets_bar.be.User;
import easv.easv_tickets_bar.bll.Logic;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.LoadException;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField usernameField;
    @FXML private TextField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;

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
    }



    @FXML
    private void loginClick() throws DataBaseConnectionException {
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
                if (obj instanceof IUserPanel){
                    ((IUserPanel) obj).setUser(user);
                }
                Stage currentStage = (Stage) this.usernameField.getScene().getWindow();
                currentStage.close();
            }
            catch (IOException e) {
                loginButton.setDisable(false);
                this.errorLabel.setText("Page can't be rendered");
                this.errorLabel.setOpacity(1.0);
                return;
                //or here needs to be an alert
            }
        });

        loginTask.setOnFailed(event -> {
            Throwable cause = loginTask.getException();
            loginButton.setDisable(false);
            if (cause instanceof DataBaseConnectionException) {
                this.errorLabel.setText("Sorry something went wrong please try later");
            }
            else if (cause instanceof LoginException) {
                this.errorLabel.setText("Username or Password is incorrect");
            }
            else {
                errorLabel.setText("Unknown error");
            }
            this.errorLabel.setOpacity(1.0);
        });
        new Thread(loginTask).start();
    }


}
