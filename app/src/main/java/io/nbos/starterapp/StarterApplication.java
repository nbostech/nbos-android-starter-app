package io.nbos.starterapp;

import android.app.Application;
import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;

import com.facebook.FacebookSdk;
import com.facebook.accountkit.AccountKit;
import io.nbos.android.capi.AndroidApiContext;


/**
 * Created by vineelanalla on 14/01/16.
 */
public class StarterApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        AndroidApiContext.initialize(getApplicationContext());
        FacebookSdk.sdkInitialize(getApplicationContext());
        AccountKit.initialize(getApplicationContext());
        AndroidApiContext.generateKeyHash(getApplicationContext(), "in.wavelabs.starterapp");
    }
}
