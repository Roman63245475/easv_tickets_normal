package easv.easv_tickets_bar.repo;

import easv.easv_tickets_bar.CustomExceptions.DataBaseConnectionException;
import easv.easv_tickets_bar.be.Event;
import easv.easv_tickets_bar.be.EventCoordinator;
import easv.easv_tickets_bar.dal.EventCoordinatorAccessObject;

import java.util.ArrayList;
import java.util.List;

public class EventCoordinatorRepository {
    EventCoordinatorAccessObject ecao;

    public EventCoordinatorRepository() {
        this.ecao = new EventCoordinatorAccessObject();
    }

    public List<EventCoordinator> getAvailableEventCoordinators(Event selectedEvent) throws DataBaseConnectionException {
        return ecao.getAvailableEventCoordinators(selectedEvent);
    }

    public void assignCoordinator(Event event, EventCoordinator selectedCoordinator) throws DataBaseConnectionException {
        ecao.assignCoordinator(selectedCoordinator.getId(), event.getId());
    }
}
