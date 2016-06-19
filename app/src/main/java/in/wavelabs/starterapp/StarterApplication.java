package in.wavelabs.starterapp;

import android.app.Application;

import com.digits.sdk.android.Digits;
import com.facebook.FacebookSdk;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import in.wavelabs.idn.WaveLabsSdk;
import io.fabric.sdk.android.Fabric;

/**
 * Created by vineelanalla on 14/01/16.
 */
public class StarterApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the SDK before executing any other operations,
        // especially, if you're using Facebook UI elements.
            TwitterAuthConfig authConfig = new TwitterAuthConfig("fUs0SZi3ZHrKTWamffDIV5jb3", "J5FlL2vay2wtJ4VYXW2VDRG0Fp4GlEv5nSTUVnFKZT7rZFy948");
            Fabric.with(this, new TwitterCore(authConfig), new Digits());
            FacebookSdk.sdkInitialize(getApplicationContext());
            WaveLabsSdk.SdkInitialize(getApplicationContext());
            WaveLabsSdk.generateKeyHash(getApplicationContext(),"in.wavelabs.starterapp" );
        }
}
