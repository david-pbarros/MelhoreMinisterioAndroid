package br.com.dbcorp.melhoreministerio.preferencias;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by david.barros on 04/11/2015.
 */
public class DurationPreference extends DialogPreference {
    private int minutes = 0;
    private int seconds = 0;
    private DBDurationPicker picker = null;
    private final String DEFAULT_VALUE = "00:00";

    public static int getMinutes(String time) {
        String[] pieces = time.split(":");
        return Integer.parseInt(pieces[0]);
    }

    public static int getSeconds(String time) {
        String[] pieces = time.split(":");
        return Integer.parseInt(pieces[1]);
    }

    public DurationPreference(Context context) {
        super(context, null);
    }

    public DurationPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DurationPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setPositiveButtonText("OK");
        setNegativeButtonText("Cancel");
    }

    public void setTime(int minutes, int seconds) {
        this.minutes = minutes;
        this.seconds = seconds;
        String time = toTime(this.minutes, this.seconds);
        persistString(time);
        notifyDependencyChange(shouldDisableDependents());
        notifyChanged();
    }

    public String toTime(int hour, int minute) {
        return String.valueOf(hour) + ":" + String.valueOf(minute);
    }

    public void updateSummary() {
        String time = this.leftZero(String.valueOf(this.minutes)) + ":" + this.leftZero(String.valueOf(this.seconds));
        setSummary(time);
    }

    @Override
    protected View onCreateDialogView() {
        picker = new DBDurationPicker(getContext());
        return picker;
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        picker.setMinute(this.minutes);
        picker.setSeconds(this.seconds);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            int currMinute = picker.getMinute();
            int currSecond = picker.getSecond();

            if (!callChangeListener(toTime(currMinute, currSecond))) {
                return;
            }

            // persist
            setTime(currMinute, currSecond);
            updateSummary();
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        String time = null;

        if (restorePersistedValue) {
            if (defaultValue == null) {
                time = getPersistedString(DEFAULT_VALUE);
            }
            else {
                time = getPersistedString(DEFAULT_VALUE);
            }
        }
        else {
            time = defaultValue.toString();
        }

        // need to persist here for default value to work
        setTime(getMinutes(time), getSeconds(time));
        updateSummary();
    }

    public static String leftZero(String inTime) {
        while (inTime.length() < 2) {
            inTime = "0" + inTime;
        }

        return inTime;
    }
}