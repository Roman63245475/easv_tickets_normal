package easv.easv_tickets_bar.be;

public class Admin extends User
{
    public Admin(int id, String username, String password)
    {super(id, username, password);}

    public Role getRole()
    {
        return Role.ADMIN;
    }
}
