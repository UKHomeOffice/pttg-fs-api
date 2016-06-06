package steps;

import java.util.HashMap;
import java.util.Map;

/* BDD keys can be mapped directly to jsonpath if they are in the format "<keyword> <keyword>"  (separated by a space)
   If a more readable name is required in the automated test  - add an entry to the KEY_MAP.

   Collections are not currently supported
 */

public class FeatureKeyMapper {

    private final static Map<String, String> KEY_MAP;

    static {
        KEY_MAP = new HashMap<>();
        KEY_MAP.put("National Insurance Number", "individual nino");
        KEY_MAP.put("Application Raised to date", "categoryCheck assessmentStartDate");
        KEY_MAP.put("Application Raised date", "categoryCheck applicationRaisedDate");
        KEY_MAP.put("Financial requirement met", "categoryCheck passed");
        KEY_MAP.put("Employment requirement met", "categoryCheck passed");
        KEY_MAP.put("Failure reason", "categoryCheck failureReason");
    }

    public static String buildJsonPath(final String key) {
        String resolvedKey = KEY_MAP.getOrDefault(key, key);
        StringBuilder sb = new StringBuilder(resolvedKey.replaceAll(" ", "."));
        sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
        sb.insert(0,"$.");
        return sb.toString();
    }
}
