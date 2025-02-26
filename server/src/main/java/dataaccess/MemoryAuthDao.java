package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDao implements GameDAO{
    final private HashMap<String, AuthData> authTokens = new HashMap<>();

    @Override
    public void clearGames() {
        authTokens.clear();
    }
}
