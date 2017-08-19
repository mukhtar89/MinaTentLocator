package sa.iff.minatentlocator.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mukht on 10/31/2016.
 */

public class StringManipulation {

    public static String CapsFirst(String string) {
        String[] words = string.split(" ");
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<words.length; i++) {
            builder.append(Character.toUpperCase(words[i].charAt(0))
                + words[i].substring(1).toLowerCase() + " ");
        }
        return builder.toString();
    }

    public static String getFormattedDateTime(Long seconds) {
        Date date = new Date(seconds);
        DateFormat simpleDateFormat = SimpleDateFormat.getDateTimeInstance();
        return simpleDateFormat.format(date);
    }

    public static String getFormattedDate(Long seconds) {
        Date date = new Date(seconds);
        DateFormat simpleDateFormat = SimpleDateFormat.getDateInstance();
        return simpleDateFormat.format(date);
    }

    public static String getFormattedTime(Long seconds) {
        Date date = new Date(seconds);
        DateFormat simpleDateFormat = SimpleDateFormat.getTimeInstance();
        return simpleDateFormat.format(date);
    }
}
