package easv.easv_tickets_bar.gui;

import easv.easv_tickets_bar.be.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class OpenWindow {

    public Object openNewWindow(String fileName, String title, Boolean mod) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fileName));
        Stage stage = new Stage();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        if (mod){
            stage.initModality(Modality.APPLICATION_MODAL);
        }
        stage.setTitle(title);
        stage.show();
        return loader.getController();
    }
}
