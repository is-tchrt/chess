package client;

import java.util.Scanner;

public class Repl {
    private Client client;
    private ServerFacade serverFacade;

    public Repl(String url) {
        serverFacade = new ServerFacade(url);
        client = new PreLoginClient(serverFacade);
    }

    public void run() {
        System.out.println("Welcome to Chess!");

        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("quit")) {
            System.out.print(">>>");
            String command = scanner.nextLine();
            try {
                result = client.eval(command);
                if (result.equals("login")) {
                    client = new PostLoginClient(client);
                }
                System.out.println(result);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
