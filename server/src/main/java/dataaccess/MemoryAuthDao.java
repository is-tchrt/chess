package dataaccess;

import model.AuthData;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class MemoryAuthDao implements AuthDao{
    final private HashMap<String, AuthData> authTokens = new HashMap<>();

    @Override
    public void clearAuthTokens() {
        authTokens.clear();
    }

    @Override
    public void addAuthToken(AuthData authData) {
        authTokens.put(authData.authToken(), authData);
    }

    @Override
    public Collection<AuthData> listAuthTokens() {
        return authTokens.values();
    }

    @Override
    public AuthData getAuthData(String authToken) {
        return authTokens.get(authToken);
    }

    @Override
    public void removeAuthData(String authToken) {
        authTokens.remove(authToken);
    }
}
