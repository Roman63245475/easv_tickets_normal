package easv.easv_tickets_bar.gui;

import easv.easv_tickets_bar.CustomExceptions.DataBaseConnectionException;
import easv.easv_tickets_bar.be.Event;
import easv.easv_tickets_bar.be.User;
import easv.easv_tickets_bar.bll.Logic;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AdminController implements Initializable, IUserPanel, IRefreshable{
    @FXML private VBox sideBar;
    @FXML private StackPane contentBox;
    @FXML private Label welcomeUserLabel;


    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Integer> userIdColumn;
    @FXML private TableColumn<User, String> userUsernameColumn;
    @FXML private TableColumn<User, String> userStatusColumn;
    @FXML private TableColumn<User, String> userRoleColumn;

    private boolean isMenuOpen = false;
    private User user;
    private OpenWindow openWindow;
    private ObservableList<Event> userList;
    private Logic logic;



    public AdminController(){
        this.openWindow = new OpenWindow();
        this.logic = new Logic();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpColumnsAlignment();
        //setupUserTableColumns();
        //userTable.setItems(FXCollections.observableArrayList());
    }


    private List<User> getUsersWithoutCurrent() {
        List<User> users = new ArrayList<>();
        if (user == null) return users;
        try {
             users = logic.getUsersWithoutCurrent(user.getId());
        } catch (DataBaseConnectionException e) {
            System.out.println("idk what to do here");
        }
        return users;
    }

    private void setUserTable(List<User> users){
        ObservableList<User> userList = FXCollections.observableArrayList(users);
        usersTable.setItems(userList);
    }

    private void setupUserTableColumns() {
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        userRoleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        userStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void setUpColumnsAlignment(){
        userIdColumn.setStyle("-fx-alignment: CENTER;");
        userUsernameColumn.setStyle("-fx-alignment: CENTER;");
        userRoleColumn.setStyle("-fx-alignment: CENTER;");
        userStatusColumn.setStyle("-fx-alignment: CENTER;");
    }

    public void menuSlide(){
        UIHelper.sideBarAnimation(isMenuOpen, sideBar, () -> isMenuOpen = !isMenuOpen);
    }

    @FXML
    public void switchTab(String name){
        for(Node n : contentBox.getChildren()){
            if (n instanceof VBox && n.getId().equals(name)) n.setVisible(true);
            else n.setVisible(false);
        }
    }

    public void onUserManClick(){
        switchTab("userManagementBox");
    }

    public void onEventManClick(){
        switchTab("eventManagementBox");
    }


    @Override
    public void setUser(User user) {
        this.user = user;
        welcomeUserLabel.setText("Welcome " + user.getUsername());
        setUserTable(getUsersWithoutCurrent());
        setupUserTableColumns();
    }

    @FXML
    private void addUser(){
        String fileName = "create_user.fxml";
        String title = "Create new user";
        try {
            openWindow.openNewWindow(fileName, title, this.user, true, this);
        } catch (IOException e) {
            System.out.println("here needs to be an alert");
        }


    }

    @Override
    public void refreshTable() {

    }


}
