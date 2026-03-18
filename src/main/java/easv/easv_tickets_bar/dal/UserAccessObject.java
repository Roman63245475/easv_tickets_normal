package easv.easv_tickets_bar.dal;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import easv.easv_tickets_bar.CustomExceptions.DataBaseConnectionException;
import easv.easv_tickets_bar.be.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserAccessObject {

    private ConnectionManager cm;

    public User findUser(String username) throws DataBaseConnectionException {
        Connection con = null;
        try {
            con = cm.getConnection();
            String sqlPrompt = "Select * from users where username = ?";
            PreparedStatement ps = con.prepareStatement(sqlPrompt);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            rs.next();
            int id  = rs.getInt("id");
            String usName = rs.getString("username");
            String pass = rs.getString("password");
            int roleID = rs.getInt("role_id");

            return new User(id, usName, pass, roleID);
        }
        catch (SQLServerException sse){
            throw new DataBaseConnectionException();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
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
