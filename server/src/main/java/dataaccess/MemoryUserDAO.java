package dataaccess;

import model.UserData;
import java.util.*;

public class MemoryUserDAO implements UserDAO {
    private final HashMap<String, UserData> users = new HashMap<>();

    @Override
    public UserData getUser(String username) throws DataAccessException {
        if (users.containsKey(username)) {
            return users.get(username);
        }
        throw new DataAccessException("User is not in DataBase");
    }

    @Override
    public void createUser(UserData user) {
        users.put(user.username(), user);
    }

    @Override
    public void clear() {
        users.clear();
    }

    @Override
    public boolean isEmpty() {
        return users.isEmpty();
    }

    @Override
    public void validatePassword(UserData user, String password) throws DataAccessException {
        if (!Objects.equals(password, user.password())) {
            throw new DataAccessException("Password does not match");
        }
    }

}
