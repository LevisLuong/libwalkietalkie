package com.firefly.walkietalkie;

import android.app.Activity;
import android.content.Context;
import android.webkit.JavascriptInterface;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by XuanTrung on 3/20/2014.
 */
public class WebAppInterface {
    Context mContext;
    LiveWebView wv;

    public WebAppInterface(Context context, LiveWebView _wv) {
        mContext = context;
        wv = _wv;
    }

    //---------Cac ham giao tiep javascript voi server----------------
    @JavascriptInterface
    public void IOError(String jSonInfo) {
        AppUtil.Log_WalkieTalkie(jSonInfo);
        try {
            JSONObject jObj = new JSONObject(jSonInfo);
            String code = jObj.getString("code");
            String name = jObj.getString("name");
            String message = jObj.getString("message");
            if (code.equals("401")) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject object = new JSONObject();
                        try {
                            object.put("username", mSharedPreferences.getUserName(mContext));
                            object.put("password", mSharedPreferences.getPassword(mContext));
                            object.put("staff_id", mSharedPreferences.getStaffid(mContext));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println(object);
                        wv.executeJavascript("authenIORequest", object);
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void serverReady() {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                wv.executeJavascript("sendIORequest", "pendingTalkies", "");
                wv.executeJavascript("sendIORequest", "idle", "false");
            }
        });
    }

    /**
     * Ham listen button record
     *
     * @param start Did press button
     */
    @JavascriptInterface
    public void native_recordVoice(final boolean start) {
    }

}
