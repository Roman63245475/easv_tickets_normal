package easv.easv_tickets_bar.gui;

import easv.easv_tickets_bar.be.User;
import easv.easv_tickets_bar.bll.Logic;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class SellTicketController implements IPanel, Initializable {

    @FXML private TextField nameInput;
    @FXML private TextField emailInput;
    @FXML private Spinner<Integer> quantityInput;

    @FXML private Label totalInput;

    private Logic logic = new Logic();

    private IRefreshable controller;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UIHelper.emailInputValidator(emailInput);
    }

    @Override
    public void setController(IRefreshable controller) {
        this.controller = controller;
    }

    public void onSellTicket(){
        String name = nameInput.getText();
        String email = emailInput.getText();
        int quantity = quantityInput.getValue();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                //logic.
                return null;
            }
        };

    }
}
