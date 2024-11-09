package dataaccess;
import model.*;

public interface AuthDAO {
    String createAuth(String username);
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken);
    void clear();
}
