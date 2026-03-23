package easv.easv_tickets_bar.dal;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import easv.easv_tickets_bar.be.User;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EventCoordinatorsDAO {

    ConnectionManager connectionManager;

    public EventCoordinatorsDAO() {
        try {
            this.connectionManager = new ConnectionManager();
        } catch (IOException e) {
            System.out.println("kalivan");
        }
    }

    public void assignCoordinator(int userID, int eventID) {
        try(Connection con = connectionManager.getConnection()) {

            try(PreparedStatement ps = con.prepareStatement("INSERT INTO EventCoordinators (EventID, UserID) VALUES (?, ?)")) {
                ps.setInt(1, eventID);
                ps.setInt(2, userID);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
