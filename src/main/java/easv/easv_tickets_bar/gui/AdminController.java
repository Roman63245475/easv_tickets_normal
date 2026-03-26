package easv.easv_tickets_bar.gui;

import easv.easv_tickets_bar.CustomExceptions.DataBaseConnectionException;
import easv.easv_tickets_bar.be.Event;
import easv.easv_tickets_bar.be.EventCoordinator;
import easv.easv_tickets_bar.be.User;
import easv.easv_tickets_bar.bll.Logic;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
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
    @FXML private Button deleteEventButton;

    //users table and it's columns
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Integer> userIdColumn;
    @FXML private TableColumn<User, String> userUsernameColumn;
    @FXML private TableColumn<User, String> userStatusColumn;
    @FXML private TableColumn<User, String> userRoleColumn;

    //events table and it's columns
    @FXML private TableView<Event> eventsTable;
    @FXML private TableColumn<Event, Integer> eventIdColumn;
    @FXML private TableColumn<Event, String> eventNameColumn;
    @FXML private TableColumn<Event, String> startDateTimeColumn;
    @FXML private TableColumn<Event, String> endDateTimeColumn;
    @FXML private TableColumn<Event, String> eventLocationColumn;
    @FXML private TableColumn<Event, String> eventStatusColumn;

    private boolean isMenuOpen = false;
    private User user;
    private OpenWindow openWindow;
    private Logic logic;
    private ObservableList<User> userList = FXCollections.observableArrayList();
    private ObservableList<Event> eventList = FXCollections.observableArrayList();
    private User selectedUser;
    private Event selectedEvent;



    public AdminController(){
        this.openWindow = new OpenWindow();
        this.logic = new Logic();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usersTable.setItems(userList);
        eventsTable.setItems(eventList);
        setUpColumnsAlignment();
        setupEventTableColumns();
        fillEventTable();

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

    private void fillUserTable(List<User> users){
        userList.setAll(users);
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

    private void setupEventTableColumns() {
        eventIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        eventNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        startDateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));
        endDateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endDateTime"));
        eventLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        eventStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void fillEventTable(){
        List<Event> events = new ArrayList<>();
        try {
            events = logic.getAllEvents();
        } catch (DataBaseConnectionException e) {
            System.out.println("idk what to do here");
        }

        this.eventList.setAll(events);
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
        setupUserTableColumns();
        fillUserTable(getUsersWithoutCurrent());
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

    @FXML
    private void deleteEvent(){
        Event selectedEvent = eventsTable.getSelectionModel().getSelectedItem();
        if (selectedEvent == null){
            return;
        }
        this.deleteEventButton.setDisable(true);
        Task<List<Event>> deleteTask = new Task<List<Event>>() {
            @Override
            protected List<Event> call() throws Exception {
                logic.deleteSelectedEvent(selectedEvent);
                return logic.getAllEvents();
            }
        };
        deleteTask.setOnSucceeded(e -> {
            this.deleteEventButton.setDisable(false);
            eventList.setAll(deleteTask.getValue());
        });
        deleteTask.setOnFailed(e -> {
            this.deleteEventButton.setDisable(false);
            Throwable cause = deleteTask.getException();
            if (cause instanceof DataBaseConnectionException) {
                System.out.println("here needs to be an alert, or a error message");
            }
        });
        new Thread(deleteTask).start();
    }

    @FXML
    private void assignCoordinator(){
        Event selectedEvent = eventsTable.getSelectionModel().getSelectedItem();
        if (selectedEvent == null){
            return;
        }
        showAvailableCoordinators(selectedEvent);
    }

    private void showAvailableCoordinators(Event selectedEvent){
        Task<List<EventCoordinator>> getAvailableEventCoordinatorsTask = new Task<>() {
            @Override
            protected List<EventCoordinator> call() throws Exception {
                return logic.getAvailableEventCoordinators(selectedEvent);
            }
        };
        getAvailableEventCoordinatorsTask.setOnSucceeded(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("available_event_coordinators.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(loader.load());
                IAssignCoordinator controller = loader.getController();
                controller.setCoordinators(getAvailableEventCoordinatorsTask.getValue());
                controller.setEvent(selectedEvent);
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.show();
            } catch (IOException ex) {
                System.out.println("blya ya v ahue");
            }
        });
        getAvailableEventCoordinatorsTask.setOnFailed(e -> {
            System.out.println("idk what to do here");
        });
        new Thread(getAvailableEventCoordinatorsTask).start();
    }



    @Override
    public void refreshTable() {

    }


}
