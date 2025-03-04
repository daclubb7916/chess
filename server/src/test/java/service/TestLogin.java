package service;

import dataaccess.*;
import exception.ResponseException;
import request.LoginRequest;
import result.LoginResult;
import model.UserData;
import org.junit.jupiter.api.*;

public class TestLogin {

    private MemoryUserDAO userDAO;
    private MemoryAuthDAO authDAO;
    private LoginRequest request;
    private UserService userService;

    @BeforeEach
    public void setup() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        request = new LoginRequest("billy", "billy");
        userService = new UserService(userDAO, authDAO);
    }

    @Test
    public void testLoginWithValidUser() {
        UserData user = new UserData("billy", "billy", "billy@aol.com");
        userDAO.createUser(user);
        try {
            LoginResult result = userService.login(request);
            Assertions.assertEquals(request.username(), result.username(),
                    "Usernames of request and result are not equal");
        } catch (ResponseException ex) {
            Assertions.fail(ex.getMessage());
        }
    }

    @Test
    public void testLoginWithoutExistingUser() {
        ResponseException exception = Assertions.assertThrows(ResponseException.class,
                () -> userService.login(request), "A ResponseException should have been thrown");
        Assertions.assertEquals("Error: unauthorized", exception.getMessage());
    }
}
