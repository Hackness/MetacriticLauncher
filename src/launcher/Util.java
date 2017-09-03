package launcher;

import java.util.Calendar;

/**
 * Created by Hack
 * Date: 03.09.2017 23:45
 */
public class Util {
    /**
     * Create a filename with date and simple comment
     * @param header - simple comment
     * @param extension - extension of file
     * @return - String filename
     */
    public static String makeFileNameWithDate(String header, String extension) {
        Calendar calendar = Calendar.getInstance();
        return header + " " + calendar.get(Calendar.DATE) + "-" + calendar.get(Calendar.MONTH) + "-"
                + calendar.get(Calendar.YEAR) + " " + calendar.get(Calendar.HOUR_OF_DAY) + "-"
                + calendar.get(Calendar.MINUTE) + "-" + calendar.get(Calendar.SECOND)
                + (!extension.isEmpty() ? "." + extension : "");
    }
}
