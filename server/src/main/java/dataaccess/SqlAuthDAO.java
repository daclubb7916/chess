package dataaccess;

import model.AuthData;

public class SqlAuthDAO implements AuthDAO {
    @Override
    public String createAuth(String username) {
        return "";
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void clear() {

    }
}
