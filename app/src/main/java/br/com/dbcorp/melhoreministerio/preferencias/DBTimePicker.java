package br.com.dbcorp.melhoreministerio.preferencias;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by david.barros on 04/11/2015.
 */
public class DBTimePicker extends TimePicker {

    private static final int TIME_PICKER_INTERVAL = 1;

    public DBTimePicker(Context context) {
        super(context);
        this.setIs24HourView(true);
    }

    @SuppressWarnings("deprecation")
    public int getMinute() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return super.getHour();

        } else {
            return super.getCurrentHour();
        }
    }

    @SuppressWarnings("deprecation")
    public int getSecond() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return super.getMinute();

        } else {
            return super.getCurrentMinute();
        }
    }

    @SuppressWarnings("deprecation")
    public void setMinute(int minute) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.setHour(minute);

        } else {
            this.setCurrentHour(minute);
        }
    }

    @SuppressWarnings("deprecation")
    public void setSeconds(int second) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            super.setMinute(second);

        } else {
            super.setCurrentMinute(second);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        try {
            Class<?> classForid = Class.forName("com.android.internal.R$id");
            Field fieldM = classForid.getField("minute");
            Field fieldH = classForid.getField("hour");

            NumberPicker minuteSpinner = (NumberPicker) findViewById(fieldM.getInt(null));
            minuteSpinner.setMinValue(0);
            minuteSpinner.setMaxValue((60 / TIME_PICKER_INTERVAL) - 1);

            NumberPicker hourSpinner = (NumberPicker) findViewById(fieldH.getInt(null));
            hourSpinner.setMinValue(0);
            hourSpinner.setMaxValue((60 / TIME_PICKER_INTERVAL) - 1);

            List<String> displayedValues = new ArrayList<String>();

            for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL) {
                displayedValues.add(String.format("%02d", i));
            }

            minuteSpinner.setDisplayedValues(displayedValues.toArray(new String[0]));
            hourSpinner.setDisplayedValues(displayedValues.toArray(new String[0]));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}