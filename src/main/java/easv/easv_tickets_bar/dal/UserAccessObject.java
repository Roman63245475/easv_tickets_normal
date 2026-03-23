package easv.easv_tickets_bar.dal;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import easv.easv_tickets_bar.CustomExceptions.DataBaseConnectionException;
import easv.easv_tickets_bar.CustomExceptions.DuplicateException;
import easv.easv_tickets_bar.CustomExceptions.LoginException;
import easv.easv_tickets_bar.be.User;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserAccessObject {

    private ConnectionManager cm;

    public UserAccessObject(){
        try {
            this.cm = new ConnectionManager();
        } catch (IOException e) {
            System.out.println("omg2");
        }
    }

    public User findUser(String username) throws DataBaseConnectionException, LoginException {
        Connection con = null;
        try {
            con = cm.getConnection();
            String sqlPrompt = "Select * from users where username = ?";
            PreparedStatement ps = con.prepareStatement(sqlPrompt);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id  = rs.getInt("id");
                String usName = rs.getString("username");
                String pass = rs.getString("password");
                return new User(id, usName, pass);
            }
            else{
                throw new LoginException();
            }
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

    public void createUser(String username, String hashed_password, int role_id) throws DataBaseConnectionException, DuplicateException {
        Connection con = null;
        try {
            con = cm.getConnection();
            String sqlPrompt = "Insert Into users (username, password, role_id) values (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sqlPrompt);
            ps.setString(1, username);
            ps.setString(2, hashed_password);
            ps.setInt(3, role_id);
            ps.execute();
        }
        catch (SQLException e){
            if (con == null){
                throw new DataBaseConnectionException();
            }
            else if (e.getErrorCode() == 2627 || e.getErrorCode() == 2601) {
                throw new DuplicateException();
            }
            else {
                throw new RuntimeException();
            }
        }

        finally {
            if (con != null){
                try {
                    con.close();
                }
                catch (SQLException se){
                    System.out.println("omg");
                }
            }
        }

    }
}
