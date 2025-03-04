package service;

import dataaccess.*;
import exception.ResponseException;
import request.LogoutRequest;
import result.LogoutResult;
import org.junit.jupiter.api.*;

public class TestLogout {

    private MemoryUserDAO userDAO;
    private MemoryAuthDAO authDAO;
    private UserService userService;

    @BeforeEach
    public void setup() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
    }

    @Test
    public void testLogoutWithValidAuthToken() {
        String authToken = authDAO.createAuth("billy");
        LogoutRequest request = new LogoutRequest(authToken);
        try {
            LogoutResult result = userService.logout(request);
            DataAccessException exception = Assertions.assertThrows(DataAccessException.class,
                    () -> authDAO.getAuth(authToken), "authToken still exists after logging out");
            Assertions.assertEquals("AuthToken does not exist", exception.getMessage());
            Assertions.assertInstanceOf(LogoutResult.class, result);
        } catch (ResponseException ex) {
            Assertions.fail(ex.getMessage());
        }
    }

    @Test
    public void testLogoutWithoutValidAuthToken() {
        LogoutRequest request = new LogoutRequest("abcdefghijklmnop");
        ResponseException exception = Assertions.assertThrows(ResponseException.class,
                () -> userService.logout(request), "Response Exception should have been thrown");
        Assertions.assertEquals("Error: unauthorized", exception.getMessage());
    }
}
