package http.request;

import model.AuthData;

public record LogoutRequest(String authToken) {
}
