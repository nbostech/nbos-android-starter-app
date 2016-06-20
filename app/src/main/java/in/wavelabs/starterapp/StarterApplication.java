package in.wavelabs.starterapp;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.facebook.FacebookSdk;

import in.wavelabs.idn.WaveLabsSdk;

/**
 * Created by vineelanalla on 14/01/16.
 */
public class StarterApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the SDK before executing any other operations,
        // especially, if you're using Facebook UI elements.
            FacebookSdk.sdkInitialize(getApplicationContext());
            WaveLabsSdk.SdkInitialize(getApplicationContext());
            WaveLabsSdk.generateKeyHash(getApplicationContext(),"in.wavelabs.starterapp" );
        }
}
