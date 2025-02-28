package service;

import chess.ChessGame;
import dataaccess.AuthDao;
import dataaccess.GameDao;
import dataaccess.UserDao;
import model.GameData;
import requestResult.CreateGameRequest;
import requestResult.CreateGameResult;
import requestResult.ListGamesRequest;
import requestResult.ListGamesResult;

import java.util.ArrayList;
import java.util.Collection;

public class GameService extends Service {
    private int nextGameID = 0;

    public GameService(UserDao users, GameDao games, AuthDao tokens) {
        super(users, games, tokens);
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) {
        CreateGameResult result;
        if (request.gameName().isBlank()) {
            result = new CreateGameResult(null, "Error: bad request");
        } else if (isValidAuthToken(authToken)) {
            try {
                games.addGame(new GameData(nextGameID, "", "", request.gameName(), new ChessGame()));
                result = new CreateGameResult(nextGameID, null);
                nextGameID++;
            } catch (Exception e) {
                result = new CreateGameResult(null, "Error: ".concat(e.getMessage()));
            }
        } else {
            result = new CreateGameResult(null, "Error: unauthorized");
        }
        return result;
    }

    public ListGamesResult listGame(ListGamesRequest request) {
        ListGamesResult result;
        if (isValidAuthToken(request.authToken())) {
            try {
                Collection<GameData> gameList = getGameMetaData(games.listGames());
                result = new ListGamesResult(gameList, null);
            } catch (Exception e) {
                result = new ListGamesResult(null, "Error: ".concat(e.getMessage()));
            }
        } else {
            result = new ListGamesResult(null, "Error: unauthorized");
        }
        return result;
    }

    /*
    Takes an array of GameData objects and removes the actual game, returning only the
    metadata fields.
     */
    private Collection<GameData> getGameMetaData(Collection<GameData> gameList) {
        Collection<GameData> newGameList = new ArrayList<>();
        for (GameData game : gameList) {
            newGameList.add(new GameData(game.GameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), null));
        }
        return newGameList;
    }
}
