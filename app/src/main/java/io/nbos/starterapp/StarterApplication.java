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
        AndroidApiContext.generateKeyHash(getApplicationContext(), "io.nbos.starterapp");
        try {
            Class.forName("io.nbos.capi.modules.token.v0.TokenIdsRegistry");
            Class.forName("io.nbos.capi.modules.media.v0.MediaIdsRegistry");
            Class.forName("io.nbos.capi.modules.identity.v0.IdentityIdsRegistry");
        } catch (Exception x) {
            x.printStackTrace();
        }
    }
}
