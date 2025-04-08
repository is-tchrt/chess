package handler;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

@WebSocket
public class WebSocketHandler {

    @OnWebSocketMessage
    public void onMessage(String message) {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command);
            case MAKE_MOVE -> makeMove(new Gson().fromJson(message, MakeMoveCommand.class));
            case LEAVE -> leave(command);
            case RESIGN -> resign(command);
        }
    }

    private void connect(UserGameCommand command) {
        throw new RuntimeException("Not implemented");
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
}
