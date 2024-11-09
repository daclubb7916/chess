package dataaccess;
import model.*;

public interface UserDAO {
    UserData getUser(String username) throws DataAccessException;
    void createUser(UserData user);
    void clear();
    void validatePassword(UserData user, String password) throws DataAccessException;
}
