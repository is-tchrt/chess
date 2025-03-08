package service;

import chess.ChessGame;
import dataaccess.*;
import http.request.*;
import http.result.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServiceTests {
    UserDao users = new MemoryUserDao();
    GameDao games = new MemoryGameDao();
    AuthDao tokens = new MemoryAuthDao();

    UserService userService = new UserService(users, games, tokens);
    GameService gameService = new GameService(users, games, tokens);

    @BeforeEach
    void reset() throws DataAccessException {
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
    void register200() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("isaac", "password", "email");

        RegisterResult result = userService.register(request);

        assert !users.listUsers().isEmpty();
        assert !tokens.listAuthTokens().isEmpty();
        assert result.username().equals("isaac");
    }

    @Test
    void register400() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("", "password", "email");

        RegisterResult result = userService.register(request);

        assert result.message().equals("Error: bad request");
    }

    @Test
    void register403() throws DataAccessException {
        RegisterRequest requestOne = new RegisterRequest("isaac", "password", "email");
        RegisterRequest requestTwo = new RegisterRequest("isaac", "password", "email");

        userService.register(requestOne);
        RegisterResult result = userService.register(requestTwo);

        assert result.message().equals("Error: already taken");
        assert users.listUsers().size() == 1;
    }

    @Test
    void login200() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("isaac", "password", "email");
        LoginRequest loginRequest = new LoginRequest("isaac", "password");

        userService.register(registerRequest);
        LoginResult result = userService.login(loginRequest);

        assert result.username().equals("isaac");
        assert !result.authToken().isBlank();
    }

    @Test
    void login401() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("isaac", "password", "email");
        LoginRequest loginRequest = new LoginRequest("isaac", "password1");

        userService.register(registerRequest);
        LoginResult result = userService.login(loginRequest);

        assert result.username() == null;
        assert result.message().equals("Error: unauthorized");
    }

    @Test
    void logout200() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("isaac", "password", "email");

        RegisterResult registerResult = userService.register(registerRequest);

        LogoutRequest logoutRequest = new LogoutRequest(registerResult.authToken());
        BlankResult result = userService.logout(logoutRequest);

        assert result.message() == null;
        assert users.getUser(registerResult.username()) == null;
        assert tokens.getAuthData(registerResult.authToken()) == null;
    }

    @Test
    void logout401() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("isaac", "password", "email");
        userService.register(registerRequest);

        LogoutRequest logoutRequest = new LogoutRequest("invalid authToken");
        BlankResult result = userService.logout(logoutRequest);

        assert result.message().equals("Error: unauthorized");
    }

    @Test
    void createGame200() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("isaac", "password", "email");

        RegisterResult registerResult = userService.register(registerRequest);

        CreateGameRequest createGameRequest = new CreateGameRequest("new game");
        CreateGameResult result = gameService.createGame(createGameRequest, registerResult.authToken());

        assert result.message() == null;
        assert result.gameID() != null;
        assert games.listGames().size() == 1;
    }

    @Test
    void createGame400() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("isaac", "password", "email");

        RegisterResult registerResult = userService.register(registerRequest);

        CreateGameRequest createGameRequest = new CreateGameRequest("");
        CreateGameResult result = gameService.createGame(createGameRequest, registerResult.authToken());

        assert result.message().equals("Error: bad request");
    }

    @Test
    void createGame401() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("isaac", "password", "email");
        userService.register(registerRequest);

        CreateGameRequest createGameRequest = new CreateGameRequest("new game");
        CreateGameResult result = gameService.createGame(createGameRequest, "invalid authToken");

        assert result.message().equals("Error: unauthorized");
    }

    @Test
    void listGames200() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("isaac", "password", "email");
        RegisterResult registerResult = userService.register(registerRequest);

        CreateGameRequest createGameRequest = new CreateGameRequest("new game");
        gameService.createGame(createGameRequest, registerResult.authToken());

        ListGamesRequest listGamesRequest = new ListGamesRequest(registerResult.authToken());
        ListGamesResult result = gameService.listGame(listGamesRequest);

        assert result.message() == null;
        assert result.games().size() == 1;
    }

    @Test
    void listGames401() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("isaac", "password", "email");
        userService.register(registerRequest);

        ListGamesRequest listGamesRequest = new ListGamesRequest("invalid authToken");
        ListGamesResult result = gameService.listGame(listGamesRequest);

        assert result.message().equals("Error: unauthorized");
    }

    @Test
    void joinGame200() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("isaac", "password", "email");
        RegisterResult registerResult = userService.register(registerRequest);

        CreateGameRequest createGameRequest = new CreateGameRequest("new game");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest, registerResult.authToken());

        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", createGameResult.gameID());
        BlankResult result = gameService.joinGame(joinGameRequest, registerResult.authToken());

        assert result.message() == null;
        assert games.getGame(createGameResult.gameID()).whiteUsername().equals("isaac");
    }

    @Test
    void joinGame400FakeGame() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("isaac", "password", "email");
        RegisterResult registerResult = userService.register(registerRequest);

        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", 77);
        BlankResult result = gameService.joinGame(joinGameRequest, registerResult.authToken());

        assert result.message().equals("Error: bad request");
    }

    @Test
    void joinGame400BadColor() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("isaac", "password", "email");
        RegisterResult registerResult = userService.register(registerRequest);

        CreateGameRequest createGameRequest = new CreateGameRequest("new game");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest, registerResult.authToken());

        JoinGameRequest joinGameRequest = new JoinGameRequest("BLUE", createGameResult.gameID());
        BlankResult result = gameService.joinGame(joinGameRequest, registerResult.authToken());

        assert result.message().equals("Error: bad request");
    }

    @Test
    void joinGame401() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("isaac", "password", "email");
        userService.register(registerRequest);

        JoinGameRequest joinGameRequest = new JoinGameRequest("BLACK", 10);
        BlankResult result = gameService.joinGame(joinGameRequest, "invalid authToken");

        assert result.message().equals("Error: unauthorized");
    }

    @Test
    void joinGame403() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("isaac", "password", "email");
        RegisterResult registerResult = userService.register(registerRequest);

        games.addGame(new GameData(0, "jacob", "henry", "new game", new ChessGame()));

        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", 0);
        BlankResult result = gameService.joinGame(joinGameRequest, registerResult.authToken());

        assert result.message().equals("Error: already taken");
    }
}
