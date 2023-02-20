package Core.UI.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {
    public static List<String> extractTextByRegEx(String text, String regex) {
        List<String> tagValues = new ArrayList<String>();
        Pattern REGEX = Pattern.compile(regex);
        Matcher matcher = REGEX.matcher(text);
        while (matcher.find()) {
            tagValues.add(matcher.group(1));
        }
        return tagValues;
    }
}
