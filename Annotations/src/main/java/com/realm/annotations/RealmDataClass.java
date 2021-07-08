package com.realm.annotations;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;



public class RealmDataClass {
    public  HashMap package_tables;


    public  HashMap table_columns;

    public  String[] tables;

    public  String[][][] table_column_json;

    public  String[] getDynamicClassPaths() {
        return new String[]{};
    }

    public  String[] getDynamicSyncClassPaths() {
        return new String[]{};
    }

    public  List<sync_service_description> getSyncDescription() {return new ArrayList<>();}

    public  HashMap<String, sync_service_description> getHashedSyncDescriptions() {return null;}

   public  List<sync_service_description> getSyncDescription(Object obj) {return null;}


    public String getPackageTable(String package_name) {return "";}

    public HashMap<String, String> getTableColumns(String table_name) {return new HashMap<>();}

    public String getTableCreateSttment(String table_name, Boolean copy) {return "";}

    public String getTableCreateIndexSttment(String table_name) {return "";}

    public String getDeleteRecordSttment(String table_name, String sid)  {return "";}

    /**
     * Returns true if the json does not have the active key defined in db_class or if the json has the key and the value is true
     */
    public Boolean jsonHasActiveKey(JSONObject json) {return false;
    }
//
//    /**
//     * Returns true if the json does not have the active key defined in db_class or if the json has the key and the value is true
//     */
    public Object getContentValuesFromJson(JSONObject json, String table_name) { Object cv=new Object();
         return cv;
    }
//
    public String[][][] getTableColumnJson() {
        return new String[][][]{{{}}};
    }
//
    public String[] getTables() {
        return new String[]{};
    }

    public Object getObjectFromCursor(Object c, String package_name) { return null;


    }
//
    public JSONObject getJsonFromCursor(Object c, String package_name) throws JSONException {

         return null;
    }
//
//    /**
//     * Returns most efficient direct insert queries as per sqlite 3.7 /nAdjust database compound Limit for optimization
//     */
    public String[][] getInsertStatementsFromJson(JSONArray array, String package_name) throws
            JSONException {
        return null;
    }
}
