package service;

import chess.ChessGame;
import dataaccess.AuthDao;
import dataaccess.DataAccessException;
import dataaccess.GameDao;
import dataaccess.UserDao;
import http.request.*;
import http.result.BlankResult;
import http.result.CreateGameResult;
import http.result.ListGamesResult;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public class GameService extends Service {
    private int nextGameID = 1;

    public GameService(UserDao users, GameDao games, AuthDao tokens) {
        super(users, games, tokens);
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) {
        CreateGameResult result;
        if (request.gameName().isBlank()) {
            result = new CreateGameResult(null, "Error: bad request");
        } else if (isValidAuthToken(authToken)) {
            try {
                games.addGame(new GameData(nextGameID, null, null, request.gameName(), new ChessGame()));
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

    public BlankResult joinGame(JoinGameRequest request, String authToken) throws DataAccessException {
        BlankResult result;
        if (!isValidAuthToken(authToken)) {
            result = new BlankResult("Error: unauthorized");
        } else if (!isValidPlayerColor(request.playerColor()) || !isValidGameID(request.gameID())) {
            result = new BlankResult("Error: bad request");
        } else if (!isAvailablePlayerColor(request)) {
            result = new BlankResult("Error: already taken");
        } else {
            result = joinGameWithValidRequest(request, authToken);
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
            newGameList.add(new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), null));
        }
        return newGameList;
    }

    private boolean isValidPlayerColor(String playerColor) {
        return (playerColor != null) && (playerColor.equals("WHITE") || playerColor.equals("BLACK"));
    }

    private boolean isValidGameID(int gameID) throws DataAccessException {
        return games.getGame(gameID) != null;
    }

    private boolean isAvailablePlayerColor(JoinGameRequest request) throws DataAccessException {
        GameData gameData = games.getGame(request.gameID());
        if (request.playerColor().equals("WHITE")) {
            return gameData.whiteUsername() == null;
        } else {
            return gameData.blackUsername() == null;
        }
    }

    private BlankResult joinGameWithValidRequest(JoinGameRequest request, String authToken) {
        BlankResult result;
        try {
            GameData gameData = games.getGame(request.gameID());
            String userName = tokens.getAuthData(authToken).username();
            if (request.playerColor().equals("WHITE")) {
                games.addGame(new GameData(gameData.gameID(), userName, gameData.blackUsername(),
                        gameData.gameName(), gameData.game()));
            } else {
                games.addGame(new GameData(gameData.gameID(), gameData.whiteUsername(), userName,
                        gameData.gameName(), gameData.game()));
            }
            result = new BlankResult(null);
        } catch (Exception e) {
//                result = new CreateGameResult(null, "Error: ".concat(e.getMessage()));
            result = new BlankResult("Error: ".concat(e.getMessage()));
        }
        return result;
    }
}
