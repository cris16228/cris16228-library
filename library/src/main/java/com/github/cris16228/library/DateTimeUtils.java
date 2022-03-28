package com.github.cris16228.library;

import android.content.Context;
import android.text.format.DateFormat;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

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

    public String getDateTime(AppCompatActivity activity) {
        Context context = activity.getBaseContext();
        AtomicLong date_ms = new AtomicLong(0L);
        StringBuilder dateTime = new StringBuilder();
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(date_ms.get() == 0L ? MaterialDatePicker.todayInUtcMilliseconds() : date_ms.get())
                .build();
        datePicker.addOnPositiveButtonClickListener(selection -> {
            dateTime.append(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(selection)));
            dateTime.append(" ");
            date_ms.set(selection);
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTitleText("Select time")
                    .setTimeFormat(DateFormat.is24HourFormat(context) ? TimeFormat.CLOCK_24H : TimeFormat.CLOCK_12H)
                    .setHour(calendar.get(Calendar.HOUR_OF_DAY)).setMinute(calendar.get(Calendar.MINUTE)).build();
            timePicker.addOnPositiveButtonClickListener(v -> {
                dateTime.append(timePicker.getHour()).append(":").append(timePicker.getMinute() == 0 ? "00" : (timePicker.getMinute() <= 9 ?
                        "0" + timePicker.getMinute() : timePicker.getMinute()));
            });
            timePicker.show(activity.getSupportFragmentManager(), context.getClass().getSimpleName());
        });
        datePicker.show(activity.getSupportFragmentManager(), context.getClass().getSimpleName());
        return dateTime.toString();
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
