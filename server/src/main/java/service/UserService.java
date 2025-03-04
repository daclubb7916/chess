package service;

import dataaccess.*;
import exception.ResponseException;
import request.*;
import result.*;
import model.*;

import java.util.Objects;

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
            if (!Objects.equals(ex.getMessage(), "User is not in DataBase")) {
                throw new ResponseException(500, "Error: " + ex.getMessage());
            }
            UserData userData = new UserData(request.username(), request.password(), request.email());
            try {
                userDAO.createUser(userData);
                String authToken = authDAO.createAuth(userData.username());
                return new RegisterResult(userData.username(), authToken);
            } catch (DataAccessException exc) {
                throw new ResponseException(500, "Error: " + exc.getMessage());
            }
        }
    }

    public LoginResult login(LoginRequest request) throws ResponseException {
        try {
            UserData userData = userDAO.getUser(request.username());
            userDAO.validatePassword(userData, request.password());
            String authToken = authDAO.createAuth(userData.username());
            return new LoginResult(userData.username(), authToken);
        } catch (DataAccessException ex) {
            if (Objects.equals(ex.getMessage(), "Password does not match") ||
                    (Objects.equals(ex.getMessage(), "User is not in DataBase"))) {
                throw new ResponseException(401, "Error: unauthorized");
            }
            throw new ResponseException(500, "Error: " + ex.getMessage());
        }
    }

    public LogoutResult logout(LogoutRequest request) throws ResponseException {
        try {
            AuthData authData = authDAO.getAuth(request.authorization());
            authDAO.deleteAuth(authData.authToken());
            return new LogoutResult();
        } catch (DataAccessException ex) {
            if (Objects.equals(ex.getMessage(), "AuthToken does not exist")) {
                throw new ResponseException(401, "Error: unauthorized");
            }
            throw new ResponseException(500, "Error: " + ex.getMessage());
        }
    }

}
