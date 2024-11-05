package service;

import dataaccess.*;
import request.*;
import result.*;
import model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class testLogin {

    @Test
    public void successfulLogin() {
        UserData user = new UserData("billy", "billy", "billy@aol.com");
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        userDAO.createUser(user);
        loginRequest request = new loginRequest("billy", "billy");
        UserService userService = new UserService(userDAO, authDAO);
        // Record result = userService.login(request);
        // Assertions.assertEquals(request.username(), result.username());
    }

    @Test
    public void userDoesNotExist() {

    }
}
