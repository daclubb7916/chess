package service;

import dataaccess.*;
import exception.ResponseException;
import request.LoginRequest;
import result.LoginResult;
import model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class TestLogin {

    @Test
    public void TestLoginWithValidUser() {
        UserData user = new UserData("billy", "billy", "billy@aol.com");
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        userDAO.createUser(user);
        LoginRequest request = new LoginRequest("billy", "billy");
        UserService userService = new UserService(userDAO, authDAO);
        try {
            LoginResult result = userService.login(request);
            Assertions.assertEquals(request.username(), result.username(),
                    "Usernames of request and result are not equal");
        } catch (ResponseException ex) {
            System.out.print("Threw Response Exception: ");
            System.out.println(ex.getMessage());
        }
    }

    @Test
    public void TestLoginWithoutExistingUser() {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        LoginRequest request = new LoginRequest("billy", "billy");
        UserService userService = new UserService(userDAO, authDAO);
        ResponseException exception = Assertions.assertThrows(ResponseException.class,
                () -> userService.login(request), "A ResponseException should have been thrown");
        Assertions.assertEquals("Error: unauthorized", exception.getMessage());
    }
}