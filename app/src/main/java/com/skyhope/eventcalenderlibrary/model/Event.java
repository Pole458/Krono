package com.skyhope.eventcalenderlibrary.model;

/*
 *  ****************************************************************************
 *  * Created by : Md Tariqul Islam on 11/30/2018 at 11:40 AM.
 *  * Email : tariqul@w3engineers.com
 *  *
 *  * Purpose:
 *  *
 *  * Last edited by : Md Tariqul Islam on 11/30/2018.
 *  *
 *  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
 *  ****************************************************************************
 */

import java.util.Calendar;

public class Event {
    private long time;
    private String eventText;

    public Event(long time, String eventText) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(time);
        date.set(Calendar.MILLISECOND, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.HOUR_OF_DAY, 0);
        this.time = date.getTimeInMillis();

        this.eventText = eventText;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getEventText() {
        return eventText;
    }

    public void setEventText(String eventText) {
        this.eventText = eventText;
    }
}
