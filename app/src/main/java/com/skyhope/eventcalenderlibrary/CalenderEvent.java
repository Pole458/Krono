package com.skyhope.eventcalenderlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.pole.krono.R;
import com.skyhope.eventcalenderlibrary.listener.OnCalenderDayClickListener;
import com.skyhope.eventcalenderlibrary.model.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
/*
 *  ****************************************************************************
 *  * Created by : Md Tariqul Islam on 11/29/2018 at 3:03 PM.
 *  * Email : tariqul@w3engineers.com
 *  *
 *  * Purpose:
 *  *
 *  * Last edited by : Md Tariqul Islam on 11/29/2018.
 *  *
 *  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
 *  ****************************************************************************
 */


public class CalenderEvent extends LinearLayout {

    private static final String TAG = "Pole: CalenderEvent";

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
    private static SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    private LinearLayout[] weeks;
    private Day[] days;

    private Button textViewMonthName;
    private Button buttonNext;
    private Button buttonPrevious;

    private Calendar mCalendar;

    private static final String CUSTOM_GREY = "#a0a0a0";

    private Context mContext;

    private List<Event> events = new ArrayList<>();

    private long mToday;

    // selected day
    private Day mSelectedDay;

    private OnCalenderDayClickListener mCalenderDayClickListener;

    private int mSelectedDayTextColor;
    private int mCurrentMonthDayColor;
    private int mOffMonthDayColor;
    private int mMonthTextColor;
    private int mMonthButtonColor;
    private int mWeekNameColor;

    private Drawable nextButtonDrawable;
    private Drawable prevButtonDrawable;


    public CalenderEvent(Context context) {
        super(context);
    }

    public CalenderEvent(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initAttrs(attrs);

        initView(context);
    }

    private View weeksLayout;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        LinearLayout weekOneLayout = findViewById(R.id.calendar_week_1);
        LinearLayout weekTwoLayout = findViewById(R.id.calendar_week_2);
        LinearLayout weekThreeLayout = findViewById(R.id.calendar_week_3);
        LinearLayout weekFourLayout = findViewById(R.id.calendar_week_4);
        LinearLayout weekFiveLayout = findViewById(R.id.calendar_week_5);
        LinearLayout weekSixLayout = findViewById(R.id.calendar_week_6);

        weeksLayout = findViewById(R.id.weeks_layout);

        buttonNext = findViewById(R.id.button_next);
        buttonPrevious = findViewById(R.id.button_previous);

        textViewMonthName = findViewById(R.id.text_view_month_name);

        textViewMonthName.setOnClickListener(v -> {
            if(weeksLayout.getVisibility() == View.VISIBLE) {
                closeCalendar();
            } else {
               openCalendar();
            }
        });

        // week container
        LinearLayout linearLayoutWeak = findViewById(R.id.linear_layout_week);

        weeks = new LinearLayout[6];
        days = new Day[6 * 7];

        weeks[0] = weekOneLayout;
        weeks[1] = weekTwoLayout;
        weeks[2] = weekThreeLayout;
        weeks[3] = weekFourLayout;
        weeks[4] = weekFiveLayout;
        weeks[5] = weekSixLayout;

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        mCalendar = Calendar.getInstance();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        mToday = cal.getTimeInMillis();

        Log.v(TAG, "Today, " + cal.getTime());

        initDaysInCalender(getDaysLayoutParams(), mContext, metrics);

        buttonPrevious.setOnClickListener(v -> gotoPreviousMonth());
        buttonNext.setOnClickListener(v -> gotoNextMonth());

        textViewMonthName.setTextColor(mMonthTextColor);
        DrawableCompat.setTint(DrawableCompat.wrap(textViewMonthName.getBackground()), mMonthButtonColor);

        for (int i = 0; i < linearLayoutWeak.getChildCount(); i++) {
            TextView textViewWeek = (TextView) linearLayoutWeak.getChildAt(i);
            textViewWeek.setTextColor(mWeekNameColor);
        }

        if (nextButtonDrawable != null)
            buttonNext.setBackground(nextButtonDrawable);

        if (prevButtonDrawable != null)
            buttonPrevious.setBackground(prevButtonDrawable);

