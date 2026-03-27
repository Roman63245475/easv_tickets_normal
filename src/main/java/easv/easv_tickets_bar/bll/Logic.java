package easv.easv_tickets_bar.bll;
import easv.easv_tickets_bar.CustomExceptions.DataBaseConnectionException;
import easv.easv_tickets_bar.CustomExceptions.DuplicateException;
import easv.easv_tickets_bar.CustomExceptions.LoginException;
import easv.easv_tickets_bar.CustomExceptions.MyException;
import easv.easv_tickets_bar.be.Event;
import easv.easv_tickets_bar.be.EventCoordinator;
import easv.easv_tickets_bar.be.User;
import easv.easv_tickets_bar.dal.EventCoordinatorAccessObject;
import easv.easv_tickets_bar.dal.EventAccessObject;
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
    EventCoordinatorAccessObject eventCorDAO = new EventCoordinatorAccessObject();
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    UserRepository userRepo = new UserRepository();
    EventRepository eventRepo = new EventRepository();
    EventCoordinatorRepository eventCoordinatorRepo = new EventCoordinatorRepository();


    public User login(String username, String password) throws MyException {
        User user = null;
        try {
            user = userRepo.getFullUser(username);
            if (passwordEncoder.matches(password, user.getPassword())){
                return user;
            }
            else {
                throw new LoginException("password doesn't match");
            }
        }
        catch (DataBaseConnectionException | LoginException ex) {
            if (ex instanceof DataBaseConnectionException) {
                System.out.println("do some job");
            } else {
                System.out.println("do some otehr job");
            }
            throw new MyException(ex.getMessage());
        }
    }

    public void createUser(String username, String password, Role role) throws MyException {
        try {
            String hashed_password = passwordEncoder.encode(password);
            userRepo.createUser(username, hashed_password, role);
        }
        catch(DataBaseConnectionException | DuplicateException ex){
            if (ex instanceof DataBaseConnectionException) {
                System.out.println("do some job");
            } else {
                System.out.println("do some otehr job");
            }
            throw new MyException(ex.getMessage());
        }

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

    public List<User> getUsersWithoutCurrent(int id) throws MyException {
        try {
            List<User> users = userRepo.getUsersWithoutCurrent(id);
            return users;
        }
        catch (DataBaseConnectionException ex){
            System.out.println("do some job");
            throw new MyException(ex.getMessage());
        }

    }

    public List<Event> getAllEvents() throws DataBaseConnectionException {
        return eventRepo.getAllEvents();
    }

    public void deleteSelectedEvent(Event selectedEvent) throws MyException {
        try {
            eventRepo.deleteSelectedEvent(selectedEvent);
        }
        catch (DataBaseConnectionException ex){
            System.out.println("simulating sone job");
            throw new MyException(ex.getMessage());
        }

    }

    public List<EventCoordinator> getAvailableEventCoordinators(Event selectedEvent) throws MyException {
        try {
            return eventCoordinatorRepo.getAvailableEventCoordinators(selectedEvent);
        }
        catch (DataBaseConnectionException ex) {
            System.out.println("do some job");
            throw new MyException(ex.getMessage());
        }

    }

    public void assingCoordinator(Event event, EventCoordinator selectedCoordinator) throws MyException {
        try {
            eventCoordinatorRepo.assignCoordinator(event, selectedCoordinator);
        }
        catch (DataBaseConnectionException ex) {
            System.out.println("do some job");
            throw new MyException(ex.getMessage());
        }

    }
}
