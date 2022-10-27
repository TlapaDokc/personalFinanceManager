import com.google.gson.Gson;

import java.io.*;
import java.text.ParseException;
import java.util.*;

public class FinanceManager implements Serializable {
    private static File fileCategories = new File("categories.tsv");
    private Map<String, Integer> sumPurchases = new HashMap<>();
    private Map<String, String> categories = new HashMap<>();

    public FinanceManager() {
    }

    public void loadCategories() {
        try (Scanner sc = new Scanner(fileCategories)) {
            while (sc.hasNextLine()) {
                String[] array = sc.nextLine().split("\t");
                categories.put(array[0], array[1]);
                if (!sumPurchases.containsKey(array[1])) {
                    sumPurchases.put(array[1], 0);
                }
            }
            sumPurchases.put("другое", 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addingPurchase(String json) throws ParseException {
        Gson gson = new Gson();
        Purchase newPurchase = gson.fromJson(json, Purchase.class);
        if (categories.containsKey(newPurchase.getTitle())) {
            newPurchase.setCategory(categories.get(newPurchase.getTitle()));
        } else {
            newPurchase.setCategory("другое");
        }
        int sum = sumPurchases.get(newPurchase.getCategory()) + newPurchase.getSum();
        sumPurchases.put(newPurchase.getCategory(), sum);
    }

    public String getMaxCategory() {
        String maxKey = sumPurchases.keySet().stream()
                .max(Comparator.comparing(sumPurchases::get))
                .orElse(null);
        MaxCategory maxCategory = new MaxCategory(maxKey, sumPurchases.get(maxKey));
        Gson gson = new Gson();
        return gson.toJson(maxCategory);
    }

    public void saveBin(File binFile) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(binFile))) {
            out.writeObject(this);
        }
    }

    public static FinanceManager loadFromBinFile(File binFile) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(binFile))) {
            return (FinanceManager) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
