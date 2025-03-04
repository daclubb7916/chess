package service;

import dataaccess.*;
import exception.ResponseException;
import request.RegisterRequest;
import result.RegisterResult;
import model.UserData;
import org.junit.jupiter.api.*;

public class TestRegister {

    private MemoryUserDAO userDAO;
    private MemoryAuthDAO authDAO;
    private RegisterRequest request;
    private UserService userService;

    @BeforeEach
    public void setup() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        request = new RegisterRequest("billy", "billy", "billy@aol.com");
        userService = new UserService(userDAO, authDAO);
    }

    @Test
    public void testUserIsAddedCorrectly() {
        try {
            RegisterResult result = userService.register(request);
            UserData userData = userDAO.getUser(result.username());
            Assertions.assertEquals(result.username(), userData.username());
        } catch (ResponseException | DataAccessException ex) {
            Assertions.fail(ex.getMessage());
        }
    }

    @Test
    public void testUserExistsAlready() {
        UserData userData = new UserData("billy", "billy", "billy@aol.com");
        userDAO.createUser(userData);
        ResponseException exception = Assertions.assertThrows(ResponseException.class,
                () -> userService.register(request), "ResponseException should be thrown");
        Assertions.assertEquals("Error: already taken", exception.getMessage());
    }
}
