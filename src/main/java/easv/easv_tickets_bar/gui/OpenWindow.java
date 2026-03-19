package easv.easv_tickets_bar.gui;

import easv.easv_tickets_bar.be.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class OpenWindow {

    public void openNewWindow(String fileName, String title, User user, Boolean mod) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fileName));
        Stage stage = new Stage();
        Scene scene = new Scene(loader.load());
        IUserPanel controller = loader.getController();
        controller.setUser(user);
        stage.setScene(scene);
        if (mod){
            stage.initModality(Modality.APPLICATION_MODAL);
        }
        stage.setTitle(title);
        stage.show();
    }
}
