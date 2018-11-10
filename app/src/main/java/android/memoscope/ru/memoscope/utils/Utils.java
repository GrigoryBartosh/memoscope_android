package android.memoscope.ru.memoscope.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;

public class Utils {
    public static String getBestQualityURL(JSONObject photo) throws JSONException {
        int max = -1;
        String url = "";
        for(Iterator<String> iterator = photo.keys(); iterator.hasNext(); ) {
            String key = iterator.next();
            if (key.matches("photo_.*")) {
                int size = Integer.parseInt(key.substring(6));
                if (size > max) {
                    max = size;
                    url = photo.getString(key);
                }
            }
        }
        return url;
    }

    public static String formatTime(long time) {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
        return format.format(new Date(time * 1000));
    }
}
