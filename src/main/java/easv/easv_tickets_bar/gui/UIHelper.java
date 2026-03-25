package easv.easv_tickets_bar.gui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.function.UnaryOperator;

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
    public static void timeInputValidator(TextField input) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getControlNewText();
            if (text.matches("^$|^([0-2]?|([0-1]\\d|2[0-3])|([0-1]\\d|2[0-3])(:|:[0-5]\\d?)?)$") && text.length() <= 5) {
                return change;
            }

            return null;
        };

        input.setTextFormatter(new TextFormatter<>(filter));
    }

    @FXML
    public static void numberInputValidator(TextField input) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getControlNewText();
            if (text.matches("[0-9]*")){
                return change;
            }
            return null;
        };
        input.setTextFormatter(new TextFormatter<>(filter));
    }

    @FXML
    public static void priceInputValidator(TextField input) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getControlNewText();
            if (text.matches("^[0-9]*(\\.[0-9]{0,2})?$")) {
                return change;
            }
            return null;
        };
        input.setTextFormatter(new TextFormatter<>(filter));
    }

    @FXML
    public static void emailInputValidator(TextField input) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getControlNewText();
            if (text.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9+_.-]+\\.[A-Za-z]{2,6}$")){
                return change;
            }
            return null;
        };
        input.setTextFormatter(new TextFormatter<>(filter));
    }
}
