package service;

import dataaccess.*;
import exception.ResponseException;
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

    public RegisterResult register(RegisterRequest request) throws ResponseException {
        try {
            UserData userData = userDAO.getUser(request.username());
            throw new ResponseException(403, "Error: already taken");
        } catch (DataAccessException ex) {
            UserData userData = new UserData(request.username(), request.password(), request.email());
            userDAO.createUser(userData);
            String authToken = authDAO.createAuth(userData.username());
            return new RegisterResult(userData.username(), authToken);
        }
    }

    public LoginResult login(LoginRequest request) throws ResponseException {
        try {
            UserData userData = userDAO.getUser(request.username());
            userDAO.validatePassword(userData, request.password());
            String authToken = authDAO.createAuth(userData.username());
            return new LoginResult(userData.username(), authToken);
        } catch (DataAccessException ex) {
            throw new ResponseException(401, "Error: unauthorized");
        }
    }

    public void logout(LogoutRequest request) {

    }

}
