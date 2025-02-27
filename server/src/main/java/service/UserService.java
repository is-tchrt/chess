package service;

import dataaccess.AuthDao;
import dataaccess.GameDao;
import dataaccess.UserDao;
import model.AuthData;
import model.UserData;
import requestResult.LoginRequest;
import requestResult.LoginResult;
import requestResult.RegisterRequest;
import requestResult.RegisterResult;

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
            String authToken = generateAuthToken();
            result = new LoginResult(request.username(), authToken, null);
        } else {
            result = new LoginResult(null, null, "Error: unauthorized");
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
