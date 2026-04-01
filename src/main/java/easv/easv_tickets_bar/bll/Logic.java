package easv.easv_tickets_bar.bll;

import easv.easv_tickets_bar.CustomExceptions.DataBaseConnectionException;
import easv.easv_tickets_bar.CustomExceptions.DuplicateException;
import easv.easv_tickets_bar.CustomExceptions.LoginException;
import easv.easv_tickets_bar.CustomExceptions.MyException;
import easv.easv_tickets_bar.be.*;
import easv.easv_tickets_bar.repo.EventCoordinatorRepository;
import easv.easv_tickets_bar.repo.EventRepository;
import easv.easv_tickets_bar.repo.TicketRepository;
import easv.easv_tickets_bar.repo.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class Logic {
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    UserRepository userRepo = new UserRepository();
    EventRepository eventRepo = new EventRepository();
    EventCoordinatorRepository eventCoordinatorRepo = new EventCoordinatorRepository();
    TicketRepository ticketRepo = new TicketRepository();



    public User login(String username, String password) throws MyException {
        User user = null;
        try {
            user = userRepo.getFullUser(username);
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            } else {
                throw new LoginException("password doesn't match");
            }
        } catch (DataBaseConnectionException | LoginException ex) {
            if (ex instanceof DataBaseConnectionException) {
                System.out.println("do some job");
            } else {
                System.out.println("do some otehr job");
            }
            throw new MyException(ex.getMessage());
        }
    }
    private boolean checkRole(Role role) throws MyException {
        if (role != null) {
            return true;
        }
        throw new MyException("Role needs to be selected");
    }

    public void createUser(String username, String password, Role role) throws MyException {
        if (checkPassword(password) && checkUsername(username, password) && checkRole(role)) {
            try {
                String hashed_password = passwordEncoder.encode(password);
                userRepo.createUser(username, hashed_password, role);
            } catch (DataBaseConnectionException | DuplicateException ex) {
                if (ex instanceof DataBaseConnectionException) {
                    System.out.println("do some job");
                } else {
                    System.out.println("do some otehr job");
                }
                throw new MyException(ex.getMessage());
            }
        }
    }

    public boolean isInvalidString(String text) {
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
        try {
            startTime = LocalTime.parse(startTimeStr);
            endTime = LocalTime.parse(endTimeStr);
        } catch (Exception e) {
            throw new Exception("Please fill out the time fields correctly (hh:mm)");
        }
        //com

        int capacity;
        try {
            capacity = Integer.parseInt(capacityStr);
            if (capacity <= 0) throw new Exception();
        } catch (Exception e) {
            throw new Exception("Capacity must be a positive number");
        }

        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

        int eventId = eventRepo.createNewEvent(name, startDateTime, endDateTime, location, venue, guidance, notes, capacity);
        eventCoordinatorRepo.assignSelf(userId, eventId);
    }

    public List<Event> getCorEvents(int userId) throws MyException {
        try {
            return eventRepo.getEvents(userId);
        } catch (DataBaseConnectionException ex) {
            System.out.println("do some job");
            throw new MyException(ex.getMessage());
        }

    }

    public void createTicket(int id, int maxCapacity, String name, String price, String description) throws Exception {
        if (isInvalidString(name) || isInvalidString(price) || isInvalidString(description)) {
            throw new MyException("Make sure all the fields are filled out");
        }
        //first we need to check and only then parse, otherwise value of an empty string can't be parsed
        double priceDouble = Double.parseDouble(price);
        name = name.strip();
        String lowercaseName = name.toLowerCase();
        try {
            ticketRepo.createTicket(id, lowercaseName, priceDouble, description);
        }
        catch (DataBaseConnectionException | DuplicateException ex) {
            if (ex instanceof DataBaseConnectionException) {
                System.out.println("do some certain job");
                throw new MyException(ex.getMessage());
            }
            System.out.println("do some other job");
            throw new MyException(ex.getMessage());
        }


    }

    public List<User> getUsersWithoutCurrent(int id) throws MyException {
        try {
            List<User> users = userRepo.getUsersWithoutCurrent(id);
            return users;
        } catch (DataBaseConnectionException ex) {
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
        } catch (DataBaseConnectionException ex) {
            System.out.println("simulating sone job");
            throw new MyException(ex.getMessage());
        }

    }

    public List<EventCoordinator> getAvailableEventCoordinators(Event selectedEvent) throws MyException {
        try {
            return eventCoordinatorRepo.getAvailableEventCoordinators(selectedEvent);
        } catch (DataBaseConnectionException ex) {
            System.out.println("do some job");
            throw new MyException(ex.getMessage());
        }

    }

    public void assingCoordinator(Event event, EventCoordinator selectedCoordinator) throws MyException {
        try {
            eventCoordinatorRepo.assignCoordinator(event, selectedCoordinator);
        } catch (DataBaseConnectionException ex) {
            System.out.println("do some job");
            throw new MyException(ex.getMessage());
        }

    }

    public List<TicketEvent> getTicketsByCoordinator(int id) throws MyException {
        try {
            return ticketRepo.getTicketsByCoordinator(id);
        } catch (DataBaseConnectionException ex) {
            System.out.println("do some job");
            throw new MyException(ex.getMessage());
        }
    }

    public void sellTicket(int id, String name, String email, int quantity, int available) throws Exception {
        if (isInvalidString(name) || isInvalidString(email)) {
            throw new Exception("Make sure fields are filled out.");
        }
        if (quantity <= 0) throw new Exception("Quantity must be a positive number");
        ticketRepo.sellTicket(id, name, email, quantity, available);
    }

    public void editUser(User user, String changedUsername, String changedPassword, Role changedRole) throws MyException {
        if (checkUsername(changedUsername, changedPassword)) {
            String hashed_password = "";
            if (!changedPassword.isEmpty()) {
                if (checkPassword(changedPassword)){
                    hashed_password = passwordEncoder.encode(changedPassword);
                }
            }
            else {
                hashed_password = user.getPassword();
            }
            try {
                userRepo.editUser(user, changedUsername, hashed_password, changedRole);
                if (changedRole == Role.ADMIN) {
                    userRepo.eraseAllAssignedEvents(user.getId());
                }
            } catch (DataBaseConnectionException e) {
                System.out.println("simulating some job");
                throw new MyException("Connection failed");
            }
        }


    }

    private boolean checkPassword(String password) throws MyException {
        if (password.contains(" ")) {
            throw new MyException("Password shall not contain any spaces");
        }
        if (password.length() < 8) {
            throw new MyException("Password must contain at least 8 characters");
        }
        return true;
    }

    private boolean checkUsername(String username, String password) throws MyException {
        if (username.isEmpty()) {
            throw new MyException("Please fill all fields");
        }
        if (username.contains(" ")) {
            throw new MyException("Username shall not contain spaces");
        }
        if (username.length() < 8) {
            throw new MyException("Username must have at least 8 characters");
        }
        if (username.equals(password)) {
            throw new MyException("Password can't be equal to username");
        }
        return true;
    }

    public void deleteSelectedUser(User selectedUser) throws MyException {
        try {
            userRepo.deleteSelectedUser(selectedUser);
        }
        catch (DataBaseConnectionException ex) {
            System.out.println("simulating some job");
            throw new MyException(ex.getMessage());
        }

    }

    public void updateEvent(int id, String name, String startTimeStr, String endTimeStr, LocalDate startDate, LocalDate endDate, String location, String venue, String guidance, String notes, String capacityStr) throws Exception {
        if (isInvalidString(name) || isInvalidString(location) || isInvalidString(venue)) {
            throw new Exception("Make sure Name, Start Time, Location, Venue and Capacity fields are filled out!");
        }
        if (startDate == null || endDate == null) {
            throw new Exception("Please select a valid Start Date and End Date");
        }
        LocalTime startTime, endTime;
        try {
            startTime = LocalTime.parse(startTimeStr);
            endTime = LocalTime.parse(endTimeStr);
        } catch (Exception e) {
            throw new Exception("Please fill out the time fields correctly (hh:mm)");
        }

        int capacity;
        try {
            capacity = Integer.parseInt(capacityStr);
            if (capacity <= 0) throw new Exception();
        } catch (Exception e) {
            throw new Exception("Capacity must be a positive number");
        }

        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

        eventRepo.updateEvent(id, name, startDateTime, endDateTime, location, venue, guidance, notes, capacity);
    }
}
