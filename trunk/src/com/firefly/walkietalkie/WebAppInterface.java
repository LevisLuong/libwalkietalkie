package com.firefly.walkietalkie;

import android.app.Activity;
import android.content.Context;
import android.webkit.JavascriptInterface;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


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
        AppUtil.Log_WalkieTalkie("IOError:" + jSonInfo);
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
    public void pendingTalkies(String jSon) {
        AppUtil.Log_WalkieTalkie("pendingTalkies:" + jSon);

        //receive {[{from_staff_id,file_name},{from_staff_id, file_name}......]}
        //send: {from_staff_id: id, file_name:['acb','weg'....]}
        wv.executeJavascript("resolve", jSon);

    }

    @JavascriptInterface
    public void notify(String funcName, String json) {
        AppUtil.Log_WalkieTalkie("Function name:" + funcName + "," + "Json:" + json);
        if (funcName.equals("IOError")) {
            try {
                JSONObject jObj = new JSONObject(json);
                String code = jObj.getString("code");
                String name = jObj.getString("name");
                String message = jObj.getString("message");
                if (code.equals("401")) {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("username", mSharedPreferences.getUserName(mContext));
                        object.put("password", mSharedPreferences.getPassword(mContext));
                        object.put("staff_id", mSharedPreferences.getStaffid(mContext));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    System.out.println(object);
                    wv.executeJavascript("authenIORequest", object.toString());

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (funcName.equals("serverReady")) {
            Walkietalkie.appIdle(false);
        }
        if (funcName.equals("error")) {
            /*receive: {code : errCode,
                    name : errName,
                    message : errMsg}
            }*/
            try {
                JSONObject jObj = new JSONObject(json);
                String messError = jObj.getString("message");
                if (Walkietalkie.listenerError != null) {
                    Walkietalkie.listenerError.onError(messError);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (funcName.equals("talkie")) {
            //{"status":200,"funcName":"talkie","data":{"found_target_staff":false}}
            try {
                JSONObject jsonObj = new JSONObject(json);
                JSONObject data = jsonObj.getJSONObject("data");
                boolean isStaffOnl = data.getBoolean("found_target_staff");
                Walkietalkie.listenerUpload.onUploadStatus(true, isStaffOnl);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (funcName.equals("pendingTalkies")) {
           /* receive {[{from_staff_id,file_name},{from_staff_id, file_name}......]}
            send: {from_staff_id: id, file_name:['acb','weg'....]}*/
            //receive play file
            List<Takie> lstTalkies = new ArrayList<Takie>();
            try {
                JSONObject jsonObject1 = new JSONObject(json);
                JSONObject jsObj = jsonObject1.getJSONObject("data");
                JSONArray jsonArray = jsObj.getJSONArray("talkies");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json_data = jsonArray.getJSONObject(i);
                    Takie takie = new Takie();
                    takie.setFilename(json_data.getString("file_name"));
                    takie.setFrom_staff_id(json_data.getString("from_staff_id"));
                    lstTalkies.add(takie);
                }
                //Play audio
//                for (Takie takie : lstTalkies) {
//                    new DownloadFileAsync(takie, Walkietalkie.listenerPlayTalkie).execute();
//                }
                if (!lstTalkies.isEmpty()){
                    new DownloadFileAsync(lstTalkies, Walkietalkie.listenerPlayTalkie);
                }
                //send resolve to server
                sendResolveTalkie(lstTalkies);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (funcName.equals("notify")) {
            //receive {"status":200,"funcName":"notify","data":{"message_type":"talkie","from_staff_id":"321","file":"12877-p27qo1.amr"}}
            //send: {from_staff_id: id, file_name:['name']}
            try {
                JSONObject jsonObj = new JSONObject(json);
                JSONObject data = jsonObj.getJSONObject("data");
                Takie takie = new Takie();
                takie.setFilename(data.getString("file_name"));
                takie.setFrom_staff_id(data.getString("from_staff_id"));
                new DownloadFileAsync(takie, Walkietalkie.listenerPlayTalkie);
                sendResolveTalkie(takie);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    void sendResolveTalkie(Takie talkie) {
        try {
            JSONObject jsonSend = new JSONObject();
            jsonSend.put("from_staff_id", talkie.getFrom_staff_id());
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(talkie.getFilename());
            jsonSend.put("files", jsonArray);
            wv.sendIORequest("resolve", jsonSend.toString());
            AppUtil.Log_WalkieTalkie("Send resolve: " + jsonSend.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void sendResolveTalkie(List<Takie> talkies) {
        try {
            while (!talkies.isEmpty()) {
                String staffid = talkies.get(0).getFrom_staff_id();
                List<String> files = new ArrayList<String>();
                files.add(talkies.get(0).getFilename());
                for (int i = 1; i < talkies.size(); i++) {
                    if (staffid.equals(talkies.get(i).getFrom_staff_id())) {
                        files.add(talkies.get(i).getFilename());
                    }
                }
                int sizeDel = 0;
                while (sizeDel != talkies.size()) {
                    if (talkies.get(sizeDel).getFrom_staff_id().equals(staffid)) {
                        talkies.remove(sizeDel);
                    } else {
                        sizeDel++;
                    }
                }

                JSONObject jsonSend = new JSONObject();
                jsonSend.put("from_staff_id", staffid);
                JSONArray jsonArray = new JSONArray();
                for (String file : files) {
                    jsonArray.put(file);
                }
                jsonSend.put("files", jsonArray);
                wv.sendIORequest("resolve", jsonSend.toString());
                AppUtil.Log_WalkieTalkie("Send resolve: " + jsonSend.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
