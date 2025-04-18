package service;

import dataaccess.AuthDao;
import dataaccess.DataAccessException;
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

    public void clear() throws DataAccessException {
        users.clearUsers();
        games.clearGames();
        tokens.clearAuthTokens();
    }

    protected boolean isValidAuthToken(String authToken) throws DataAccessException {
        AuthData result = tokens.getAuthData(authToken);
        return result != null;
    }

    protected String generateAuthToken() {
        return UUID.randomUUID().toString();
    }
}
