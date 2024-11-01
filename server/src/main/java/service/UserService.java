package service;

import dataaccess.*;
import request.*;
import result.*;
import model.*;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public void register(registerRequest request) {

    }

    public Record login(loginRequest request) {
        try {
            UserData userData = userDAO.getUser(request.username());
            String authToken = authDAO.createAuth(userData.username());
            return new loginResult(userData.username(), authToken);
        } catch (DataAccessException ex) {
            return new errorResult("Error: unauthorized");
        }
    }

    public void logout(logoutRequest request) {

    }

}
