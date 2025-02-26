import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;
import requestResult.RegisterRequest;
import requestResult.RegisterResult;
import service.Service;
import service.UserService;

public class ServiceTests {
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
    void register() throws DataAccessException {
        UserDao users = new MemoryUserDao();
        GameDao games = new MemoryGameDao();
        AuthDao tokens = new MemoryAuthDao();

        UserService service = new UserService(users, games, tokens);
        RegisterRequest request = new RegisterRequest("isaac", "password", "email");

        RegisterResult result = service.register(request);

        assert !users.listUsers().isEmpty();
        assert !tokens.listAuthTokens().isEmpty();
        assert result.userName().equals("isaac");
    }
}
