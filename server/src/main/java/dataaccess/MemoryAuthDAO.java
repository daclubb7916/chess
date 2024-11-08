package dataaccess;

import model.*;
import java.util.UUID;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    private HashMap<String, AuthData> authTokens = new HashMap<>();

    @Override
    public String createAuth(String username) {
        String authToken = UUID.randomUUID().toString();
        authTokens.put(authToken, new AuthData(authToken, username));
        return authToken;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (authTokens.containsKey(authToken)) {
            return authTokens.get(authToken);
        } else {
            throw new DataAccessException("AuthToken does not exist");
        }
    }

    @Override
    public void deleteAuth(String authToken) {
        authTokens.remove(authToken);
    }

    @Override
    public void clear() {
        authTokens.clear();
    }
}
