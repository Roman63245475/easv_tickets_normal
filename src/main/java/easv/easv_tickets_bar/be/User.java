package easv.easv_tickets_bar.be;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class User {
    private int id;
    private String username;
    private String password;
    private int roleID;

    public User(int id, String username, String password, int roleID) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.roleID = roleID;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getRoleID() {
        return roleID;
    }
}
