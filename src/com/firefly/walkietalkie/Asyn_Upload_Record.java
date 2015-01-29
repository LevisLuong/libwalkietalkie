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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * Created by XuanTrung on 3/20/2014.
 */
public class Asyn_Upload_Record extends AsyncTask<Void, Void, String> {
    String staffid;
    Context mContext;

    File file;

    public Asyn_Upload_Record(Context context, String _staffid) {
        mContext = context;
        staffid = _staffid;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpContext httpContext = new BasicHttpContext();
            // Create a local instance of cookie store
            HttpPost httpPost = new HttpPost("http://walkie.fireflyinnov.com/talkie");
            //httpContext.setAttribute(ClientContext.COOKIE_STORE, mSharedPreferences.getSession(mContext));
            httpPost.addHeader("Cookie", mSharedPreferences.getSession(mContext));

            MultipartEntity multipart = new MultipartEntity();
            multipart.addPart("to_staff_id", new StringBody(staffid));
            AppUtil.Log_WalkieTalkie("Path file record: " + AppUtil.GET_STORAGE_RECORD());
            file = new File(AppUtil.GET_STORAGE_RECORD());
            AppUtil.Log_WalkieTalkie("Path file record isexist: " + file.exists());
            multipart.addPart("audio", new FileBody(file));
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
            return s.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    protected void onPostExecute(String response) {
        if (file != null) {
            file.delete();
        }
        if (!response.equals("OK")) {
            Walkietalkie.listenerUpload.onUploadStatus(false, false);
        }

        super.onPostExecute(response);
    }

}
