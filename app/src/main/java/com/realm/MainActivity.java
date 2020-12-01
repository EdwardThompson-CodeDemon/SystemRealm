package com.realm;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.common.base.Stopwatch;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.realm.annotations.RealmDataClass;
import com.realm.spartaservices.asbgw;
import com.realm.spartaservices.dbh;
import com.realm.spartaservices.svars;
import com.realm.wc.supplier_account;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import sparta.realm.Dynamics.spartaDynamics;

import static com.realm.SpartaApplication.realm;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
Activity act;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        act=this;
        asbgw as=new asbgw(new asbgw.sync_status_interface() {
            @Override
            public void on_status_code_changed(int status) {

            }

            @Override
            public void on_status_changed(String status) {

            }

            @Override
            public void on_info_updated(String status) {

            }

            @Override
            public void on_main_percentage_changed(int progress) {

            }

            @Override
            public void on_secondary_progress_changed(int progress) {

            }
        });
        //as.InitialiseAutosync();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor saver =act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

          //      saver.putString("user_name", ((EditText)findViewById(R.id.username_edt)).getText().toString());
                saver.putString("username", "admin");
                saver.putString("pass","admin123A");





                saver.commit();
                as.sync_now();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        String resp=response(1000).toString();
        Log.e("Data size :"," "+resp.getBytes().length);

        Log.e("Deleting :","Deleting");
        dbh.database.execSQL("PRAGMA cache_size=-3000");
        dbh.database.execSQL("DELETE FROM TBL_supplier_account");
        Log.e("Deleted :","Deleted");

        Log.e("gson :",""+insert_from(resp));
    //    do_multi_th ();
       // test_realm();
    }

    void test_realm(){

        for (String s: realm.getDynamicClassPaths()) {


            Log.e("Classes reflected =>", "Realm :" + s);
        }

    }

    void do_multi_th ()
    {
        String resp=response(1000).toString();
        for (int i=0;i<100;i++)
        {
            new Thread(()->{
                Log.e("Deleting :","");
                dbh.database.execSQL("DELETE FROM TBL_supplier_account");
                Log.e("Deleted :","");

                Log.e("gson :",""+insert_from(resp));


            }).start();
        }

    }
    long  insert_from(String jos)
    {
        Stopwatch stw=new Stopwatch();
        stw.start();
        try {
            JSONObject job = new JSONObject(jos);

            JSONArray array=job.getJSONArray("Result");
            synchronized (this){

                String[][] ins= realm.getInsertStatementsFromJson(array, supplier_account.class.getName());
                String sidz_qry=ins[0][0];
                String[] qryz=ins[1];
            long tr_time=stw.elapsed(TimeUnit.MILLISECONDS);
//array=null;
//ins=null;
//while(dbh.database.inTransaction()){Log.e("Waiting .. ","In transaction ");}

            dbh.database.beginTransaction();
                dbh.database.execSQL("INSERT INTO CP_TBL_supplier_account SELECT * FROM TBL_supplier_account WHERE sid in "+sidz_qry+" AND sync_status=2");
//int ins_sz=sidz_qry.getBytes().length;

                for (int i=0;i<qryz.length;i++)
            {
                  dbh.database.execSQL(qryz[i]);
             //   ins_sz+=qryz[i].getBytes().length;

            }
//                ins=null;
//                qryz=null;
//                sidz_qry=null;
             //   Log.e("Trans size ",""+ins_sz);

                dbh.database.execSQL("REPLACE INTO TBL_supplier_account SELECT * FROM CP_TBL_supplier_account");
                dbh.database.execSQL("DELETE FROM CP_TBL_supplier_account");
                dbh.database.setTransactionSuccessful();
            dbh.database.endTransaction();

        Log.e("Exec time ",""+(stw.elapsed(TimeUnit.MILLISECONDS)-tr_time));
        }

        }catch(Exception ex){
            Log.e("Error :",""+ex.getMessage());

        }
        return stw.elapsed(TimeUnit.MILLISECONDS);
    }
    long  insert_v3(String jos)
    {
        Stopwatch stw=new Stopwatch();
        stw.start();
        try {
            JsonObject job = new JsonParser().parse(jos).getAsJsonObject();

            JsonArray array=job.getAsJsonArray("Result");
            int ar_sz=array.size();
            int max=ar_sz<=500?1:ar_sz%500>0?(ar_sz/500)+1:(ar_sz/500);
            Log.e("Max :",""+max);
            Log.e("Arr :",""+ar_sz);
            dbh.database.beginTransaction();
            dbh.database.beginTransaction();
            for(int m=0;m<max;m++)
            {

                for (int s=0;s<(((m*500)+500)<=ar_sz?500:ar_sz-(m*500));s++)
                {
                    int i=(m*500)+s;
                    // Log.e("Iterating :","Round :"+m+" Position "+s+" Array possition :"+i);


                    JsonObject jo= (JsonObject) array.get(i);
                    StringBuffer sb=new StringBuffer();
                    sb.append("INSERT INTO TBL_supplier_account(sid,member_id,acc_id,acc_name,sync_var) VALUES ");


//                        sb.append(s==0?"SELECT "+String.valueOf(jo.get("id"))+" AS sid,"+
//                                String.valueOf(jo.get("member_id"))+" AS member_id,"+
//                                String.valueOf(jo.get("acc_id"))+" AS acc_id,"+
//                                String.valueOf(jo.get("acc_name"))+" AS acc_name,"+
//                                String.valueOf(jo.get("datecomparer"))+" AS sync_var":
//                                " UNION SELECT "+String.valueOf(jo.get("id"))+","+String.valueOf(jo.get("member_id"))+
//                                        ","+String.valueOf(jo.get("acc_id"))+","+String.valueOf(jo.get("acc_name"))+","+String.valueOf(jo.get("datecomparer"))+"");


                    sb.append("("+String.valueOf(jo.get("id"))+","+String.valueOf(jo.get("member_id"))+
                            ","+String.valueOf(jo.get("acc_id"))+","+String.valueOf(jo.get("acc_name"))+","+String.valueOf(jo.get("datecomparer"))+")");

                    //  Log.e("INSERT QUERY :",sb.toString());
                    dbh.database.execSQL(sb.toString());

                }

            }
            dbh.database.setTransactionSuccessful();
            dbh.database.endTransaction();

//            for(int i=0;i<ar_sz;i++){
//                JsonObject jo= (JsonObject) array.get(i);
//            /*
//            INSERT INTO 'centers' ('rid','bp')
//          SELECT 'data1' AS 'rid', '00' AS 'bp'
//UNION  SELECT 'data2', '01'
//UNION  SELECT 'data3', '02'
//UNION  SELECT 'data4', '03'
//             */
//                sb.append(i==0?"SELECT "+String.valueOf(jo.get("id"))+" AS sid,"+
//                        String.valueOf(jo.get("member_id"))+" AS member_id,"+
//                        String.valueOf(jo.get("acc_id"))+" AS acc_id,"+
//                        String.valueOf(jo.get("acc_name"))+" AS acc_name,"+
//                        String.valueOf(jo.get("datecomparer"))+" AS sync_var":
//                        " UNION SELECT "+String.valueOf(jo.get("id"))+","+String.valueOf(jo.get("member_id"))+
//                                ","+String.valueOf(jo.get("acc_id"))+","+String.valueOf(jo.get("acc_name"))+","+String.valueOf(jo.get("datecomparer"))+"");
//
//
//                //           sb.append("("+String.valueOf(jo.get("id"))+","+String.valueOf(jo.get("member_id"))+
////                    ","+String.valueOf(jo.get("acc_id"))+","+String.valueOf(jo.get("acc_name"))+","+String.valueOf(jo.get("datecomparer"))+");");
//
//
//
//            }
//            Log.e("INSERT QUERY :",sb.toString());
//            dbh.database.beginTransaction();
//            dbh.database.execSQL(sb.toString());
//            dbh.database.setTransactionSuccessful();
//            dbh.database.endTransaction();

        }catch(Exception ex){
            Log.e("Error :",""+ex.getMessage());

        }
        return stw.elapsed(TimeUnit.MILLISECONDS);
    }
    JSONObject response(int count)
    {
        JSONObject jo=new JSONObject();
        try {

            jo = new JSONObject("{\"$id\":\"1\",\"IsOkay\":true,\"Message\":\"Details Found\"}\n");

            JSONArray result = new JSONArray();
            for (int i = 0; i < count; i++) {
//    {"$id":"2","id":614,"member_id":308,"acc_id":614,"active":true,"acc_name":"Payments Account","datecomparer":15990436450000000}
                JSONObject jo_in = new JSONObject();
                jo_in.put("id", 200 + i);
                jo_in.put("member_id", 10 + i);
                jo_in.put("acc_id", 614 + i);
                jo_in.put("acc_name", "acc_name" + i);
                jo_in.put("datecomparer", "1234567890" + i);
                result.put(jo_in);

            }jo.put("Result",result);

        }catch(Exception ex){}
        return jo;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}