package service;

import dataaccess.AuthDao;
import dataaccess.GameDao;
import dataaccess.UserDao;
import model.AuthData;

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

    protected boolean isValidAuthToken(String authToken) {
        AuthData result = tokens.getAuthData(authToken);
        if (result == null) {
            return false;
        } else {
            return true;
        }
    }

    protected String generateAuthToken() {
        return UUID.randomUUID().toString();
    }
}
