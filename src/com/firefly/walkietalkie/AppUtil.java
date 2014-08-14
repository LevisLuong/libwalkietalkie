package com.firefly.walkietalkie;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.webkit.CookieManager;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;

/**
 * Created by XuanTrung on 2/25/14.
 */
public class AppUtil {
    public static void Log_WalkieTalkie(Object oj) {
        Log.i("WalkieTalkie_Log", oj + "");
    }

    public static String getCarrier(Context context) {

        try {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            System.out.println("Carrier: " + manager.getNetworkOperatorName());
            return manager.getNetworkOperatorName();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getTimeZone() {
        Calendar cal = Calendar.getInstance();
        return ((cal.get(Calendar.ZONE_OFFSET) + cal
                .get(Calendar.DST_OFFSET)) / 60000 / 60) + "";
    }

    public static String getIDDevice(final Context c) {
        final TelephonyManager mTelephonyMgr = (TelephonyManager) c
                .getSystemService(Context.TELEPHONY_SERVICE);
        String imei = mTelephonyMgr.getDeviceId();
        if (imei == null || imei.equals("000000000000000") || imei.equals("")) {
            imei = "35"
                    + // we make this look like a valid IMEI
                    Build.BOARD.length() % 10 + Build.BRAND.length() % 10
                    + Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10
                    + Build.DISPLAY.length() % 10 + Build.HOST.length() % 10
                    + Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10
                    + Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10
                    + Build.TAGS.length() % 10 + Build.TYPE.length() % 10
                    + Build.USER.length() % 10; // 13 digits
        }
        System.out.println("imei:" + imei);
        return imei;
    }


    public static boolean isGpsEnableDriver(Context mContext) {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isGpsEnableClient(Context mContext) {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER)) {
            return true;
        } else {
            return false;
        }
    }


    public static String getCookie(String siteName, String CookieName) {
        String CookieValue = null;
        CookieManager cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie(siteName);
        String[] temp = cookies.split("[;]");
        for (String ar1 : temp) {
            if (ar1.contains(CookieName)) {
                String[] temp1 = ar1.split("[=]");
                CookieValue = temp1[1];
            }
        }
        return CookieValue;
    }

    public static String GET_STORAGE_AUDIO() {
        File myDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                myDir = new File(
                        Environment
                                .getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS),
                        ""
                );
            } else {
                myDir = new File(Environment.getExternalStorageDirectory()
                        + "/dcim/");
            }
            if (myDir != null) {
                if (!myDir.mkdirs()) {
                    if (!myDir.exists()) {
                        myDir = null;
                    }
                }
            }
        }
        return myDir.getAbsolutePath() + "/record.amr";
    }

    public static boolean isNetworkConnection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void setDataState(Context context, boolean enabled) {
        try {
            //set mobile data state
            final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final Class conmanClass = Class.forName(conman.getClass().getName());
            final Field connectivityManagerField = conmanClass.getDeclaredField("mService");
            connectivityManagerField.setAccessible(true);
            final Object connectivityManager = connectivityManagerField.get(conman);
            final Class connectivityManagerClass = Class.forName(connectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = connectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);

            setMobileDataEnabledMethod.invoke(connectivityManager, enabled);
            //Set wifi state
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            wifi.setWifiEnabled(enabled);
        } catch (Exception e) {
            System.out.println(e.toString());
        }


    }
}
