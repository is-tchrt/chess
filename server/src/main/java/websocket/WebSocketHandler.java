package websocket;

import chess.ChessGame;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorServerMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    AuthDao authDao;
    GameDao gameDao;
    ClientsManager clients;

    public WebSocketHandler() {
        try {
            authDao = new MySqlAuthDao();
            gameDao = new MySqlGameDao();
            clients = new ClientsManager();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        try {
            String username = getUsernameFromCommand(command, session);
            Client client = new Client(username, session);
            switch (command.getCommandType()) {
                case CONNECT -> connect(command, client);
                case MAKE_MOVE -> makeMove(new Gson().fromJson(message, MakeMoveCommand.class), client);
                case LEAVE -> leave(command, client);
                case RESIGN -> resign(command, client);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void connect(UserGameCommand command, Client client) throws IOException {
        try {
            clients.add(command.getGameID(), client);
            sendLoadGame(client, command.getGameID());
            String playerStatus = getPlayerStatusFromCommand(command, client.username);
            clients.notifyOtherClients(command.getGameID(), client, client.username + " has joined the game as " + playerStatus + ".");
        } catch (Exception e) {
            client.sendError("Error: " + e.getMessage());
        }
    }

    private void makeMove(MakeMoveCommand command, Client client) throws IOException {
        try {
            canMakeMove(command, client);
            GameData gameData = getGameData(command.getGameID());
            gameData.game().makeMove(command.getMove());
            gameDao.updateGame(gameData);
            clients.allClientsLoadGame(command.getGameID(), gameData.game());
            clients.notifyOtherClients(command.getGameID(), client, client.username + " moved " +
                    makePositionHumanReadable(command.getMove().getStartPosition()) + " to " +
                    makePositionHumanReadable(command.getMove().getEndPosition()) + ".");
            sendCheckStatus(command.getGameID(), gameData.game(), getPlayerColorFromCommand(command, client.username));
        } catch (InvalidMoveException e) {
            client.sendError("Error: Invalid move");
        } catch (Exception e) {
            client.sendError("Error: " + e.getMessage());
        }
    }

    private void leave(UserGameCommand command, Client client) throws IOException {
        try {
            GameData gameData = getGameData(command.getGameID());
            GameData newGameData;
            if (gameData.blackUsername() != null && gameData.blackUsername().equals(client.username)) {
                newGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), null, gameData.gameName(), gameData.game());
            } else if (gameData.whiteUsername() != null && gameData.whiteUsername().equals(client.username)) {
                newGameData = new GameData(gameData.gameID(), null, gameData.blackUsername(), gameData.gameName(), gameData.game());
            } else {
                newGameData = gameData;
            }
            gameDao.updateGame(newGameData);
            clients.notifyOtherClients(command.getGameID(), client, client.username + " has left the game.");
            clients.remove(command.getGameID(), client.username);
        } catch (Exception e) {
            client.sendError("Error: " + e.getMessage());
        }
    }

    private void resign(UserGameCommand command, Client client) throws IOException {
        try {
            if (getPlayerColorFromCommand(command, client.username) == null) {
                throw new Exception("Non-players cannot resign.");
            }
            GameData gameData = getGameData(command.getGameID());
            if (!gameData.game().getInProgress()) {
                throw new Exception("This game has already ended.");
            }
            endGame(command.getGameID());
            clients.notifyAllClients(command.getGameID(), client.username + " has resigned.");
        } catch (Exception e) {
            client.sendError("Error: " + e.getMessage());
        }
    }

    private String getUsernameFromCommand(UserGameCommand command, Session session) throws Exception {
        String authToken = command.getAuthToken();
        AuthData authData = authDao.getAuthData(authToken);
        if (authData != null) {
            return authDao.getAuthData(authToken).username();
        } else {
            ErrorServerMessage error = new ErrorServerMessage(ServerMessage.ServerMessageType.ERROR, "Error: Unauthorized");
            session.getRemote().sendString(new Gson().toJson(error));
            throw new Exception("Unauthorized");
        }
    }

    private String getPlayerStatusFromCommand(UserGameCommand command, String username) throws DataAccessException {
        ChessGame.TeamColor color = getPlayerColorFromCommand(command, username);
        switch (color) {
            case WHITE -> {return "white";}
            case BLACK -> {return "black";}
            case null -> {return "an observer";}
        }
    }

    private ChessGame.TeamColor getPlayerColorFromCommand(UserGameCommand command, String username) throws DataAccessException {
        GameData gameData = gameDao.getGame(command.getGameID());
        if (gameData.blackUsername() != null && gameData.blackUsername().equals(username)) {
            return ChessGame.TeamColor.BLACK;
        } else if (gameData.whiteUsername() != null && gameData.whiteUsername().equals(username)) {
            return ChessGame.TeamColor.WHITE;
        } else {
            return null;
        }
    }

    private void sendLoadGame(Client client, Integer gameID) throws Exception {
        client.sendLoadGame(getGameData(gameID).game());
    }

    private GameData getGameData(Integer gameID) throws DataAccessException {
        GameData gameData = gameDao.getGame(gameID);
        if (gameData != null) {
            return gameData;
        } else {
            throw new DataAccessException("Invalid gameID");
        }
    }

    private void canMakeMove(MakeMoveCommand command, Client client) throws Exception {
            ChessGame.TeamColor color = getPlayerColorFromCommand(command, client.username);
            ChessGame game = getGameData(command.getGameID()).game();
            if (game.getBoard().getPiece(command.getMove().getStartPosition()) == null) {
                throw new Exception("Invalid move");
            }
            ChessGame.TeamColor moveColor = game.getBoard().getPiece(command.getMove().getStartPosition()).getTeamColor();
            if (!game.getInProgress()) {
                throw new Exception("This game has already ended.");
            }
            if (!moveColor.equals(color)) {
                throw new Exception("Piece is wrong color");
            }
    }

    private void sendCheckStatus(Integer gameID, ChessGame game, ChessGame.TeamColor color) throws DataAccessException {
        ChessGame.TeamColor otherColor;
        if (color == null) {
            return;
        }
        if (color.equals(ChessGame.TeamColor.WHITE)) {
            otherColor = ChessGame.TeamColor.BLACK;
        } else if (color.equals(ChessGame.TeamColor.BLACK)) {
            otherColor = ChessGame.TeamColor.WHITE;
        } else {return;}
        if (game.isInCheckmate(otherColor)) {
            endGame(gameID);
            clients.notifyAllClients(gameID, otherColor.name() + " is in checkmate!");
        } else if (game.isInCheck(otherColor)) {
            clients.notifyAllClients(gameID, otherColor.name() + " is in check.");
        } else if (game.isInStalemate(otherColor)) {
            endGame(gameID);
            clients.notifyAllClients(gameID, "Stalemate!");
        }
    }

    private String makePositionHumanReadable(ChessPosition position) {
        String column = switch (position.getColumn()) {
            case 1 -> "a";
            case 2 -> "b";
            case 3 -> "c";
            case 4 -> "d";
            case 5 -> "e";
            case 6 -> "f";
            case 7 -> "g";
            case 8 -> "h";
            default -> null;
        };
        String row = String.valueOf(position.getRow());
        return column + row;
    }

    private void endGame(int gameID) throws DataAccessException {
        GameData gameData = getGameData(gameID);
        gameData.game().setInProgress(false);
        gameDao.updateGame(gameData);
    }
}
