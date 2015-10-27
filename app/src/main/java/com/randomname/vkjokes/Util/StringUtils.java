package com.randomname.vkjokes.Util;

import android.app.Notification;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    public static String declarationOfNum (int number, String[] titles) {
        int[] cases = {2, 0, 1, 1, 1, 2};

        return titles[(number % 100 > 4 && number % 100 < 20) ? 2 : cases[(number % 10 < 5) ? number % 10 : 5]];
    }

    public static String replaceVkLinks(String input) {
        String output = input;
        String regex = "\\Q[\\E(.+?)\\Q|\\E(.+?)\\Q]\\E";
        String replacement = "<a href='https://vk.com/$1'>$2</a>";

        output = output.replaceAll(regex, replacement);

        return output;
    }

    public static String replaceURLwithAnchor(String input) {

        String urlRegex = "((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?";
        String replacement = "<a href='$0'>$0</a>";

        String output = input.replaceAll(urlRegex, replacement);

        return output;
    }

    public static String getDateString(long milliseconds) {
        String output = "";

        if (DateUtils.isToday(milliseconds)) {
            output = (String) DateUtils.getRelativeTimeSpanString(milliseconds);
        } else {
            output = DateFormat.format("dd MMMM kk:mm", new Date(milliseconds)).toString();
        }

        return output;
    }
}
