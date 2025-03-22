package client;

import model.GameData;

public abstract class Client {
    ServerFacade serverFacade;
    String authToken;
    String username;
    GameData game;

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
