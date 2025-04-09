import server.Server;

public class Main {
    public static void main(String[] args) {
        // System.out.println("♕ 240 Chess Server");
        try {
            var port = 8080;
            if (args.length >= 1) {
                port = Integer.parseInt(args[0]);
            }

            Server server = new Server();
            server.run(port);
            port = server.port();
            System.out.printf("Server started on port %d", port);
        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
    }
}