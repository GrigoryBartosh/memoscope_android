package ru.memoscope.android.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import ru.memoscope.android.R;

public class Utils {

    private static final String VK_APP_PACKAGE_ID = "com.vkontakte.android";

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

    public static void openLink(Activity activity, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        List<ResolveInfo> resInfo = activity.getPackageManager().queryIntentActivities(intent, 0);

        if (resInfo.isEmpty()) return;

        for (ResolveInfo info: resInfo) {
            if (info.activityInfo == null) continue;
            if (VK_APP_PACKAGE_ID.equals(info.activityInfo.packageName)) {
                intent.setPackage(info.activityInfo.packageName);
                break;
            }
        }
        activity.startActivity(intent);
    }

    public static void shareLink(Activity activity, String url) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
        i.putExtra(Intent.EXTRA_TEXT, url);
        activity.startActivity(Intent.createChooser(i, activity.getString(R.string.share_chooser_title)));
    }
}
