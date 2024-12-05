package dataaccess;

import model.UserData;

public class SqlUserDAO implements UserDAO {
    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void createUser(UserData user) {

    }

    @Override
    public void clear() {

    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void validatePassword(UserData user, String password) throws DataAccessException {

    }
}
