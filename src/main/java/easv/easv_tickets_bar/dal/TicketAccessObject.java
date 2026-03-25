package easv.easv_tickets_bar.dal;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import easv.easv_tickets_bar.CustomExceptions.DataBaseConnectionException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TicketAccessObject {

    ConnectionManager connectionManager;

    public TicketAccessObject() {
        try {
            this.connectionManager = new ConnectionManager();
        } catch (IOException e) {
            System.out.println("kalivan one love");
        }
    }

    public void createTicket(int id, String name, double priceDouble, int quantityInt) throws DataBaseConnectionException {
        try (Connection con = connectionManager.getConnection()){

            try(PreparedStatement ps = con.prepareStatement("INSERT INTO event_ticket(event_id, name, price, quantity) VALUES (?, ?, ?, ?)")){
                ps.setInt(1, id);
                ps.setString(2, name);
                ps.setDouble(3, priceDouble);
                ps.setInt(4, quantityInt);
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            if (e.getSQLState().startsWith("08")) {
                throw new DataBaseConnectionException();
            }else{
                throw new RuntimeException(e.getMessage());
            }
        }
    }
}
