import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    ArrayList<ClientThread> clients = new ArrayList<>();
    ArrayList<String> usernames = new ArrayList<>();
    TheServer server;

    Server() {
        this.server = new TheServer();
        this.server.start();
    }

    public class TheServer extends Thread {
        public void run() {
            try (ServerSocket servSock = new ServerSocket(5555);) {
                while (true) {
                    ClientThread ct = new ClientThread(servSock.accept(), clients.size());
                    clients.add(ct);
                    ct.start();
                }
            } catch(Exception ignored) {
            }
        }
    }

    class ClientThread extends Thread {
        Socket connection;
        int count;
        ObjectInputStream in;
        ObjectOutputStream out;

        ClientThread(Socket s, int count) {
            this.connection = s;
            this.count = count;
        }

        public void updateClient(String message, int clientIndex) {
            ClientThread t = clients.get(clientIndex);
            try {
                t.out.writeObject(message);
            } catch(Exception ignored) {
            }
        }

        public void run() {
            try {
                in = new ObjectInputStream(connection.getInputStream());
                out = new ObjectOutputStream(connection.getOutputStream());
                connection.setTcpNoDelay(true);
            } catch(Exception ignored) {
            }
            while (true) {
                try {
                    String data = (String) in.readObject();
                    if (data.contains("/")) {
                        int index = data.indexOf("/");
                        String message = data.substring(0, index);
                        while (index != -1) {
                            int nextIndex = data.indexOf("/", index + 1);
                            String client;
                            if (nextIndex == -1) {
                                client = data.substring(index + 1);
                            } else {
                                client = data.substring(index + 1, nextIndex);
                            }
                            updateClient(message, usernames.indexOf(client));
                            index = nextIndex;
                        }
                    } else {
                        usernames.add(data);
                        for (int i = 0; i < clients.size(); i++) {
                            for (String username : usernames) {
                                updateClient(username, i);
                            }
                        }
                    }
                } catch (Exception e) {
                    clients.remove(this);
                    for (int i = 0; i < clients.size(); i++) {
                        updateClient("-" + usernames.get(count), i);
                    }
                    usernames.set(count, "NIL");
                    break;
                }
            }
        }
    }
}