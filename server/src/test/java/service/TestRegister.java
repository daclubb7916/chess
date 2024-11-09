package service;

import dataaccess.*;
import exception.ResponseException;
import request.RegisterRequest;
import result.RegisterResult;
import model.UserData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class TestRegister {

    @Test
    public void testUserIsAddedCorrectly() {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        RegisterRequest request = new RegisterRequest("billy", "billy", "billy@aol.com");
        UserService userService = new UserService(userDAO, authDAO);

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
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        UserData userData = new UserData("billy", "billy", "billy@aol.com");
        userDAO.createUser(userData);
        RegisterRequest request = new RegisterRequest("billy", "billy", "billy@aol.com");
        UserService userService = new UserService(userDAO, authDAO);

        ResponseException exception = Assertions.assertThrows(ResponseException.class,
                () -> userService.register(request), "ResponseException should be thrown");
        Assertions.assertEquals("Error: already taken", exception.getMessage());
    }
}
