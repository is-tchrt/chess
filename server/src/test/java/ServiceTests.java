import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requestResult.RegisterRequest;
import requestResult.RegisterResult;
import service.Service;
import service.UserService;

import java.beans.beancontext.BeanContextChild;

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
    void register_success() throws DataAccessException {
//        UserDao users = new MemoryUserDao();
//        GameDao games = new MemoryGameDao();
//        AuthDao tokens = new MemoryAuthDao();
//
//        UserService service = new UserService(users, games, tokens);
        RegisterRequest request = new RegisterRequest("isaac", "password", "email");

        RegisterResult result = service.register(request);

        assert !users.listUsers().isEmpty();
        assert !tokens.listAuthTokens().isEmpty();
        assert result.userName().equals("isaac");
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
}
