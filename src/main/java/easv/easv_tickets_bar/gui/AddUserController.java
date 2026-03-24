package easv.easv_tickets_bar.gui;

import easv.easv_tickets_bar.CustomExceptions.DataBaseConnectionException;
import easv.easv_tickets_bar.CustomExceptions.DuplicateException;
import easv.easv_tickets_bar.be.Role;
import easv.easv_tickets_bar.be.User;
import easv.easv_tickets_bar.bll.Logic;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.swing.plaf.basic.BasicButtonUI;
import java.net.URL;
import java.util.ResourceBundle;

public class AddUserController implements IUserPanel, Initializable {


    @FXML private TextField passwordField;
    @FXML private TextField userNameField;
    @FXML private ComboBox<Role> userRoleBox;
    @FXML private Label errorLabel;
    @FXML private Button createUserButton;

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
        if (username.isEmpty() || password.isEmpty() || role == null){
            this.errorLabel.setText("Please fill all the fields");
            this.errorLabel.setOpacity(1.0);
            return;
        }
        if (username.contains(" ")){
            this.errorLabel.setText("Username can't contain spaces");
            this.errorLabel.setOpacity(1.0);
            return;
        }
        if (username.length() < 8){
            this.errorLabel.setText("Username must contain at least 8 characters");
            this.errorLabel.setOpacity(1.0);
            return;
        }
        if (username.equals(password)){
            this.errorLabel.setText("Password can't be equal to username");
            this.errorLabel.setOpacity(1.0);
            return;
        }
        if (password.contains(" ")){
            this.errorLabel.setText("Password can't contain spaces");
            this.errorLabel.setOpacity(1.0);
            return;
        }
        if (password.length() < 8){
            this.errorLabel.setText("Password must contain at least 8 characters");
            this.errorLabel.setOpacity(1.0);
            return;
        }


        createUserButton.setDisable(true);
        Task<Void> createUserTask = new Task() {
            @Override
            protected Void call() throws Exception {
                logic.createUser(username, password, role);
                return null;
            }
        };


        createUserTask.setOnSucceeded(event -> {
            Stage st = (Stage) this.userNameField.getScene().getWindow();
            st.close();
        });


        createUserTask.setOnFailed(event -> {
            Throwable cause = createUserTask.getException();
            if (cause instanceof DataBaseConnectionException) {
                this.errorLabel.setText("Database connection error");
            }
            else if (cause instanceof DuplicateException) {
                this.errorLabel.setText("User with this username already exists");
            }
            else {
                this.errorLabel.setText("Unknown Error");
            }
            this.errorLabel.setOpacity(1.0);
            this.createUserButton.setDisable(false);
        });
        new Thread(createUserTask).start();
//         catch (DataBaseConnectionException | DuplicateException e) {
//            if (e instanceof DataBaseConnectionException){
//                showAlert(Alert.AlertType.ERROR, "Error", "Something went wrong", "An unexpected error occurred.");
//            }
//            else {
//                showAlert(Alert.AlertType.ERROR, "Error", "duplicate data", "User with the same username already exists");
//            }
//
//        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content){
        Alert errorAlert = new Alert(type);
        errorAlert.setTitle(title);
        errorAlert.setHeaderText(header);
        errorAlert.setContentText(content);
        errorAlert.show();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.userRoleBox.getItems().setAll(Role.values());
        this.errorLabel.setStyle("-fx-text-fill: red");
        this.errorLabel.setOpacity(0.0);
    }
}
