package easv.easv_tickets_bar.gui;

import easv.easv_tickets_bar.CustomExceptions.DataBaseConnectionException;
import easv.easv_tickets_bar.be.TicketEvent;
import easv.easv_tickets_bar.bll.Logic;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class SellTicketController implements Initializable {

    @FXML private Label errorLabel;
    @FXML private Label ticketTypeLabel;

    @FXML private TextField nameInput;
    @FXML private TextField emailInput;
    @FXML private Spinner<Integer> quantityInput;

    @FXML private Label totalInput;

    private Logic logic = new Logic();
    private CoordinatorController cController;
    private TicketEvent ticket;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public boolean isValidEmail(){
        return emailInput.getText().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9+_.-]+\\.[A-Za-z]{2,6}$");
    }

    public void onSellTicket(ActionEvent actionEvent) {
        if (!isValidEmail()){
            errorLabel.setManaged(true);
            errorLabel.setText("Invalid Email.");
            return;
        }
        String name = nameInput.getText();
        String email = emailInput.getText();
        int quantity = quantityInput.getValue();

        Button btn = (Button) actionEvent.getSource();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                logic.sellTicket(ticket.getId(), name, email, quantity, ticket.getAvailable());
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            Stage stage = (Stage) btn.getScene().getWindow();
            try{
                cController.refreshTable();
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

    public void setTicket(TicketEvent ticket) {
        this.ticket = ticket;
        ticketTypeLabel.setText("Sell Ticket - " + ticket.getTicketType());
    }

    public void setController(CoordinatorController cController) {
        this.cController = cController;
    }
}
