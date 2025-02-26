package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDao implements GameDao {
    final private HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public void clearGames() {
        games.clear();
    }

    @Override
    public void addGame(GameData game) {
        games.put(game.GameID(), game);
    }

    @Override
    public Collection<GameData> listGames() {
        return games.values();
    }
}
