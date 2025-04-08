package websocket.messages;

public class NotificationServerMessage extends ServerMessage {
    public String message;

    public NotificationServerMessage(ServerMessageType type, String message) {
        super(type);
        this.message = message;
    }
}
