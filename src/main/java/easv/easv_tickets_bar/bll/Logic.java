package easv.easv_tickets_bar.bll;
import easv.easv_tickets_bar.CustomExceptions.DataBaseConnectionException;
import easv.easv_tickets_bar.CustomExceptions.LoginException;
import easv.easv_tickets_bar.be.User;
import easv.easv_tickets_bar.dal.UserAccessObject;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


public class Logic {
    UserAccessObject uao = new UserAccessObject();
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User findUser(String username, String password) throws DataBaseConnectionException, LoginException {
        User user = uao.findUser(username);
        if (passwordEncoder.matches(password, user.getPassword())){
            return user;
        }
        else {
            throw new LoginException();
        }
    }
}
