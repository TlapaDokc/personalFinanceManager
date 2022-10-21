import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

@DisplayName("Тестирование: FinanceManager")
public class FinanceManagerTest {
    private static File fileCategoriesTest = new File("categoriesByTests.tsv");
    private Map<String, Integer> sumPurchasesTest = new HashMap<>();
    private Map<String, Integer> sumPurchases = new HashMap<>();
    private Map<String, String> categoriesTest = new HashMap<>();
    private Map<String, String> categories = new HashMap<>();

    @BeforeEach
    void setUp() {
        sumPurchases.put("одежда", 0);
        sumPurchases.put("еда", 0);
        sumPurchases.put("быт", 0);
        sumPurchases.put("гаджеты", 0);
        sumPurchases.put("другое", 0);
        categories.put("круассан", "еда");
        categories.put("сосиска", "еда");
        categories.put("сапоги", "одежда");
        categories.put("панама", "одежда");
        categories.put("смартфон", "гаджеты");
        categories.put("шампунь", "быт");
    }


    @Test
    @DisplayName("Тестирование метода loadCategories на корректный результат")
    void loadCategoriesTest() {
        try (Scanner sc = new Scanner(fileCategoriesTest)) {
            while (sc.hasNextLine()) {
                String[] array = sc.nextLine().split("\t");
                categoriesTest.put(array[0], array[1]);
                if (!sumPurchasesTest.containsKey(array[1])) {
                    sumPurchasesTest.put(array[1], 0);
                }
            }
            sumPurchasesTest.put("другое", 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(sumPurchases, sumPurchasesTest);
        Assertions.assertEquals(categories, categoriesTest);
    }

    @Test
    @DisplayName("Тестирование метода addingPurchase на корректный результат")
    void addingPurchaseTest() throws ParseException {
        String jsonTest = "{\"title\": \"сосиска\", \"date\": \"2022.02.08\", \"sum\": 500}";
        Gson gson = new Gson();
        Purchase newPurchaseTest = gson.fromJson(jsonTest, Purchase.class);
        if (categories.containsKey(newPurchaseTest.getTitle())) {
            newPurchaseTest.setCategory(categories.get(newPurchaseTest.getTitle()));
        } else {
            newPurchaseTest.setCategory("другое");
        }
        int sum = sumPurchases.get(newPurchaseTest.getCategory()) + newPurchaseTest.getSum();
        sumPurchases.put(newPurchaseTest.getCategory(), sum);
        Map<String, Integer> expectedSumPurchases = new HashMap<>();
        expectedSumPurchases.put("одежда", 0);
        expectedSumPurchases.put("еда", 500);
        expectedSumPurchases.put("быт", 0);
        expectedSumPurchases.put("гаджеты", 0);
        expectedSumPurchases.put("другое", 0);
        Assertions.assertEquals(expectedSumPurchases, sumPurchases);
    }

    @ParameterizedTest
    @DisplayName("Тестирование метода getMaxCategory на корректный результат")
    @MethodSource("arguments")
    void getMaxCategoryTest(String key, Integer value, String expectedResult) {
        sumPurchases.put(key, value);
        String maxKey = sumPurchases.keySet().stream()
                .max(Comparator.comparing(sumPurchases::get))
                .orElse(null);
        String maxCategory = "{\n" +
                "  \"maxCategory\": {\n" +
                "    \"category\": \"" + maxKey + "\",\n" +
                "    \"sum\": " + sumPurchases.get(maxKey) + "\n" +
                "  }\n" +
                "}";
        Gson gson = new Gson();
        String result = gson.toJson(maxCategory);
        Assertions.assertEquals(expectedResult, gson.fromJson(result, String.class));
    }

    private static Stream<Arguments> arguments() {
        return Stream.of(
                Arguments.of("одежда", 500,
                        "{\n" +
                                "  \"maxCategory\": {\n" +
                                "    \"category\": \"одежда\",\n" +
                                "    \"sum\": 500\n" +
                                "  }\n" +
                                "}"),
                Arguments.of("еда", 400,
                        "{\n" +
                                "  \"maxCategory\": {\n" +
                                "    \"category\": \"еда\",\n" +
                                "    \"sum\": 400\n" +
                                "  }\n" +
                                "}"),
                Arguments.of("быт", 600,
                        "{\n" +
                                "  \"maxCategory\": {\n" +
                                "    \"category\": \"быт\",\n" +
                                "    \"sum\": 600\n" +
                                "  }\n" +
                                "}"),
                Arguments.of("гаджеты", 800,
                        "{\n" +
                                "  \"maxCategory\": {\n" +
                                "    \"category\": \"гаджеты\",\n" +
                                "    \"sum\": 800\n" +
                                "  }\n" +
                                "}"),
                Arguments.of("другое", 400,
                        "{\n" +
                                "  \"maxCategory\": {\n" +
                                "    \"category\": \"другое\",\n" +
                                "    \"sum\": 400\n" +
                                "  }\n" +
                                "}"));
    }
}
