package easv.easv_tickets_bar.gui;

import easv.easv_tickets_bar.be.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class OpenWindow {

    public void openNewWindow(String fileName, String title, User user, Boolean mod, IRefreshable parent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fileName));
        Stage stage = new Stage();
        Scene scene = new Scene(loader.load());
        Object controller = loader.getController();
        if (controller instanceof IUserPanel){
            ((IUserPanel)controller).setUser(user);
        }
        if (controller instanceof IPanel && parent != null){
            ((IPanel)controller).setController(parent);
        }
        stage.setScene(scene);
        if (mod){
            stage.initModality(Modality.APPLICATION_MODAL);
        }
        stage.setTitle(title);
        stage.show();
    }
}
