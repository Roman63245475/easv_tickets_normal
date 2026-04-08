package easv.easv_tickets_bar.bll;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import easv.easv_tickets_bar.CustomExceptions.DataBaseConnectionException;
import easv.easv_tickets_bar.CustomExceptions.DuplicateException;
import easv.easv_tickets_bar.CustomExceptions.LoginException;
import easv.easv_tickets_bar.CustomExceptions.MyException;
import easv.easv_tickets_bar.be.*;
import easv.easv_tickets_bar.gui.TicketController;
import easv.easv_tickets_bar.gui.TicketTemplateController;
import easv.easv_tickets_bar.repo.EventCoordinatorRepository;
import easv.easv_tickets_bar.repo.EventRepository;
import easv.easv_tickets_bar.repo.TicketRepository;
import easv.easv_tickets_bar.repo.UserRepository;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
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
        return text == null || text.isBlank();
    }

    public void validateEventData(String name, String startTimeStr, String endTimeStr, LocalDate startDate, LocalDate endDate, String location, String venue, String guidance, String notes, String capacityStr) throws MyException {
        if (isInvalidString(name) || isInvalidString(location) || isInvalidString(venue) || isInvalidString(notes)) {
            throw new MyException("Make sure Name, Start Time, Location, Venue and Capacity fields are filled out!");
        }
        if (startDate == null) {
            throw new MyException("Please select a valid Start Date and End Date");
        }
        LocalTime startTime, endTime;
        try {
            startTime = LocalTime.parse(startTimeStr);
            //endTime = LocalTime.parse(endTimeStr);
        } catch (DateTimeParseException ex) {
            throw new MyException("Please fill out the time fields correctly (hh:mm)");
        }

        int capacity;
        try {
            capacity = Integer.parseInt(capacityStr);
        } catch (NumberFormatException e) {
            throw new MyException("Capacity must be a valid number");
        }

        if (capacity <= 0) throw new MyException("Capacity must be a positive number");
    }

    public void createNewEvent(int userId, String name, String startTimeStr, String endTimeStr, LocalDate startDate, LocalDate endDate, String location, String venue, String guidance, String notes, String capacityStr) throws MyException {

        validateEventData(name, startTimeStr, endTimeStr, startDate, endDate, location, venue, guidance, notes, capacityStr);
        LocalTime startTime, endTime;
        startTime = LocalTime.parse(startTimeStr);
        try {
            endTime = LocalTime.parse(endTimeStr);
        }
        catch (DateTimeParseException ex) {
            endTime = null;
        }

        int capacity;
        capacity = Integer.parseInt(capacityStr);

        int eventId = eventRepo.createNewEvent(name, startTime, endTime, location, venue, guidance, notes, capacity, startDate, endDate);
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

//    public List<Ticket> getTicketsByCoordinator(int id) throws MyException {
//        try {
//            return ticketRepo.getTicketsByCoordinator(id);
//        } catch (DataBaseConnectionException ex) {
//            System.out.println("do some job");
//            throw new MyException(ex.getMessage());
//        }
//    }


    private int validateQuantity(String quantity) throws MyException {
        try {
            int quantityInt = Integer.parseInt(quantity);
            if (quantityInt <= 0) throw new MyException("Quantity must be a positive number");
            else {
                return quantityInt;
            }
        }
        catch (NumberFormatException ex) {
            throw new MyException("Quantity must be a positive number and ");
        }
    }

    public void sellTicket(Event event, Ticket ticketType, String name, String secondName, String email, String quantity) throws Exception {
        if (isInvalidString(name) || !isValidEmail(email) || isInvalidString(secondName) || ticketType == null) {
            throw new MyException("Make sure fields are filled out.");
        }
        List<String> ticketIds = new ArrayList<>();
        int validatedQuantity = validateQuantity(quantity);
        for (int i = 0; i < validatedQuantity; i++) {
            ticketIds.add(java.util.UUID.randomUUID().toString().replace("-", ""));
        }
        int id = event.getId();
        int ticketTypeId = ticketType.getId();
        if (ticketRepo.sellTicket(id, ticketTypeId, name, secondName, email, ticketIds)){
            sendTickets(event, ticketType, name, secondName, email, ticketIds);
        }
    }

//    int id = rs.getInt("EventID");
//    String name = rs.getString("Name");
//    LocalDateTime startDateTime = rs.getObject("StartTime", LocalDateTime.class);
//    LocalDateTime endDateTime = rs.getObject("EndTime", LocalDateTime.class);
//    String location = rs.getString("Location");
//    String venue = rs.getString("Venue");
//    String guidance = rs.getString("LocationGuidance");
//    String notes = rs.getString("Notes");
//    int capacity = rs.getInt("Capacity");
//    int count = rs.getInt("CoordinatorCount");
//    int sold_amount = rs.getInt("sold_amount")
    private void sendTickets(Event event, Ticket ticketType, String name, String secondName, String email, List<String> ticketIds) {
        try {
            List<BufferedImage> qrCodes = generateQRCodes(ticketIds);
            List<WritableImage> ticketsImages = generateTickets(event, qrCodes);
            File pdfDocument = convertToPDF(ticketsImages, event.getNameForFile(), name + "_" + secondName);
            EmailSender emailSender = new EmailSender(pdfDocument, email);
            boolean sent = emailSender.sendEmail(name, event.getName());
            if (sent) {
                ticketRepo.markEmailSent(ticketIds);
            }
            else{
                throw new MyException("Email will be sent later");
            }

        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private File convertToPDF(List<WritableImage> ticketsImages, String eventName, String userFullName) throws MyException {
        try (PDDocument document = new PDDocument();){
            for (WritableImage ticketImage : ticketsImages) {
                BufferedImage img = SwingFXUtils.fromFXImage(ticketImage, null);
                PDPage page = new PDPage();
                document.addPage(page);
                PDImageXObject pdImage = LosslessFactory.createFromImage(document, img);
                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)){
                    contentStream.drawImage(pdImage, 0, 0);
                }

            }
            Path dirPath = Path.of("tickets");
            dirPath.toFile().mkdirs();
            Path targetPath = dirPath.resolve(eventName + "_" + userFullName + ".pdf");
            int i = 1;
            while (Files.exists(targetPath)) {
                targetPath = dirPath.resolve(eventName + "_" + userFullName + "(" + i + ")" + ".pdf");
                i++;
            }
            document.save(targetPath.toFile());
            return targetPath.toFile();//return after
        }
        catch (IOException ex) {
            throw new MyException("something went wrong while generating PDF");
        }
    }

    private List<WritableImage> generateTickets(Event event, List<BufferedImage> qrCodes) throws MyException {
        String fileName = "ticket.fxml";
        List<WritableImage> ticketsImages = new ArrayList<>();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/easv/easv_tickets_bar/gui/ticket.fxml"));
            Parent root = (Parent) loader.load();
            new Scene(root);
            TicketTemplateController controller = loader.getController();
            root.applyCss();
            root.layout();
            for (BufferedImage qrCode : qrCodes) {
                Image code = SwingFXUtils.toFXImage(qrCode, null );
                String endDate = (event.getEndDate() != null) ? event.getEndDate().toString() : "End date is not specified";
                controller.setData(event.getName(), event.getStartTime(), event.getEndTime(), event.getStartDate().toString(), endDate, event.getLocation(), event.getVenue(), event.getLocationGuidance(), event.getNotes(), code);
                WritableImage image = root.snapshot(new SnapshotParameters(), null);
                ticketsImages.add(image);
            }
            return ticketsImages;
        } catch (IOException e) {
            System.out.println("idi nahuy");
            throw new MyException("something went wrong while generating a ticket");
        }
    }

    private List<BufferedImage> generateQRCodes(List<String> ticketsIds) throws MyException {
        List<BufferedImage> qrCodes = new ArrayList<>();
        QRCodeWriter qrCodeWriter = new QRCodeWriter();//an object which can convert text into qr code
        try {
            for (String ticketId : ticketsIds){
                BitMatrix matrix = qrCodeWriter.encode(ticketId, BarcodeFormat.QR_CODE, 150, 150); //2d boolean matrix, if true then 0, if false then 255, grayscale although
                BufferedImage image = new BufferedImage(150, 150, BufferedImage.TYPE_INT_RGB);
                for (int x = 0; x < 150; x++) {
                    for (int y = 0; y < 150; y++) {
                        image.setRGB(x, y, matrix.get(x, y) ? 0x000000 : 0xFFFFFF);
                    }
                }
                qrCodes.add(image);
            }
            return qrCodes;
        } catch (Exception e) {
            throw new MyException("QR Code generating failed");
        }
    }

    public boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9+_.-]+\\.[A-Za-z]{2,6}$");
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
        validateEventData(name, startTimeStr, endTimeStr, startDate, endDate, location, venue, guidance, notes, capacityStr);
        LocalTime startTime, endTime;
        startTime = LocalTime.parse(startTimeStr);
        try {
            endTime = LocalTime.parse(endTimeStr);
        }
        catch (DateTimeParseException ex) {
            endTime = null;
        }

        int capacity;
        capacity = Integer.parseInt(capacityStr);
        eventRepo.updateEvent(id, name, startTime, startDate, endTime, endDate, location, venue, guidance, notes, capacity);
    }

    public List<Ticket> getAllTicketsByEvent(Event event) throws DataBaseConnectionException {
        return ticketRepo.getTicketsOfEvent(event.getId());
    }
}
