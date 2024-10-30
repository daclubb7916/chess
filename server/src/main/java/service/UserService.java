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
    public void login(loginRequest request) {

    }
    public void logout(logoutRequest request) {

    }

}
