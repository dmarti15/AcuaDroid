package com.d4n1.acuadroid.dialogos;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
public class LevelDialog   extends DialogFragment {
    private int valorMin, valorLitros;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.set_level, null);
        builder.setView(v)
                .setPositiveButton(getResources().getString(R.string.PositiveButton), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.NegativeButton), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        final TextView TxUmbral = (TextView)v.findViewById(R.id.TxUmbral);
        SeekBar sbUmbral = (SeekBar)v.findViewById(R.id.seekBar);
        sbUmbral.setMax(100);
        sbUmbral.setProgress(0);
        sbUmbral.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                valorMin = progress;
                valorLitros=Integer.parseInt(TxUmbral.getText().toString());
                TxUmbral.setText(String.valueOf((progress/100)*valorLitros) );
            }
        });

        return builder.create();
    }
}
