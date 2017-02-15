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
        KEY_MAP.put("Lowest Balance Date", "failureReason lowestBalanceDate");
        KEY_MAP.put("Application Raised to date", "categoryCheck assessmentStartDate");
        KEY_MAP.put("Application Raised date", "categoryCheck applicationRaisedDate");
        KEY_MAP.put("Pass", "pass");
        KEY_MAP.put("Failure reason", "categoryCheck failureReason");
        KEY_MAP.put("Sort code", "account sortCode");
        KEY_MAP.put("sort code", "account sortCode");
        KEY_MAP.put("Minimum", "minimum");
        KEY_MAP.put("Lowest Balance Value", "failureReason lowestBalanceValue");
        KEY_MAP.put("Failure reason", "categoryCheck failureReason");
        KEY_MAP.put("From Date", "fromDate");
        KEY_MAP.put("From date", "fromDate");
        KEY_MAP.put("To Date", "toDate");
        KEY_MAP.put("To date", "toDate");
        KEY_MAP.put("Account number", "account accountNumber");
        KEY_MAP.put("Account Holder Name", "accountHolderName");
        KEY_MAP.put("Consent", "consent");
        KEY_MAP.put("Description", "status message");
        KEY_MAP.put("Record Count", "failureReason recordCount");
        KEY_MAP.put("Leave end date", "leaveEndDate");
        KEY_MAP.put("Leave end date", "leaveEndDate");
        KEY_MAP.put("Threshold", "threshold");
        KEY_MAP.put("Course Length", "cappedValues courseLength");
        KEY_MAP.put("Response Description", "status message");
    }

    public static String buildJsonPath(final String key) {
        String resolvedKey = KEY_MAP.getOrDefault(key, key);
        StringBuilder sb = new StringBuilder(resolvedKey.replaceAll(" ", "."));
        sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
        sb.insert(0,"$.");
        return sb.toString();
    }
}
