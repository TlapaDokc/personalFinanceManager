import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Purchase implements Serializable {
    private String title;
    private String category = null;
    private String date;
    private int sum;

    public Purchase() {
    }

    public int getSum() {
        return sum;
    }

    public String getTitle() {
        return title;
    }

    public Date getDate() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyy.MM.dd");
        return format.parse(date);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}

