package client;

import chess.ChessMove;
import com.google.gson.Gson;
import model.GameData;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorServerMessage;
import websocket.messages.LoadServerMessage;
import websocket.messages.NotificationServerMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.net.URI;

public class WebSocketClient extends Endpoint {
    private final Session session;
    private final GamePlayClient gamePlayClient;

    public WebSocketClient(String url, GamePlayClient gamePlayClient) throws Exception {
        this.gamePlayClient = gamePlayClient;
        URI uri = new URI(url.replace("http", "ws") + "/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
        sendUserCommand(UserGameCommand.CommandType.CONNECT);

        session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                switch (serverMessage.getServerMessageType()) {
                    case LOAD_GAME -> load_game(new Gson().fromJson(message, LoadServerMessage.class));
                    case NOTIFICATION -> notification(new Gson().fromJson(message, NotificationServerMessage.class));
                    case ERROR -> error(new Gson().fromJson(message, ErrorServerMessage.class));
                }
            }
        });
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void sendUserCommand(UserGameCommand.CommandType type) {
        try {
            UserGameCommand command = new UserGameCommand(type, gamePlayClient.authToken,
                    gamePlayClient.game.gameID());
            session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (Exception e) {
            gamePlayClient.printNotification("Error: " + e.getMessage());
        }
    }

    public void sendMakeMove(ChessMove move) {
        try {
            MakeMoveCommand command = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, gamePlayClient.authToken,
                    gamePlayClient.game.gameID(), move);
            session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (Exception e) {
            gamePlayClient.printNotification("Error: " + e.getMessage());
        }
    }

    private void load_game(LoadServerMessage serverMessage) {
        GameData gameData = gamePlayClient.game;
        GameData newGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(),
                gameData.gameName(), serverMessage.game);
        gamePlayClient.setGame(newGameData);
        gamePlayClient.printBoard();
    }

    private void notification(NotificationServerMessage serverMessage) {
        gamePlayClient.printNotification(serverMessage.message);
    }

    private void error(ErrorServerMessage serverMessage) {
        gamePlayClient.printNotification(serverMessage.errorMessage);
    }
}
