package easv.easv_tickets_bar.dal;

import easv.easv_tickets_bar.CustomExceptions.DataBaseConnectionException;
import easv.easv_tickets_bar.be.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EventCoordinatorAccessObject {

    ConnectionManager cm;

    public EventCoordinatorAccessObject() {
        try {
            this.cm = new ConnectionManager();
        } catch (IOException e) {
            System.out.println("kalivan");
        }
    }

    public void assignCoordinator(int userID, int eventID) {
        try(Connection con = cm.getConnection()) {

            try(PreparedStatement ps = con.prepareStatement("INSERT INTO event_to_coordinator (EventID, UserID) VALUES (?, ?)")) {
                ps.setInt(1, eventID);
                ps.setInt(2, userID);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<EventCoordinator> getAvailableEventCoordinators(Event selectedEvent) throws DataBaseConnectionException {
        Connection con = null;
        List<EventCoordinator> availableEventCoordinators = new ArrayList<>();
        try {
            con = cm.getConnection();
            String sqlPrompt = "SELECT * FROM users LEFT JOIN event_to_coordinator ON users.id = event_to_coordinator.UserID AND event_to_coordinator.EventID = ? WHERE users.role_id = 2 AND event_to_coordinator.UserID IS NULL;";
            PreparedStatement ps = con.prepareStatement(sqlPrompt);
            ps.setInt(1, selectedEvent.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int coordinatorId = rs.getInt("id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                availableEventCoordinators.add(new EventCoordinator(coordinatorId, username, password));
            }
            return availableEventCoordinators;
        }
        catch (SQLException e) {
            if (con == null) {
                throw new DataBaseConnectionException();
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
