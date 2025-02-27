package service;

import dataaccess.AuthDao;
import dataaccess.GameDao;
import dataaccess.UserDao;
import model.AuthData;
import model.UserData;
import requestResult.*;

public class UserService extends Service {
    public UserService(UserDao users, GameDao games, AuthDao tokens) {
        super(users, games, tokens);
    }

    public RegisterResult register(RegisterRequest request) {
        RegisterResult result;
        if (!isValidRequest(request)) {
            result = new RegisterResult(null, null, "Error: bad request");
        } else if (!isUniqueUsername(request.username())) {
            result = new RegisterResult(null, null, "Error: already taken");
        } else {
            try {
                users.addUser(new UserData(request.username(), request.password(), request.email()));
                AuthData authToken = new AuthData(generateAuthToken(), request.username());
                tokens.addAuthToken(authToken);
                result = new RegisterResult(authToken.username(), authToken.authToken(), null);
            } catch (Exception e) {
                result = new RegisterResult(null, null, "Error: ".concat(e.getMessage()));
            }
        }
        return result;
    }

    public LoginResult login(LoginRequest request) {
        LoginResult result;
        if (users.getUserByNameAndPassword(request.username(), request.password()) != null) {
            try {
                String authToken = generateAuthToken();
                tokens.addAuthToken(new AuthData(authToken, request.username()));
                result = new LoginResult(request.username(), authToken, null);
            } catch (Exception e) {
                result = new LoginResult(null, null, "Error: ".concat(e.getMessage()));
            }
        } else {
            result = new LoginResult(null, null, "Error: unauthorized");
        }
        return result;
    }

    public BlankResult logout(LogoutRequest request) {
        BlankResult result;
        System.out.println(request.authToken());
        if (isValidAuthToken(request.authToken())) {
            try {
                AuthData authData = tokens.getAuthData(request.authToken());
                tokens.removeAuthData(authData.authToken());
                users.removeUser(authData.username());
                result = new BlankResult(null);
            } catch (Exception e) {
                result = new BlankResult("Error: ". concat(e.getMessage()));
            }
        } else {
            result = new BlankResult("Error: unauthorized");
        }
        return result;
    }

    private boolean isValidRequest(RegisterRequest request) {
        return !request.username().isBlank() && !request.password().isBlank() && !request.email().isBlank();
    }

    private boolean isUniqueUsername(String userName) {
        return users.getUser(userName) == null;
    }
}
