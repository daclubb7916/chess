package service;

import dataaccess.*;
import exception.ResponseException;
import request.LogoutRequest;
import result.LogoutResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class TestLogout {

    @Test
    public void testLogoutWithValidAuthToken() {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(userDAO, authDAO);
        String authToken = authDAO.createAuth("billy");
        LogoutRequest request = new LogoutRequest(authToken);
        try {
            LogoutResult result = userService.logout(request);
            DataAccessException exception = Assertions.assertThrows(DataAccessException.class,
                    () -> authDAO.getAuth(authToken), "authToken still exists after logging out");
            Assertions.assertEquals("AuthToken does not exist", exception.getMessage());
            Assertions.assertInstanceOf(LogoutResult.class, result);
        } catch (ResponseException ex) {
            System.out.print("Threw ResponseException: ");
            System.out.println(ex.getMessage());
        }
    }

    @Test
    public void testLogoutWithoutValidAuthToken() {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(userDAO, authDAO);
        LogoutRequest request = new LogoutRequest("abcdefghijklmnop");
        ResponseException exception = Assertions.assertThrows(ResponseException.class,
                () -> userService.logout(request), "Response Exception should have been thrown");
        Assertions.assertEquals("Error: unauthorized", exception.getMessage());
    }
}
