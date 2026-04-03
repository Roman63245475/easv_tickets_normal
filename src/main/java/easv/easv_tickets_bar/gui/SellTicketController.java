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
    @FXML private Label EventLabel;

    @FXML private TextField firstNameField;
    @FXML private TextField secondNameField;
    @FXML private TextField emailField;
    @FXML private TextField amountField;
    @FXML private ComboBox<Ticket> ticketTypeBox;
    @FXML private Label totalSum;

    private Logic logic = new Logic();
    private IRefreshable cController;
    private ObservableList<Ticket> tickets =  FXCollections.observableArrayList();
    private Event event;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UIHelper.numberInputValidator(amountField);
        ticketTypeBox.setItems(tickets);
    }




    public void onSellTicket(ActionEvent actionEvent) {
        String name = firstNameField.getText();
        String secondName = secondNameField.getText();
        String email = emailField.getText();
        String quantity = amountField.getText();
        Ticket ticketType = ticketTypeBox.getSelectionModel().getSelectedItem();
        Button btn = (Button) actionEvent.getSource();

        Task<Void> sellTicketTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                logic.sellTicket(event, ticketType, name, secondName, email, quantity);
                return null;
            }
        };

        sellTicketTask.setOnSucceeded(event -> {
            Stage stage = (Stage) btn.getScene().getWindow();
            cController.refreshTable();
            cController.restoreTimeLine();
            stage.close();
        });

        sellTicketTask.setOnFailed(event -> {
            errorLabel.setOpacity(1);
            errorLabel.setText(sellTicketTask.getException().getMessage());
        });

        new Thread(sellTicketTask).start();
    }

//    public void setTicket(TicketEvent ticket) {
//        this.ticket = ticket;
//        EventLabel.setText("Sell Ticket - " + ticket.getTicketType());
//    }


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
        stage.setOnCloseRequest(e->onClose());
    }

    @Override
    public void onClose() {
        cController.restoreTimeLine();
    }
}
