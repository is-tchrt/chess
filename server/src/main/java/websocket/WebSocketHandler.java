package websocket;

import com.google.gson.Gson;
import dataaccess.AuthDao;
import dataaccess.DataAccessException;
import dataaccess.MySqlAuthDao;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

@WebSocket
public class WebSocketHandler {
    AuthDao authDao = new MySqlAuthDao();
    ClientsManager clients = new ClientsManager();

    public WebSocketHandler() throws DataAccessException {
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command, session);
            case MAKE_MOVE -> makeMove(new Gson().fromJson(message, MakeMoveCommand.class));
            case LEAVE -> leave(command);
            case RESIGN -> resign(command);
        }
    }

    private void connect(UserGameCommand command, Session session) {
        try {
            String username = getUsernameFromCommand(command);
            clients.add(command.getGameID(), username, session);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
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
}
