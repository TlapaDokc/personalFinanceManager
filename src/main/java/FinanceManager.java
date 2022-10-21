import com.google.gson.Gson;

import java.io.*;
import java.text.ParseException;
import java.util.*;

public class FinanceManager implements Serializable {
    private static File fileCategories = new File("categories.tsv");
    private static Map<String, Integer> sumPurchases = new HashMap<>();
    private static Map<String, Integer> sumPurchasesForYear = new HashMap<>();
    private static Map<String, Integer> sumPurchasesForMonth = new HashMap<>();
    private static Map<String, Integer> sumPurchasesForDay = new HashMap<>();
    private Date datePurchase;
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
        datePurchase = newPurchase.getDate();
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
            if (sumPurchases.containsKey(i.getCategory())) {
                sumPurchases.put(i.getCategory(), i.getSum() + sumPurchases.get(i.getCategory()));
            } else {
                sumPurchases.put(i.getCategory(), i.getSum());
            }
        }
    }

    public void sortByDate() throws ParseException {
        for (Purchase i : purchaseList) {
            if (i.getDate().getYear() == datePurchase.getYear()) {
                if (sumPurchasesForYear.containsKey(i.getCategory())) {
                    sumPurchasesForYear.put(i.getCategory(), i.getSum()
                            + sumPurchasesForYear.get(i.getCategory()));
                } else {
                    sumPurchasesForYear.put(i.getCategory(), i.getSum());
                }
            }
            if (i.getDate().getYear() == datePurchase.getYear() &&
                    i.getDate().getMonth() == datePurchase.getMonth()) {
                if (sumPurchasesForMonth.containsKey(i.getCategory())) {
                    sumPurchasesForMonth.put(i.getCategory(), i.getSum()
                            + sumPurchasesForMonth.get(i.getCategory()));
                } else {
                    sumPurchasesForMonth.put(i.getCategory(), i.getSum());
                }
            }
            if (i.getDate().getYear() == datePurchase.getYear() &&
                    i.getDate().getMonth() == datePurchase.getMonth() &&
                    i.getDate().getDay() == datePurchase.getDay()) {
                if (sumPurchasesForDay.containsKey(i.getCategory())) {
                    sumPurchasesForDay.put(i.getCategory(), i.getSum()
                            + sumPurchasesForDay.get(i.getCategory()));
                } else {
                    sumPurchasesForDay.put(i.getCategory(), i.getSum());
                }
            }
        }
    }

    public String getMaxCategory() {
        String maxYearCategory = "";
        String maxMonthCategory = "";
        String maxDayCategory = "";
        String keySumPurchases = sumPurchases.keySet().stream()
                .max(Comparator.comparing(sumPurchases::get))
                .orElse(null);
        String maxCategory = "{\n" +
                "  \"maxCategory\": {\n" +
                "    \"category\": \"" + keySumPurchases + "\",\n" +
                "    \"sum\": " + sumPurchases.get(keySumPurchases) + "\n" +
                "  },\n";

        if (!sumPurchasesForYear.isEmpty()) {
            String keySumPurchasesForYear = sumPurchasesForYear.keySet().stream()
                    .max(Comparator.comparing(sumPurchasesForYear::get))
                    .orElse(null);
            maxYearCategory = "  \"maxYearCategory\": {\n" +
                    "    \"category\": \"" + keySumPurchasesForYear + "\",\n" +
                    "    \"sum\": " + sumPurchasesForYear.get(keySumPurchasesForYear) + "\n" +
                    "  },\n";
        }

        if (!sumPurchasesForMonth.isEmpty()) {
            String keySumPurchasesForMonth = sumPurchasesForMonth.keySet().stream()
                    .max(Comparator.comparing(sumPurchasesForMonth::get))
                    .orElse(null);
            maxMonthCategory = "  \"maxMonthCategory\": {\n" +
                    "    \"category\": \"" + keySumPurchasesForMonth + "\",\n" +
                    "    \"sum\": " + sumPurchasesForMonth.get(keySumPurchasesForMonth) + "\n" +
                    "  },\n";
        }

        if (!sumPurchasesForDay.isEmpty()) {
            String keySumPurchasesForDay = sumPurchasesForDay.keySet().stream()
                    .max(Comparator.comparing(sumPurchasesForDay::get))
                    .orElse(null);
            maxDayCategory = "  \"maxDayCategory\": {\n" +
                    "    \"category\": \"" + keySumPurchasesForDay + "\",\n" +
                    "    \"sum\": " + sumPurchasesForDay.get(keySumPurchasesForDay) + "\n" +
                    "  },\n" +
                    "}\n";
        }

        Gson gson = new Gson();
        return gson.toJson(maxCategory + maxYearCategory
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
