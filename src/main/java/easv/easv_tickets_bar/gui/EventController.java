package easv.easv_tickets_bar.gui;

import easv.easv_tickets_bar.CustomExceptions.DataBaseConnectionException;
import easv.easv_tickets_bar.be.Event;
import easv.easv_tickets_bar.be.User;
import easv.easv_tickets_bar.bll.Logic;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class EventController implements Initializable, IUserPanel, IPanel {

    @FXML private Label titleLabel;
    @FXML private Label errorLabel;

    @FXML private TextField nameInput;
    @FXML private TextField startTimeInput;
    @FXML private TextField endTimeInput;
    @FXML private DatePicker startDateInput;
    @FXML private DatePicker endDateInput;
    @FXML private TextField locationInput;
    @FXML private TextField venueInput;
    @FXML private TextField guidanceInput;
    @FXML private TextField notesInput;
    @FXML private TextField capacityInput;

    @FXML private Button finishBtn;

    private IRefreshable controller;
    private Logic logic = new Logic();
    private Event event;
    private User user;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startDateInput.getEditor().setDisable(true);
        endDateInput.getEditor().setDisable(true);
        UIHelper.timeInputValidator(startTimeInput);
        UIHelper.timeInputValidator(endTimeInput);
        UIHelper.numberInputValidator(capacityInput);
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public void setController(IRefreshable controller) {
        this.controller = controller;
        Stage stage = (Stage) finishBtn.getScene().getWindow();
        stage.setOnCloseRequest((event) -> {onClose();});
    }

    @Override
    public void onClose() {
        controller.restoreTimeLine();
    }

    public void setEvent(Event selectedEvent) {
        this.event = selectedEvent;
        titleLabel.setText("Edit Event");
        finishBtn.setText("Save Changes");

        nameInput.setText(selectedEvent.getName());

        startTimeInput.setText(selectedEvent.getStartTime());
        endTimeInput.setText(selectedEvent.getEndTime());

        startDateInput.setValue(selectedEvent.getStartDate());
        endDateInput.setValue(selectedEvent.getEndDate());

        locationInput.setText(selectedEvent.getLocation());
        venueInput.setText(selectedEvent.getVenue());
        guidanceInput.setText(selectedEvent.getLocationGuidance());
        notesInput.setText(selectedEvent.getNotes());
        capacityInput.setText(selectedEvent.getCapacity() + "");

        finishBtn.setOnAction(e -> {
            editEvent();
        });
    }

    public void onCreateEvent(ActionEvent event) {
        String name = nameInput.getText();

        String startTime = startTimeInput.getText();
        String endTime = endTimeInput.getText();
        LocalDate startDate = startDateInput.getValue();
        LocalDate endDate = endDateInput.getValue();

        String location = locationInput.getText();
        String venue = venueInput.getText();
        String guidance = guidanceInput.getText();
        String notes = notesInput.getText();
        String capacity = capacityInput.getText();

        Button btn = (Button) event.getSource();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                logic.createNewEvent(user.getId(), name, startTime, endTime, startDate, endDate, location, venue, guidance, notes, capacity);
                return null;
            }
        };

        btn.disableProperty().bind(task.runningProperty());

        task.setOnSucceeded(e -> {
            Stage stage = (Stage) btn.getScene().getWindow();
            controller.refreshTable();
            controller.restoreTimeLine();
            stage.close();
        });

        task.setOnFailed(e -> {
            errorLabel.setManaged(true);
            errorLabel.setText(task.getException().getMessage());
        });

        Thread thread = new Thread(task);
        thread.start();
    }

    public void editEvent() {
        String name = nameInput.getText();

        String startTime = startTimeInput.getText();
        String endTime = endTimeInput.getText();
        LocalDate startDate = startDateInput.getValue();
        LocalDate endDate = endDateInput.getValue();

        String location = locationInput.getText();
        String venue = venueInput.getText();
        String guidance = guidanceInput.getText();
        String notes = notesInput.getText();
        String capacity = capacityInput.getText();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                logic.updateEvent(event.getId(), name, startTime, endTime, startDate, endDate, location, venue, guidance, notes, capacity);
                return null;
            }
        };
        finishBtn.disableProperty().bind(task.runningProperty());
        task.setOnSucceeded(e -> {
            controller.refreshTable();
            controller.restoreTimeLine();
            Stage stage = (Stage) finishBtn.getScene().getWindow();
            stage.close();
        });

        task.setOnFailed(e -> {
            errorLabel.setManaged(true);
            errorLabel.setText(task.getException().getMessage());
        });

        Thread thread = new Thread(task);
        thread.start();

    }
}
