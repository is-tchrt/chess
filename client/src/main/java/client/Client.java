package client;

import chess.ChessGame;
import model.GameData;

import java.util.HashMap;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;

public abstract class Client {
    ServerFacade serverFacade;
    String authToken;
    String username;
    protected HashMap<Integer, GameData> gameList = new HashMap<>();

    public GameData getGame() {
        return game;
    }

    public void setGame(GameData game) {
        this.game = game;
    }

    GameData game;
    ChessGame.TeamColor color;
    protected final static String COMMAND_NAME_COLOR = SET_TEXT_COLOR_BLUE;
    protected final static String COMMAND_DESCRIPTION_COLOR = SET_TEXT_COLOR_GREEN;

    public Client(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public Client(Client other) {
        this.serverFacade = other.serverFacade;
        this.authToken = other.authToken;
        this.username = other.username;
        this.game = other.game;
        this.color = other.color;
    }

    abstract String eval(String line);
}
