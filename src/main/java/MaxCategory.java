import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

public class MaxCategory implements Serializable {
    private Map<String, Object> maxCategory = new TreeMap<>();
    private Map<String, Object> maxYearCategory = new TreeMap<>();
    private Map<String, Object> maxMonthCategory = new TreeMap<>();
    private Map<String, Object> maxDayCategory = new TreeMap<>();


    public MaxCategory(String category, int sum) {
        this.maxCategory.put("category", category);
        this.maxCategory.put("sum", sum);
    }

    public void setMaxYearCategory(String category, int sum) {
        this.maxYearCategory.put("category", category);
        this.maxYearCategory.put("sum", sum);
    }

    public void setMaxMonthCategory(String category, int sum) {
        this.maxMonthCategory.put("category", category);
        this.maxMonthCategory.put("sum", sum);
    }

    public void setMaxDayCategory(String category, int sum) {
        this.maxDayCategory.put("category", category);
        this.maxDayCategory.put("sum", sum);
    }
}
