package easv.easv_tickets_bar.dal;

import easv.easv_tickets_bar.CustomExceptions.DataBaseConnectionException;
import easv.easv_tickets_bar.CustomExceptions.LoginException;
import easv.easv_tickets_bar.be.Role;
import easv.easv_tickets_bar.be.User;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoleAccessObject {

    private ConnectionManager cm;

    public RoleAccessObject() {
        try {
            this.cm = new ConnectionManager();
        } catch (IOException e) {
            System.out.println("omg2");
        }
    }

    public Role getUserRole(int id) throws LoginException, DataBaseConnectionException {
        Connection con = null;
        try {
            con = cm.getConnection();
            String sqlPrompt = "select role.name as role_name from users INNER Join role on users.role_id = role.id where users.id = ?";
            PreparedStatement ps = con.prepareStatement(sqlPrompt);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String roleName = rs.getString("role_name");
                Role role = null;
                try {
                    role = Role.valueOf(roleName);
                }
                catch (IllegalArgumentException e) {
                    System.out.println("developer who was developing this is an idiot");
                }
                return role;
            }
            else{
                throw new LoginException("this is unreal");
            }
        }
        catch (SQLException e) {
            if (con == null) {
                throw new DataBaseConnectionException("Connection failed");
            }
            else
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
