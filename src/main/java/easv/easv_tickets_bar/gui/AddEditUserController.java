package easv.easv_tickets_bar.gui;

import easv.easv_tickets_bar.be.Role;
import easv.easv_tickets_bar.be.User;
import easv.easv_tickets_bar.bll.Logic;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AddEditUserController implements IUserPanel, Initializable, IPanel {


    @FXML private PasswordField passwordField;
    @FXML private TextField revealField;
    @FXML private TextField userNameField;
    @FXML private ComboBox<Role> userRoleBox;

    @FXML private Label errorLabel;
    @FXML private Label titleLabelTop;
    @FXML private Label titleLabelBottom;

    @FXML private Button createUserButton;

    @FXML private Region eyeIcon;

    private Logic logic;
    private User user;
    private IRefreshable controller;


    public AddEditUserController() {
        this.logic = new Logic();
    }


    @Override
    public void setUser(User user) {
        this.user = user;
        titleLabelTop.setText("Edit User");
        titleLabelBottom.setText("Edit an existing account");
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.userRoleBox.getItems().setAll(Role.values());
        this.errorLabel.setStyle("-fx-text-fill: red");
        this.errorLabel.setOpacity(0.0);
        revealField.textProperty().bindBidirectional(passwordField.textProperty());
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
