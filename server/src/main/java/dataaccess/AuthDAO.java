package dataaccess;
import model.*;

public interface AuthDAO {
    public void createAuth(AuthData auth);
    public AuthData getAuth(String authToken);
    public void deleteAuth(String authToken);
    public void clear();
}
