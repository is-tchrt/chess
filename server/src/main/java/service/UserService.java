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
        users.addUser(new UserData(request.username(), request.password(), request.email()));
        AuthData authToken = new AuthData(generateAuthToken(), request.username());
        tokens.addAuthToken(authToken);
        return new RegisterResult(authToken.username(), authToken.authToken(), null);
    }
}
