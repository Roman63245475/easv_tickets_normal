package easv.easv_tickets_bar.gui;

import easv.easv_tickets_bar.be.Event;
import easv.easv_tickets_bar.be.Ticket;
import easv.easv_tickets_bar.bll.Logic;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SellTicketController implements Initializable, IPanel {

    @FXML private Label errorLabel;

    @FXML private TextField firstNameField;
    @FXML private TextField secondNameField;
    @FXML private TextField emailField;
    @FXML private TextField amountField;
    @FXML private ComboBox<Ticket> ticketTypeBox;
    @FXML private Label totalSum;
    @FXML private Label priceLabel;
    @FXML private Button cancelButton;
    @FXML private Button sendTicketButton;

    private Logic logic = new Logic();
    private IRefreshable cController;
    private ObservableList<Ticket> tickets =  FXCollections.observableArrayList();
    private Event event;

    private Task<List<String>> dbTask;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UIHelper.numberInputValidator(amountField);
        ticketTypeBox.setItems(tickets);

        amountField.textProperty().addListener((observable, oldValue, newValue) -> {
            updatePrice();
        });

        ticketTypeBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            updatePrice();
        });
    }

    private void updatePrice(){
        Ticket selectedTicket = ticketTypeBox.getSelectionModel().getSelectedItem();
        String amount = amountField.getText();

        if (selectedTicket == null) {
            priceLabel.setText("Price for one: 0kr.");
            totalSum.setText("Total: 0kr.");
            return;
        }

        priceLabel.setManaged(true);
        totalSum.setManaged(true);
        priceLabel.setText("Price for one: " + selectedTicket.getPrice() + "kr.");

        if (amountField == null || amount.isBlank()){
            totalSum.setText("Total: 0kr.");
            return;
        }

        try{
            int quantity = Integer.parseInt(amountField.getText());
            totalSum.setText("Total: " + selectedTicket.getPrice() * quantity + "kr.");
        } catch (NumberFormatException e) {
            totalSum.setText("Total: Max limit reached");
        }
    }


    @FXML
    private void onCancelClick(){
        onClose();
        Stage stage = (Stage) this.amountField.getScene().getWindow();
        stage.close();
    }



    public void onSellTicket(ActionEvent actionEvent) {
        String name = firstNameField.getText();
        String secondName = secondNameField.getText();
        String email = emailField.getText();
        String quantity = amountField.getText();
        Ticket ticketType = ticketTypeBox.getSelectionModel().getSelectedItem();
        Button btn = (Button) actionEvent.getSource();

        dbTask = new Task<>() {
            @Override
            protected List<String> call() throws Exception {
                return logic.sellTicket(event, ticketType, name, secondName, email, quantity);
            }
        };

        cancelButton.disableProperty().bind(dbTask.runningProperty());
        sendTicketButton.disableProperty().bind(dbTask.runningProperty());

        dbTask.setOnSucceeded(event -> {
            Stage stage = (Stage) btn.getScene().getWindow();
            onClose();
            stage.close();
            startBackgroundTask(this.event, name, secondName, email, dbTask.getValue());
        });

        dbTask.setOnFailed(event -> {
            errorLabel.setOpacity(1);
            errorLabel.setText(dbTask.getException().getMessage());
        });

        new Thread(dbTask).start();
    }

    private void startBackgroundTask(Event event, String name, String secondName, String email, List<String> ids) {
        Task<Void> sendEmailsTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                logic.sendTickets(event, name, secondName, email, ids);
                return null;
            }
        };
        new Thread(sendEmailsTask).start();
    }

    public void setEvent(Event event){
        Task<List<Ticket>> getTickets = new Task<List<Ticket>>() {

            @Override
            protected List<Ticket> call() throws Exception {
                return logic.getAllTicketsByEvent(event);
            }
        };
        getTickets.setOnSucceeded(e -> {
            if (getTickets.getValue().isEmpty()){
                errorLabel.setOpacity(1);
                errorLabel.setText("No tickets are added yet");
            }
            else {
                tickets.setAll(getTickets.getValue());
            }

        });
        getTickets.setOnFailed(e -> {
            Throwable cause = getTickets.getException();
            cause.printStackTrace();
            errorLabel.setOpacity(1);
            errorLabel.setText(cause.getMessage());
        });
        this.event = event;
        new Thread(getTickets).start();
    }

    @Override
    public void setController(IRefreshable controller) {
        this.cController = controller;
        Stage stage = (Stage) firstNameField.getScene().getWindow();
        stage.setOnCloseRequest(e -> {
            if (dbTask != null && dbTask.isRunning()) {
                e.consume();
            }
            else{
                onClose();
            }
        });
    }

    @Override
    public void onClose() {
        cController.refreshTable();
        cController.restoreTimeLine();
    }
}
