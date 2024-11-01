package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    private HashMap<String, UserData> users = new HashMap<>();

    @Override
    public UserData getUser(String username) throws DataAccessException {
        if (users.containsKey(username)) {
            return users.get(username);
        }
        throw new DataAccessException("user is not in DataBase");
    }

    @Override
    public void createUser(UserData user) {

    }

    @Override
    public void deleteUser(UserData user) {

    }

    @Override
    public void clear() {

    }
}
