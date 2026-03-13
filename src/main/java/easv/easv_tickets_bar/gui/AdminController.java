package easv.easv_tickets_bar.gui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class AdminController {
    @FXML private VBox sideBar;
    @FXML private StackPane contentBox;

    private boolean isMenuOpen = false;


    @FXML
    public void menuSlide(){
        double targetWidth = isMenuOpen ? 0.0 : 210.0;
        KeyValue keyValue = new KeyValue(sideBar.prefWidthProperty(), targetWidth);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(300), keyValue);
        Timeline timeline = new Timeline(keyFrame);

        timeline.setOnFinished(e -> isMenuOpen = !isMenuOpen);
        timeline.play();
    }

    @FXML
    public void switchTab(String name){
        for(Node n : contentBox.getChildren()){
            if (n instanceof VBox && n.getId().equals(name)) n.setVisible(true);
            else n.setVisible(false);
        }
    }

    public void onUserManClick(){
        switchTab("userManagementBox");
    }

    public void onEventManClick(){
        switchTab("eventManagementBox");
    }


}
