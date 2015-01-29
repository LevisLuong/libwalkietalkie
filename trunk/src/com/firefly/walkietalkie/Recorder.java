/*
 * This code is part of the project "Audio Analyzer for the Android"
 * developed for the course CSE 599Y
 * "Mobile and Cloud Applications for Emerging Regions" 
 * at the University of Washington Computer Science & Engineering
 * 
 * The goal of this project is to create an audio analyzer that
 * allows the user to record, play and analyze audio files.
 * The program plot the waveform of the recording, the spectrogram,
 * and plot several audio descriptors.
 * 
 * At the current state the audio descriptors are:
 * 	- Spectral Centroid
 * 	- Spectral Centroid Variation
 * 	- Energy
 * 	- Energy Variation
 * 	- Zero Crossing
 * 	- Zero Crossing Variation
 * 
 * In addition to this temporal descriptors the total average of them
 * is presented in numeral format with the duration of the recording, and
 * the number of samples.
 * 
 * Otherwise noticed, the code was created by Hugo Solis
 * hugosg@uw.edu, feel free to contact me if you have any questions.
 * Dec 16, 2009
 * hugosg
 */
package com.firefly.walkietalkie;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;

import java.io.*;
import java.util.ArrayList;

/**
 * @author hugosg
 *         <p/>
 *         This code is an extension and variation of the code in
 *         http://emeadev.blogspot.com/2009/09/raw-audio-manipulation-in-android.html
 */
public class Recorder extends AsyncTask<Void, Void, Boolean> {
    private int RECORDER_CHANNELS = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private int RECORDER_SAMPLERATE = 44100;

    public boolean isRecording = false;

    private AudioRecord audioRecord = null;
    private DataOutputStream dos = null;

    private RecorderListener rl;

    interface RecorderListener {
        public void onConpleted(boolean status);
    }

    /**
     * @param rl As the Player, we assume there will be only one listener
     */
    public void addRecorderListener(RecorderListener rl) {
        this.rl = rl;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        File file = new File(AppUtil.GET_STORAGE_RECORD());

        // Delete any previous recording.
        if (file.exists())
            file.delete();


        // Create the new file.
        try {
            file.createNewFile();
            Log.i("HUGO", "file created fine");
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create " + file.toString());
        }

        try {
            // Create a DataOuputStream to write the audio data into the saved file.
            OutputStream os = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(os);
            dos = new DataOutputStream(bos);

            // Create a new AudioRecord object to record the audio.
            // Get the minimum buffer size required for the successful creation of an AudioRecord object.
            int bufferSizeInBytes = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                    RECORDER_AUDIO_ENCODING);
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, RECORDER_SAMPLERATE,
                    RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, bufferSizeInBytes);

            short[] buffer = new short[bufferSizeInBytes];
            Log.i("HUGO", "The audio record created fine ready to record");

            audioRecord.startRecording();
            isRecording = true;

            Log.i("HUGO", "Start recording fine");

            while (isRecording) {
                int bufferReadResult = audioRecord.read(buffer, 0, bufferSizeInBytes);
                float maxOfBuffer = 0;
                for (int i = 0; i < bufferReadResult; i++) {
                    dos.writeShort(buffer[i]);

                    //This two lines are for extracting the Mayor value in the current buffer
                    float currentValue = (float) (Math.abs(buffer[i] * 1.0 / (Short.MAX_VALUE + 1)));
                    if (currentValue > maxOfBuffer) maxOfBuffer = currentValue;
                }
                //These three lines are for sending (publishing) the value inside an array which is passed to the listener
                //so we can track the max value
                ArrayList<Float> maxData = new ArrayList<Float>();
                maxData.add(maxOfBuffer);
            }
            audioRecord.stop();
            audioRecord.release();
            try {
                dos.flush();
                dos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Throwable t) {
            Log.e("HUGO", "Recording Failed");
            t.printStackTrace();
            audioRecord.release();
            try {
                dos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean status) {
        super.onPostExecute(status);
        rl.onConpleted(status);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        isRecording = false;
        Log.i("HUGO", "Out of recording");
        audioRecord.stop();
        audioRecord.release();
        try {
            dos.flush();
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This stop the recording, this is called from the main activity
     */
    public void stopRecording() {
        isRecording = false;
        Log.i("HUGO", "Out of recording");
    }
}