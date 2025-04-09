package websocket;

import com.google.gson.Gson;
import dataaccess.*;
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
            switch (command.getCommandType()) {
                case CONNECT -> connect(command, session);
                case MAKE_MOVE -> makeMove(new Gson().fromJson(message, MakeMoveCommand.class));
                case LEAVE -> leave(command);
                case RESIGN -> resign(command);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void connect(UserGameCommand command, Session session) throws IOException {
        try {
            String username = getUsernameFromCommand(command);
            Client client = new Client(username, session);
            clients.add(command.getGameID(), client);
            sendLoadGame(client, command.getGameID());
            String playerStatus = getPlayerStatusFromCommand(command, username);
            clients.notifyOtherClients(command.getGameID(), client, username + " has joined the game as " + playerStatus + ".");
        } catch (Exception e) {
            ErrorServerMessage error = new ErrorServerMessage(ServerMessage.ServerMessageType.ERROR, "Error: " + e.getMessage());
            session.getRemote().sendString(new Gson().toJson(error));
        }
    }

    private void makeMove(MakeMoveCommand command) {
        throw new RuntimeException("Not implemented");
    }

    private void leave(UserGameCommand command) {
        throw new RuntimeException("Not implemented");
    }

    private void resign(UserGameCommand command) {
        throw new RuntimeException("Not implemented");
    }

    private String getUsernameFromCommand(UserGameCommand command) throws DataAccessException {
        String authToken = command.getAuthToken();
        return authDao.getAuthData(authToken).username();
    }

    private String getPlayerStatusFromCommand(UserGameCommand command, String username) throws DataAccessException {
        GameData gameData = gameDao.getGame(command.getGameID());
        if (gameData.blackUsername().equals(username)) {
            return "black";
        } else if (gameData.whiteUsername().equals(username)) {
            return "white";
        } else {
            return "an observer";
        }
    }

    private void sendLoadGame(Client client, Integer gameID) throws Exception {
        GameData gameData = gameDao.getGame(gameID);
        if (gameData != null) {
            client.sendLoadGame(gameData.game());
        } else {
            throw new DataAccessException("Invalid gameID");
        }
    }
}
