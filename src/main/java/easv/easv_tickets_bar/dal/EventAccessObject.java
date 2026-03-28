package easv.easv_tickets_bar.dal;

import easv.easv_tickets_bar.CustomExceptions.DataBaseConnectionException;
import easv.easv_tickets_bar.be.Event;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventAccessObject {
    ConnectionManager connectionManager;

    public EventAccessObject(){
        try {
            this.connectionManager = new ConnectionManager();
        } catch (IOException e) {
            System.out.println("o moy bog");
        }
    }


    public int createNewEvent(String name, LocalDateTime startDateTime, LocalDateTime endDateTime, String location, String venue, String guidance, String notes, int capacity) throws DataBaseConnectionException {
        int id = -1;
        Connection con = null;
        try {
            con = connectionManager.getConnection();
            try(PreparedStatement ps = con.prepareStatement("INSERT INTO Events(Name, StartTime, EndTime, Location, Venue, LocationGuidance, Notes, Capacity) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)){
                ps.setString(1, name);
                ps.setObject(2, startDateTime);
                ps.setObject(3, endDateTime);
                ps.setString(4, location);
                ps.setString(5, venue);
                ps.setString(6, guidance);
                ps.setString(7, notes);
                ps.setInt(8, capacity);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getInt(1);
                    }
                }
            }
        }
        catch (SQLException e) {
            if (con == null) {
                throw new DataBaseConnectionException("Connection failed");
            }
            else{
                throw new RuntimeException(e);
            }
        }
        finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    System.out.println("o moy bog");
                }
            }
        }
        return id;
    }
    //SELECT *, (SELECT COUNT(*) FROM event_to_coordinator WHERE EventID = Events.id) AS CoordinatorCount FROM Events

    public List<Event> getEvents(int userId) throws DataBaseConnectionException {
        List<Event> events = new ArrayList<>();
        Connection con = null;
        try {
            con = connectionManager.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT *, (SELECT COUNT(*) FROM event_to_coordinator WHERE EventID = Events.id) AS CoordinatorCount FROM Events INNER JOIN event_to_coordinator ON Events.id = event_to_coordinator.EventID WHERE event_to_coordinator.UserID = ?");
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("EventID");
                    String name = rs.getString("Name");
                    LocalDateTime startDateTime = rs.getObject("StartTime", LocalDateTime.class);
                    LocalDateTime endDateTime = rs.getObject("EndTime", LocalDateTime.class);
                    String location = rs.getString("Location");
                    String venue = rs.getString("Venue");
                    String guidance = rs.getString("LocationGuidance");
                    String notes = rs.getString("Notes");
                    int capacity = rs.getInt("Capacity");
                    int count = rs.getInt("CoordinatorCount");
                    events.add(new Event(id, name, startDateTime, endDateTime, location, venue, guidance, notes, count, capacity));
                }
            }
            return events;
        }
        catch (SQLException e) {
            if (con == null) {
                throw new DataBaseConnectionException("Connection failed");
            }
            else{
                throw new RuntimeException(e);
            }
        }
        finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    System.out.println("omg");
                }
            }
        }
    }

    public List<Event> getAllEvents() throws DataBaseConnectionException {
        List<Event> events = new ArrayList<>();
        Connection con = null;
        try {
            con = connectionManager.getConnection();
            PreparedStatement ps = con.prepareStatement("Select * From Events");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("Name");
                    LocalDateTime startDateTime = rs.getObject("StartTime", LocalDateTime.class);
                    LocalDateTime endDateTime = rs.getObject("EndTime", LocalDateTime.class);
                    String location = rs.getString("Location");
                    events.add(new Event(id, name,  startDateTime, endDateTime, location));
                }
            }
            return events;
        }
        catch (SQLException e) {
            if (con == null) {
                throw new DataBaseConnectionException("Connection failed");
            }
            else{
                throw new RuntimeException(e);
            }
        }
        finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    System.out.println("omg");
                }
            }
        }
    }

    public void deleteSelectedEvent(Event selectedEvent) throws DataBaseConnectionException {
        Connection con = null;
        try {
            con = connectionManager.getConnection();
            PreparedStatement ps = con.prepareStatement("Delete From Events Where id = ?");
            ps.setInt(1, selectedEvent.getId());
            ps.execute();
        }
        catch (SQLException e) {
            if (con == null) {
                throw new DataBaseConnectionException("Connection failed");
            }
            else{
                throw new RuntimeException(e);
            }
        }
        finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    System.out.println("omg");
                }
            }
        }
    }
}
