package easv.easv_tickets_bar.gui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

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

    @FXML
    public static void openNewWindow(String fxmlPath, String title){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(UIHelper.class.getResource(fxmlPath));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
