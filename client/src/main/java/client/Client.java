package client;

public abstract class Client {
    ServerFacade serverFacade;
    String authToken;
    String username;

    public Client(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    abstract String eval(String line);
}
