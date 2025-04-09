package websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;
import java.util.HashMap;

public class ClientsManager {
    private HashMap<Integer, HashMap<String, Client>> clients = new HashMap<>();

    public void add(Integer gameID, Client client) {
        if (clients.get(gameID) != null) {
            clients.get(gameID).put(client.username, client);
        } else {
            HashMap<String, Client> gameClients = new HashMap<>();
            gameClients.put(client.username, client);
            clients.put(gameID, gameClients);
        }
    }

    public void remove(Integer gameID, String username) {
        if (clients.get(gameID) != null) {
            clients.get(gameID).remove(username);
        }
    }

    public void notifyOtherClients(Integer gameID, Client currentClient, String message) {
        try {
            for (Client client : clients.get(gameID).values()) {
                if (!client.equals(currentClient)) {
                    client.sendNotification(message);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
