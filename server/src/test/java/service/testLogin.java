package service;

import dataaccess.*;
import request.*;
import model.*;
import org.junit.jupiter.api.Test;

public class testLogin {

    @Test
    public void successfulLogin() {
        UserData user = new UserData("billy", "billy", "billy@aol.com");
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        userDAO.createUser(user);
        LoginRequest request = new LoginRequest("billy", "billy");
        UserService userService = new UserService(userDAO, authDAO);
        // Record result = userService.login(request);
        // Assertions.assertEquals(request.username(), result.username());
    }

    @Test
    public void userDoesNotExist() {

    }
}
