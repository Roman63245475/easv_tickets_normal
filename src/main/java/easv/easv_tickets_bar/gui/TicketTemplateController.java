package easv.easv_tickets_bar.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;


public class TicketTemplateController {
    @FXML private Label eventName;
    @FXML private Label eventStartDate;
    @FXML private Label eventEndDate;
    @FXML private Label eventStartTime;
    @FXML private Label eventEndTime;
    @FXML private Label locationField;
    @FXML private Label venueField;
    @FXML private Label locationGuidanceField;
    @FXML private Label notesField;
    @FXML private ImageView qrCode;

    public void setData(String name, String startTime, String endTime, String startDate, String endDate, String location, String venue, String locationGuidance, String notes, Image qrCode){
        this.eventName.setText(name);
        this.eventStartTime.setText(startTime);
        this.eventEndTime.setText(endTime);
        this.eventStartDate.setText(startDate);
        this.eventEndDate.setText(endDate);
        this.locationField.setText(location);
        this.venueField.setText(venue);
        this.locationGuidanceField.setText(locationGuidance);
        this.notesField.setText(notes);
        this.qrCode.setImage(qrCode);
    }
}
