package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
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
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT username, password, email FROM users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new UserData(rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("email"));
                    }
                    throw new DataAccessException("User is not in DataBase");
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            try (var ps = conn.prepareStatement(statement)) {
                String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
                ps.setString(1, user.username());
                ps.setString(2, hashedPassword);
                ps.setString(3, user.email());
                ps.executeUpdate();
            }

        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "TRUNCATE users";
            try (var ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }

        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public boolean isEmpty() throws DataAccessException {
        return false;
    }

    @Override
    public void validatePassword(UserData user, String password) throws DataAccessException {

    }
}
