package dataaccess;

import model.UserData;

import java.util.Collection;

public interface UserDao {
    public void clearUsers();
    public void addUser(UserData user);
    public Collection<UserData> listUsers();
    public UserData getUser(String username);
}
