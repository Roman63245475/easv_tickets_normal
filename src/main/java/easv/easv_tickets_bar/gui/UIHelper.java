package easv.easv_tickets_bar.gui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class UIHelper {
    @FXML
    public static void sideBarAnimation(boolean isMenuOpen, VBox sideBar, Runnable r1) {
        double targetWidth = isMenuOpen ? 0.0 : 210.0;
        KeyValue keyValue = new KeyValue(sideBar.prefWidthProperty(), targetWidth);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(300), keyValue);
        Timeline timeline = new Timeline(keyFrame);

        timeline.setOnFinished(e -> r1.run());
        timeline.play();
    }
}
