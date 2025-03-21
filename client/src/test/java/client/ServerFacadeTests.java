package client;

import DataTypes.LoginResponse;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);
        serverFacade.clear();
    }

    @AfterAll
    static void stopServer() {
        serverFacade.clear();
        server.stop();
    }

    @Test
    public void register() {
        UserData user = new UserData("user", "password", "email");
        LoginResponse result = serverFacade.register(user);

        assert result.username().equals("user");
        assert !result.authToken().isBlank();
    }

    @Test
    public void registerDuplicate() {
        UserData user = new UserData("user", "password", "email");
        UserData user2 = new UserData("user", "password", "email");

        serverFacade.register(user);
        try {
            LoginResponse result = serverFacade.register(user2);
        } catch (Exception e) {
            assert e.getMessage().equals("Error: already taken");
        }
    }

    @Test
    public void registerBadRequest() {
        UserData user = new UserData("user", "password", "");

        try {
            LoginResponse result = serverFacade.register(user);
        } catch (Exception e) {
            assert e.getMessage().equals("Error: bad request");
        }
    }

    @Test
    public void login() {
        UserData user = new UserData("user", "password", "email");

        serverFacade.register(user);
        LoginResponse response = serverFacade.login(user.username(), user.password());

        assert response.username().equals("user");
        assert !response.authToken().isBlank();
    }

    @Test
    public void loginUnauthorized() {
        UserData user = new UserData("user", "password", "email");

        serverFacade.register(user);

        try {
            LoginResponse result = serverFacade.login(user.username(), "wrong password");
        } catch (Exception e) {
            assert e.getMessage().equals("Error: unauthorized");
        }
    }

    @Test
    public void logout() {
        UserData user = new UserData("user", "password", "email");

        LoginResponse response = serverFacade.register(user);
        serverFacade.logout(response.authToken());

        String result = "";
        try {
            serverFacade.createGame("game", "");
        } catch (Exception e) {
            result = e.getMessage();
        }
        assert result.equals("Error: unauthorized");
    }

    @Test
    public void logoutUnauthorized() {
        String result = "";
        try {
            serverFacade.logout("");
        } catch (Exception e) {
            result = e.getMessage();
        }
        assert result.equals("Error: unauthorized");
    }

    @Test
    public void createGame() {
        UserData user = new UserData("user", "password", "email");
        String gameName = "game";

        LoginResponse loginResponse = serverFacade.register(user);
        int response = serverFacade.createGame(gameName, loginResponse.authToken());

        assert response > 0;
    }

    @Test
    public void createGameNoName() {
        UserData user = new UserData("user", "password", "email");
        LoginResponse loginResponse = serverFacade.register(user);

        String result = "";
        try {
            serverFacade.createGame("", loginResponse.authToken());
        } catch (Exception e) {
            result = e.getMessage();
        }
        assert result.equals("Error: bad request");
    }

    @Test
    public void createGameUnauthorized() {
        String result = "";
        try {
            serverFacade.createGame("game", "");
        } catch (Exception e) {
            result = e.getMessage();
        }
        assert result.equals("Error: unauthorized");
    }
}
