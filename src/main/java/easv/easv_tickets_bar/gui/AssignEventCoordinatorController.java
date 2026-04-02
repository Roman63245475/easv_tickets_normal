package easv.easv_tickets_bar.gui;

import easv.easv_tickets_bar.be.Event;
import easv.easv_tickets_bar.be.EventCoordinator;
import easv.easv_tickets_bar.bll.Logic;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class AssignEventCoordinatorController implements Initializable, IAssignCoordinator, IPanel {

    @FXML private ListView<EventCoordinator> eventCoordinatorListView;
    private ObservableList<EventCoordinator> eventCoordinators = FXCollections.observableArrayList();
    private Event event;
    private Logic logic = new Logic();
    private IRefreshable controller;



    public void setCoordinators(List<EventCoordinator> eventCoordinators) {
        this.eventCoordinators.setAll(eventCoordinators);
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        eventCoordinatorListView.setItems(eventCoordinators);
    }

    @FXML
    private void AssignClick() {
        EventCoordinator selectedCoordinator = eventCoordinatorListView.getSelectionModel().getSelectedItem();
        if (selectedCoordinator == null) {
            return;
        }
        Task<Void> assignCoordinatorTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                logic.assingCoordinator(event, selectedCoordinator);
                return null;
            }
        };
        assignCoordinatorTask.setOnSucceeded(event -> {
            controller.refreshTable();
            controller.restoreTimeLine();
           Stage st = (Stage) eventCoordinatorListView.getScene().getWindow();
           st.close();
        });
        assignCoordinatorTask.setOnFailed(event -> {
            Throwable cause = assignCoordinatorTask.getException();
            System.out.println(cause.getMessage());
        });
        new Thread(assignCoordinatorTask).start();
    }

    @FXML
    private void escapeClick() {
        onClose();
        Stage stage = (Stage) eventCoordinatorListView.getScene().getWindow();
        stage.close();
    }

    @Override
    public void setController(IRefreshable controller) {
        this.controller = controller;
        Stage stage = (Stage) eventCoordinatorListView.getScene().getWindow();
        stage.setOnCloseRequest(event -> {onClose();});
    }

    @Override
    public void onClose() {
        controller.restoreTimeLine();
    }
}
