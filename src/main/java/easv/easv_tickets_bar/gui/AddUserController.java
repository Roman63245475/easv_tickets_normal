package easv.easv_tickets_bar.gui;

import easv.easv_tickets_bar.be.User;
import easv.easv_tickets_bar.bll.Logic;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AddUserController implements IUserPanel, Initializable {


    @FXML TextField passwordField;
    @FXML TextField userNameField;
    @FXML ComboBox<Role> userRoleBox;

    private Logic logic;
    private User user;


    public AddUserController() {
        this.logic = new Logic();
    }


    @Override
    public void setUser(User user) {
        this.user = user;
    }

    @FXML
    private void createUser(){
        String username = userNameField.getText();
        String password = passwordField.getText();
        Role role = userRoleBox.getValue();
        if (role == null){
            System.out.println("role is null");
        }
        else{
            System.out.println("role is not null");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.userRoleBox.getItems().setAll(Role.values());
    }
}
