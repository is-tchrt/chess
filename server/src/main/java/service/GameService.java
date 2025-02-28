package service;

import dataaccess.AuthDao;
import dataaccess.GameDao;
import dataaccess.UserDao;
import requestResult.CreateGameRequest;
import requestResult.CreateGameResult;

public class GameService extends Service {
    public GameService(UserDao users, GameDao games, AuthDao tokens) {
        super(users, games, tokens);
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) {
        throw new RuntimeException("Not implemented");
    }
}
