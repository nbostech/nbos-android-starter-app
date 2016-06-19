package in.wavelabs.starterapp.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import in.wavelabs.idn.utils.TokenPrefrences;
import in.wavelabs.starterapp.R;


public class SplashScreen extends AppCompatActivity {


    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        Date date = new Date();   // given date
        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTime(date);   // assigns calendar to given date

        int SPLASH_TIME_OUT = 2000;
        if (TokenPrefrences.getAccessToken(this).equals("")) {


            new Handler().postDelayed(new Runnable() {

                /*
                 * Showing splash screen with a timer. This will be useful when you
                 * want to show case your app logo / company
                 */

                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    // Start your app main activity*/

                    Intent i = new Intent(SplashScreen.this, AuthActivity.class);
                    startActivity(i);
                    finish();
                    // close this activity

                }
            }, SPLASH_TIME_OUT);
        }
        else{
            new Handler().postDelayed(new Runnable() {

                /*
                 * Showing splash screen with a timer. This will be useful when you
                 * want to show case your app logo / company
                 */

                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    // Start your app main activity*/

                    Intent i = new Intent(SplashScreen.this, AuthActivity.class);
                    startActivity(i);
                    finish();
                    // close this activity

                }
            }, SPLASH_TIME_OUT);
        }
    }
}
