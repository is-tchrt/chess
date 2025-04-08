package websocket.messages;

import chess.ChessGame;

public class LoadServerMessage extends ServerMessage {
    public ChessGame game;

    public LoadServerMessage(ServerMessageType type, ChessGame game) {
        super(type);
        this.game = game;
    }
}
