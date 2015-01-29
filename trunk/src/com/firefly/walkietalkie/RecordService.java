package com.firefly.walkietalkie;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.IBinder;

public class RecordService extends Service implements
        MediaRecorder.OnInfoListener, MediaRecorder.OnErrorListener {
    public static final String BROADCAST_RECORDER = "record.taxi";

    public MediaRecorder recorder = null;
    private boolean isRecording = false;

    @Override
    public void onCreate() {
        super.onCreate();
        recorder = new MediaRecorder();
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    @Override
    public void onStart(Intent intent, int startId) {
        boolean isStart = intent.getBooleanExtra("Start", true);
        String staffid = intent.getStringExtra("staffid");
        if (!isStart) {
            stopSelf();
            Intent t = new Intent(BROADCAST_RECORDER);
            t.putExtra("staffid",staffid);
            sendBroadcast(t);
        } else {
            try {
                if (recorder == null)
                    recorder = new MediaRecorder();
                recorder.reset();
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                recorder.setAudioChannels(1);
                recorder.setOutputFile(AppUtil.GET_STORAGE_AUDIO());

                recorder.setOnInfoListener(this);
                recorder.setOnErrorListener(this);
                try {
                    recorder.prepare();
                } catch (java.io.IOException e) {
                    recorder = null;
                    return;
                }

                recorder.start();
                isRecording = true;

            } catch (Exception e) {
                recorder = null;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != recorder) {
            AppUtil.Log_WalkieTalkie("Stop service recorder!!");
            isRecording = false;
            recorder.stop();
            recorder.release();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }

    @Override
    public void onRebind(Intent intent) {
    }

    // MediaRecorder.OnInfoListener
    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        isRecording = false;
    }

    // MediaRecorder.OnErrorListener
    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        isRecording = false;
        mr.release();
    }
}
