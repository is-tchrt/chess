package client;

import model.GameData;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;

public abstract class Client {
    ServerFacade serverFacade;
    String authToken;
    String username;
    GameData game;
    protected final String COMMAND_NAME_COLOR = SET_TEXT_COLOR_BLUE;
    protected final String COMMAND_DESCRIPTION_COLOR = SET_TEXT_COLOR_GREEN;

    public Client(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public Client(Client other) {
        this.serverFacade = other.serverFacade;
        this.authToken = other.authToken;
        this.username = other.username;
    }

    abstract String eval(String line);
}
