package easv.easv_tickets_bar.dal;

import easv.easv_tickets_bar.CustomExceptions.DataBaseConnectionException;
import easv.easv_tickets_bar.CustomExceptions.DuplicateException;
import easv.easv_tickets_bar.CustomExceptions.MyException;
import easv.easv_tickets_bar.be.Ticket;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
                throw new DuplicateException("Ticket with the same name already exists");
            }
            else{
                throw new RuntimeException(e.getMessage());
            }

        }
    }

//    public List<Ticket> getTicketsByCoordinator(int user_id) throws DataBaseConnectionException {
//        List<Ticket> ticketEvents = new ArrayList<>();
//        try (Connection con = connectionManager.getConnection()){
//            try(PreparedStatement ps = con.prepareStatement("SELECT event_ticket.*, Events.Name AS EventName, (SELECT COUNT(*) FROM tickets WHERE tickets.typeId = event_ticket.id) AS SoldQuantity FROM event_ticket INNER JOIN Events ON Events.id = event_ticket.event_id INNER JOIN event_to_coordinator ON event_to_coordinator.EventID = Events.id WHERE event_to_coordinator.UserID = ?")){
//                ps.setInt(1, user_id);
//                ResultSet rs = ps.executeQuery();
//                while (rs.next()) {
//                    int id = rs.getInt("id");
//                    String name = rs.getString("name");
//                    String eventName = rs.getString("EventName");
//                    double price = rs.getDouble("price");
//                    int quantity = rs.getInt("quantity");
//                    int soldQuantity = rs.getInt("SoldQuantity");
//                    ticketEvents.add(new Ticket(id, eventName, name, price, quantity, soldQuantity));
//                }
//            }
//            return ticketEvents;
//
//        } catch (SQLException e) {
//            if (e.getSQLState().startsWith("08")) {
//                throw new DataBaseConnectionException("Error");
//            }else{
//                throw new RuntimeException(e.getMessage());
//            }
//        }
//    }

    public List<Ticket> getTicketsOfEvent(int event_id) throws DataBaseConnectionException {
        List<Ticket> ticketEvents = new ArrayList<>();
        try (Connection con = connectionManager.getConnection()){
            try(PreparedStatement ps = con.prepareStatement("SELECT * FROM event_ticket WHERE event_id = ?")){
                ps.setInt(1, event_id);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    double price = rs.getDouble("price");
                    String description = rs.getString("description");
                    ticketEvents.add(new Ticket(id, name, price, description));
                }
            }
            return ticketEvents;

        } catch (SQLException e) {
            if (e.getSQLState().startsWith("08")) {
                throw new DataBaseConnectionException("Connection Failed");
            }else{
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    public boolean sellTicket(int id, int ticketTypeId, String name, String secondName, String email, List<String> ticketIds) throws Exception {

        //add small db check here for how much left just in case.
        Connection con = null;
        try {
            con = connectionManager.getConnection();
            con.setAutoCommit(false);
            try(PreparedStatement ps = con.prepareStatement("select capacity, (select count(*) from tickets inner join event_ticket on tickets.typeId = event_ticket.id where event_ticket.event_id = ?) as sold_amount From Events WITH (ROWLOCK, XLOCK) where id = ?")){
                ps.setInt(1, id);
                ps.setInt(2, id);
                try (ResultSet rs = ps.executeQuery()){
                    if (rs.next()) {
                        int capacity = rs.getInt("capacity");
                        int sold_amount = rs.getInt("sold_amount");
                        System.out.println("capacity: " + capacity);
                        System.out.println("sold_amount: " + sold_amount);
                        System.out.println("tickets left: " + (capacity - sold_amount));
                        if (capacity - sold_amount >= ticketIds.size()) {
                            try (PreparedStatement ps2 = con.prepareStatement("insert into tickets(id, typeId, CustomerName, CustomerEmail, IsScanned, second_name) values (?, ?, ?, ?, ?, ?)")){
                                for (String uniqueTicketId : ticketIds) {
                                    //String uniqueTicketId = java.util.UUID.randomUUID().toString();
                                    ps2.setString(1, uniqueTicketId);
                                    ps2.setInt(2, ticketTypeId);
                                    ps2.setString(3, name);
                                    ps2.setString(4, email);
                                    ps2.setBoolean(5, false);
                                    ps2.setString(6, secondName);
                                    ps2.addBatch();
                                }
                                ps2.executeBatch();
                                con.commit();
                                return true;
                            }
                        }
                        else{
                            if (capacity - sold_amount == 0) {
                                throw new MyException("Sorry no tickets left for this event");
                            }
                            else{
                                int left = capacity - sold_amount;
                                throw new MyException("Sorry only " + left + " tickets left for this event");
                            }
                        }
                    }
                    else{
                        throw new MyException("Sorry Event not found");
                    }
                }
            }
        } catch (SQLException e) {
            if (con == null){
                throw new DataBaseConnectionException("Connection Failed");
            }
            else{
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException("Rollback failed", ex);
                }
            }
        }
        finally {
            if (con != null){
                try {
                    con.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }

//            try(PreparedStatement ps = con.prepareStatement("INSERT INTO tickets(id, typeId, CustomerName, CustomerEmail) VALUES (?, ?, ?, ?)")){
//                for(int i = 0; i < quantity; i++){
//                    String UUID = java.util.UUID.randomUUID().toString();
//                    ps.setString(1, UUID);
//                    ps.setInt(2, id);
//                    ps.setString(3, name);
//                    ps.setString(4, email);
//
//                    ps.addBatch();
//                }
//                ps.executeBatch();
//            }


}
