package dataaccess;

import exception.ResponseException;
import model.UserData;
import java.sql.*;

public class SqlUserDAO implements UserDAO {

    public SqlUserDAO() throws DataAccessException {
        String createTableStatement = """
            CREATE TABLE IF NOT EXISTS users (
                username VARCHAR(255) NOT NULL,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL,
                PRIMARY KEY (username)
            )
            """;
        DatabaseManager.configureDatabase(createTableStatement);
    }

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
