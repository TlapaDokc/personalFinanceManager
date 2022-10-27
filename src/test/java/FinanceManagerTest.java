import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

@DisplayName("Тестирование: FinanceManager")
public class FinanceManagerTest {
    private static File fileCategoriesTest = new File("categoriesByTests.tsv");
    private static Map<String, Integer> sumPurchasesTest = new HashMap<>();
    private static Map<String, Integer> sumPurchasesForYearTest = new HashMap<>();
    private static Map<String, Integer> sumPurchasesForMonthTest = new HashMap<>();
    private static Map<String, Integer> sumPurchasesForDayTest = new HashMap<>();
    private Map<String, String> categoriesTest = new HashMap<>();
    private List<Purchase> purchaseListTest = new ArrayList<>();
    private static String jsonTest = "{\"title\": \"сосиска\", \"date\": \"2022.02.08\", \"sum\": 300}";
    private Date datePurchaseTest;
    private Map<String, String> categoriesExpected = new HashMap<>();
    private static Map<String, Integer> sumPurchasesExpected = new HashMap<>();
    private static Map<String, Integer> sumPurchasesForYearExpected = new HashMap<>();
    private static Map<String, Integer> sumPurchasesForMonthExpected = new HashMap<>();
    private static Map<String, Integer> sumPurchasesForDayExpected = new HashMap<>();
    private List<Purchase> purchaseListExpected = new ArrayList<>();
    private Gson gson = new Gson();

    @BeforeEach
    void setUp() throws ParseException {
        sumPurchasesExpected.put("еда", 300);
        sumPurchasesForYearExpected.put("еда", 300);
        sumPurchasesForYearExpected.put("одежда", 500);
        sumPurchasesForMonthExpected.put("еда", 300);
        sumPurchasesForDayExpected.put("еда", 300);
        SimpleDateFormat format = new SimpleDateFormat("yyy.MM.dd");
        datePurchaseTest = format.parse("2022.02.08");
        categoriesExpected.put("круассан", "еда");
        categoriesExpected.put("сосиска", "еда");
        categoriesExpected.put("сапоги", "одежда");
        categoriesExpected.put("панама", "одежда");
        categoriesExpected.put("смартфон", "гаджеты");
        categoriesExpected.put("шампунь", "быт");
        Purchase purchase = gson.fromJson(jsonTest, Purchase.class);
        purchase.setCategory("еда");
        Purchase purchase2 = gson.fromJson("{\"title\": \"панама\", " +
                "\"date\": \"2022.03.08\", \"sum\": 500}", Purchase.class);
        purchase2.setCategory("одежда");
        purchaseListExpected.add(purchase);
        categoriesTest.put("круассан", "еда");
        categoriesTest.put("сосиска", "еда");
        categoriesTest.put("сапоги", "одежда");
        categoriesTest.put("панама", "одежда");
        categoriesTest.put("смартфон", "гаджеты");
        categoriesTest.put("шампунь", "быт");
    }


