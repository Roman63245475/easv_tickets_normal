package easv.easv_tickets_bar.gui;

import easv.easv_tickets_bar.be.Event;
import easv.easv_tickets_bar.be.EventCoordinator;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

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

    public Object openAssignCoordinatorView(Event selectedEvent, List<EventCoordinator> eventCoordinators) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("available_event_coordinators.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(loader.load());
        IAssignCoordinator controller = loader.getController();
        controller.setCoordinators(eventCoordinators);
        controller.setEvent(selectedEvent);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
        return loader.getController();
    }
}
