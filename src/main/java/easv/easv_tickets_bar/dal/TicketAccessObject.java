package easv.easv_tickets_bar.dal;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import easv.easv_tickets_bar.CustomExceptions.DataBaseConnectionException;
import easv.easv_tickets_bar.CustomExceptions.DuplicateException;
import easv.easv_tickets_bar.be.TicketEvent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TicketAccessObject {

    ConnectionManager connectionManager;

    public TicketAccessObject() {
        try {
            this.connectionManager = new ConnectionManager();
        } catch (IOException e) {
            System.out.println("kalivan one love");
        }
    }

    public void createTicket(int id, String name, double priceDouble, String description) throws DataBaseConnectionException, DuplicateException {
        try (Connection con = connectionManager.getConnection()){

            try(PreparedStatement ps = con.prepareStatement("INSERT INTO event_ticket(event_id, name, price, description) VALUES (?, ?, ?, ?)")){
                ps.setInt(1, id);
                ps.setString(2, name);
                ps.setDouble(3, priceDouble);
                ps.setString(4, description);
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            if (e.getSQLState().startsWith("08")) {
                throw new DataBaseConnectionException("Connection Failed");
            } else if (e.getSQLState().startsWith("23")) {
                throw new DuplicateException("Insertion of duplicate");
            }
            else{
                throw new RuntimeException(e.getMessage());
            }

        }
    }

    public List<TicketEvent> getTicketsByCoordinator(int user_id) throws DataBaseConnectionException {
        List<TicketEvent> ticketEvents = new ArrayList<>();
        try (Connection con = connectionManager.getConnection()){
            try(PreparedStatement ps = con.prepareStatement("SELECT event_ticket.*, Events.Name AS EventName, (SELECT COUNT(*) FROM tickets WHERE tickets.typeId = event_ticket.id) AS SoldQuantity FROM event_ticket INNER JOIN Events ON Events.id = event_ticket.event_id INNER JOIN event_to_coordinator ON event_to_coordinator.EventID = Events.id WHERE event_to_coordinator.UserID = ?")){
                ps.setInt(1, user_id);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String eventName = rs.getString("EventName");
                    double price = rs.getDouble("price");
                    int quantity = rs.getInt("quantity");
                    int soldQuantity = rs.getInt("SoldQuantity");
                    ticketEvents.add(new TicketEvent(id, eventName, name, price, quantity, soldQuantity));
                }
            }
            return ticketEvents;

        } catch (SQLException e) {
            if (e.getSQLState().startsWith("08")) {
                throw new DataBaseConnectionException("Error");
            }else{
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    public List<TicketEvent> getTicketsOfEvent(int event_id) throws DataBaseConnectionException {
        List<TicketEvent> ticketEvents = new ArrayList<>();
        try (Connection con = connectionManager.getConnection()){
            try(PreparedStatement ps = con.prepareStatement("SELECT * FROM event_ticket WHERE event_id = ?")){
                ps.setInt(1, event_id);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    double price = rs.getDouble("price");
                    int quantity = rs.getInt("quantity");
                    ticketEvents.add(new TicketEvent(id, name, price, quantity));
                }
            }
            return ticketEvents;

        } catch (SQLException e) {
            if (e.getSQLState().startsWith("08")) {
                throw new DataBaseConnectionException("Error");
            }else{
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    public void sellTicket(int id, String name, String email, int quantity, int available) throws Exception {
        if (available - quantity < 0 || available <= 0) {
            throw new Exception("No tickets available or the quantity you wrote is bigger than what's left!");
        }

        //add small db check here for how much left just in case.

        try (Connection con = connectionManager.getConnection()){
            try(PreparedStatement ps = con.prepareStatement("INSERT INTO tickets(id, typeId, CustomerName, CustomerEmail) VALUES (?, ?, ?, ?)")){
                for(int i = 0; i < quantity; i++){
                    String UUID = java.util.UUID.randomUUID().toString();
                    ps.setString(1, UUID);
                    ps.setInt(2, id);
                    ps.setString(3, name);
                    ps.setString(4, email);

                    ps.addBatch();
                }
                ps.executeBatch();
            }

        } catch (SQLException e) {
            if (e.getSQLState().startsWith("08")) {
                throw new DataBaseConnectionException("Error");
            }else{
                throw new RuntimeException(e.getMessage());
            }
        }
    }
}
