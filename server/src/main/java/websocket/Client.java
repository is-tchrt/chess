package websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.LoadServerMessage;
import websocket.messages.NotificationServerMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Objects;

public class Client {
    public String username;
    public Session session;

    public Client(String username, Session session) {
        this.username = username;
        this.session = session;
    }

    public void sendLoadGame(ChessGame game) throws IOException {
        LoadServerMessage message = new LoadServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        session.getRemote().sendString(new Gson().toJson(message));
    }

    public void sendNotification(String message) throws IOException {
        NotificationServerMessage serverMessage = new NotificationServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        session.getRemote().sendString(new Gson().toJson(serverMessage));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Client client = (Client) o;
        return Objects.equals(username, client.username) && Objects.equals(session, client.session);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, session);
    }
}
