import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;

public class ServiceTests {
    final Service service = new Service();

    @Test
    void clear() throws DataAccessException {
        UserDao users = new MemoryUserDao();
        GameDao games = new MemoryGameDao();
        AuthDao tokens = new MemoryAuthDao();

        users.addUser(new UserData("isaac", "isaac", "isaac"));
        games.addGame(new GameData(1, "white", "black", "game", new ChessGame()));
        tokens.addAuthToken(new AuthData("authToken", "isaac"));

        service.clear();

        assert(users.listUsers().isEmpty());
        assert(games.listGames().isEmpty());
        assert(tokens.listAuthTokens().isEmpty());
    }
}
