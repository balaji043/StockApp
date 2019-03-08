package sample.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StockManagementUtils {
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");


    public static String formatDateTimeString(Long time) {
        return DATE_TIME_FORMAT.format(new Date(time));
    }
}
