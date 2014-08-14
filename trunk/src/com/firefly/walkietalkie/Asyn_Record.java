package com.firefly.walkietalkie;

import android.media.MediaRecorder;
import android.os.AsyncTask;

/**
 * Created by XuanTrung on 3/21/2014.
 */
public class Asyn_Record extends AsyncTask<Void, Void, Boolean> {
    MediaRecorder recorder = null;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        recorder = new MediaRecorder();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        recorder.reset();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setAudioChannels(1);

        recorder.setOutputFile(AppUtil.GET_STORAGE_AUDIO());

        recorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {

            }
        });
        recorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                recorder.release();
            }
        });
        try {
            recorder.prepare();
        } catch (java.io.IOException e) {
            recorder = null;
            return false;
        }

        recorder.start();
        return true;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

    }

    @Override
    protected void onPostExecute(Boolean isSuccess) {
        super.onPostExecute(isSuccess);
    }
}
