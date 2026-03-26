package easv.easv_tickets_bar.dal;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import easv.easv_tickets_bar.CustomExceptions.DataBaseConnectionException;
import easv.easv_tickets_bar.CustomExceptions.DuplicateException;
import easv.easv_tickets_bar.CustomExceptions.LoginException;
import easv.easv_tickets_bar.be.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public List<User> getUsersWithoutCurrent(int id) throws DataBaseConnectionException {
        Connection con = null;
        List<User> users = new ArrayList<>();
        try {
            con = cm.getConnection();
            String sqlPrompt = "select users.id as user_id, users.username as user_username, users.password as user_password, role.name as role_name from dbo.users Inner Join role on users.role_id = role.id where users.id !=?";
            PreparedStatement ps = con.prepareStatement(sqlPrompt);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int recordId  = rs.getInt("user_id");
                String usName = rs.getString("user_username");
                String pass = rs.getString("user_password");
                String roleName = rs.getString("role_name");
                Role role = null;
                try {
                    role = Role.valueOf(roleName);
                }
                catch (IllegalArgumentException e){
                    System.out.println("dev is an idiot");
                }
                User addUser;
                if (role == Role.ADMIN) {
                    addUser = new Admin(recordId, usName, pass);
                }
                else {
                    addUser = new EventCoordinator(recordId, usName, pass, null);
                }
                users.add(addUser);
            }
            return users;
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
