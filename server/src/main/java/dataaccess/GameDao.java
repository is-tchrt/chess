package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDao {
    void clearGames() throws DataAccessException;
    void addGame(GameData game) throws DataAccessException;
    void updateGame(GameData game) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
}
