package com.realm.annotations;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;



public class RealmDataClass {
    public static HashMap package_tables;

    public static HashMap table_columns;

    public static String[] tables;

    public static String[][][] table_column_json;

    public static String[] getDynamicClassPaths() {
        return new String[]{};
    }

    public static String[] getDynamicSyncClassPaths() {
        return new String[]{};
    }

    public static List<sync_service_description> getSyncDescription() {return new ArrayList<>();}

    public static String getPackageTable(String package_name) {return "";}

    public static HashMap<String, String> getTableColumns(String table_name) {return new HashMap<>();}

    public static String getTableCreateSttment(String table_name, Boolean copy) {return "";}

    public static String getTableCreateIndexSttment(String table_name) {return "";}

    public static String getDeleteRecordSttment(String table_name, String sid)  {return "";}

    /**
     * Returns true if the json does not have the active key defined in db_class or if the json has the key and the value is true
     */
//    public static Boolean jsonHasActiveKey(JSONObject json) {return false;
//    }
//
//    /**
//     * Returns true if the json does not have the active key defined in db_class or if the json has the key and the value is true
//     */
//    public static ContentValues getContentValuesFromJson(JSONObject json, String table_name) { ContentValues cv=new ContentValues();
//         return cv;
//    }
//
    public static String[][][] getTableColumnJson() {
        return new String[][][]{{{}}};
    }
//
    public static String[] getTables() {
        return new String[]{};
    }

//    public static Object getObjectFromCursor(Cursor c, String package_name) { return null;
//    }
//
//    public static JSONObject getJsonFromCursor(Cursor c, String package_name) throws JSONException {
//
//         return null;
//    }
//
//    /**
//     * Returns most efficient direct insert queries as per sqlite 3.7 /nAdjust database compound Limit for optimization
//     */
//    public static String[][] getInsertStatementsFromJson(JSONArray array, String package_name) throws
//            JSONException {
//        return null;
//    }
}