    @Test
    @DisplayName("Тестирование метода loadCategories на корректный результат")
    void loadCategoriesTest() {
        try (Scanner sc = new Scanner(fileCategoriesTest)) {
            while (sc.hasNextLine()) {
                String[] array = sc.nextLine().split("\t");
                categoriesTest.put(array[0], array[1]);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(categoriesExpected, categoriesTest);
    }

    @Test
    @DisplayName("Тестирование метода addingPurchase на корректный результат")
    void addingPurchaseTest() throws ParseException {
        Purchase newPurchase = gson.fromJson(jsonTest, Purchase.class);
        datePurchaseTest = newPurchase.getDate();
        purchaseListTest.add(newPurchase);
        sumPurchasesTest.clear();
        sumPurchasesForYearTest.clear();
        sumPurchasesForMonthTest.clear();
        sumPurchasesForDayTest.clear();
        for (Purchase i : purchaseListTest) {
            if (categoriesTest.containsKey(i.getTitle())) {
                i.setCategory(categoriesTest.get(i.getTitle()));

            } else {
                i.setCategory("другое");
            }
            if (sumPurchasesTest.containsKey(i.getCategory())) {
                sumPurchasesTest.put(i.getCategory(), i.getSum() + sumPurchasesTest.get(i.getCategory()));
            } else {
                sumPurchasesTest.put(i.getCategory(), i.getSum());
            }
        }
        Assertions.assertEquals(sumPurchasesExpected, sumPurchasesTest);
        Assertions.assertEquals(purchaseListExpected, purchaseListTest);
    }

    @Test
    @DisplayName("Тестирование метода sortByDate на корректный результат")
    public void sortByDate() throws ParseException {
        Purchase purchase1 = gson.fromJson(jsonTest, Purchase.class);
        purchase1.setCategory("еда");
        Purchase purchase2 = gson.fromJson("{\"title\": \"панама\", " +
                "\"date\": \"2022.03.08\", \"sum\": 500}", Purchase.class);
        purchase2.setCategory("одежда");
        purchaseListTest.add(purchase1);
        purchaseListTest.add(purchase2);

        for (Purchase i : purchaseListTest) {
            if (i.getDate().getYear() == datePurchaseTest.getYear()) {
                if (sumPurchasesForYearTest.containsKey(i.getCategory())) {
                    sumPurchasesForYearTest.put(i.getCategory(), i.getSum()
                            + sumPurchasesForYearTest.get(i.getCategory()));
                } else {
                    sumPurchasesForYearTest.put(i.getCategory(), i.getSum());
                }
            }
            if (i.getDate().getYear() == datePurchaseTest.getYear() &&
                    i.getDate().getMonth() == datePurchaseTest.getMonth()) {
                if (sumPurchasesForMonthTest.containsKey(i.getCategory())) {
                    sumPurchasesForMonthTest.put(i.getCategory(), i.getSum()
                            + sumPurchasesForMonthTest.get(i.getCategory()));
                } else {
                    sumPurchasesForMonthTest.put(i.getCategory(), i.getSum());
                }
            }
            if (i.getDate().getYear() == datePurchaseTest.getYear() &&
                    i.getDate().getMonth() == datePurchaseTest.getMonth() &&
                    i.getDate().getDay() == datePurchaseTest.getDay()) {
                if (sumPurchasesForDayTest.containsKey(i.getCategory())) {
                    sumPurchasesForDayTest.put(i.getCategory(), i.getSum()
                            + sumPurchasesForDayTest.get(i.getCategory()));
                } else {
                    sumPurchasesForDayTest.put(i.getCategory(), i.getSum());
                }
            }
        }
        Assertions.assertEquals(sumPurchasesForYearExpected, sumPurchasesForYearTest);
        Assertions.assertEquals(sumPurchasesForMonthExpected, sumPurchasesForMonthTest);
        Assertions.assertEquals(sumPurchasesForDayExpected, sumPurchasesForDayTest);
    }

    @Test
    @DisplayName("Тестирование метода getMaxCategory на корректный результат")
    public void getMaxCategory() {
        sumPurchasesTest.put("еда", 300);
        sumPurchasesTest.put("одежда", 500);
        sumPurchasesForYearTest.put("еда", 300);
        sumPurchasesForYearTest.put("одежда", 500);
        sumPurchasesForMonthTest.put("еда", 300);
        sumPurchasesForDayTest.put("еда", 300);
        String keySumPurchases = sumPurchasesTest.keySet().stream()
                .max(Comparator.comparing(sumPurchasesTest::get))
                .orElse(null);
        MaxCategory maxCategoryTest = new MaxCategory(keySumPurchases, sumPurchasesTest.get(keySumPurchases));
        if (!sumPurchasesForYearTest.isEmpty()) {
            String keySumPurchasesForYear = sumPurchasesForYearTest.keySet().stream()
                    .max(Comparator.comparing(sumPurchasesForYearTest::get))
                    .orElse(null);
            maxCategoryTest.setMaxYearCategory(keySumPurchasesForYear, sumPurchasesForYearTest.get(keySumPurchasesForYear));
        }
        if (!sumPurchasesForMonthTest.isEmpty()) {
            String keySumPurchasesForMonth = sumPurchasesForMonthTest.keySet().stream()
                    .max(Comparator.comparing(sumPurchasesForMonthTest::get))
                    .orElse(null);
            maxCategoryTest.setMaxMonthCategory(keySumPurchasesForMonth, sumPurchasesForMonthTest.get(keySumPurchasesForMonth));
        }
        if (!sumPurchasesForDayTest.isEmpty()) {
            String keySumPurchasesForDay = sumPurchasesForDayTest.keySet().stream()
                    .max(Comparator.comparing(sumPurchasesForDayTest::get))
                    .orElse(null);
            maxCategoryTest.setMaxDayCategory(keySumPurchasesForDay, sumPurchasesForDayTest.get(keySumPurchasesForDay));
        }
        Gson gson = new Gson();
        String result = gson.toJson(maxCategoryTest);
        MaxCategory maxCategoryExpected = new MaxCategory("одежда", 500);
        maxCategoryExpected.setMaxYearCategory("одежда", 500);
        maxCategoryExpected.setMaxMonthCategory("еда", 300);
        maxCategoryExpected.setMaxDayCategory("еда", 300);
        String resultExpected = gson.toJson(maxCategoryExpected);
        Assertions.assertEquals(resultExpected, result);
    }
}