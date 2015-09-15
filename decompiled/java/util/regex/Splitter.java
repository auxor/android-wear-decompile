package java.util.regex;

import java.util.ArrayList;
import java.util.List;
import libcore.util.EmptyArray;
import org.xmlpull.v1.XmlPullParser;

public class Splitter {
    private static final String METACHARACTERS = "\\?*+[](){}^$.|";

    private Splitter() {
    }

    public static String[] fastSplit(String re, String input, int limit) {
        int len = re.length();
        if (len == 0) {
            return null;
        }
        int ch;
        char ch2 = re.charAt(0);
        if (!(len == 1 && METACHARACTERS.indexOf((int) ch2) == -1)) {
            if (len != 2 || ch2 != '\\') {
                return null;
            }
            ch = re.charAt(1);
            if (METACHARACTERS.indexOf(ch) == -1) {
                return null;
            }
        }
        if (input.isEmpty()) {
            return new String[]{XmlPullParser.NO_NAMESPACE};
        }
        int separatorCount = 0;
        int begin = 0;
        while (separatorCount + 1 != limit) {
            int end = input.indexOf(ch, begin);
            if (end == -1) {
                break;
            }
            separatorCount++;
            begin = end + 1;
        }
        int lastPartEnd = input.length();
        if (limit == 0 && begin == lastPartEnd) {
            if (separatorCount == lastPartEnd) {
                return EmptyArray.STRING;
            }
            do {
                begin--;
            } while (input.charAt(begin - 1) == ch);
            separatorCount -= input.length() - begin;
            lastPartEnd = begin;
        }
        String[] result = new String[(separatorCount + 1)];
        begin = 0;
        for (int i = 0; i != separatorCount; i++) {
            end = input.indexOf(ch, begin);
            result[i] = input.substring(begin, end);
            begin = end + 1;
        }
        result[separatorCount] = input.substring(begin, lastPartEnd);
        return result;
    }

    public static String[] split(Pattern pattern, String re, String input, int limit) {
        String[] fastResult = fastSplit(re, input, limit);
        if (fastResult != null) {
            return fastResult;
        }
        if (input.isEmpty()) {
            return new String[]{XmlPullParser.NO_NAMESPACE};
        }
        ArrayList<String> list = new ArrayList();
        Matcher matcher = new Matcher(pattern, input);
        int begin = 0;
        while (list.size() + 1 != limit && matcher.find()) {
            list.add(input.substring(begin, matcher.start()));
            begin = matcher.end();
        }
        return finishSplit(list, input, begin, limit);
    }

    private static String[] finishSplit(List<String> list, String input, int begin, int limit) {
        if (begin < input.length()) {
            list.add(input.substring(begin));
        } else if (limit != 0) {
            list.add(XmlPullParser.NO_NAMESPACE);
        } else {
            int i = list.size() - 1;
            while (i >= 0 && ((String) list.get(i)).isEmpty()) {
                list.remove(i);
                i--;
            }
        }
        return (String[]) list.toArray(new String[list.size()]);
    }
}
