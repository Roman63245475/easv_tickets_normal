package easv.easv_tickets_bar.repo;

import easv.easv_tickets_bar.CustomExceptions.DataBaseConnectionException;
import easv.easv_tickets_bar.be.Event;
import easv.easv_tickets_bar.dal.EventAccessObject;

import java.util.List;

public class EventRepository {

    private EventAccessObject eao;

    public EventRepository(){
        this.eao = new EventAccessObject();
    }
    public List<Event> getAllEvents() throws DataBaseConnectionException {
        return eao.getAllEvents();
    }
}
