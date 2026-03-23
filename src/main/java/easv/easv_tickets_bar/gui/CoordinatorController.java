package easv.easv_tickets_bar.gui;

import easv.easv_tickets_bar.CustomExceptions.DataBaseConnectionException;
import easv.easv_tickets_bar.be.Event;
import easv.easv_tickets_bar.be.User;
import easv.easv_tickets_bar.bll.Logic;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class CoordinatorController implements IUserPanel, IRefreshable, Initializable {
    @FXML private VBox sideBar;
    @FXML private StackPane contentBox;

    @FXML private TableView<Event> eventTable;
    @FXML private TableColumn<Event, Integer> idColumn;
    @FXML private TableColumn<Event, String> nameColumn;
    @FXML private TableColumn<Event, LocalDateTime> dateTimeColumn;
    @FXML private TableColumn<Event, String> locationColumn;
    @FXML private TableColumn<Event, String> statusColumn;
    @FXML private TableColumn<Event, Integer> ticketColumn;
    @FXML private TableColumn<Event, Integer> coordinatorsColumn;
    @FXML private TableColumn<Event, Void> actionsColumn;


    private boolean isMenuOpen = false;

    private User user;
    private OpenWindow openWindow;
    private Logic logic =  new Logic();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        coordinatorsColumn.setCellValueFactory(new PropertyValueFactory<>("coordinators"));
        ticketColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        actionsColumn.setCellFactory(param -> new TableCell<Event, Void>(){
            private final Region manageIcon = new Region();
            private final Region editIcon = new Region();
            private final Region deleteIcon = new Region();

            private final HBox actionButtons =  new HBox(15, manageIcon, editIcon, deleteIcon);

            {
                actionButtons.setAlignment(Pos.CENTER);
                deleteIcon.setStyle("-fx-pref-width: 20px; -fx-min-width: 20px; -fx-max-height: 20px; -fx-background-color: #cc4747; -fx-shape: 'M280-120q-33 0-56.5-23.5T200-200v-520h-40v-80h200v-40h240v40h200v80h-40v520q0 33-23.5 56.5T680-120H280Zm400-600H280v520h400v-520ZM360-280h80v-360h-80v360Zm160 0h80v-360h-80v360ZM280-720v520-520Z'");
                editIcon.setStyle("-fx-pref-width: 20px; -fx-min-width: 20px; -fx-max-height: 20px; -fx-background-color: #7470dc; -fx-shape: 'm490-527 37 37 217-217-37-37-217 217ZM200-200h37l233-233-37-37-233 233v37Zm355-205L405-555l167-167-29-29-219 219-56-56 218-219q24-24 56.5-24t56.5 24l29 29 50-50q12-12 28.5-12t28.5 12l93 93q12 12 12 28.5T828-678L555-405ZM270-120H120v-150l285-285 150 150-285 285Z'");
                manageIcon.setStyle("-fx-pref-width: 20px; -fx-min-width: 20px; -fx-max-height: 20px; -fx-background-color: #9eca6d; -fx-shape: 'M40-160v-112q0-34 17.5-62.5T104-378q62-31 126-46.5T360-440q66 0 130 15.5T616-378q29 15 46.5 43.5T680-272v112H40Zm720 0v-120q0-44-24.5-84.5T666-434q51 6 96 20.5t84 35.5q36 20 55 44.5t19 53.5v120H760ZM247-527q-47-47-47-113t47-113q47-47 113-47t113 47q47 47 47 113t-47 113q-47 47-113 47t-113-47Zm466 0q-47 47-113 47-11 0-28-2.5t-28-5.5q27-32 41.5-71t14.5-81q0-42-14.5-81T544-792q14-5 28-6.5t28-1.5q66 0 113 47t47 113q0 66-47 113ZM120-240h480v-32q0-11-5.5-20T580-306q-54-27-109-40.5T360-360q-56 0-111 13.5T140-306q-9 5-14.5 14t-5.5 20v32Zm296.5-343.5Q440-607 440-640t-23.5-56.5Q393-720 360-720t-56.5 23.5Q280-673 280-640t23.5 56.5Q327-560 360-560t56.5-23.5ZM360-240Zm0-400Z'");
            }

            @Override
            protected void updateItem(Void item, boolean empty){
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                }else{
                    setGraphic(actionButtons);
                }
            }
        });
    }

    @Override
    public void refreshTable() throws DataBaseConnectionException {
        updateEventTable();
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

    private void updateEventTable() throws DataBaseConnectionException {
        List<Event> events = logic.getCorEvents(user.getId());

        ObservableList<Event> observableList = FXCollections.observableList(events);

        eventTable.setItems(observableList);
    }

    public void onEventManClick() throws DataBaseConnectionException {
        switchTab("eventManagementBox");
        updateEventTable();
    }



    public void onTicketManClick(){
        switchTab("ticketManagementBox");
    }

    public void onCreateEvent(){
        try{
            openWindow.openNewWindow("create-event-view.fxml", "Create Event", this.user, true, this);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onAddTicket(){
        try{
            openWindow.openNewWindow("new-ticket-view.fxml", "Create New Ticket", this.user, true, this);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onSellTicket(){
        try{
            openWindow.openNewWindow("sell-ticket-view.fxml", "Sell Ticket", this.user, true,this);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setUser(User user){
        this.user = user;
        try {
            updateEventTable();
        }
        catch (DataBaseConnectionException e) {
            System.out.println("kapec");
        }

    }
}
