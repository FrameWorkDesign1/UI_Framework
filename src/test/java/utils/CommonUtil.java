package utils;


import Core.UI.TestContext;
import Core.UI.utils.PK_UI_Framework;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class CommonUtil {

    private TestContext context;

    public CommonUtil(TestContext context) {
        this.context = context;
    }

    static final Logger LOGGER = PK_UI_Framework.getLogger(CommonUtil.class);

    public static Map<String, String> convertFieldsAndValuesToMap(String[] names, String[] values) {
        TreeMap<String, String> fieldNameValueMap = new TreeMap<>();
        if (names.length == values.length) {
            for (int i = 0; i < names.length; i++) {
                if (values[i].equals("EMPTY")) {
                    fieldNameValueMap.put(names[i], "");
                } else {
                    fieldNameValueMap.put(names[i], values[i]);
                }
            }
        } else {
            LOGGER.error("names and values lengths should match. names length : " + names.length + "; values length : " + values.length);
            return null;
        }
        return fieldNameValueMap;
    }

    public static ArrayList<String> compareTwoStringHashMaps(Map<String, String> expectedMap, Map<String, String> actualMap) {
        Set<String> keysInExpectedMap;
        Set<String> keysInActualMap;

        ArrayList<String> log_info = new ArrayList<String>();
        if (expectedMap.size() != actualMap.size()) {
            keysInExpectedMap = expectedMap.keySet();
            keysInActualMap = actualMap.keySet();
            log_info.add("compareStringHashMaps count do not match \nExpected : " + expectedMap.size() + "\n" + "Actual : " + actualMap.size());

            if (expectedMap.size() > actualMap.size()) {
                Set<String> missingFromActual = new HashSet<>(keysInExpectedMap);
                missingFromActual.removeAll(keysInActualMap);
                keysInExpectedMap.retainAll(keysInActualMap);
                log_info.add("\n Missing key in actual map [" + missingFromActual + "]");
            } else {
                Set<String> extraKeysInActual = new HashSet<>(keysInActualMap);
                extraKeysInActual.removeAll(keysInExpectedMap);
                keysInActualMap.retainAll(keysInExpectedMap);
                log_info.add("\n Extra key in actual map [" + extraKeysInActual + "]");
            }
        }

        Iterator iteratorExpectedMap = expectedMap.entrySet().iterator();
        Iterator iteratorActualMap = actualMap.entrySet().iterator();
        int i = 1;
        while (iteratorExpectedMap.hasNext() && iteratorActualMap.hasNext()) {

            Map.Entry expectedMapEntry = (Map.Entry) iteratorExpectedMap.next();
            Map.Entry actualMapEntry = (Map.Entry) iteratorActualMap.next();
            if (!(expectedMapEntry.toString().trim().equalsIgnoreCase(actualMapEntry.toString().trim()))) {
                log_info.add("\n" + i + ". Expected : " + expectedMapEntry.toString() + "\n" + "   Actual   : " + actualMapEntry.toString() + "\n");
                i++;
            }
        }

        return log_info;
    }

    public static String leadiginZero(String value) {
        String strPattern = "^0+";
        value = value.trim().replaceAll(strPattern, "");
        if (value.equals(""))
            value = "0";
        return value;

    }

    public static <K, V> Map<K, V> sortByKeys(Map<K, V> unsortedMap) {
        // construct a `TreeMap` from the given map and return it
        return new TreeMap<>(unsortedMap);
    }

    public static String spaceintegration(String s, int[] sp) {
        int M = s.length(), N = sp.length, l = 0, r = 0;
        String res = newstr(M + N, ' ');

        // Iterate over M+N length
        for (int i = 0; i < M + N; i++) {

            if (l < N && i == sp[l] + l)
                l++;
            else
                res = res.substring(0, i) + s.charAt(r++) + res.substring(i + 1);
        }

        // Return the required String
        return res;
    }

    static String newstr(int i, char c) {
        String str = "";
        for (int j = 0; j < i; j++) {
            str += c;
        }
        return str;
    }
//    public static String maskCardNumber(String cardNumber, String mask) {
//        int index = 0;
//        String
//        StringBuilder maskedNumber = new StringBuilder();
//        for (int i = 0; i < mask.length(); i++) {
//            char c = mask.charAt(i);
//            if (c == ´#´) {
//                maskedNumber.append(cardNumber.charAt(index));
//                index++;
//            } else if (c == ´X´) {
//                maskedNumber.append(c);
//                index++;
//            } else {
//                maskedNumber.append(c);
//            }
//        }
//
//// return the masked number
//        return maskedNumber.toString();
//    }

}
