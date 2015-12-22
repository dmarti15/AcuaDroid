package com.d4n1.acuadroid.dialogos;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.d4n1.acuadroid.R;

/**
 * Created by dmartinm on 30/11/2015.
 */


public class TempDialog  extends DialogFragment {

    private int valorMax;
    private int valorMin;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.set_temp, null);
        builder.setView(v)
                .setPositiveButton(getResources().getString(R.string.PositiveButton), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(valorMax>valorMin) {dialog.cancel();}
                    }
                })
                .setNegativeButton(getResources().getString(R.string.NegativeButton), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        final TextView TxMaxTemp = (TextView)v.findViewById(R.id.txMaxTemp);
        SeekBar sbMax = (SeekBar)v.findViewById(R.id.seekBarMax);
        sbMax.setMax(15);
        sbMax.setProgress(0);
        sbMax.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                valorMax = progress + 20;
                TxMaxTemp.setText(getResources().getString(R.string.Calentador) + String.valueOf(valorMax) + getResources().getString(R.string.grados));
            }
        });
        final TextView TxMinTemp = (TextView)v.findViewById(R.id.txMinTemp);
        SeekBar sbMin = (SeekBar)v.findViewById(R.id.seekBarMin);
        sbMin.setMax(15);
        sbMin.setProgress(0);
        sbMin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                valorMin=progress+15;
                TxMinTemp.setText(getResources().getString(R.string.Calentador) + String.valueOf(valorMin)+getResources().getString(R.string.grados));
            }
        });
        return builder.create();
    }


}