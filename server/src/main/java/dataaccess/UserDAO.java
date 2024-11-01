package dataaccess;
import model.*;

public interface UserDAO {
    // I think all these need to throw exceptions
    public UserData getUser(String username) throws DataAccessException;
    public void createUser(UserData user);
    public void deleteUser(UserData user);
    public void clear();
}
