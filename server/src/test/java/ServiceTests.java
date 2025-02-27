import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.util.log.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requestResult.*;
import service.Service;
import service.UserService;

public class ServiceTests {
    UserDao users = new MemoryUserDao();
    GameDao games = new MemoryGameDao();
    AuthDao tokens = new MemoryAuthDao();

    UserService service = new UserService(users, games, tokens);

    @BeforeEach
    void reset() {
        service.clear();
    }

    @Test
    void clear() throws DataAccessException {
        UserDao users = new MemoryUserDao();
        GameDao games = new MemoryGameDao();
        AuthDao tokens = new MemoryAuthDao();

        users.addUser(new UserData("isaac", "isaac", "isaac"));
        games.addGame(new GameData(1, "white", "black", "game", new ChessGame()));
        tokens.addAuthToken(new AuthData("authToken", "isaac"));

        Service service = new Service(users, games, tokens);

        service.clear();

        assert(users.listUsers().isEmpty());
        assert(games.listGames().isEmpty());
        assert(tokens.listAuthTokens().isEmpty());
    }

    @Test
    void register_200() throws DataAccessException {
//        UserDao users = new MemoryUserDao();
//        GameDao games = new MemoryGameDao();
//        AuthDao tokens = new MemoryAuthDao();
//
//        UserService service = new UserService(users, games, tokens);
        RegisterRequest request = new RegisterRequest("isaac", "password", "email");

        RegisterResult result = service.register(request);

        assert !users.listUsers().isEmpty();
        assert !tokens.listAuthTokens().isEmpty();
        assert result.username().equals("isaac");
    }

    @Test
    void register_400() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("", "password", "email");

        RegisterResult result = service.register(request);

        assert result.message().equals("Error: bad request");
    }

    @Test
    void register_403() throws DataAccessException {
        RegisterRequest requestOne = new RegisterRequest("isaac", "password", "email");
        RegisterRequest requestTwo = new RegisterRequest("isaac", "password", "email");

        service.register(requestOne);
        RegisterResult result = service.register(requestTwo);

        assert result.message().equals("Error: already taken");
        assert users.listUsers().size() == 1;
    }

    @Test
    void login_200() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("isaac", "password", "email");
        LoginRequest loginRequest = new LoginRequest("isaac", "password");

        service.register(registerRequest);
        LoginResult result = service.login(loginRequest);

        assert result.username().equals("isaac");
        assert !result.authToken().isBlank();
    }

    @Test
    void login_401() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("isaac", "password", "email");
        LoginRequest loginRequest = new LoginRequest("isaac", "password1");

        service.register(registerRequest);
        LoginResult result = service.login(loginRequest);

        assert result.username() == null;
        assert result.message().equals("Error: unauthorized");
    }

    @Test
    void logout_200() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("isaac", "password", "email");

        RegisterResult registerResult = service.register(registerRequest);

        LogoutRequest logoutRequest = new LogoutRequest(registerResult.authToken());
        BlankResult result = service.logout(logoutRequest);

        assert result.message() == null;
        assert users.getUser(registerResult.username()) == null;
        assert tokens.getAuthData(registerResult.authToken()) == null;
    }
}
