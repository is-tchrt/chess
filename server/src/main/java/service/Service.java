package service;

import dataaccess.AuthDao;
import dataaccess.GameDao;
import dataaccess.UserDao;
import model.AuthData;
import requestResult.RegisterRequest;
import requestResult.RegisterResult;

public class Service {
    private UserDao users;
    private GameDao games;
    private AuthDao tokens;

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
}
