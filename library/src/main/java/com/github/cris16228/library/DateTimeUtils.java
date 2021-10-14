package com.github.cris16228.library;

import com.google.android.material.timepicker.MaterialTimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtils {

    private final String DEFAULT_FORMAT_DATE = "dd/MM/yyyy";
    private final String DEFAULT_FORMAT_TIME = "hh:mm";
    private String DEFAULT_FORMAT = "dd/MM/yyyy hh:mm";
    private long date;
    private long time;

    public String getDefaultFormat() {
        return DEFAULT_FORMAT;
    }

    public void setDefaultFormat(String defaultFormat) {
        this.DEFAULT_FORMAT = defaultFormat;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    private String timePicker(MaterialTimePicker timePicker) {
        return timePicker.getHour() + ":" + (timePicker.getMinute() == 0 ? "00" : timePicker.getMinute());
    }

    public String getDateFormat(MaterialTimePicker timePicker) {
        StringBuilder dateTime = new StringBuilder();
        dateTime.append(new SimpleDateFormat(DEFAULT_FORMAT_DATE, Locale.getDefault()).format(new Date(getDate())));
        dateTime.append(timePicker(timePicker));
        return dateTime.toString();
    }

    public long getMilliseconds(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(DEFAULT_FORMAT_DATE, Locale.getDefault());
        Date temp_date = format.parse(date);
        return temp_date != null ? temp_date.getTime() : 0;
    }
}
