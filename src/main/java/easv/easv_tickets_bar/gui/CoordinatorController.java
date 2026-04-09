package easv.easv_tickets_bar.gui;

import easv.easv_tickets_bar.be.Event;
import easv.easv_tickets_bar.be.EventCoordinator;
import easv.easv_tickets_bar.bll.Logic;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;

import javafx.util.Duration;
//import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;

public class CoordinatorController implements IRefreshable, Initializable {
    @FXML private VBox sideBar;
    @FXML private StackPane contentBox;

    //Events Table
    @FXML private TableView<Event> eventTable;
    @FXML private TableColumn<Event, String> nameColumn;
    @FXML private TableColumn<Event, LocalDate> dateColumn;
    @FXML private TableColumn<Event, LocalTime> timeColumn;
    @FXML private TableColumn<Event, String> locationColumn;
    @FXML private TableColumn<Event, Integer> ticketColumn;
    @FXML private TableColumn<Event, Integer> coordinatorsColumn;

    //Tickets Table
    @FXML private TableView<Event> ticketsTable;
    @FXML private TableColumn<Event, String> eventTName;
    @FXML private TableColumn<Event, Integer> quantityTColumn;
    @FXML private TableColumn<Event, Integer> soldColumn;
    @FXML private TableColumn<Event, Integer> availableColumn;


    @FXML private Label welcomeUserLabel;

    private boolean isMenuOpen = false;

    private EventCoordinator user;
    private OpenWindow openWindow;
    private Logic logic =  new Logic();
    private ObservableList<Event> eventsObservableList = FXCollections.observableArrayList();
    private Timeline timeLine;
    private int eventsUpdateCounter = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Events
        //Stage stage = (Stage) welcomeUserLabel.getScene().getWindow();
        //stage.setOnCloseRequest(event -> stopAutoRefresh());
        setUpEventManagementTableView();
        setUpTicketsManagementTable();
        this.timeLine = new Timeline(new KeyFrame(Duration.seconds(14), e -> refreshTable()));
        this.timeLine.setCycleCount(Timeline.INDEFINITE);
        restoreTimeLine();
        //Tickets


    }

    private void stopAutoRefresh() {
        this.timeLine.stop();
    }

    private void setUpEventManagementTableView(){
        eventTable.setItems(eventsObservableList);

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        coordinatorsColumn.setCellValueFactory(new PropertyValueFactory<>("coordinators"));
        ticketColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
    }

    private void setUpTicketsManagementTable(){
        ticketsTable.setItems(eventsObservableList);

        eventTName.setCellValueFactory(new PropertyValueFactory<>("name"));
        //ticketName.setCellValueFactory(new PropertyValueFactory<>("ticketType"));
        //priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityTColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        soldColumn.setCellValueFactory(new PropertyValueFactory<>("soldAmount"));
        availableColumn.setCellValueFactory(new PropertyValueFactory<>("availableTickets"));
    }

    @Override
    public void refreshTable() {
        updateAllTables();
        //updateTicketTable();
    }

    public CoordinatorController(){
        this.openWindow = new OpenWindow();
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
    //comment
    private void updateAllTables() {
        int localCounter = ++this.eventsUpdateCounter;
        Task<List<Event>> getEvents = new Task<List<Event>>() {
            @Override
            protected List<Event> call() throws Exception {
                return logic.getCorEvents(user.getId());
            }
        };
        getEvents.setOnSucceeded(event -> {
            if(localCounter < eventsUpdateCounter){
                return;
            }
            List<Event> events = (List<Event>) getEvents.getValue();
            eventsObservableList.setAll(events);
            this.user.setEvents(events);
            System.out.println(this.user.getEvents());
        });
        getEvents.setOnFailed(event -> {
            Throwable cause = getEvents.getException();
            System.out.println(cause.getMessage());
        });

        new Thread(getEvents).start();
    }

//    private void updateTicketTable(){
//        try {
//            List<TicketEvent> tickets = logic.getTicketsByCoordinator(user.getId());
//
//            ObservableList<TicketEvent> observableList = FXCollections.observableList(tickets);
//
//            ticketsTable.setItems(observableList);
//        }
//        catch (MyException ex){
//            System.out.println("error label needs to be filled with this: " + ex.getMessage());
//        }
//    }

    public void onEventManClick() {
        switchTab("eventManagementBox");
        //updateEventTable();
    }

    public void onTicketManClick() {
        switchTab("ticketManagementBox");
        //updateTicketTable();
    }

    public void onCreateEvent(){
        try{
            stopAutoRefresh();
            Object obj = openWindow.openNewWindow("create-event-view.fxml", "Create Event", true);
            EventController eController = (EventController) obj;
            eController.setController(this);
            eController.setUser(this.user);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void editEvent(){
        Event selectedEvent = eventTable.getSelectionModel().getSelectedItem();
        if (selectedEvent == null) return;
        try{
            stopAutoRefresh();
            Object obj = openWindow.openNewWindow("create-event-view.fxml", "Edit Event", true);
            EventController eController = (EventController) obj;
            eController.setController(this);
            eController.setEvent(selectedEvent);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onAddTicket(){
        try{
            stopAutoRefresh();
            Object obj = openWindow.openNewWindow("new-ticket-view.fxml", "Create New Ticket", true);
            TicketController tController = (TicketController) obj;
            tController.setController(this);
            tController.setUser(this.user);
        }catch (IOException e) {
            restoreTimeLine();
        }
    }

    public void onSellTicket(){
        Event selectedEvent = ticketsTable.getSelectionModel().getSelectedItem();
        if (selectedEvent == null) return;
        try{
            stopAutoRefresh();
            Object obj = openWindow.openNewWindow("sell-ticket-view.fxml", "Sell Ticket", true);
            SellTicketController stController = (SellTicketController) obj;
            stController.setController(this);
            stController.setEvent(selectedEvent);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("hello");
    }

    public void setUser(EventCoordinator user){
        this.user = user;
        this.welcomeUserLabel.setText("Welcome " + user.getUsername());
        refreshTable();
    }

    @FXML
    private void assignCoordinator(){
        Event selectedEvent = eventTable.getSelectionModel().getSelectedItem();
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
            try {
                stopAutoRefresh();
                AssignEventCoordinatorController ctr = (AssignEventCoordinatorController) openWindow.openAssignCoordinatorView(selectedEvent, getAvailableEventCoordinatorsTask.getValue());
                ctr.setController(this);
            } catch (Exception ex) {
                restoreTimeLine();
            }

        });
        getAvailableEventCoordinatorsTask.setOnFailed(e -> {
            Throwable ex = getAvailableEventCoordinatorsTask.getException();
            System.out.println(ex.getMessage());
        });
        new Thread(getAvailableEventCoordinatorsTask).start();
    }

    public void restoreTimeLine(){
        this.timeLine.play();
    }
}
