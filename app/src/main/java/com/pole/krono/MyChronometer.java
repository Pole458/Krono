package com.pole.krono;

/*
 * The Android chronometer widget revised so as to count milliseconds
 * https://github.com/antoniom/Millisecond-Chronometer
 */

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;

public class MyChronometer extends android.support.v7.widget.AppCompatTextView {

    private long mBase;
    private boolean mVisible;
    private boolean mStarted;
    private boolean mRunning;

    private static final int TICK_WHAT = 2;

    private long timeElapsed;
    private long lastLap;
    private int laps;

    public MyChronometer(Context context) {
        this (context, null, 0);
    }

    public MyChronometer(Context context, AttributeSet attrs) {
        this (context, attrs, 0);
    }

    public MyChronometer(Context context, AttributeSet attrs, int defStyle) {
        super (context, attrs, defStyle);

        init();
    }

    private void init() {
        mBase = SystemClock.elapsedRealtime();
        updateText(mBase);
    }

    private void setBase(long base) {
        mBase = base;
        updateText(SystemClock.elapsedRealtime());
    }

    private void start() {
        mStarted = true;
        updateRunning();
    }

    public void restart() {
        setBase(SystemClock.elapsedRealtime());
        lastLap = 0;
        laps = 0;
        start();
    }

    public void reset() {
        setText(R.string.zero_time);
    }

    public void stop() {
        mStarted = false;
        updateRunning();
    }

    public void pause() {
        mStarted = false;
        updateRunning();
    }

    public void resume() {
        setBase(SystemClock.elapsedRealtime() - timeElapsed);
        start();
    }

    public long lap() {
        long lapTime = timeElapsed - lastLap;
        lastLap = timeElapsed;
        laps++;
        return lapTime;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mVisible = false;
        updateRunning();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        mVisible = visibility == VISIBLE;
        updateRunning();
    }

    private synchronized void updateText(long now) {

        timeElapsed = now - mBase;
        setText(getTimeString(timeElapsed));

    }

    private void updateRunning() {
        boolean running = mVisible && mStarted;
        if (running != mRunning) {
            if (running) {
                updateText(SystemClock.elapsedRealtime());
                mHandler.sendMessageDelayed(Message.obtain(mHandler, TICK_WHAT), 10);
            } else {
                mHandler.removeMessages(TICK_WHAT);
            }
            mRunning = running;
        }
    }

    private Handler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {

        WeakReference<MyChronometer> chrono;

        MyHandler(MyChronometer chronometer){
            chrono = new WeakReference<>(chronometer);
        }

        @Override
        public void handleMessage(Message msg) {
            if(chrono != null && chrono.get().mRunning) {
                chrono.get().updateText(SystemClock.elapsedRealtime());
                sendMessageDelayed(Message.obtain(this , TICK_WHAT), 10);
            }
        }
    }


    public int getLaps() {
        return laps;
    }

    private static DecimalFormat df = new DecimalFormat("00");

    public static String getTimeString(long time) {

        int hours = (int)(time / (3600000));
        int remaining = (int)(time % (3600000));

        int minutes = remaining / (60000);
        remaining = remaining % (60000);

        int seconds = remaining / 1000;

        int milliseconds = ((int)time % 1000) / 10;

        String text = "";

        if (hours > 0) {
            text += df.format(hours) + ":";
        }

        text += df.format(minutes) + ":";
        text += df.format(seconds) + ",";
        text += df.format(milliseconds);

        return text;
    }

    public static String getGapString(long gap) {

        long time  = Math.abs(gap);
        String text = "";
        long remaining = time;

        if (time > 3600000) {
            int hours = (int)(time / (3600000));
            remaining = (int)(time % (3600000));
            text += df.format(hours) + ":";
        }

        if (time > 60000) {
            int minutes = (int) (remaining / (60000));
            remaining = remaining % (60000);
            text += df.format(minutes) + ":";
        }

        int seconds = (int) (remaining / 1000);
        text += df.format(seconds) + ",";

        int milliseconds = ((int)time % 1000) / 10;
        text += df.format(milliseconds);

        return gap > 0 ? "+" + text : "-" + text;
    }
}
