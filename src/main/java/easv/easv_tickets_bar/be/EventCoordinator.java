package easv.easv_tickets_bar.be;


import java.util.List;

public class EventCoordinator extends User{

    private List<Event> events;
    public EventCoordinator(int id, String username, String password, List<Event> events) {
        super(id, username, password);
        this.events = events;
    }
    public EventCoordinator(int id, String username, String password) {
        super(id, username, password);
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public Role getRole(){
        return Role.EVENT_COORDINATOR;
    }

    @Override
    public String toString() {
        return this.username;
    }

}
