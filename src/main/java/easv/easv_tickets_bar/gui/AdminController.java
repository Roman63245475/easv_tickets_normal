package easv.easv_tickets_bar.gui;

import easv.easv_tickets_bar.be.Admin;
import easv.easv_tickets_bar.be.Event;
import easv.easv_tickets_bar.be.EventCoordinator;
import easv.easv_tickets_bar.be.User;
import easv.easv_tickets_bar.bll.Logic;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AdminController implements Initializable, IRefreshable{
    @FXML private VBox sideBar;
    @FXML private StackPane contentBox;
    @FXML private Label welcomeUserLabel;
    @FXML private Button deleteEventButton;
    @FXML private Button deleteButton;

    //users table and it's columns
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Integer> userIdColumn;
    @FXML private TableColumn<User, String> userUsernameColumn;
    @FXML private TableColumn<User, String> userRoleColumn;

    //events table and it's columns
    @FXML private TableView<Event> eventsTable;
    @FXML private TableColumn<Event, Integer> eventIdColumn;
    @FXML private TableColumn<Event, String> eventNameColumn;
    @FXML private TableColumn<Event, String> startDateColumn;
    @FXML private TableColumn<Event, String> startTimeColumn;
    @FXML private TableColumn<Event, String> endDateColumn;
    @FXML private TableColumn<Event, String> endTimeColumn;
    @FXML private TableColumn<Event, String> eventLocationColumn;


    private boolean isMenuOpen = false;
    private Admin user;
    private OpenWindow openWindow;
    private Logic logic;
    private ObservableList<User> userList = FXCollections.observableArrayList();
    private ObservableList<Event> eventList = FXCollections.observableArrayList();
    private User selectedUser;
    private Event selectedEvent;
    private Timeline timeline;



    public AdminController(){
        this.openWindow = new OpenWindow();
        this.logic = new Logic();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.timeline = new Timeline(new KeyFrame(Duration.seconds(14), e -> refreshTable()));
        this.timeline.setCycleCount(Timeline.INDEFINITE);
        this.timeline.play();
        setupUserTableColumns();
        setupEventTableColumns();
        setUpColumnsAlignment();
        fillEventTable();

        //setupUserTableColumns();
        //userTable.setItems(FXCollections.observableArrayList());
    }

    private void updateAllTables() {
    }


//    private List<User> getUsersWithoutCurrent() {
//        List<User> users = new ArrayList<>();
//        if (user == null) return users;
//        users = logic.getUsersWithoutCurrent(user.getId());
//        return users;
//    }

    private void fillUserTable(List<User> users){
        userList.setAll(users);
    }


    private void setupUserTableColumns() {
        usersTable.setItems(userList);
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        userRoleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
    }

    private void setUpColumnsAlignment(){
        userIdColumn.setStyle("-fx-alignment: CENTER;");
        userUsernameColumn.setStyle("-fx-alignment: CENTER;");
        userRoleColumn.setStyle("-fx-alignment: CENTER;");
    }

    private void setupEventTableColumns() {
        eventsTable.setItems(eventList);
        eventIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        eventNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        eventLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
    }

    private void fillEventTable(){
        Task<List<Event>> getEvents = new Task<List<Event>>() {
            @Override
            protected List<Event> call() throws Exception {
                List<Event> events = logic.getAllEvents();
                return events;
            }
        };
        getEvents.setOnSucceeded(event -> {
            this.eventList.setAll(getEvents.getValue());
        });
        getEvents.setOnFailed(event -> {
            Throwable ex = getEvents.getException();
            System.out.println(ex.getMessage());
        });
        new Thread(getEvents).start();
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


    public void setUser(Admin user) {
        this.user = user;
        welcomeUserLabel.setText("Welcome " + user.getUsername());
        loadUsers();
    }
    private void loadUsers(){
        Task<List<User>> getUsers = new Task<List<User>>() {
            @Override
            protected List<User> call() throws Exception {
                List<User> users = logic.getUsersWithoutCurrent(user.getId());
                return users;
            }
        };
        getUsers.setOnSucceeded(e -> {
            fillUserTable(getUsers.getValue());
        });
        getUsers.setOnFailed(e -> {
            Throwable cause = getUsers.getException();
            System.out.println(cause.getMessage());
        });
        new Thread(getUsers).start();
    }

    @FXML
    private void addUser(){
        String fileName = "create_user.fxml";
        String title = "Create new user";
        try {
            stopAutoRefresh();
            Object obj = openWindow.openNewWindow(fileName, title, true);
            AddEditUserController adController = (AddEditUserController) obj;
            adController.setController(this);
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
            System.out.println(cause.getMessage());
        });
        new Thread(deleteTask).start();
    }

    @FXML
    private void assignCoordinator(){
        Event selectedEvent = eventsTable.getSelectionModel().getSelectedItem();
        if (selectedEvent == null){
            return;
        }
        Task<List<EventCoordinator>> getAvailableEventCoordinatorsTask = new Task<>() {
            @Override
            protected List<EventCoordinator> call() throws Exception {
                return logic.getAvailableEventCoordinators(selectedEvent);
            }
        };
        getAvailableEventCoordinatorsTask.setOnSucceeded(e -> {
            stopAutoRefresh();
            try {
                AssignEventCoordinatorController ctr = (AssignEventCoordinatorController) openWindow.openAssignCoordinatorView(selectedEvent, getAvailableEventCoordinatorsTask.getValue());
                ctr.setController(this);
            } catch (IOException ex) {
                restoreTimeLine();
            }

        });
        getAvailableEventCoordinatorsTask.setOnFailed(e -> {
            Throwable ex = getAvailableEventCoordinatorsTask.getException();
            System.out.println(ex.getMessage());
        });
        new Thread(getAvailableEventCoordinatorsTask).start();
    }

    @FXML
    private void editUser(){
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null){
            return;
        }
        String fileName = "create_user.fxml";
        String title = "Edit User";
        try {
            stopAutoRefresh();
            Object obj = openWindow.openNewWindow(fileName, title, true);
            AddEditUserController adController = (AddEditUserController) obj;
            adController.setController(this);
            adController.setUser(selectedUser);
        }
        catch (IOException e) {
            System.out.println("here needs to be an alert");
        }

    }

    @FXML
    private void deleteUser(){
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null){
            return;
        }
        this.deleteButton.setDisable(true);
        Task<Void> deleteUserTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                logic.deleteSelectedUser(selectedUser);
                return null;
            }
        };
        deleteUserTask.setOnSucceeded(e -> {
            this.deleteButton.setDisable(false);
            refreshTable();
        });
        deleteUserTask.setOnFailed(e -> {
            Throwable ex = deleteUserTask.getException();
            System.out.println(ex.getMessage());
            //need to use a label or alert
            this.deleteButton.setDisable(false);
        });

        new Thread(deleteUserTask).start();
    }



    @Override
    public void refreshTable() {
        fillEventTable();
        loadUsers();
    }

    @Override
    public void restoreTimeLine() {
        timeline.play();
    }

    private void stopAutoRefresh(){
        timeline.stop();
    }



}
