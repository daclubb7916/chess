package dataaccess;

import model.AuthData;
import java.sql.SQLException;
import java.util.UUID;

public class SqlAuthDAO implements AuthDAO {

    public SqlAuthDAO() throws DataAccessException {
        String createTableStatement = """
            CREATE TABLE IF NOT EXISTS authTokens (
                authToken VARCHAR(255) NOT NULL,
                username VARCHAR(255) NOT NULL,
                PRIMARY KEY (authToken)
            )
            """;
        DatabaseManager.configureDatabase(createTableStatement);
    }

    @Override
    public String createAuth(String username) throws DataAccessException {
        return "";
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public boolean isEmpty() throws DataAccessException {
        return false;
    }

    @Override
    public void clear() throws DataAccessException {

    }
}
