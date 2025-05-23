package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.util.UUID;

public class AuthDAOTests {

    private static AuthDAO authDAO;

    @BeforeAll
    public static void createDatabase() throws DataAccessException {
        authDAO = new SqlAuthDAO();
    }

    @AfterEach
    public void emptyTable() throws DataAccessException {
        authDAO.clear();
    }

    @Test
    public void testCreateAuthSuccess() {
        try {
            String authToken = authDAO.createAuth("bernie");
            AuthData authData = authDAO.getAuth(authToken);
            Assertions.assertEquals(authToken, authData.authToken());
        } catch (DataAccessException ex) {
            Assertions.fail(ex.getMessage());
        }
    }

    @Test
    public void testCreateAuthUnsuccess() {
        DataAccessException ex = Assertions.assertThrows(DataAccessException.class,
                () -> authDAO.createAuth(null));
    }

    @Test
    public void testGetAuthWithValidAuthToken() throws DataAccessException {
        String[] authTokens = addSomeAuthTokens();
        AuthData authData = authDAO.getAuth(authTokens[1]);
        Assertions.assertEquals("kdot", authData.username());
    }

    @Test
    public void testGetAuthWithoutValidAuthToken() throws DataAccessException {
        String[] authTokens = addSomeAuthTokens();
        DataAccessException ex = Assertions.assertThrows(DataAccessException.class,
                () -> authDAO.getAuth(UUID.randomUUID().toString()));
        Assertions.assertEquals("AuthToken does not exist", ex.getMessage());
    }

    @Test
    public void testDeleteAuthWithValidAuthToken() throws DataAccessException {
        String[] authTokens = addSomeAuthTokens();
        authDAO.deleteAuth(authTokens[0]);
        DataAccessException ex = Assertions.assertThrows(DataAccessException.class,
                () -> authDAO.getAuth(authTokens[0]));
    }

    @Test
    public void testDeleteAuthWithoutValidAuthToken() {
        DataAccessException ex = Assertions.assertThrows(DataAccessException.class,
                () -> authDAO.getAuth(UUID.randomUUID().toString()));
    }

    @Test
    public void testIsEmptyAndClear() throws DataAccessException {
        String[] authTokens = addSomeAuthTokens();
        Assertions.assertFalse(authDAO.isEmpty());
        authDAO.clear();
        Assertions.assertTrue(authDAO.isEmpty());
    }

    private String[] addSomeAuthTokens() throws DataAccessException {
        String[] authTokens = {UUID.randomUUID().toString(),
                UUID.randomUUID().toString(), UUID.randomUUID().toString()};
        String[] usernames = {"bernard", "kdot", "gretchen"};
        String statement = "INSERT INTO authTokens (authToken, username) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {

                for (int i = 0; i < 3; i++) {
                    ps.setString(1, authTokens[i]);
                    ps.setString(2, usernames[i]);
                    ps.executeUpdate();
                }
                return authTokens;

            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }
}
