package easv.easv_tickets_bar.gui;

import easv.easv_tickets_bar.CustomExceptions.DataBaseConnectionException;
import easv.easv_tickets_bar.be.Event;
import easv.easv_tickets_bar.be.User;
import easv.easv_tickets_bar.bll.Logic;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TicketController implements IPanel, IUserPanel, Initializable {

    @FXML private Label errorLabel;

    @FXML private ChoiceBox<Event> eventChoice;
    @FXML private TextField nameInput;
    @FXML private TextField priceInput;
    @FXML private Spinner quantityInput;

    private User user;
    private Logic logic = new Logic();
    IRefreshable controller;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UIHelper.priceInputValidator(priceInput);
        UIHelper.numberInputValidator(quantityInput.getEditor());
    }

    @Override
    public void setController(IRefreshable controller) {
        this.controller = controller;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
        updateChoices();
    }

    public void updateChoices(){
        Task<List<Event>> task = new Task<>(){
            @Override
            protected List<Event> call() throws Exception {
                return logic.getCorEvents(user.getId());
            }
        };

        task.setOnSucceeded(event -> {
            eventChoice.getItems().setAll(task.getValue());
        });

        task.setOnFailed(event -> {
            System.out.println(task.getException().getMessage());
        });

        Thread thread = new Thread(task);
        thread.start();
    }

    public void createNewTicket(ActionEvent actionEvent) {
        Event chosenEvent = eventChoice.getValue();
        String name = nameInput.getText();
        String price = priceInput.getText();
        String quantity = quantityInput.getEditor().getText();

        Button btn = (Button) actionEvent.getSource();

        Task<Void> task = new Task<>(){
            @Override
            protected Void call() throws Exception {
                logic.createTicket(chosenEvent.getId(), chosenEvent.getCapacity(), name, price, quantity);
                return null;
            }
        };

        btn.disableProperty().bind(task.runningProperty());

        task.setOnSucceeded(event -> {
            Stage stage =  (Stage) btn.getScene().getWindow();
            try{
                controller.refreshTable();
            } catch (DataBaseConnectionException e) {
                throw new RuntimeException(e);
            }
            stage.close();
        });

        task.setOnFailed(event -> {
            errorLabel.setManaged(true);
            errorLabel.setText(task.getException().getMessage());
        });

        Thread thread = new Thread(task);
        thread.start();
    }
}
