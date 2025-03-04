package dataaccess;
import model.*;

public interface AuthDAO {
    String createAuth(String username) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    boolean isEmpty() throws DataAccessException;
    void clear() throws DataAccessException;
}
