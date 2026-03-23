package easv.easv_tickets_bar.bll;
import easv.easv_tickets_bar.CustomExceptions.DataBaseConnectionException;
import easv.easv_tickets_bar.CustomExceptions.DuplicateException;
import easv.easv_tickets_bar.CustomExceptions.LoginException;
import easv.easv_tickets_bar.be.Event;
import easv.easv_tickets_bar.be.User;
import easv.easv_tickets_bar.dal.EventCoordinatorsDAO;
import easv.easv_tickets_bar.dal.EventDAO;
import easv.easv_tickets_bar.dal.UserAccessObject;
import easv.easv_tickets_bar.gui.Role;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


public class Logic {
    UserAccessObject uao = new UserAccessObject();
    EventDAO eventDAO = new EventDAO();
    EventCoordinatorsDAO eventCorDAO = new EventCoordinatorsDAO();
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public User login(String username, String password) throws DataBaseConnectionException, LoginException {
        User user = uao.findUser(username);
        if (passwordEncoder.matches(password, user.getPassword())){
            return user;
        }
        else {
            throw new LoginException();
        }
    }

    public void createUser(String username, String password, Role role) throws DataBaseConnectionException, DuplicateException {
        String hashed_password = passwordEncoder.encode(password);
        int role_id = (role == Role.ADMIN) ? 1 : 2;
        uao.createUser(username, hashed_password, role_id);
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

    public List<Event> getCorEvents(int userId) {
        return eventDAO.getEvents(userId);
    }
}
