package client;

import chess.ChessGame;
import com.google.gson.Gson;
import websocket.messages.ErrorServerMessage;
import websocket.messages.LoadServerMessage;
import websocket.messages.NotificationServerMessage;
import websocket.messages.ServerMessage;

import javax.websocket.ContainerProvider;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.net.URI;

public class WebSocketClient {
    private Session session;
    private GamePlayClient gamePlayClient;

    public WebSocketClient(String url, GamePlayClient gamePlayClient) throws Exception {
        this.gamePlayClient = gamePlayClient;
        URI uri = new URI(url.replace("http", "ws"));
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

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

    private void load_game(LoadServerMessage serverMessage) {
        gamePlayClient.printBoard(serverMessage.game.getBoard());
    }

    private void notification(NotificationServerMessage serverMessage) {
        gamePlayClient.printNotification(serverMessage.message);
    }

    private void error(ErrorServerMessage serverMessage) {
        gamePlayClient.printNotification(serverMessage.errorMessage);
    }
}
