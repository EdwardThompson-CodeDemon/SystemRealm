package com.realm.spartaservices;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Parcel;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.common.base.Stopwatch;
import com.google.common.reflect.ClassPath;
import com.realm.annotations.sync_service_description;
import com.realm.annotations.sync_status;
import com.realm.wc.supplier_account;

import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import dalvik.system.DexFile;
import sparta.realm.RealmDynamics.spartaDynamics;


import static com.realm.SpartaApplication.realm;


/**
 * Created by Thompsons on 01-Feb-17.
 */

public class dbh {

    Parcel myParcel = Parcel.obtain();
    static Context act;
    public static sdb_model main_db=null;
    public static SQLiteDatabase database=null;
    public static dbh sd;
    public static boolean loaded_db=false;
    spartaDynamics sdy=new spartaDynamics();
    public dbh(Context act)
    {
        this.act=act;
        if(!loaded_db)
        {
            //setup_db_model();
            try {
                //  setup_db();
                setup_db_ann();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }



    }
    public  SQLiteDatabase getDatabase(){
        return database;
    }


    public dbh getInstance()
    {
        if(sd==null)
        {
            sd=new dbh(act);
            return sd;
        }else {
            return sd;
        }
    }




    /**
     *Sets up database by creating and adding missing tables and missing columns and indices in their respective tables from mappery of annotated data which is pre-reflected at pre build
     */
    void setup_db_ann()  {


        sdb_model dbm=new sdb_model();
        dbm.db_name=svars.DB_NAME;
        dbm.db_path=svars.WORKING_APP.file_path_db(act);
        //   dbm.db_path=act.getExternalFilesDir(null).getAbsolutePath()+"/"+svars.DB_NAME;
        dbm.db_password=svars.DB_PASS;

        SQLiteDatabase.loadLibs(act);



        //  SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile.getAbsolutePath(), , null);
        database = SQLiteDatabase.openOrCreateDatabase(dbm.db_path, dbm.db_password, null);
        // if(svars.version_action_done(act, svars.version_action.DB_CHECK)){  loaded_db=true;return;}

        svars.set_photo_camera_type(act,svars.image_indexes.profile_photo,1);



        Stopwatch sw=new Stopwatch();
        sw.start();






        for (String s: sdy.getDynamicClassPaths()) {


            Log.e("Classes reflected =>", "Ann :" + s);

            String table_name=realm.getPackageTable(s);
            try {
                Cursor cursor1 = database.rawQuery("SELECT * FROM "+table_name, null);
                cursor1.moveToFirst();
                if (!cursor1.isAfterLast()) {
                    do {
                        cursor1.getString(0);
                    } while (cursor1.moveToNext());
                }
                cursor1.close();
            } catch (Exception e) {
                database.execSQL(realm.getTableCreateSttment(table_name,false));
                database.execSQL(realm.getTableCreateSttment(table_name,true));
                String crt_stt=realm.getTableCreateIndexSttment(table_name);
                if(crt_stt.length()>1&crt_stt.contains(";"))
                {

                    for(String st:crt_stt.split(";"))
                    {
                        try{
                            Log.e("DB :","Index statement creating =>"+st);
                            database.execSQL(st);
                            Log.e("DB :","Index statement created =>"+st);
                        }catch (Exception ex1){}

                    }



                }
                continue;
            }

            for (Map.Entry<String, String> col : realm.getTableColumns(table_name).entrySet()) {
                try {
                    Cursor cursor1 = database.rawQuery("SELECT count(" + col.getKey() + ") FROM "+table_name, null);
                    cursor1.moveToFirst();
                    if (!cursor1.isAfterLast()) {
                        do {
                            cursor1.getString(0);
                        } while (cursor1.moveToNext());
                    }
                    cursor1.close();
                } catch (Exception e) {
                    database.execSQL("ALTER TABLE "+table_name+" ADD COLUMN " + col.getKey() );
//                                database.execSQL("ALTER TABLE "+db_tb.table_name+" ADD COLUMN " + col.getKey() + " "+col.data_type+" "+col.default_value);
                }
            }








        }

        Log.e("Classes reflected :", "Ann :" + sw.elapsed(TimeUnit.MICROSECONDS));

        svars.set_version_action_done(act, svars.version_action.DB_CHECK);
        Log.e("DB","Finished DB Verification");
        main_db=null;
        loaded_db=true;

    }

    sdb_model.sdb_table table_from_dyna_property_class(Class<?> main_class)
    {

        Object main_obj=null;
        sdb_model.sdb_table tabl=null;//new sdb_model.sdb_table();
        try {
            main_obj = main_class.newInstance();
        } catch (IllegalAccessException e) {
            return null;

        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        try {
            Field ff=main_class.getField("table_name");
            ff.setAccessible(true);
            try {
                String table_name_= (String) ff.get(main_class.newInstance());
                Log.e("TABLE CLASS ", "TABLE :" +table_name_);

                tabl=new sdb_model.sdb_table(table_name_);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            // e.printStackTrace();
        }
        if(tabl==null||tabl.table_name==null||tabl.table_name.length()<1)
        {
            return null;
        }
//Field[] fields =concatenate(main_class.getSuperclass().getFields(),main_class.getDeclaredFields());
        for (Field field : main_class.getDeclaredFields()) {
            field.setAccessible(true); // if you want to modify private fields
            if(field.getType()== dynamic_property.class)
            {
                try {

                    Log.e("DP CLASS ", "" + field.getName());
                    Class<?> clazz = field.get(main_obj).getClass();
                    Field dyna_column_name_field = clazz.getDeclaredField("column_name");
                    Field dyna_index_field = clazz.getDeclaredField("index");
                    dyna_column_name_field.setAccessible(true);
//                  sdb_model.sdb_table.column col=new sdb_model.sdb_table.column(true,(String) dyna_column_name_field.get(field.get(main_obj)));

                    tabl.columns.add(new sdb_model.sdb_table.column((boolean) dyna_index_field.get(field.get(main_obj)),(String) dyna_column_name_field.get(field.get(main_obj))));


                }catch (Exception ex){
                    Log.e("REFLECTION ERROR =>",""+ex.getMessage());
                }
            }else {
//                try {
//                    Log.e("CLASS ", field.getName()+ " - " + field.getType());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        }
        return tabl;
    }
    sdb_model.sdb_table table_from_dyna_property_class_ann(Class<?> main_class)
    {

        Object main_obj=null;
        sdb_model.sdb_table tabl=null;//new sdb_model.sdb_table();
        try {
            main_obj = main_class.newInstance();
        } catch (IllegalAccessException e) {
            return null;

        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        try {
            Field ff=main_class.getField("table_name");
            ff.setAccessible(true);
            try {
                String table_name_= (String) ff.get(main_class.newInstance());
                Log.e("TABLE CLASS ", "TABLE :" +table_name_);

                tabl=new sdb_model.sdb_table(table_name_);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            // e.printStackTrace();
        }
        if(tabl==null||tabl.table_name==null||tabl.table_name.length()<1)
        {
            return null;
        }
//Field[] fields =concatenate(main_class.getSuperclass().getFields(),main_class.getDeclaredFields());
        for (Field field : main_class.getDeclaredFields()) {
            field.setAccessible(true); // if you want to modify private fields
            if(field.getType()== dynamic_property.class)
            {
                try {

                    Log.e("DP CLASS ", "" + field.getName());
                    Class<?> clazz = field.get(main_obj).getClass();
                    Field dyna_column_name_field = clazz.getDeclaredField("column_name");
                    Field dyna_index_field = clazz.getDeclaredField("index");
                    dyna_column_name_field.setAccessible(true);
//                  sdb_model.sdb_table.column col=new sdb_model.sdb_table.column(true,(String) dyna_column_name_field.get(field.get(main_obj)));

                    tabl.columns.add(new sdb_model.sdb_table.column((boolean) dyna_index_field.get(field.get(main_obj)),(String) dyna_column_name_field.get(field.get(main_obj))));


                }catch (Exception ex){
                    Log.e("REFLECTION ERROR =>",""+ex.getMessage());
                }
            }else {
//                try {
//                    Log.e("CLASS ", field.getName()+ " - " + field.getType());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        }
        return tabl;
    }
    public static <T> T[] concatenate(T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        //    @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }











/////////////////////////////////////////////UPDATE //////////////////////////

    public long update_check_period()
    {
        //Date date = svars.sparta_EA_calendar().getTime();
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");
        String dttt = format1.format(date);



        if(svars.update_check_time(act)==null)
        {
            return svars.regsyncinterval_mins;
        }
        Date time1=null;
        try {
            try {
                time1 = new SimpleDateFormat("HH:mm:ss").parse(format1.format(date));
            }catch (Exception ex){
                Log.e("Time Error =>",ex.getMessage());
            }
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(time1);
            Date time2 =null;
            try{
                Log.e("DATE 2 =>"," t "+date.getTime()+" Time=>"+svars.update_check_time(act).split(" ")[1]);
                time2 = new SimpleDateFormat("HH:mm:ss").parse(svars.update_check_time(act).split(" ")[1]);
            }catch (Exception ex){ Log.e("Time Error =>",ex.getMessage());}
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(time2);
            long diffference=Calendar.getInstance().getTimeInMillis()-calendar2.getTimeInMillis();
            // return (int)Math.round((double)diffference/60000);
            int diff_1=(int) ((diffference/ (1000*60)) % 60);
            return  diff_1+(((int) ((diffference / (1000*60*60)) % 24))*60);
        }catch (Exception ex){return svars.regsyncinterval_mins;}



    }






    //////////////////////////////////////////////////////////////////////////////














    public ArrayList<Object> load_dynamic_records(sync_service_description ssd, String[] table_filters)
    {
        ArrayList<Object> objs=new ArrayList<>();

        Cursor c = database.rawQuery("SELECT * FROM "+ssd.table_name+(table_filters==null?"":" "+conccat_sql_filters(table_filters))+" ORDER BY data_status DESC LIMIT "+ssd.chunk_size, null);


        if (c.moveToFirst()) {
            do {


                try {
                    objs.add(load_object_from_Cursor(c,Class.forName(ssd.object_package).newInstance()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                // objs.add(load_object_from_Cursor(c,deepClone(obj)));


            } while (c.moveToNext());
        }
        c.close();



        return objs;
    }

    /*
     *
     * Loads obects from cursor
     *
     *
     */
    public ArrayList<Object> load_dynamic_records_ann(sync_service_description ssd, String[] table_filters)
    {
        ArrayList<Object> objs=new ArrayList<>();

        Cursor c = database.rawQuery("SELECT * FROM "+ssd.table_name+(table_filters==null?"":" "+conccat_sql_filters(table_filters))+" ORDER BY data_status DESC LIMIT "+ssd.chunk_size, null);

        spartaDynamics sd=new spartaDynamics();

        if (c.moveToFirst()) {
            do {


                try {

                    objs.add(sd.getObjectFromCursor(c,ssd.object_package));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // objs.add(load_object_from_Cursor(c,deepClone(obj)));


            } while (c.moveToNext());
        }
        c.close();



        return objs;
    }


    public ArrayList<JSONObject> load_dynamic_json_records_ann(sync_service_description ssd, String[] table_filters)
    {
        ArrayList<JSONObject> objs=new ArrayList<>();

        Cursor c = database.rawQuery("SELECT * FROM "+ssd.table_name+(table_filters==null?"":" "+conccat_sql_filters(table_filters))+" ORDER BY data_status DESC LIMIT "+ssd.chunk_size, null);


        if (c.moveToFirst()) {
            do {


                try {

                    objs.add(realm.getJsonFromCursor(c,ssd.object_package));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // objs.add(load_object_from_Cursor(c,deepClone(obj)));


            } while (c.moveToNext());
        }
        c.close();



        return objs;
    }



    public JSONObject load_JSON_from_object(Object obj) {


        //employee mem=new employee();
        JSONObject jo=new JSONObject();

        Field[] fieldz=concatenate(obj.getClass().getDeclaredFields(),obj.getClass().getSuperclass().getDeclaredFields());
        for (Field field : fieldz) {
            field.setAccessible(true); // if you want to modify private fields
            if(field.getType()== dynamic_property.class)
            {
                try {

                    //  Log.e("DB Field ", "" + field.getName());
                    Class<?> clazz = field.get(obj).getClass();
                    Field dyna_value_field = clazz.getDeclaredField("value");
                    Field dyna_json_name_field = clazz.getDeclaredField("json_name");
                    Field dyna_storage_mode_field = clazz.getDeclaredField("storage_mode");

                    dyna_json_name_field.setAccessible(true);
                    dyna_value_field.setAccessible(true);
                    dyna_storage_mode_field.setAccessible(true);

                    String j_key=(String) dyna_json_name_field.get(field.get(obj));

                    if(j_key!=null)
                    {
                        int storage_mode=(int) dyna_storage_mode_field.get(field.get(obj));
                        if(storage_mode==2){
                            String data=get_saved_doc_base64((String)dyna_value_field.get(field.get(obj)));
                            if(data==error_return)
                            {
                                Log.e("DATA ERROR =>","  :: "+data);

                                //   cv.put("data_status","e");
                                jo.put(j_key,data);
                            }else{
                                jo.put(j_key,data);

                            }

                        }else{
                            jo.put(j_key,(String)dyna_value_field.get(field.get(obj)));
                        }
                    }


                }catch (Exception ex){
                    Log.e("REFLECTION ERROR =>","load_JSON_from_object :: "+ex.getMessage());
                }
            }else {
                try {
                    //   Log.e("CLASS ", field.getName()+ " - " + field.getType());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return jo;

    }

    public static String get_saved_doc_base64(String data_name)
    {
        String res="";
        try {
            String path = Environment.getExternalStorageDirectory().toString();

            File file = new File(svars.WORKING_APP.file_path_employee_data, data_name);
            //java.nio.file.Files.readAllBytes(Path path);
            //);
            //  res = Base64.encodeToString(s_bitmap_handler.getBytes(BitmapFactory.decodeFile(file.getAbsolutePath())), 0);
            res = Base64.encodeToString(org.apache.commons.io.FileUtils.readFileToByteArray(file), 0);
            return res;
        }catch (Exception ex){
            Log.e("Data file retreival :"," "+ex.getMessage());

        }



        return  res;
    }

    public void register_object_auto_ann(Boolean first_record,JSONObject j_obj,sync_service_description ssd)
    {

        if(first_record!=null&&first_record)
        {
            database.beginTransaction();
            Log.e(ssd.service_name+"::Insertion =>","transaction begun");

            return;
        }else if(first_record!=null&&first_record==false)
        {
            Log.e(ssd.service_name+"::Insertion =>","transaction complete");

            database.setTransactionSuccessful();
            database.endTransaction();
            return;
        }
        try {
            try{
                if(ssd.use_download_filter)
                {
                    database.execSQL(realm.getDeleteRecordSttment(ssd.table_name,j_obj.getString("id")));
                }

            }catch(Exception ex){

                Log.e("DELETING ERROR =>",""+ex.getMessage());
            }
            if(realm.jsonHasActiveKey(j_obj))
            {

                ContentValues cv= (ContentValues)realm.getContentValuesFromJson(j_obj,ssd.table_name);
                cv.put("sync_status", sync_status.syned.ordinal());

                Log.e(ssd.service_name+":: Insert result =>"," "+database.insert(ssd.table_name, null, cv));
if(ssd.service_name.equalsIgnoreCase("JobAllInventory"))
{
    Log.e("Timming error :",ssd.service_name+"::"+cv.toString());
}
            }





        }catch (Exception ex){
            Log.e("insert error",""+ex.getMessage());}




    }

    long  register_object_auto_ann_v2(JSONArray array)
    {
        Stopwatch stw=new Stopwatch();
        stw.start();
        try {


            String[][] ins= realm.getInsertStatementsFromJson(array, supplier_account.class.getName());
            String sidz_qry=ins[0][0];
            String[] qryz=ins[1];
            long tr_time=stw.elapsed(TimeUnit.MILLISECONDS);


//while(dbh.database.inTransaction()){Log.e("Waiting .. ","In transaction ");}
            dbh.database.beginTransaction();
            for (int i=0;i<qryz.length;i++)
            {
                dbh.database.execSQL(qryz[i]);
            }

            dbh.database.setTransactionSuccessful();
            dbh.database.endTransaction();
            Log.e("Exec time ",""+(stw.elapsed(TimeUnit.MILLISECONDS)-tr_time));


        }catch(Exception ex){
            Log.e("Error :",""+ex.getMessage());

        }
        return stw.elapsed(TimeUnit.MILLISECONDS);
    }

    public void register_dynadata(Boolean first_record, JSONObject dyna_obj, String dyna_type, String parent)
    {

        if(first_record!=null&&first_record)
        {
            database.beginTransaction();
            Log.e("Dynadata Starting =>","transaction begun");


        }

        try {







            ContentValues cv = new ContentValues();


            cv.put("sid", dyna_obj.getString("Id"));
            cv.put("data_type", dyna_type);
            cv.put("data",dyna_obj.getString("Name"));
            try{   cv.put("code",dyna_obj.getString("Code").equalsIgnoreCase("null")?null:dyna_obj.getString("Code"));
            }catch (Exception ex){}

            try {
                cv.put("parent",dyna_obj.getString("MotherId"));
            }catch (Exception ex){

            }
            try {
                cv.put("parent",dyna_obj.getString("CommuneId"));
            }catch (Exception ex){

            }
            // cv.put("data_code",dyna_obj.getString("Code"));




            database.insert("dyna_data_table", null, cv);
            Log.d("Dynadata inserted =>",""+dyna_obj.toString());




        }catch (Exception ex){
            Log.e("Dynadata insert error",""+ex.getMessage());}
        if(first_record!=null&&first_record==false)
        {
            Log.e("Dynadata ENDING =>","transaction complete");

            database.setTransactionSuccessful();
            database.endTransaction();

        }
    }

    public void register_exception_codes(JSONObject exception_code)
    {


        try {


            ContentValues cv = new ContentValues();

            cv.put("sid", exception_code.getString("id"));
            cv.put("code", exception_code.getString("excep_code"));
            cv.put("data_status", "i");








            Log.e("code insert=>",""+database.insert("exception_codes", null, cv));



        }catch (Exception ex){
            Log.e("Dynadata insert error",""+ex.getMessage());}

    }

    public Object load_object_from_Cursor(Cursor c,Object mem) {

        Field[] fieldz=concatenate(mem.getClass().getDeclaredFields(),mem.getClass().getSuperclass().getDeclaredFields());
        for (Field field : fieldz) {

            // for (Field field : mem.getClass().getDeclaredFields()) {
            field.setAccessible(true); // if you want to modify private fields
            if(field.getType()== dynamic_property.class)
            {
                try {

                    //  Log.e("DB Field ", "" + field.getName());
                    Class<?> clazz = field.get(mem).getClass();
                    Field dyna_value_field = clazz.getDeclaredField("value");
                    Field dyna_db_name_field = clazz.getDeclaredField("column_name");
                    dyna_db_name_field.setAccessible(true);
                    dyna_value_field.setAccessible(true);
                    String c_key=(String) dyna_db_name_field.get(field.get(mem));
                    if(c_key!=null&&c_key.length()>1&&c.getColumnIndex(c_key)!=-1)
                    {
                        dyna_value_field.set(field.get(mem), c.getString(c.getColumnIndex(c_key)));
                    }

                }catch (Exception ex){
                    Log.e("REFLECTION ERROR =>","load_object_from_Cursor "+ex.getMessage());
                }
            }else {
                try {
                    //   Log.e("CLASS ", field.getName()+ " - " + field.getType());9999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            return deepClone(mem);
        } catch (Exception e) {
            e.printStackTrace();
            return mem;
        }

    }

    public static Object deepClone(Object object) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            ByteArrayInputStream bais = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }


    public   interface  data_loading_interface{
        void onDataLoaded(ArrayList x);
        void onDataLoading(int percent, ArrayList x);

    }






    public void populate_dummy_data()
    {
        int pref_count=0;
        int commune_count=0;
        int locality_count=0;

        database.execSQL("DELETE FROM dyna_data_table WHERE data_type IN('rg','prf','cmn','lcl','jc')");
        Log.e("Data build","Begun");
        database.beginTransaction();

        for(int i=0;i<5;i++)
        {

            try {
                ContentValues cv = new ContentValues();
                cv.put("sid", i+"");
                cv.put("data_type", "rg");
                //  cv.put("parent", parent);
                cv.put("data", "REGION "+i);
                database.insert("dyna_data_table", null, cv);

                for(int j=0;j<10;j++)
                {
                    pref_count++;
                    cv = new ContentValues();
                    cv.put("sid", pref_count+"");
                    cv.put("data_type", "prf");
                    cv.put("parent", ""+i);
                    cv.put("data", "REGION "+i+" Pref"+pref_count);
                    database.insert("dyna_data_table", null, cv);

                    for(int k=0;k<10;k++)
                    {
                        commune_count++;
                        cv = new ContentValues();
                        cv.put("sid", commune_count+"");
                        cv.put("data_type", "cmn");
                        cv.put("parent", ""+pref_count);
                        cv.put("data", "REGION "+i+" Pref "+pref_count+" COMMUNE "+commune_count);
                        database.insert("dyna_data_table", null, cv);
                        for(int l=0;l<10;l++)
                        {
                            locality_count++;
                            cv = new ContentValues();
                            cv.put("sid", locality_count+"");
                            cv.put("data_type", "lcl");
                            cv.put("parent", ""+commune_count);
                            cv.put("data", "REGION "+i+" Pref "+pref_count+" COMMUNE "+commune_count+" LOCAL "+locality_count);
                            database.insert("dyna_data_table", null, cv);
                        }

                    }

                }
                Log.e("Data build","Region built "+i);
            }catch (Exception ex){

                Toast.makeText(act,"Region data has failed to be built ", Toast.LENGTH_LONG).show();

            }


        }


        for(int i=0;i<10;i++) {

            try {
                ContentValues cv = new ContentValues();
                cv.put("sid", i + "");
                cv.put("data_type", "jc");

                cv.put("data", "JOB CATEGORY " + i);
                database.insert("dyna_data_table", null, cv);
                Log.e("Data build","Job category built "+i);


            } catch (Exception ex) {
            }
        }


        for(int i=0;i<10;i++) {

            try {
                ContentValues cv = new ContentValues();
                cv.put("sid", i + "");
                cv.put("data_type", "jc");

                cv.put("data", "JOB CATEGORY " + i);
                database.insert("dyna_data_table", null, cv);
            } catch (Exception ex) {
            }
        }

        database.setTransactionSuccessful();
        database.endTransaction();
        Toast.makeText(act,"Data has been built ", Toast.LENGTH_LONG).show();

    }






    public String load_dynadata_name(String sid, String data_type)
    {
        Cursor c=database.rawQuery("SELECT data FROM dyna_data_table WHERE data_type='"+data_type+"' AND sid='"+sid+"'",null);

        if(c.moveToFirst())
        {
            do{

                return c.getString(c.getColumnIndex("data"));

            }while (c.moveToNext());
        }
        c.close();
        return null;
    }














//    static  MethodHandle DIRECT_GET_MH=null, DIRECT_SET_MH=null;
//    static  MethodHandles.Lookup LOOKUP=MethodHandles.lookup();
//
//    public Object load_object_from_Cursor_mh(Cursor c,Object mem) {
//
//       Field[] fieldz=concatenate(mem.getClass().getDeclaredFields(),mem.getClass().getSuperclass().getDeclaredFields());
//     for (Field field : fieldz) {
//
//         // for (Field field : mem.getClass().getDeclaredFields()) {
//         field.setAccessible(true); // if you want to modify private fields
//         if(field.getType()== dynamic_property.class)
//         {
//             try {
//
//               //  Log.e("DB Field ", "" + field.getName());
//                 Class<?> clazz = field.get(mem).getClass();
//                 Field dyna_value_field = clazz.getDeclaredField("value");
//                 Field dyna_db_name_field = clazz.getDeclaredField("column_name");
//              //   dyna_db_name_field.setAccessible(true);
//                // dyna_value_field.setAccessible(true);
//
//                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                     DIRECT_GET_MH = LOOKUP.unreflectGetter(dyna_db_name_field);
//                     DIRECT_SET_MH = LOOKUP.unreflectSetter(dyna_value_field);
//                 }
//                 String c_key="";//(String) DIRECT_GET_MH.invokeExact(VALUE);
//                 try
//                 {
//
//                     c_key=(String) DIRECT_GET_MH.invokeExact(clazz);
//                 }
//                 catch(Throwable ex)
//                 {
//                     throw new AssertionError(ex);
//                 }
//            try
//                 {
//
//                     DIRECT_SET_MH.invokeExact(clazz, c.getString(c.getColumnIndex(c_key)));
//                 }
//                 catch(Throwable ex)
//                 {
//                     throw new AssertionError(ex);
//                 }
////                  if(c_key!=null&&c_key.length()>1&&c.getColumnIndex(c_key)!=-1)
////                 {
////                     dyna_value_field.set(field.get(mem), c.getString(c.getColumnIndex(c_key)));
////                 }
//
//             }catch (Exception ex){
//                 Log.e("DR REFLECTION ERROR =>","load_object_from_Cursor "+ex.getMessage());
//             }
//         }else {
//
//
//         }
//     }
//    try {
//        return deepClone(mem);
//    } catch (Exception e) {
//        e.printStackTrace();
//    return mem;
//    }
//
//}




  public String greatest_sync_var(String table_name, @Nullable String...filters)
    {


        Cursor c=database.rawQuery("SELECT CAST(sync_var AS INTEGER) FROM "+table_name+(filters==null?"":" "+conccat_sql_filters(filters))+" ORDER BY CAST(sync_var AS INTEGER) DESC LIMIT 1",null);

        if(c.moveToFirst())
        {
            do{

                String res=c.getString(0);
                c.close();
                return res;
            }while (c.moveToNext());
        }
        c.close();
        return "0";
    }

    public String get_record_count(String table_name, @Nullable String...filters)
    {

        String qry="SELECT COUNT(*) FROM "+table_name+(filters==null?"":" "+conccat_sql_filters(filters));
        //  Log.e("QRY :",""+qry);
        Cursor c=database.rawQuery(qry,null);

        if(c.moveToFirst())
        {
            do{
                String res=c.getString(0);
                c.close();
                return res;

            }while (c.moveToNext());
        }
        c.close();
        return "0";
    }

    String conccat_sql_filters(String[] str_to_join)
    {
        String result="";
        for(int i=0;i<str_to_join.length;i++)
        {
            result=result+(i==0?"WHERE ":" AND ")+str_to_join[i];
        }
        return result;

    }


    public String record_count(String table_name)
    {


        Cursor c=database.rawQuery("SELECT COUNT(*) FROM "+table_name,null);

        if(c.moveToFirst())
        {
            do{

                return c.getString(0);

            }while (c.moveToNext());
        }
        c.close();
        return "0";
    }


    String conccat_sql_string(String[] str_to_join)
    {
        String result="";
        for(int i=0;i<str_to_join.length;i++)
        {
            result=result+(i==0?"":",")+"'"+str_to_join[i]+"'";
        }
        return result;

    }


    String conccat_sql_string(String[] str_to_join, ArrayList<String> str_to_join2)
    {
        String result="";
        for(int i=0;i<str_to_join.length;i++)
        {
            result=result+(i==0?"":",")+"'"+str_to_join[i]+"'";
        }
        for(int i=0;i<str_to_join2.size();i++)
        {
            result=result+(result.length()<0?"":",")+"'"+str_to_join2.get(i)+"'";
        }
        return result;

    }
    String conccat_sql_string(ArrayList<String> str_to_join2)
    {
        String result="";


        for(int i=0;i<str_to_join2.size();i++)
        {
            result=result+(result.length()<1?"":",")+"'"+str_to_join2.get(i)+"'";
        }
        return result;

    }




















    String save_doc_us(String base64_bytes)
    {
        byte[] file_bytes= Base64.decode(base64_bytes,0);

        String img_name="TA_DAT"+ System.currentTimeMillis()+"JPG_IDC.JPG";
        //  String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOutputStream = null;
        //  File file = new File(path + "/TimeAndAttendance/.RAW_EMPLOYEE_DATA/");
        File file = new File(svars.WORKING_APP.file_path_employee_data);
        if (!file.exists()) {
            Log.e("Creating data dir=>",""+ String.valueOf(file.mkdirs()));
        }
        //  file = new File(path + "/TimeAndAttendance/.RAW_EMPLOYEE_DATA/", img_name);
        file = new File(svars.WORKING_APP.file_path_employee_data, img_name);

        try {
            fOutputStream = new FileOutputStream(file);
            fOutputStream.write(file_bytes);

            //fpb.compress(Bitmap.CompressFormat.JPEG, 100, fOutputStream);

            fOutputStream.flush();
            fOutputStream.close();

            //  MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //   Toast.makeText(this, "Save Failed", Toast.LENGTH_SHORT).show();
            return "--------------";
        } catch (IOException e) {
            e.printStackTrace();

            //   Toast.makeText(this, "Save Failed", Toast.LENGTH_SHORT).show();
            return "--------------";
        }
        return img_name;
    }
    public static  String error_return="!!!";





    public long sync_period()
    {
        //Date date = svars.sparta_EA_calendar().getTime();
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");
        String dttt = format1.format(date);



        if(svars.sync_time(act)==null)
        {
            return svars.regsyncinterval_mins;
        }
        Date time1=null;
        try {
            try {
                time1 = new SimpleDateFormat("HH:mm:ss").parse(format1.format(date));
            }catch (Exception ex){
                Log.e("Time Error =>",ex.getMessage());
            }
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(time1);
            Date time2 =null;
            try{
                Log.e("DATE 2 =>"," t "+date.getTime()+" Time=>"+svars.sync_time(act).split(" ")[1]);
                time2 = new SimpleDateFormat("HH:mm:ss").parse(svars.sync_time(act).split(" ")[1]);
            }catch (Exception ex){ Log.e("Time Error =>",ex.getMessage());}
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(time2);
            long diffference= Calendar.getInstance().getTimeInMillis()-calendar2.getTimeInMillis();
            // return (int)Math.round((double)diffference/60000);
            int diff_1=(int) ((diffference/ (1000*60)) % 60);
            return  diff_1+(((int) ((diffference / (1000*60*60)) % 24))*60);
        }catch (Exception ex){return svars.regsyncinterval_mins;}



    }


    public static Gpsprobe_r gps;


    public static void log_event(Context act, String data)
    {

        gps =gps==null?new Gpsprobe_r(act):gps;
        String prefix=svars.sparta_EA_calendar().getTime().toString()+"   :   "+gps.getLatitude()+","+gps.getLongitude()+"     =>";

        //   String prefix=svars.sparta_EA_calendar().getTime().toString()+"     =>";
        String root = act.getExternalFilesDir(null).getAbsolutePath() + "/logs";
        Log.e("LOG_TAG", "PATH: " + root);

        File file = new File(root);
        file.mkdirs();
        try {
            File gpxfile = new File(file, svars.gett_date()+""+svars.Log_file_name);
            FileWriter writer = new FileWriter(gpxfile,true);
            writer.append(svars.APP_OPERATION_MODE==svars.OPERATION_MODE.DEV?prefix+data+"\n": s_cryptor.encrypt(prefix+data+"\n"));
            writer.flush();
            writer.close();
        }catch (Exception ex)
        {

        }

    }

    public static void log_String(Context act, String data)
    {
        gps =gps==null?new Gpsprobe_r(act):gps;
        String prefix=svars.sparta_EA_calendar().getTime().toString()+"   :   "+gps.getLatitude()+","+gps.getLongitude()+"     =>";

        //  String prefix=svars.sparta_EA_calendar().getTime().toString()+"     =>";
        String root = act.getExternalFilesDir(null).getAbsolutePath() + "/logs";
        Log.e("LOG_TAG", "PATH: " + root);

        File file = new File(root);
        file.mkdirs();
        try {
            File gpxfile = new File(file, svars.string_file_name);
            FileWriter writer = new FileWriter(gpxfile,true);
            writer.append(svars.APP_OPERATION_MODE!=svars.OPERATION_MODE.DEV?prefix+data+"\n":prefix+data+"\n");
            writer.flush();
            writer.close();
        }catch (Exception ex)
        {

        }

    }










    public boolean payment_code_exists(String payment_code)
    {


        return database.rawQuery("SELECT * FROM payment_codes_table WHERE pay_code='"+payment_code+"'",null).moveToFirst();



    }






}
