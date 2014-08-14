package com.firefly.walkietalkie;

import android.content.Context;

/**
 * Created by XuanTrung on 8/13/2014.
 */
public class Walkietalkie {
    static LiveWebView webView;
    static Context context;

    public static void initialise(Context mcontext, String username, String password, String staff_id) {
        context = mcontext;
        mSharedPreferences.saveUserName(mcontext, username);
        mSharedPreferences.savePassword(mcontext, password);
        mSharedPreferences.saveStaffid(mcontext, staff_id);
        webView = new LiveWebView(mcontext, "http://walkie.fireflyinnov.com/?os=1");
    }

    public static void appIdle() {
        webView.executeJavascript("sendIORequest", "idle", "true");
    }

    public static void recordVoice(boolean isstart, String staffid) {
        if (isstart) {

        } else {

        }
    }
}
