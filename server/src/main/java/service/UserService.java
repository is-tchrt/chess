package service;

import dataaccess.AuthDao;
import dataaccess.GameDao;
import dataaccess.UserDao;
import requestResult.RegisterRequest;
import requestResult.RegisterResult;

public class UserService extends Service {
    public UserService(UserDao users, GameDao games, AuthDao tokens) {
        super(users, games, tokens);
    }

    public RegisterResult register(RegisterRequest request) {
        throw new RuntimeException("Not Implemented");
    }
}
