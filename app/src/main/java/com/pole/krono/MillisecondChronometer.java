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
import android.util.Log;

import java.text.DecimalFormat;

public class MillisecondChronometer extends android.support.v7.widget.AppCompatTextView {

    @SuppressWarnings("unused")
    private static final String TAG = "MillisecondChronometer";

    public interface OnChronometerTickListener {

        void onChronometerTick(MillisecondChronometer chronometer);
    }

    private long mBase;
    private boolean mVisible;
    private boolean mStarted;
    private boolean mRunning;
    private OnChronometerTickListener mOnChronometerTickListener;

    private static final int TICK_WHAT = 2;

    private long timeElapsed;
    private long lastLap;
    private int laps;

    public MillisecondChronometer(Context context) {
        this (context, null, 0);
    }

    public MillisecondChronometer(Context context, AttributeSet attrs) {
        this (context, attrs, 0);
    }

    public MillisecondChronometer(Context context, AttributeSet attrs, int defStyle) {
        super (context, attrs, defStyle);

        init();
    }

    private void init() {
        mBase = SystemClock.elapsedRealtime();
        updateText(mBase);
    }

    public void setBase(long base) {
        mBase = base;
        dispatchChronometerTick();
        updateText(SystemClock.elapsedRealtime());
    }

    public long getBase() {
        return mBase;
    }

    public void setOnChronometerTickListener(OnChronometerTickListener listener) {
        mOnChronometerTickListener = listener;
    }

    public OnChronometerTickListener getOnChronometerTickListener() {
        return mOnChronometerTickListener;
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

    public void stop() {
        mStarted = false;
        updateRunning();
        setText("00:00:00");
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

    public void setStarted(boolean started) {
        mStarted = started;
        updateRunning();
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
                dispatchChronometerTick();
                mHandler.sendMessageDelayed(Message.obtain(mHandler, TICK_WHAT), 10);
            } else {
                mHandler.removeMessages(TICK_WHAT);
            }
            mRunning = running;
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message m) {
            if (mRunning) {
                updateText(SystemClock.elapsedRealtime());
                dispatchChronometerTick();
                sendMessageDelayed(Message.obtain(this , TICK_WHAT), 10);
            }
        }
    };

    private void dispatchChronometerTick() {
        if (mOnChronometerTickListener != null) {
            mOnChronometerTickListener.onChronometerTick(this);
        }
    }

    public long getTimeElapsed() {
        return timeElapsed;
    }

    public int getLaps() {
        return laps;
    }


    /*
     Chronometer mChronometer = (Chronometer) findViewById(R.id.chronometer);
        mChronometer.start();

        //IF you want to stop your chrono after X seconds or minutes.
        mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
			public void onChronometerTick(Chronometer chronometer) {
				if (chronometer.getText().toString().equalsIgnoreCase("00:05:0")) { //When reaches 5 seconds.
				    //Define here what happens when the Chronometer reaches the time above.
				    chronometer.stop();
				    Toast.makeText(getBaseContext(),"Reached the end.",
						Toast.LENGTH_LONG).show();
				}
			}
		});

     */

    private static DecimalFormat df = new DecimalFormat("00");

    public static String getTimeString(long time) {

        int hours = (int)(time / (3600000));
        int remaining = (int)(time % (3600000));

        int minutes = remaining / (60000);
        remaining = remaining % (60000);

        int seconds = remaining / 1000;
//        remaining = remaining % (1000);

        int milliseconds = ((int)time % 1000) / 10;

        String text = "";

        if (hours > 0) {
            text += df.format(hours) + ":";
        }

        text += df.format(minutes) + ":";
        text += df.format(seconds) + ":";
        text += df.format(milliseconds);

        return text;
    }

}
