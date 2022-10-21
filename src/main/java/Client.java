
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private static final int port = 8989;
    private static final String host = "127.0.0.1";

    public static void main(String[] args) {
        Gson gson = new Gson();
        try (Socket clientSocket = new Socket(host, port);
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) {
            out.println("{\"title\": \"тапки\", \"date\": \"2022.02.08\", \"sum\": 500}");
            System.out.println(gson.fromJson(in.readLine(), String.class));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
