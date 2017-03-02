package com.example.shami.sunshine.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Shami on 2/26/2017.
 */

public class LocationEditTextPreference extends EditTextPreference {
    static final private int DEFAULT_MININIUM_LOCATION_LENGTH=2;
    private int mMinLength;

    public LocationEditTextPreference(Context context, AttributeSet attributeSet)
    {
        super(context,attributeSet);

            TypedArray array=context.getTheme().obtainStyledAttributes(attributeSet,R.styleable.LocationEditTextPreference,0,0);

        try{
            mMinLength=array.getInteger(R.styleable.LocationEditTextPreference_minLength,DEFAULT_MININIUM_LOCATION_LENGTH);
        }
        finally {
            array.recycle();
        }


    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);
        EditText et=getEditText();
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Dialog d=getDialog();
                if(d instanceof AlertDialog)
                {
                    AlertDialog dialog=(AlertDialog)d;
                    Button positiveButton=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    if(s.length()<mMinLength)
                    {
                        positiveButton.setEnabled(false);
                    }
                    else
                    {

                        positiveButton.setEnabled(true);
                    }
                }
            }
        });

    }
}
