import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;

public class Main {

    public static void main(String[] args) {
        File saveData = new File("data.bin");
        FinanceManager financeManager = new FinanceManager();
        financeManager.loadCategories();
        if (saveData.exists()) {
            financeManager = FinanceManager.loadFromBinFile(saveData);
        }
        try (ServerSocket serverSocket = new ServerSocket(8989);) {
            while (true) {
                System.out.println("Server started");
                try (
                        Socket socket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                ) {
                    System.out.println("New connection accepted");
                    String newPurchase = in.readLine();
                    financeManager.addingPurchase(newPurchase);
                    financeManager.sortByDate();
                    out.println(financeManager.getMaxCategory());
                    financeManager.saveBin(saveData);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            System.out.println("server cannot start");
            e.printStackTrace();
        }
    }
}