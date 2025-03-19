package client;

import model.AuthData;
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
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void register() {
        UserData user = new UserData("user", "password", "email");
        AuthData result = serverFacade.register(user);

        assert result.username().equals("user");
        assert !result.authToken().isBlank();
    }
//
//    public void registerDuplicate() {
//        UserData user = new UserData("user", "password", "email");
//        UserData user2 = new UserData("user", "password", "email");
//
//        serverFacade.register(user);
//        try {
//            AuthData result = serverFacade.register(user2);
//        } catch (Exception e) {
//            assert e.getMessage().equals("Error: already taken");
//        }
//    }
}
