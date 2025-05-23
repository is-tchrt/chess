package service;

import dataaccess.AuthDao;
import dataaccess.DataAccessException;
import dataaccess.GameDao;
import dataaccess.UserDao;
import http.request.*;
import http.result.BlankResult;
import http.result.LoginResult;
import http.result.RegisterResult;
import model.AuthData;
import model.UserData;

public class UserService extends Service {
    public UserService(UserDao users, GameDao games, AuthDao tokens) {
        super(users, games, tokens);
    }

    public RegisterResult register(RegisterRequest request) {
        RegisterResult result;
        try {
            if (!isValidRegisterRequest(request)) {
                result = new RegisterResult(null, null, "Error: bad request");
            } else if (!isUniqueUsername(request.username())) {
                result = new RegisterResult(null, null, "Error: already taken");
            } else {
                users.addUser(new UserData(request.username(), request.password(), request.email()));
                AuthData authToken = new AuthData(generateAuthToken(), request.username());
                tokens.addAuthToken(authToken);
                result = new RegisterResult(authToken.username(), authToken.authToken(), null);
            }
        } catch (Exception e) {
            result = new RegisterResult(null, null, "Error: ".concat(e.getMessage()));
        }
        return result;
    }

    public LoginResult login(LoginRequest request) {
        LoginResult result;
        try {
            if (users.getUserByNameAndPassword(request.username(), request.password()) != null) {
                String authToken = generateAuthToken();
                tokens.addAuthToken(new AuthData(authToken, request.username()));
                result = new LoginResult(request.username(), authToken, null);
            } else {
                result = new LoginResult(null, null, "Error: unauthorized");
            }
        } catch (Exception e) {
            result = new LoginResult(null, null, "Error: ".concat(e.getMessage()));
        }
        return result;
    }

    public BlankResult logout(LogoutRequest request) {
        BlankResult result;
        try {
            if (isValidAuthToken(request.authToken())) {
                AuthData authData = tokens.getAuthData(request.authToken());
                tokens.removeAuthData(authData.authToken());
                result = new BlankResult(null);
            } else {
                result = new BlankResult("Error: unauthorized");
            }
        }  catch (Exception e) {
            result = new BlankResult("Error: ".concat(e.getMessage()));
        }
        return result;
    }

    private boolean isValidRegisterRequest(RegisterRequest request) {
        return (request.username() != null) && (request.password() != null) && (request.email() != null) &&
                !request.username().isBlank() && !request.password().isBlank() && !request.email().isBlank();
    }

    private boolean isUniqueUsername(String userName) throws DataAccessException {
        return users.getUser(userName) == null;
    }
}
