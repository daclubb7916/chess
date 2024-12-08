package dataaccess;
import model.*;

public interface UserDAO {
    UserData getUser(String username) throws DataAccessException;
    void createUser(UserData user) throws DataAccessException;
    void clear() throws DataAccessException;
    boolean isEmpty() throws DataAccessException;
    void validatePassword(UserData user, String password) throws DataAccessException;
}
