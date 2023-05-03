import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

public class Client extends Thread {
    Socket connection;
    ObjectOutputStream out;
    ObjectInputStream in;
    private Consumer<Serializable> callback;
    String username;

    Client(Consumer<Serializable> call, String username) {
        this.callback = call;
        this.username = username;
    }

    public void run() {
        try {
            connection = new Socket("127.0.0.1", 5555);
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
            connection.setTcpNoDelay(true);
            sendStr(username);
        } catch (Exception ignored) {
        }
        while (true) {
            try {
                String data = (String) in.readObject();
                callback.accept(data);
            } catch (Exception ignored) {
            }
        }
    }

    public void sendStr(String data) {
        try {
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}