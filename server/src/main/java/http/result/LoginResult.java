package http.result;

public record LoginResult(String username, String authToken, String message) {
}
