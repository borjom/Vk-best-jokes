package com.randomname.vkjokes.Util;

import android.util.Log;

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
}
