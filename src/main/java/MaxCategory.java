import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

public class MaxCategory implements Serializable {
    private Map<String, Object> maxCategory = new TreeMap<>();

    public MaxCategory(String category, int sum) {
        this.maxCategory.put("category", category);
        this.maxCategory.put("sum", sum);
    }
}
