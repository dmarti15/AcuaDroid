package com.d4n1.acuadroid.dialogos;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.d4n1.acuadroid.R;

/**
 * Created by dmartinm on 09/12/2015.
 */
public class ManLuxA    extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.manluxa, null);
        final SeekBar sbA = (SeekBar) v.findViewById(R.id.seekBarA);
        builder.setView(v)
                .setPositiveButton(getResources().getString(R.string.PositiveButton),new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("AcuaDroid", "Modo Manual Azul DL: " + sbA.getProgress()+ "pow ");
                        listener.onPossitiveLuxAButtonClick(sbA.getProgress());
                    }
                })
                .setNegativeButton(getResources().getString(R.string.NegativeButton), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        final TextView TxPercent = (TextView)v.findViewById(R.id.textPercent);
        sbA.setMax(100);
        sbA.setProgress(0);
        sbA.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar sbA, int progress, boolean fromUser) {
                TxPercent.setText(String.valueOf(progress)+getString(R.string.PercentSign));
            }
        });

        return builder.create();
    }
    public interface LuxADialogListener {
        void onPossitiveLuxAButtonClick(int Pow);// Eventos Botón Positivo
    }

    // Interfaz de comunicación
    LuxADialogListener listener;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (LuxADialogListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(
                    activity.toString() +
                            " no implementó OnSimpleDialogListener");

        }
    }

}
