package dataaccess;
import model.*;

public interface AuthDAO {
    public String createAuth(String username);
    public AuthData getAuth(String authToken);
    public void deleteAuth(String authToken);
    public void clear();
}
