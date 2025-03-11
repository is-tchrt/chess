package dataaccess;

import model.AuthData;

import java.util.Collection;
import java.util.List;

public class MySqlAuthDao implements AuthDao {
    @Override
    public void clearAuthTokens() throws DataAccessException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void addAuthToken(AuthData authData) throws DataAccessException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Collection<AuthData> listAuthTokens() throws DataAccessException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public AuthData getAuthData(String authToken) throws DataAccessException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void removeAuthData(String authToken) throws DataAccessException {
        throw new RuntimeException("Not implemented");
    }
}
