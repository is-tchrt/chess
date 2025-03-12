package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;

public class MySqlGameDao extends MySqlDao implements GameDao {

    public MySqlGameDao() throws DataAccessException {
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS games (
                `gameID` int NOT NULL,
                `whiteUsername` varchar(256),
                `blackUsername` varchar(256),
                `gameName` varchar(256),
                `game` TEXT,
                PRIMARY KEY (`gameID`)
            );
            """
        };
        configureDatabase(createStatements);
    }

    @Override
    public void clearGames() throws DataAccessException {
        String statement = "TRUNCATE games";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException("Error clearing database: ".concat(e.getMessage()));
        }
    }

    @Override
    public void addGame(GameData game) throws DataAccessException {
        String statement =
                "INSERT INTO games (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
        String gameDataJson = new Gson().toJson(game.game());
        executeUpdate(statement, game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), gameDataJson);
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String statement =
                "UPDATE games SET whiteUsername=?, blackUsername=?, gameName=?, game=? WHERE gameID=?";
        String gameDataJson = new Gson().toJson(game.game());
        executeUpdate(statement, game.whiteUsername(), game.blackUsername(), game.gameName(), gameDataJson, game.gameID());
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT gameID, whiteUsername, blackUsername, gameName FROM games;";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                return formatListGameResult(ps);
            }
        } catch (Exception e) {
            throw new DataAccessException("Error reading database: ".concat(e.getMessage()));
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT * FROM games WHERE gameID=?;";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                return formatGetGameResult(ps);
            }
        } catch (Exception e) {
            throw new DataAccessException("Error reading database: ".concat(e.getMessage()));
        }
    }

    private ArrayList<GameData> formatListGameResult(PreparedStatement ps) throws DataAccessException {
        ArrayList<GameData> result = new ArrayList<>();
        try (ResultSet resultSet = ps.executeQuery()) {
            while (resultSet.next()) {
                int gameID = resultSet.getInt("gameID");
                String whiteUsername = resultSet.getString("whiteUsername");
                String blackUsername = resultSet.getString("blackUsername");
                String gameName = resultSet.getString("gameName");
                result.add(new GameData(gameID, whiteUsername, blackUsername, gameName, null));
            }
            return result;
        } catch (Exception e) {
            throw new DataAccessException("Error executing query: ".concat(e.getMessage()));
        }
    }

    private GameData formatGetGameResult(PreparedStatement ps) throws DataAccessException {
        try (ResultSet resultSet = ps.executeQuery()) {
            if (resultSet.next()) {
                int gameID = resultSet.getInt("gameID");
                String whiteUsername = resultSet.getString("whiteUsername");
                String blackUsername = resultSet.getString("blackUsername");
                String gameName = resultSet.getString("gameName");
                String gameJson = resultSet.getString("game");
                ChessGame game = new Gson().fromJson(gameJson, ChessGame.class);
                return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new DataAccessException("Error executing query: ".concat(e.getMessage()));
        }
    }
}
