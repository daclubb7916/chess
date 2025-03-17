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
}
