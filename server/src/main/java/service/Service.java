package service;

import dataaccess.AuthDao;
import dataaccess.GameDao;
import dataaccess.UserDao;
import model.AuthData;
import requestResult.RegisterRequest;
import requestResult.RegisterResult;

import java.util.UUID;

public class Service {
    protected UserDao users;
    protected GameDao games;
    protected AuthDao tokens;

    public Service(UserDao users, GameDao games, AuthDao tokens) {
        this.users = users;
        this.games = games;
        this.tokens = tokens;
    }

    public void clear() {
        users.clearUsers();
        games.clearGames();
        tokens.clearAuthTokens();
    }

    protected boolean isValidAuthToken(AuthData authData) {
        return tokens.getAuthData(authData.authToken()).username().equals(authData.username());
    }

    protected String generateAuthToken() {
        return UUID.randomUUID().toString();
    }
}
