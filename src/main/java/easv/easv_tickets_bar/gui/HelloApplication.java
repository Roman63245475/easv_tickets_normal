package easv.easv_tickets_bar.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("coordinator-view.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
        //scene.setFill(Color.TRANSPARENT);
        //primaryStage.initStyle(StageStyle.TRANSPARENT);
    }
}
