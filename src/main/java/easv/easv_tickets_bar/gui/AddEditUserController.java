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

import java.net.URL;
import java.util.ResourceBundle;

public class AddEditUserController implements IUserPanel, Initializable, IPanel {


    @FXML private TextField passwordField;
    @FXML private TextField userNameField;
    @FXML private ComboBox<Role> userRoleBox;
    @FXML private Label errorLabel;
    @FXML private Button createUserButton;

    private Logic logic;
    private User user;
    private IRefreshable controller;


    public AddEditUserController() {
        this.logic = new Logic();
    }


    @Override
    public void setUser(User user) {
        this.user = user;
        createUserButton.setText("Edit User");
        createUserButton.setOnAction(e -> {editUser();});
        fillFields();
    }

    @FXML
    private void createUser(){
        String username = userNameField.getText();
        String password = passwordField.getText();
        Role role = userRoleBox.getValue();



        createUserButton.setDisable(true);
        Task<Void> createUserTask = new Task() {
            @Override
            protected Void call() throws Exception {
                logic.createUser(username, password, role);
                return null;
            }
        };


        createUserTask.setOnSucceeded(event -> {
            controller.refreshTable();
            controller.restoreTimeLine();
            Stage st = (Stage) this.userNameField.getScene().getWindow();
            st.close();
        });


        createUserTask.setOnFailed(event -> {
            Throwable cause = createUserTask.getException();
            this.errorLabel.setText(cause.getMessage());
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
    public void setController(IRefreshable controller) {
        this.controller = controller;
        Stage stage = (Stage) this.userNameField.getScene().getWindow();
        stage.setOnCloseRequest(event -> {
            controller.restoreTimeLine();
        });
    }

    @Override
    public void onClose() {
        controller.refreshTable();
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

    private void fillFields(){
        this.userNameField.setText(this.user.getUsername());
        this.userRoleBox.setValue(this.user.getRole());
    }

    @FXML
    private void editUser(){
        String changed_username = userNameField.getText();
        String changed_password = passwordField.getText();
        Role changed_role = userRoleBox.getValue();
//        if (changed_username.equals(this.user.getUsername()) && changed_role == this.user.getRole()){
//            this.errorLabel.setText("Data hasn't been changed!");
//            errorLabel.setOpacity(1.0);
//            return;
//        }

        Task<Void> editUserTask = new Task() {
            @Override
            protected Object call() throws Exception {
                logic.editUser(user, changed_username, changed_password, changed_role);
                return null;
            }
        };
        editUserTask.setOnSucceeded(event -> {
            controller.refreshTable();
            controller.restoreTimeLine();
            Stage st = (Stage) this.userNameField.getScene().getWindow();
            st.close();
        });
        editUserTask.setOnFailed(event -> {
            Throwable cause = editUserTask.getException();
            this.errorLabel.setText(cause.getMessage());
            this.errorLabel.setOpacity(1.0);
        });
        new Thread(editUserTask).start();
    }
}
