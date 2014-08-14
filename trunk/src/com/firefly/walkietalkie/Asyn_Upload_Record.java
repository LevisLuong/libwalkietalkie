package com.firefly.walkietalkie;

import android.content.Context;
import android.os.AsyncTask;
import khandroid.ext.apache.http.HttpResponse;
import khandroid.ext.apache.http.client.methods.HttpPost;
import khandroid.ext.apache.http.entity.mime.MultipartEntity;
import khandroid.ext.apache.http.entity.mime.content.FileBody;
import khandroid.ext.apache.http.entity.mime.content.StringBody;
import khandroid.ext.apache.http.impl.client.DefaultHttpClient;
import khandroid.ext.apache.http.protocol.BasicHttpContext;
import khandroid.ext.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * Created by XuanTrung on 3/20/2014.
 */
public class Asyn_Upload_Record extends AsyncTask<Void, Void, Boolean> {
    String staffid;
    String filePath;
    Context mContext;
    IListenUploadedRecord listener;

    File file;

    public Asyn_Upload_Record(Context context, String _staffid, String _file) {
        mContext = context;
        staffid = _staffid;
        filePath = _file;
    }

    public void setListener(IListenUploadedRecord _l) {
        listener = _l;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpContext httpContext = new BasicHttpContext();
            // Create a local instance of cookie store
            HttpPost httpPost = new HttpPost("http://walkie.fireflyinnov.com/talkie");
            //httpContext.setAttribute(ClientContext.COOKIE_STORE, mSharedPreferences.getSession(mContext));
            httpPost.addHeader("Cookie", mSharedPreferences.getSession(mContext));

            MultipartEntity multipart = new MultipartEntity();
            multipart.addPart("to_staff_id", new StringBody(staffid));
            if (filePath != null && !filePath.equals("")) {
                file = new File(filePath);
                multipart.addPart("audio", new FileBody(file));
            }
            // Send it
            httpPost.setEntity(multipart);
            HttpResponse response = httpClient.execute(httpPost, httpContext);

            //get response
            System.out.println("Status is " + response.getStatusLine());

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent(), "UTF-8"));
            String sResponse;
            StringBuilder s = new StringBuilder();
            while ((sResponse = reader.readLine()) != null) {
                s = s.append(sResponse);
            }
            System.out.println("Response: " + s);
            JSONObject jsonObject = new JSONObject(s.toString());
            // String request = jsonObject.getString("urlMethod");
            String action = jsonObject.getString("status");
            if (action.equals("OK")) {
                // If everything goes ok, we can get the response
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean isSuccess) {
        if (file != null) {
            file.delete();
        }
        if (isSuccess) {
            listener.onUploadedStatus(true);
        } else {
            listener.onUploadedStatus(false);
        }
        super.onPostExecute(isSuccess);
    }

    public interface IListenUploadedRecord {
        public void onUploadedStatus(boolean status);
    }
}
