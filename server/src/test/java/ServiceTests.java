import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requestResult.*;
import service.GameService;
import service.Service;
import service.UserService;

public class ServiceTests {
    UserDao users = new MemoryUserDao();
    GameDao games = new MemoryGameDao();
    AuthDao tokens = new MemoryAuthDao();

    UserService userService = new UserService(users, games, tokens);
    GameService gameService = new GameService(users, games, tokens);

    @BeforeEach
    void reset() {
        userService.clear();
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
//        UserService userService = new UserService(users, games, tokens);
        RegisterRequest request = new RegisterRequest("isaac", "password", "email");

        RegisterResult result = userService.register(request);

        assert !users.listUsers().isEmpty();
        assert !tokens.listAuthTokens().isEmpty();
        assert result.username().equals("isaac");
    }

    @Test
    void register_400() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("", "password", "email");

        RegisterResult result = userService.register(request);

        assert result.message().equals("Error: bad request");
    }

    @Test
    void register_403() throws DataAccessException {
        RegisterRequest requestOne = new RegisterRequest("isaac", "password", "email");
        RegisterRequest requestTwo = new RegisterRequest("isaac", "password", "email");

        userService.register(requestOne);
        RegisterResult result = userService.register(requestTwo);

        assert result.message().equals("Error: already taken");
        assert users.listUsers().size() == 1;
    }

    @Test
    void login_200() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("isaac", "password", "email");
        LoginRequest loginRequest = new LoginRequest("isaac", "password");

        userService.register(registerRequest);
        LoginResult result = userService.login(loginRequest);

        assert result.username().equals("isaac");
        assert !result.authToken().isBlank();
    }

    @Test
    void login_401() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("isaac", "password", "email");
        LoginRequest loginRequest = new LoginRequest("isaac", "password1");

        userService.register(registerRequest);
        LoginResult result = userService.login(loginRequest);

        assert result.username() == null;
        assert result.message().equals("Error: unauthorized");
    }

    @Test
    void logout_200() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("isaac", "password", "email");

        RegisterResult registerResult = userService.register(registerRequest);

        LogoutRequest logoutRequest = new LogoutRequest(registerResult.authToken());
        BlankResult result = userService.logout(logoutRequest);

        assert result.message() == null;
        assert users.getUser(registerResult.username()) == null;
        assert tokens.getAuthData(registerResult.authToken()) == null;
    }

    @Test
    void logout_401() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("isaac", "password", "email");

        RegisterResult registerResult = userService.register(registerRequest);

        LogoutRequest logoutRequest = new LogoutRequest("invalid authToken");
        BlankResult result = userService.logout(logoutRequest);

        assert result.message().equals("Error: unauthorized");
    }

    @Test
    void createGame_200() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("isaac", "password", "email");

        RegisterResult registerResult = userService.register(registerRequest);

        CreateGameRequest createGameRequest = new CreateGameRequest("new game");
        CreateGameResult result = gameService.createGame(createGameRequest, registerResult.authToken());

        assert result.message() == null;
        assert result.gameID() != null;
        assert games.listGames().size() == 1;
    }

    @Test
    void createGame_400() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("isaac", "password", "email");

        RegisterResult registerResult = userService.register(registerRequest);

        CreateGameRequest createGameRequest = new CreateGameRequest("");
        CreateGameResult result = gameService.createGame(createGameRequest, registerResult.authToken());

        assert result.message().equals("Error: bad request");
    }

    @Test
    void createGame_401() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("isaac", "password", "email");

        RegisterResult registerResult = userService.register(registerRequest);

        CreateGameRequest createGameRequest = new CreateGameRequest("new game");
        CreateGameResult result = gameService.createGame(createGameRequest, "invalid authToken");

        assert result.message().equals("Error: unauthorized");
    }

    @Test
    void listGames_200() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("isaac", "password", "email");
        RegisterResult registerResult = userService.register(registerRequest);

        CreateGameRequest createGameRequest = new CreateGameRequest("new game");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest, registerResult.authToken());

        ListGamesRequest listGamesRequest = new ListGamesRequest(registerResult.authToken());
        ListGamesResult result = gameService.listGame(listGamesRequest);

        assert result.message() == null;
        assert result.games().size() == 1;
    }

    @Test
    void listGames_401() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("isaac", "password", "email");
        RegisterResult registerResult = userService.register(registerRequest);

        ListGamesRequest listGamesRequest = new ListGamesRequest(registerResult.authToken());
        ListGamesResult result = gameService.listGame(listGamesRequest);

        assert result.message().equals("Error: unauthorized");
    }
}
