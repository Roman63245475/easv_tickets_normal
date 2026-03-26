package easv.easv_tickets_bar.gui;

import easv.easv_tickets_bar.be.Event;
import easv.easv_tickets_bar.be.EventCoordinator;

import java.util.List;

public interface IAssignCoordinator {
    public void setEvent(Event event);
    public void setCoordinators(List<EventCoordinator> coordinators);
}
