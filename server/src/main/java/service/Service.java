package service;

import dataaccess.AuthDao;
import dataaccess.GameDao;
import dataaccess.UserDao;
import org.eclipse.jetty.server.Authentication;

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
}
