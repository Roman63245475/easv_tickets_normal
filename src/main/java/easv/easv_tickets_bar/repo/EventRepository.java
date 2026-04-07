package easv.easv_tickets_bar.repo;

import easv.easv_tickets_bar.CustomExceptions.DataBaseConnectionException;
import easv.easv_tickets_bar.CustomExceptions.DuplicateException;
import easv.easv_tickets_bar.be.Event;
import easv.easv_tickets_bar.dal.EventAccessObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class EventRepository {

    private EventAccessObject eao;

    public EventRepository(){
        this.eao = new EventAccessObject();
    }
    public List<Event> getAllEvents() throws DataBaseConnectionException {
        return eao.getAllEvents();
    }

    public void deleteSelectedEvent(Event selectedEvent) throws DataBaseConnectionException {
        eao.deleteSelectedEvent(selectedEvent);
    }

    public int createNewEvent(String name, LocalTime startTime, LocalTime endTime, String location, String venue, String guidance, String notes, int capacity, LocalDate startDate, LocalDate endDate) throws DataBaseConnectionException, DuplicateException {
        return eao.createNewEvent(name, startTime, endTime, location, venue, guidance, notes, capacity, startDate, endDate);
    }

    public List<Event> getEvents(int userId) throws DataBaseConnectionException {
        return eao.getEvents(userId);
    }

    public void updateEvent(int id, String name, LocalDateTime startDateTime, LocalDateTime endDateTime, String location, String venue, String guidance, String notes, int capacity) throws DataBaseConnectionException {
        eao.updateEvent(id, name, startDateTime, endDateTime, location, venue, guidance, notes, capacity);
    }
}
