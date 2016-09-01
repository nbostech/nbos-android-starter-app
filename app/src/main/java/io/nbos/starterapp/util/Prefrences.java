package io.nbos.starterapp.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by vivekkiran on 6/18/16.
 */

public class Prefrences {
    public Prefrences(Context context) {

    }

    static SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "user_pref";

    public SharedPreferences getSharedPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences;
    }

    public static SharedPreferences clearPrefrences(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        return sharedPreferences;
    }

    public static String getUserId(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("userId", "");
    }

    public static void setUserId(Context context, String userId) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("userId", userId).apply();
    }

    public static void setFirstName(Context context, String firstName) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("firstName", firstName).apply();
    }

    public static String getFirstName(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("firstName", "");
    }

    public static void setLastName(Context context, String lastName) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("lastName", lastName).apply();
    }

    public static String getLastName(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("lastName", "");
    }

    public static void setEmailId(Context context, String accessToken) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("emailId", accessToken).apply();
    }

    public static String getEmailId(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("emailId", "");
    }

    public static void setPhoneNumber(Context context, Long phoneNumber) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putLong("phoneNumber", phoneNumber).apply();
    }

    public static Long getPhoneNumber(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getLong("phoneNumber", 0);
    }

    public static String getDescription(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("description", "");
    }


}
