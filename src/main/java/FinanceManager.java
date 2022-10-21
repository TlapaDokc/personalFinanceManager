import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.text.ParseException;
import java.util.*;

public class FinanceManager implements Serializable {
    private static File fileCategories = new File("categories.tsv");
    private Map<String, Integer> sumPurchases = new HashMap<>();
    private Map<String, Integer> sumPurchasesForYear = new HashMap<>();
    private Map<String, Integer> sumPurchasesForMonth = new HashMap<>();
    private Map<String, Integer> sumPurchasesForDay = new HashMap<>();
    private Map<String, String> categories = new HashMap<>();
    private List<Purchase> purchaseList = new ArrayList<>();

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
        purchaseList.add(newPurchase);
        sumPurchases.clear();
        sumPurchasesForYear.clear();
        sumPurchasesForMonth.clear();
        sumPurchasesForDay.clear();
        for (Purchase i : purchaseList) {
            if (categories.containsKey(i.getTitle())) {
                i.setCategory(categories.get(i.getTitle()));
            } else {
                i.setCategory("другое");
            }
            if (sumPurchases.size() > 0) {
                sumPurchases.put(i.getCategory(), i.getSum() + sumPurchases.get(i.getCategory()));
            } else {
                sumPurchases.put(i.getCategory(), i.getSum());
            }
        }
        for (Purchase i : purchaseList) {
            if (i.getDate().getYear() == newPurchase.getDate().getYear()) {
                if (sumPurchasesForYear.size() > 0) {
                    sumPurchasesForYear.put(i.getCategory(), i.getSum()
                            + sumPurchasesForYear.get(i.getCategory()));
                } else {
                    sumPurchasesForYear.put(i.getCategory(), i.getSum());
                }
            }
            if (i.getDate().getYear() == newPurchase.getDate().getYear() &&
                    i.getDate().getMonth() == newPurchase.getDate().getMonth()) {
                if (sumPurchasesForMonth.size() > 0) {
                    sumPurchasesForMonth.put(i.getCategory(), i.getSum()
                            + sumPurchasesForMonth.get(i.getCategory()));
                } else {
                    sumPurchasesForMonth.put(i.getCategory(), i.getSum());
                }
            }
            if (i.getDate().getYear() == newPurchase.getDate().getYear() &&
                    i.getDate().getMonth() == newPurchase.getDate().getMonth() &&
                    i.getDate().getDay() == newPurchase.getDate().getDay()) {
                if (sumPurchasesForDay.size() > 0) {
                    sumPurchasesForDay.put(i.getCategory(), i.getSum()
                            + sumPurchasesForDay.get(i.getCategory()));
                } else {
                    sumPurchasesForDay.put(i.getCategory(), i.getSum());
                }
            }
        }
    }

    public String getMaxCategory() {
        String keySumPurchases = sumPurchases.keySet().stream()
                .max(Comparator.comparing(sumPurchases::get))
                .orElse(null);
        String maxCategory = "{\"maxCategory\":{\"sum\":"
                + sumPurchases.get(keySumPurchases)
                + ",\"category\":\"" + keySumPurchases + "\"}},\n";

        String keySumPurchasesForYear = sumPurchasesForYear.keySet().stream()
                .max(Comparator.comparing(sumPurchasesForYear::get))
                .orElse(null);
        String maxYearCategory = "{\"maxYearCategory\":{\"sum\":"
                + sumPurchasesForYear.get(keySumPurchasesForYear)
                + ",\"category\":\"" + keySumPurchasesForYear + "\"}},\n";

        String keySumPurchasesForMonth = sumPurchasesForMonth.keySet().stream()
                .max(Comparator.comparing(sumPurchasesForMonth::get))
                .orElse(null);
        String maxMonthCategory = "{\"maxMonthCategory\":{\"sum\":"
                + sumPurchasesForMonth.get(keySumPurchasesForMonth)
                + ",\"category\":\"" + keySumPurchasesForMonth + "\"}},\n";

        String keySumPurchasesForDay = sumPurchasesForDay.keySet().stream()
                .max(Comparator.comparing(sumPurchasesForDay::get))
                .orElse(null);
        String maxDayCategory = "{\"maxDayCategory\":{\"sum\":"
                + sumPurchasesForDay.get(keySumPurchasesForDay)
                + ",\"category\":\"" + keySumPurchasesForDay + "\"}},\n";
        System.out.println(maxCategory +  maxYearCategory
                + maxMonthCategory + maxDayCategory);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(maxCategory +  maxYearCategory
                + maxMonthCategory + maxDayCategory);
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