        DrawableCompat.setTint(DrawableCompat.wrap(buttonPrevious.getBackground()), mMonthButtonColor);
        DrawableCompat.setTint(DrawableCompat.wrap(buttonNext.getBackground()), mMonthButtonColor);

    }

    private void updateCalendar(int selectedYear, int selectedMonth) {

        mCalendar.set(selectedYear, selectedMonth, 1);

        Calendar day = Calendar.getInstance();
        day.setTimeInMillis(mCalendar.getTimeInMillis());
        day.set(Calendar.MILLISECOND, 0);
        day.set(Calendar.SECOND, 0);
        day.set(Calendar.MINUTE, 0);
        day.set(Calendar.HOUR_OF_DAY, 0);

        textViewMonthName.setText(monthFormat.format(day.getTimeInMillis()));

        int daysInCurrentMonth = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        int index = 0;

        int firstDayOfCurrentMonth = mCalendar.get(Calendar.DAY_OF_WEEK);

        int previousLeftMonthDays = firstDayOfCurrentMonth - 1;

        int nextMonthDays = 42 - (daysInCurrentMonth + previousLeftMonthDays);

        // Past month

        if (previousLeftMonthDays != 0) {
            int prevMonth;
            int prevYear;
            if (selectedMonth > 0) {
                mCalendar.set(selectedYear, selectedMonth - 1, 1);
                prevMonth = selectedMonth - 1;
                prevYear = selectedYear;
            } else {
                mCalendar.set(selectedYear - 1, 11, 1);
                prevMonth = 11;
                prevYear = selectedYear - 1;
            }

            int previousMonthTotalDays = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            mCalendar.set(prevYear, prevMonth, (previousMonthTotalDays - previousLeftMonthDays));
            int prevCurrentDay = mCalendar.get(Calendar.DATE);
            prevCurrentDay++;

            for (int i = 0; i < previousLeftMonthDays; i++) {

                days[index].setDayOfMonth(prevCurrentDay);
                days[index].setType(OFF);
                days[index].setOnClickListener(null);

                prevCurrentDay++;
                index++;
            }
        }

        // This month
        mCalendar.set(selectedYear, selectedMonth, 1);

        for (int i = 1; i <= daysInCurrentMonth; i++) {

            days[index].setDayOfMonth(i);
            days[index].setTextColor(mCurrentMonthDayColor);

            day.set(Calendar.DAY_OF_MONTH, i);

            if (mToday == day.getTimeInMillis()) {
                Log.v(TAG, "found today at time: " + day.getTime());
                days[index].setType(TODAY);
                setSelectedDay(index, day.getTimeInMillis());
            } else {
                Event e = getEvent(day.getTimeInMillis());
                if(e != null) {
                    days[index].setType(EVENT);
                    Log.v(TAG, "found event at time: " + day.getTime());
                } else {
                    days[index].setType(NORMAL_DAY);
                }
            }

            final int finalIndex = index;
            final long finalTime = day.getTimeInMillis();
            days[index].setOnClickListener(view -> {

                Log.v(TAG, "Selected day: " + dateFormat.format(finalTime) + ", " + finalTime);

                setSelectedDay(finalIndex, finalTime);

                closeCalendar();

                if (mCalenderDayClickListener != null) {
                    mCalenderDayClickListener.onGetDay(finalTime);
                }
            });

            index++;
        }

        // now set rest of day
        for (int i = 1; i <= nextMonthDays; i++) {
            days[index].setDayOfMonth(i);
            days[index].setType(OFF);
            days[index].setOnClickListener(null);

            index++;
        }
    }

    private void gotoNextMonth() {
        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);

        if (month < 11) {
            mCalendar.set(year, month + 1, 1);
        } else {
            mCalendar.set(year + 1, 0, 1);
        }

        updateCalendar(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH));
    }

    private void gotoPreviousMonth() {
        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);

        if (month > 0) {
            mCalendar.set(year, month - 1, 1);
        } else {
            mCalendar.set(year - 1, 11, 1);
        }

        updateCalendar(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH));
    }

    private static final int OFF = 4;
    private static final int SELECTED = 3;
    private static final int TODAY = 2;
    private static final int EVENT = 1;
    private static final int NORMAL_DAY = 0;

    private class Day {

        int type;
        private final LinearLayout linearLayout;
        private final TextView textView;
        private long time;

        Day(Context context, LayoutParams buttonParams, DisplayMetrics metrics, int type) {
            this.type = type;

            linearLayout = new LinearLayout(context) {
                @Override
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    //noinspection SuspiciousNameCombination
                    super.onMeasure(widthMeasureSpec, widthMeasureSpec);
                }
            };
            linearLayout.setLayoutParams(buttonParams);
            linearLayout.setOrientation(VERTICAL);
            linearLayout.setGravity(Gravity.CENTER);

            textView = new TextView(context);
            textView.setTextColor(Color.parseColor(CUSTOM_GREY));
            textView.setBackgroundColor(Color.TRANSPARENT);
            textView.setLayoutParams(new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setTextSize((int) metrics.density * 5);
            textView.setSingleLine();
            textView.setPadding(5, 5, 5, 5);
            textView.setGravity(Gravity.CENTER);

            linearLayout.addView(textView);

        }

        void setDayOfMonth(int day) {
            textView.setText(String.valueOf(day));
        }

        void setTextColor(int color) {
            textView.setTextColor(color);
        }

        void setOnClickListener(OnClickListener listener) {
            linearLayout.setOnClickListener(listener);
        }

        void setType(int type) {
            this.type = type;
            backToDefault();
        }

        void backToDefault() {
           changeType(type);
        }

        void changeType(int type) {
            switch (type) {
                case NORMAL_DAY:
                    linearLayout.setBackground(null);
                    textView.setTextColor(mCurrentMonthDayColor);
                    textView.setTextSize((int) getResources().getDisplayMetrics().density * 5);
                    break;
                case TODAY:
                    textView.setTextSize((int) getResources().getDisplayMetrics().density * 8);
                case EVENT:
                    textView.setTextSize((int) getResources().getDisplayMetrics().density * 5);
                    linearLayout.setBackgroundResource(R.drawable.drawable_event);
                    textView.setTextColor(mSelectedDayTextColor);
                    break;
                case SELECTED:
                    textView.setTextSize((int) getResources().getDisplayMetrics().density * 5);
                    linearLayout.setBackgroundResource(R.drawable.drawable_selected);
                    textView.setTextColor(mSelectedDayTextColor);
                    break;
                case OFF:
                    textView.setTextSize((int) getResources().getDisplayMetrics().density * 5);
                    linearLayout.setBackground(null);
                    textView.setTextColor(mOffMonthDayColor);
                    break;
            }
        }

        public void setTime(long time) {
            this.time = time;
        }

        public long getTime() {
            return time;
        }
    }

    private void initDaysInCalender(LayoutParams buttonParams, Context context, DisplayMetrics metrics) {
        int engDaysArrayCounter = 0;

        for (int weekNumber = 0; weekNumber < 6; ++weekNumber) {
            for (int dayInWeek = 0; dayInWeek < 7; ++dayInWeek) {

                Day day = new Day(context, buttonParams, metrics, NORMAL_DAY);

                days[engDaysArrayCounter] = day;

                weeks[weekNumber].addView(day.linearLayout);

                engDaysArrayCounter++;
            }
        }
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CalenderEvent);

        mSelectedDayTextColor = typedArray.getColor(R.styleable.CalenderEvent_selected_day_text_color, Color.WHITE);

        mCurrentMonthDayColor = typedArray.getColor(R.styleable.CalenderEvent_current_month_day_color, Color.BLACK);

        mOffMonthDayColor = typedArray.getColor(R.styleable.CalenderEvent_off_month_day_color, Color.parseColor(CUSTOM_GREY));

        final int defaultTextColor = Color.parseColor("#808080");

        mMonthTextColor = typedArray.getColor(R.styleable.CalenderEvent_month_text_color, Color.WHITE);

        mMonthButtonColor = typedArray.getColor(R.styleable.CalenderEvent_month_color, defaultTextColor);

        mWeekNameColor = typedArray.getColor(R.styleable.CalenderEvent_week_name_color, defaultTextColor);

        nextButtonDrawable = typedArray.getDrawable(R.styleable.CalenderEvent_next_icon);

        prevButtonDrawable = typedArray.getDrawable(R.styleable.CalenderEvent_previous_icon);

        typedArray.recycle();
    }

    private void initView(Context context) {
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            inflater.inflate(R.layout.main_calendar, this);
        }
    }

    private LayoutParams getDaysLayoutParams() {
        LayoutParams buttonParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        buttonParams.weight = 1;
        return buttonParams;
    }

    private Event getEvent(long time) {
        for(Event e : events) {
            if(e.getTime() == time)
                return e;
        }
        return null;
    }

    private void setSelectedDay(int index, long time) {

        // Revert old selected view
        if (mSelectedDay != null) {
            mSelectedDay.backToDefault();
        } else {
            textViewMonthName.setText(dateFormat.format(time));
        }
        mSelectedDay = days[index];
        mSelectedDay.changeType(SELECTED);
        mSelectedDay.setTime(time);

//        textViewMonthName.setText(dateFormat.format(time));
    }

    private void closeCalendar() {
        weeksLayout.setVisibility(GONE);
        buttonPrevious.setVisibility(INVISIBLE);
        buttonNext.setVisibility(INVISIBLE);

        if(mSelectedDay != null)
            textViewMonthName.setText(dateFormat.format(mSelectedDay.getTime()));
    }

    private void openCalendar() {
        weeksLayout.setVisibility(VISIBLE);
        buttonPrevious.setVisibility(VISIBLE);
        buttonNext.setVisibility(VISIBLE);

        textViewMonthName.setText(monthFormat.format(mCalendar.getTimeInMillis()));
    }

    /*
     * ******************* User method ***************
     * */

    public void setEvents(List<Event> events) {

        this.events = events;

        updateCalendar(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH));

        Log.v(TAG, "set new events to calendar: " + events.size());
    }

    public void setOnCalendarDayClickListener(OnCalenderDayClickListener listener) {
        this.mCalenderDayClickListener = listener;
    }
}
