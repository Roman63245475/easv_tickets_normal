package easv.easv_tickets_bar.repo;

import easv.easv_tickets_bar.CustomExceptions.DataBaseConnectionException;
import easv.easv_tickets_bar.CustomExceptions.DuplicateException;
import easv.easv_tickets_bar.CustomExceptions.LoginException;
import easv.easv_tickets_bar.be.*;
import easv.easv_tickets_bar.dal.EventAccessObject;
import easv.easv_tickets_bar.dal.RoleAccessObject;
import easv.easv_tickets_bar.dal.UserAccessObject;

import java.util.List;

public class UserRepository {
    private UserAccessObject uao;
    private RoleAccessObject rao;
    private EventAccessObject eao;

    public UserRepository() {
        this.uao = new UserAccessObject();
        this.rao = new RoleAccessObject();
        this.eao = new EventAccessObject();
    }

    public User getFullUser(String username) throws DataBaseConnectionException, LoginException {
        User user = uao.findUser(username);
        Role role = rao.getUserRole(user.getId());
        User returnUser;
        if (role == Role.ADMIN) {
            returnUser = new Admin(user.getId(), user.getUsername(), user.getPassword());
        }
        else {
            List<Event> events = eao.getEvents(user.getId());
            returnUser = new EventCoordinator(user.getId(), user.getUsername(), user.getPassword(), events);
        }
        return returnUser;
    }


    public void createUser(String username, String hashedPassword, Role role) throws DuplicateException, DataBaseConnectionException {
        int roleId = role.getId();
        uao.createUser(username, hashedPassword, roleId);
    }
}
