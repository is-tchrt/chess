package handler;

import com.google.gson.Gson;
import dataaccess.*;
import http.request.*;
import http.result.*;
import service.GameService;
import service.Service;
import service.UserService;
import spark.Request;
import spark.Response;

public class HttpHandler {
    UserDao userDao = new MemoryUserDao();
    GameDao gameDao = new MemoryGameDao();
    AuthDao authDao = new MemoryAuthDao();

    Service service = new Service(userDao, gameDao, authDao);
    UserService userService = new UserService(userDao, gameDao, authDao);
    GameService gameService = new GameService(userDao, gameDao, authDao);

    public Object clear(Request req, Response res) throws DataAccessException {
        service.clear();
        res.status(200);
        return "";
    }

    public Object register(Request req, Response res) {
        RegisterRequest request = new Gson().fromJson(req.body(), RegisterRequest.class);
        RegisterResult result = userService.register(request);
        res.status(getStatusCodeFromMessage(result.message()));
        return new Gson().toJson(result);
    }

    public Object login(Request req, Response res) {
        LoginRequest request = new Gson().fromJson(req.body(), LoginRequest.class);
        LoginResult result = userService.login(request);
        res.status(getStatusCodeFromMessage(result.message()));
        return new Gson().toJson(result);
    }

    public Object logout(Request req, Response res) {
        LogoutRequest request = new LogoutRequest(req.headers("authorization"));
        BlankResult result = userService.logout(request);
        res.status(getStatusCodeFromMessage(result.message()));
        return new Gson().toJson(result);
    }

    public Object listGames(Request req, Response res) {
        ListGamesRequest request = new ListGamesRequest(req.headers("authorization"));
        ListGamesResult result = gameService.listGame(request);
        res.status(getStatusCodeFromMessage(result.message()));
        return new Gson().toJson(result);
    }

    public Object createGame(Request req, Response res) {
        CreateGameRequest request = new Gson().fromJson(req.body(), CreateGameRequest.class);
        CreateGameResult result = gameService.createGame(request, req.headers("authorization"));
        res.status(getStatusCodeFromMessage(result.message()));
        return new Gson().toJson(result);
    }

    public Object joinGame(Request req, Response res) {
        JoinGameRequest request = new Gson().fromJson(req.body(), JoinGameRequest.class);
        BlankResult result = gameService.joinGame(request, req.headers("authorization"));
        res.status(getStatusCodeFromMessage(result.message()));
        return new Gson().toJson(result);
    }

    private int getStatusCodeFromMessage(String message) {
        int status;
        if (message == null) {
            status = 200;
        } else if (message.equals("Error: unauthorized")) {
            status = 401;
        } else if (message.equals("Error: bad request")) {
            status = 400;
        } else if (message.equals("Error: already taken")) {
            status = 403;
        } else {
            status = 500;
        }
        return status;
    }
}
