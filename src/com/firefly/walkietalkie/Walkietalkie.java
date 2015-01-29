package com.firefly.walkietalkie;

import android.content.Context;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by XuanTrung on 8/13/2014.
 */
public class Walkietalkie {
    static LiveWebView webView;
    static Context context;
    static Asyn_Record recorder;
    static IUploadListener listenerUpload;
    static IErrorListener listenerError;
    static IPlayTalkieListener listenerPlayTalkie;
    static AudioPool audioPool;

    public static void initialise(Context mcontext, String username, String password, String staff_id) {
        context = mcontext;
        audioPool = new AudioPool(mcontext);
        mSharedPreferences.saveUserName(mcontext, username);
        mSharedPreferences.savePassword(mcontext, password);
        mSharedPreferences.saveStaffid(mcontext, staff_id);
        webView = new LiveWebView(mcontext, "http://walkie.fireflyinnov.com/?os=1");
    }

    public static void appIdle(boolean isIdle) {
        if (webView == null) {
            webView = new LiveWebView(context, "http://walkie.fireflyinnov.com/?os=1");
            webView.destroy();
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("is_idle", isIdle + "");
            webView.sendIORequest("idle", jsonObject.toString());
            if (!isIdle){
                if (!webView.getUrl().equals("http://walkie.fireflyinnov.com/?os=1")){
                    webView.loadAUrl("http://walkie.fireflyinnov.com/?os=1");
                }
                webView.sendIORequest("pendingTalkies", "{}");
            }else{
                webView.loadAUrl("about:blank");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void setListenerUpload(IUploadListener _listener) {
        listenerUpload = _listener;
    }

    public static void setListenerError(IErrorListener _listener) {
        listenerError = _listener;
    }

    public static void setListenerPlayTalkie(IPlayTalkieListener _listener) {
        listenerPlayTalkie = _listener;
    }

    public static void recordVoice(final Context context, boolean isstart, final String staffid) {
        if (webView == null) {
            webView = new LiveWebView(context, "http://walkie.fireflyinnov.com/?os=1");
        }
        if (isstart) {
            if (recorder != null) {
                Toast.makeText(context, "Đang ghi âm !", Toast.LENGTH_SHORT).show();
                return;
            }
            recorder = new Asyn_Record();
            recorder.execute();
        } else {
            if (recorder != null) {
                recorder.stopRecord();
                recorder = null;
                Asyn_Upload_Record upload = new Asyn_Upload_Record(context, staffid);
                upload.execute();
            }
        }
    }

//    private static BroadcastReceiver receiverRecorder = new BroadcastReceiver() {
//        @Override
//        public void onReceive(final Context context, final Intent intent) {
//            String staffid = intent.getStringExtra("staffid");
//            Asyn_Upload_Record upload = new Asyn_Upload_Record(context, staffid, AppUtil.GET_STORAGE_RECORD());
//            upload.setListener(new Asyn_Upload_Record.IListenUploadedRecord() {
//                @Override
//                public void onUploadedStatus(final boolean status) {
//
//                    context.unregisterReceiver(receiverRecorder);
//                }
//            });
//            upload.execute();
//        }
//    };
}
