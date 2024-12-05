package dataaccess;

import exception.ResponseException;
import model.UserData;
import java.sql.*;

public class SqlUserDAO implements UserDAO {

    public SqlUserDAO() throws DataAccessException {
        configureDatabase();
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

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {

            var statement = """
                    CREATE TABLE IF NOT EXISTS users (
                        id INT NOT NULL AUTO_INCREMENT,
                        username VARCHAR(255) NOT NULL,
                        userdata TEXT NOT NULL,
                        PRIMARY KEY (id)
                    )""";

            try (var createTableStatement = conn.prepareStatement(statement)) {
                createTableStatement.executeUpdate();
            }

        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }
}
