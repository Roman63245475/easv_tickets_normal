package easv.easv_tickets_bar.be;

public class UserRole {
    private int id;
    private Role role;

    public UserRole(int id, Role role) {
        this.id = id;
        this.role = role;
    }

    public Role getRole() {
        return role;
    }
}
