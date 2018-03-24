package com.example.poorvi_hegde.upgradedsoundwaves;

/**
 * Created by Poorvi_Hegde on 14-02-2018.
 */

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * See: http://stackoverflow.com/questions/5774104/android-audio-fft-to-retrieve-specific-frequency-magnitude-using-audiorecord
 * @author dwatling
 */
public class AudioMonitor implements Runnable {
    private static final String TAG = AudioMonitor.class.getSimpleName();
    private int mBufferSize;
    private short[] buffer;

    public boolean done = false;
    private AudioRecord mAudio;
    private OnUpdateListener mListener;

    private static final int REQUIRED_FREQUENCY = 44100;
    private static final int REQUIRED_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private static final int REQUIRED_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    public static final float PCM_MAXIMUM_VALUE = 32768.0f;			// 16-bit signed = 32768

    public AudioMonitor(OnUpdateListener listener) {
        this.mListener = listener;
        this.initialize();
    }

    private void initialize() {
        mBufferSize = AudioRecord.getMinBufferSize(REQUIRED_FREQUENCY, REQUIRED_CHANNEL, REQUIRED_FORMAT);
        Log.d(TAG, "Recommended bufferSize: " + mBufferSize);
        if (mBufferSize > 0) {
            buffer = new short[mBufferSize];
            mAudio = new AudioRecord(MediaRecorder.AudioSource.MIC, REQUIRED_FREQUENCY, REQUIRED_CHANNEL, REQUIRED_FORMAT, mBufferSize);
            if (mAudio.getState() == AudioRecord.STATE_UNINITIALIZED) {
                Log.d(TAG, "Unable to initialize AudioRecord. Ensure nothing else is using the microphone.");
                this.mListener.quit(OnUpdateListener.ERROR_CODE_MICROPHONE_LOCKED);
                mAudio = null;
            }
        }
    }

    public boolean isInitialized() {
        return this.mAudio != null;
    }

    @Override
    public void run() {
        Log.d(TAG, "run");
        if (mAudio != null) {
            mAudio.startRecording();
            while (!done) {
                int count = mAudio.read(buffer, 0, mBufferSize);
                if (count > 0) {
                    if (mListener != null) {
                        float sampleLength = 1.0f / ((float)REQUIRED_FREQUENCY / (float)count);
                        mListener.update(buffer, count, sampleLength);
                    }
                } else {
                    done = true;
                }
            }

            mAudio.stop();
            mAudio.release();
            mAudio = null;
        } else {
            Log.d(TAG, "mAudio was null!");
        }
    }
}
