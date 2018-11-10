package ru.memoscope.android;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private Button firstButton;
    private Button secondButton;
    private Button button;
    private boolean pressedFirst;
    private Locale locale;

    public void setButtons(Button firstButton, Button secondButton, boolean pressedFirst) {
        this.firstButton = firstButton;
        this.secondButton = secondButton;
        this.pressedFirst = pressedFirst;
        this.button = pressedFirst ? firstButton : secondButton;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        String dayStr = day < 10? "0" + day : "" + day;
        month++;
        String monthStr = month < 10? "0" + month : "" + month;

        String resultDate = dayStr + "/" + monthStr + "/" + year;
        button.setText(resultDate);
        if (overlappingDates(firstButton.getText(), secondButton.getText())) {
            if (pressedFirst)
                secondButton.setText(resultDate);
            else
                firstButton.setText(resultDate);
        }
    }

    private boolean overlappingDates(CharSequence d1, CharSequence d2) {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy", locale);
        try {
            Date dateFrom = fmt.parse((String) d1);
            Date dateTo = fmt.parse((String) d2);
            return dateTo.before(dateFrom);
        } catch (ParseException e) {
            return false;
        }
    }
}
