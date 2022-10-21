import com.google.gson.Gson;

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
        Map<String, Object> mapSumPurchases = new HashMap<>();
        mapSumPurchases.put("sum", sumPurchases.get(keySumPurchases));
        mapSumPurchases.put("category", keySumPurchases);
        Map<String, Object> maxCategory = new HashMap<>();
        maxCategory.put("maxCategory", mapSumPurchases);

        String keySumPurchasesForYear = sumPurchasesForYear.keySet().stream()
                .max(Comparator.comparing(sumPurchasesForYear::get))
                .orElse(null);
        Map<String, Object> mapSumPurchasesForYear = new HashMap<>();
        mapSumPurchasesForYear.put("sum", sumPurchasesForYear.get(keySumPurchasesForYear));
        mapSumPurchasesForYear.put("category", keySumPurchasesForYear);
        Map<String, Object> maxYearCategory = new HashMap<>();
        maxYearCategory.put("maxYearCategory", mapSumPurchasesForYear);

        String keySumPurchasesForMonth = sumPurchasesForMonth.keySet().stream()
                .max(Comparator.comparing(sumPurchasesForMonth::get))
                .orElse(null);
        Map<String, Object> mapSumPurchasesForMonth = new HashMap<>();
        mapSumPurchasesForMonth.put("sum", sumPurchasesForMonth.get(keySumPurchasesForMonth));
        mapSumPurchasesForMonth.put("category", keySumPurchasesForMonth);
        Map<String, Object> maxMonthCategory = new HashMap<>();
        maxMonthCategory.put("maxMonthCategory", mapSumPurchasesForMonth);

        String keySumPurchasesForDay = sumPurchasesForDay.keySet().stream()
                .max(Comparator.comparing(sumPurchasesForDay::get))
                .orElse(null);
        Map<String, Object> mapSumPurchasesForDay = new HashMap<>();
        mapSumPurchasesForDay.put("sum", sumPurchasesForDay.get(keySumPurchasesForDay));
        mapSumPurchasesForDay.put("category", keySumPurchasesForDay);
        Map<String, Object> maxDayCategory = new HashMap<>();
        maxDayCategory.put("maxDayCategory", mapSumPurchasesForDay);

        List<Map<String, Object>> json = new ArrayList<>();
        json.add(maxCategory);
        json.add(maxYearCategory);
        json.add(maxMonthCategory);
        json.add(maxDayCategory);

        Gson gson = new Gson();
        return gson.toJson(json);
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
