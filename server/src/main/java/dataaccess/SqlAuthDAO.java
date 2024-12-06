package dataaccess;

import model.AuthData;

public class SqlAuthDAO implements AuthDAO {

    public SqlAuthDAO() throws DataAccessException {
        String createTableStatement = """
            CREATE TABLE IF NOT EXISTS authTokens (
                authToken VARCHAR(255) NOT NULL,
                username VARCHAR(255) NOT NULL,
                PRIMARY KEY (authTOKEN)
            )
            """;
        DatabaseManager.configureDatabase(createTableStatement);
    }

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
