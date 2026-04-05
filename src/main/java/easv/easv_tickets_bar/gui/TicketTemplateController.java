package easv.easv_tickets_bar.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;


public class TicketTemplateController {
    @FXML private Label eventName;
    @FXML private Label eventStartDate;
    @FXML private Label eventEndDate;
    @FXML private Label locationField;
    @FXML private Label venueField;
    @FXML private Label locationGuidanceField;
    @FXML private Label notesField;

    public void setData(String name, String startDateTime, String endDateTime, String location, String venue, String locationGuidance, String notes){
        this.eventName.setText(name);
        this.eventStartDate.setText(startDateTime);
        this.eventEndDate.setText(endDateTime);
        this.locationField.setText(location);
        this.venueField.setText(venue);
        this.locationGuidanceField.setText(locationGuidance);
        this.notesField.setText(notes);
    }
}
