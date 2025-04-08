package client;

import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.net.URI;

public class WebSocketClient {
    private Session session;

    public WebSocketClient(String url) throws Exception {
        URI uri = new URI(url.replace("http", "ws"));
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
    }
}
