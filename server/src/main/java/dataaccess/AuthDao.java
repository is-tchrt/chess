package dataaccess;

import model.AuthData;

import java.util.Collection;

public interface AuthDao {
    public void clearAuthTokens();
    public void addAuthToken(AuthData authData);
    public Collection<AuthData> listAuthTokens();
    public AuthData getAuthData(String authToken);
}
