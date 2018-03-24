package com.example.poorvi_hegde.upgradedsoundwaves;

/**
 * Created by Poorvi_Hegde on 01-03-2018.
 */

public interface OnUpdateListener {
    int ERROR_CODE_MICROPHONE_LOCKED = 1;

    void update(short[] bytes, int length, float sampleLength);
    void quit(int errorCode);
}
