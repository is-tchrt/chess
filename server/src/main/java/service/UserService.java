package service;

import dataaccess.AuthDao;
import dataaccess.GameDao;
import dataaccess.UserDao;
import model.AuthData;
import model.UserData;
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
        } else {
            users.addUser(new UserData(request.username(), request.password(), request.email()));
            AuthData authToken = new AuthData(generateAuthToken(), request.username());
            tokens.addAuthToken(authToken);
            result = new RegisterResult(authToken.username(), authToken.authToken(), null);
        }
        return result;
    }

    protected boolean isValidRequest(RegisterRequest request) {
        return !request.username().isBlank() && !request.password().isBlank() && !request.email().isBlank();
    }
}
