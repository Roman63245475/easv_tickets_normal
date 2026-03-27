package easv.easv_tickets_bar.bll;
import easv.easv_tickets_bar.CustomExceptions.DataBaseConnectionException;
import easv.easv_tickets_bar.CustomExceptions.DuplicateException;
import easv.easv_tickets_bar.CustomExceptions.LoginException;
import easv.easv_tickets_bar.be.Event;
import easv.easv_tickets_bar.be.TicketEvent;
import easv.easv_tickets_bar.be.EventCoordinator;
import easv.easv_tickets_bar.be.User;
import easv.easv_tickets_bar.dal.EventCoordinatorAccessObject;
import easv.easv_tickets_bar.dal.EventAccessObject;
import easv.easv_tickets_bar.dal.TicketAccessObject;
import easv.easv_tickets_bar.dal.UserAccessObject;
import easv.easv_tickets_bar.be.Role;
import easv.easv_tickets_bar.repo.EventCoordinatorRepository;
import easv.easv_tickets_bar.repo.EventRepository;
import easv.easv_tickets_bar.repo.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


public class Logic {
    UserAccessObject uao = new UserAccessObject();
    EventAccessObject eventDAO = new EventAccessObject();
    TicketAccessObject ticketDAO = new TicketAccessObject();
    EventCoordinatorAccessObject eventCorDAO = new EventCoordinatorAccessObject();
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    UserRepository userRepo = new UserRepository();
    EventRepository eventRepo = new EventRepository();
    EventCoordinatorRepository eventCoordinatorRepo = new EventCoordinatorRepository();


    public User login(String username, String password) throws DataBaseConnectionException, LoginException {
        User user = userRepo.getFullUser(username);
        if (passwordEncoder.matches(password, user.getPassword())){
            return user;
        }
        else {
            throw new LoginException();
        }
    }

    public void createUser(String username, String password, Role role) throws DataBaseConnectionException, DuplicateException {
        String hashed_password = passwordEncoder.encode(password);
        userRepo.createUser(username, hashed_password, role);
    }

    public boolean isInvalidString(String text){
        return text == null || text.trim().isEmpty();
    }


    public void createNewEvent(int userId, String name, String startTimeStr, String endTimeStr, LocalDate startDate, LocalDate endDate, String location, String venue, String guidance, String notes, String capacityStr) throws Exception {
        if (isInvalidString(name) || isInvalidString(location) || isInvalidString(venue)) {
            throw new Exception("Make sure Name, Start Time, Location, Venue and Capacity fields are filled out!");
        }
        if (startDate == null || endDate == null) {
            throw new Exception("Please select a valid Start Date and End Date");
        }
        LocalTime startTime, endTime;
        try{
            startTime = LocalTime.parse(startTimeStr);
            endTime = LocalTime.parse(endTimeStr);
        } catch (Exception e) {
            throw new Exception("Please fill out the time fields correctly (hh:mm)");
        }
        //com

        int capacity;
        try{
            capacity = Integer.parseInt(capacityStr);
            if (capacity <= 0) throw new Exception();
        }catch (Exception e){
            throw new Exception("Capacity must be a positive number");
        }

        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

        int eventId = eventDAO.createNewEvent(name, startDateTime, endDateTime, location, venue, guidance, notes, capacity);
        eventCorDAO.assignCoordinator(userId, eventId);
    }

    public List<Event> getCorEvents(int userId) throws DataBaseConnectionException {
        return eventDAO.getEvents(userId);
    }

    public void createTicket(int id, String name, String price, String quantity) throws Exception {
        int quantityInt = Integer.parseInt(quantity);
        double priceDouble = Double.parseDouble(price);
        if (isInvalidString(name) || isInvalidString(price) || isInvalidString(quantity)) {
            throw new Exception("Make sure all the fields are filled out");
        }
        if (quantityInt <= 0) throw new Exception("Quantity must be a positive number");

        ticketDAO.createTicket(id, name, priceDouble, quantityInt);

    }

    public List<User> getUsersWithoutCurrent(int id) throws DataBaseConnectionException {
        List<User> users = userRepo.getUsersWithoutCurrent(id);
        return users;
    }

    public List<Event> getAllEvents() throws DataBaseConnectionException {
        return eventRepo.getAllEvents();
    }

    public void deleteSelectedEvent(Event selectedEvent) throws DataBaseConnectionException {
        eventRepo.deleteSelectedEvent(selectedEvent);
    }

    public List<EventCoordinator> getAvailableEventCoordinators(Event selectedEvent) throws DataBaseConnectionException {
        return eventCoordinatorRepo.getAvailableEventCoordinators(selectedEvent);
    }

    public void assingCoordinator(Event event, EventCoordinator selectedCoordinator) {
        eventCoordinatorRepo.assignCoordinator(event, selectedCoordinator);
    }

    public List<TicketEvent> getEventTickets(int id) throws DataBaseConnectionException {
        return ticketDAO.getEventTickets(id);
    }

    public void sellTicket(int id, String name, String email, int quantity, int available) throws Exception {
        if (isInvalidString(name) || isInvalidString(email)) {
            throw new Exception("Make sure fields are filled out.");
        }
        if  (quantity <= 0) throw new Exception("Quantity must be a positive number");
        ticketDAO.sellTicket(id, name, email, quantity, available);
    }
}
