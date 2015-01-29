package com.firefly.walkietalkie;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

class DownloadFileAsync extends AsyncTask<String, String, String> {
    IPlayTalkieListener listener;
    List<Takie> lstTalkie;
    int i = 0;

    public DownloadFileAsync(Takie takie, IPlayTalkieListener _listener) {
        i = 0;
        lstTalkie = new ArrayList<Takie>();
        lstTalkie.add(takie);
        listener = _listener;
        this.execute(lstTalkie.get(i).getFilename());
    }

    public DownloadFileAsync(List<Takie> takies, IPlayTalkieListener _listener) {
        i = 0;
        listener = _listener;
        lstTalkie = new ArrayList<Takie>();
        lstTalkie.addAll(takies);
        this.execute(lstTalkie.get(i).getFilename());
    }

    public DownloadFileAsync(List<Takie> takies, int _i, IPlayTalkieListener _listener) {
        i = _i;
        listener = _listener;
        lstTalkie = new ArrayList<Takie>();
        lstTalkie.addAll(takies);
        this.execute(lstTalkie.get(i).getFilename());
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... aurl) {
        int count;
        String resultString = "";
        try {
            String strurl = "http://walkie.fireflyinnov.com/audio/" + aurl[0];
            URL url = new URL(strurl);
            URLConnection conexion = url.openConnection();
            conexion.connect();
            String audioDir = AppUtil.GET_STORAGE_AUDIO();
            int lenghtOfFile = conexion.getContentLength();
            Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(audioDir + "/" + aurl[0]);

            byte data[] = new byte[lenghtOfFile];

            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
            resultString = audioDir + "/" + aurl[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultString;

    }

    @Override
    protected void onPostExecute(String result) {
        AppUtil.Log_WalkieTalkie("file da tải về :" + result);
        if (result.equals("")) {
            AppUtil.Log_WalkieTalkie("Lỗi khi download file de play");
        } else {
            Walkietalkie.audioPool.playAudio(Walkietalkie.audioPool.addAudio(result));
            if (listener != null) {
                listener.onPlayTalkie(lstTalkie.get(i).getFrom_staff_id());
            }
            i++;
            if (i < lstTalkie.size()) {
                new DownloadFileAsync(lstTalkie, i, listener);
            }
        }
    }
}