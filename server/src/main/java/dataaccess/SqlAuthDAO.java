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
        String authToken = UUID.randomUUID().toString();
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "INSERT INTO authTokens (authToken, username) VALUES (?, ?)";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                ps.setString(2, username);
                ps.executeUpdate();
                return authToken;
            }

        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT authToken, username FROM authTokens WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new AuthData(rs.getString("authToken"),
                                rs.getString("username"));
                    }
                    throw new DataAccessException("AuthToken does not exist");
                }
            }

        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "DELETE FROM authTokens WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                ps.executeUpdate();
            }

        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public boolean isEmpty() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT * FROM authTokens";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    return !rs.next();
                }
            }

        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "TRUNCATE authTokens";
            try (var ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }

        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }
}
