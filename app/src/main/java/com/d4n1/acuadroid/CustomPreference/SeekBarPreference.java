package com.d4n1.acuadroid.CustomPreference;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.d4n1.acuadroid.R;

/**
 * Created by dmartinm on 22/12/2015.
 */
public class SeekBarPreference  extends Preference {
    /*
    Progreso por defecto
     */
    private static final int DEFAULT_PROGRESS = 75;

    /*
    Progreso actual
     */
    private int currentProgress;

    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        currentProgress = DEFAULT_PROGRESS;
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        super.onCreateView(parent);
        LayoutInflater inflater = (LayoutInflater)
                getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.seekbarpreference, parent, false);
    }

    @Override
    protected void onBindView(@NonNull View view) {
        super.onBindView(view);
        // Setear el progreso actual a la seekbar
        SeekBar seekBar = (SeekBar)view.findViewById(R.id.selector);
        seekBar.setProgress(currentProgress);
        final TextView txSelectorValue = (TextView)view.findViewById(R.id.txSelectorValue);
        txSelectorValue.setText(String.valueOf(currentProgress));

        // Setear la escucha al seekbar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentProgress = progress;
                persistInt(currentProgress);
                txSelectorValue.setText(String.valueOf(currentProgress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // No es necesario
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // No es necesario
            }
        });
    }
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, DEFAULT_PROGRESS);
    }
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            // Obtener el progreso actual
            currentProgress = this.getPersistedInt(DEFAULT_PROGRESS);
        } else {
            // Reiniciar al valor por defecto
            currentProgress = (Integer) defaultValue;
            persistInt(currentProgress);
        }
    }
}
