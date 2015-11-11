package br.com.dbcorp.melhoreministerio.preferencias;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david.barros on 11/11/2015.
 */
public class NumberPreference extends DialogPreference {
    private NumberPicker picker;
    private int minutes;
    private final int DEFAULT_VALUE = 5;

    public NumberPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NumberPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View onCreateDialogView() {
        picker = new NumberPicker(this.getContext());

        picker.setMinValue(1);
        picker.setMaxValue(60);

        List<String> displayedValues = new ArrayList<>();

        for (int i = 1; i <= 60; i++) {
            displayedValues.add(String.format("%02d", i));
        }

        picker.setDisplayedValues(displayedValues.toArray(new String[0]));

        return picker;
    }

    public void updateSummary() {
        String time = this.leftZero(String.valueOf(this.minutes));
        setSummary(time);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        picker.setValue(this.minutes);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        Integer time = null;

        if (restorePersistedValue) {
            if (defaultValue == null) {
                time = getPersistedInt(DEFAULT_VALUE);
            }
            else {
                time = getPersistedInt(DEFAULT_VALUE);
            }
        }
        else {
            time = Integer.parseInt((String) defaultValue);
        }

        // need to persist here for default value to work
        setTime(time);
        updateSummary();
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            this.minutes = picker.getValue();

            if (!callChangeListener(this.minutes)) {
                return;
            }

            // persist
            setTime(this.minutes);
            updateSummary();
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    private void setTime(String time) {
        this.setTime(Integer.parseInt(time));
    }

    private void setTime(int time) {
        this.minutes = time;
        persistInt(this.minutes);
        notifyDependencyChange(shouldDisableDependents());
        notifyChanged();
    }

    private static String leftZero(String inTime) {
        while (inTime.length() < 2) {
            inTime = "0" + inTime;
        }

        return inTime;
    }
}
