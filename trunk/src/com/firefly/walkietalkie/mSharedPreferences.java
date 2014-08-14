package com.firefly.walkietalkie;

import android.content.Context;
import android.content.SharedPreferences;

public class mSharedPreferences {
    private static final String SESSION_COOKIE = "JSESSIONID";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String STAFFID = "staffid";
    private static String PREFS_NAME = "MyPrefsFile";
    private static String STATUS_PREFS = "StatusPrefs";

    public static boolean getFirstTime(Context context) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getBoolean("FirstTime", true);
    }

    public static void saveFirstTime(Context context,
                                     boolean variable) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("FirstTime", variable);

        // Commit the edits!
        editor.commit();
    }

    public static String getUserName(Context context) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getString(USERNAME, "");
    }

    public static void saveUserName(Context context, String str) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(USERNAME, str);

        // Commit the edits!
        editor.commit();
    }

    public static String getPassword(Context context) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getString(PASSWORD, "");
    }

    public static void savePassword(Context context, String str) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PASSWORD, str);

        // Commit the edits!
        editor.commit();
    }

    public static String getStaffid(Context context) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getString(STAFFID, "");
    }

    public static void saveStaffid(Context context, String str) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(STAFFID, str);

        // Commit the edits!
        editor.commit();
    }

    public static String getSession(Context context) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getString(SESSION_COOKIE, "");
    }

    public static void saveSession(Context context, String str) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(SESSION_COOKIE, str);

        // Commit the edits!
        editor.commit();
    }

}