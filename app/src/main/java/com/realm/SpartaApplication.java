package com.realm;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.realm.spartaservices.dbh;



public class SpartaApplication extends Application {

    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        Thread.setDefaultUncaughtExceptionHandler(new SpartaApplicationErrorHandler(appContext));
        String[] supportedABIS = Build.SUPPORTED_ABIS; // Return an ordered list of ABIs supported by this device.
for (String abi:supportedABIS) {
    Log.e("ARchit", "ss " + abi);
    Toast arc = Toast.makeText(appContext, "Device Architecture is " + abi, Toast.LENGTH_LONG);
   // arc.show();
}


        try{
           dbh sd=new dbh(appContext);
sd=null;
        }catch (Exception ex){}
    }

    public static Context getAppContext() {
        return appContext;
    }
}
