package easv.easv_tickets_bar.gui;

import easv.easv_tickets_bar.CustomExceptions.DataBaseConnectionException;
import easv.easv_tickets_bar.CustomExceptions.LoginException;
import easv.easv_tickets_bar.be.User;
import easv.easv_tickets_bar.bll.Logic;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.LoadException;
import javafx.scene.Scene;
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


        try {
            User user = logic.login(username, password);
            int role_id = user.getRoleID();
            String fileName = (role_id == 1) ? "admin-view.fxml" : "coordinator-view.fxml";
            String title = (role_id == 1) ? "Admin panel" : "Event Coordinator panel";
            openWindow.openNewWindow(fileName, title, user, false);
            Stage currentStage = (Stage) this.usernameField.getScene().getWindow();
            currentStage.close();
        }
        catch (DataBaseConnectionException | LoginException dbce){
            if (dbce instanceof DataBaseConnectionException){
                this.errorLabel.setText("Sorry something went wrong please try later");
                this.errorLabel.setOpacity(1.0);
                return;
            }
            this.errorLabel.setText("Username or Password is incorrect");
            this.errorLabel.setOpacity(1.0);
        } catch (IOException e) {
            this.errorLabel.setText("Page can't be rendered");
            this.errorLabel.setOpacity(1.0);
            return;
            //or here needs to be an alert
        }
    }


}
