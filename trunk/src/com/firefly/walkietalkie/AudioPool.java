package com.firefly.walkietalkie;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class AudioPool {

    static String TAG = "AudioPool";

    MediaPlayer mPlayer;

    int mAudioCounter;

    String mCurrentPlay;

    HashMap<Integer, String> mAudioMap;

    LinkedList<String> mAudioQueue;
    Context mContext;

    public AudioPool(Context context) {
        mContext = context;
        mAudioMap = new HashMap<Integer, String>();
        mAudioQueue = new LinkedList<String>();
        mAudioCounter = 0;

    }

    public int addAudio(String urlSound) {
        Log.d(TAG, "adding audio " + urlSound + " to the pool");

        if (mAudioMap.containsValue(urlSound)) {
            return getAudioKey(urlSound);
        }
        mAudioCounter++;
        mAudioMap.put(mAudioCounter, urlSound);
        return mAudioCounter;
    }

    public boolean playAudio(int keyAudioCounter) {

        if (mAudioMap.containsKey(keyAudioCounter) == false) {
            return false;
        }

        if (mPlayer == null) {
            setupPlayer();
        }

        if (mPlayer.isPlaying() == false) {
            return prepareAndPlayAudioNow(mAudioMap.get(keyAudioCounter));
        } else {
            Log.d(TAG, "adding audio " + mAudioMap.get(keyAudioCounter) + " to the audio queue");
            mAudioQueue.add(mAudioMap.get(keyAudioCounter));
        }
        return true;
    }

    public Integer[] getAudioIds() {
        return (Integer[]) mAudioMap.keySet().toArray(
                new Integer[mAudioMap.keySet().size()]);
    }

    public void releaseAudioPlayer() {
        AppUtil.cleanStorageAudio();
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }


    private boolean prepareAndPlayAudioNow(String urlSound) {
        mCurrentPlay = urlSound;
        try {
            Log.d(TAG, "playing audio " + urlSound + " now");
            mPlayer = new MediaPlayer();
            mPlayer.setDataSource(urlSound);
            mPlayer.prepare();
            mPlayer.start();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d(TAG, "Media play completed");
                    audioDone();
                }
            });
            return true;
        } catch (Exception e) {
            Log.d(TAG, "problems playing audio " + e.getMessage());
            return false;
        }
    }

    private boolean playAudioAgainNow() {
        try {
            mPlayer.seekTo(0);
            mPlayer.start();
            return true;
        } catch (Exception e) {
            Log.d(TAG, "problems playing audio");
            return false;
        }
    }

    private void setupPlayer() {
        mPlayer = new MediaPlayer();
    }

    private void audioDone() {
        if (mAudioQueue.size() > 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(TAG, mAudioQueue.size() + " audios in queue");
            String nextId = mAudioQueue.removeFirst();
            if (mCurrentPlay == nextId) {
                playAudioAgainNow();
            } else {
                prepareAndPlayAudioNow(nextId);
            }

        } else {
            releaseAudioPlayer();
        }
    }

    private int getAudioKey(String urlSound) {
        for (Map.Entry<Integer, String> map : mAudioMap.entrySet()) {
            if (map.getValue().equals(urlSound)) {
                return map.getKey();
            }
        }
        return -1;
    }

}