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

    public void register(RegisterRequest request) {

    }

    public LoginResult login(LoginRequest request) throws Exception {
        try {
            UserData userData = userDAO.getUser(request.username());
            userDAO.validatePassword(userData, request.password());
            String authToken = authDAO.createAuth(userData.username());
            return new LoginResult(userData.username(), authToken);
        } catch (DataAccessException ex) {
            throw new Exception("Error: unauthorized");
        }
    }

    public void logout(LogoutRequest request) {

    }

}
