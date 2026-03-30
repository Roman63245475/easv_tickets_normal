package easv.easv_tickets_bar.gui;

import easv.easv_tickets_bar.CustomExceptions.DataBaseConnectionException;
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
import java.util.ResourceBundle;

public class EventController implements Initializable, IUserPanel, IPanel {

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

    private IRefreshable controller;
    private Logic logic = new Logic();
    private User user;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
