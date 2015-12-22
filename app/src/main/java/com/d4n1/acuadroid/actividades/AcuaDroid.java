package com.d4n1.acuadroid.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.d4n1.acuadroid.R;
import com.d4n1.acuadroid.auxiliares.AcuaDroidStatus;
import com.d4n1.acuadroid.auxiliares.TimeChecker;
import com.d4n1.acuadroid.dialogos.ManLuxA;
import com.d4n1.acuadroid.dialogos.ManLuxB;

public class AcuaDroid extends AppCompatActivity implements
        ManLuxA.LuxADialogListener, ManLuxB.LuxBDialogListener {


    AcuaDroidStatus AcuaDroidStatus;

    TextView txStatusA, txStatusB, txTemp;
    ProgressBar progressBar;
    private boolean isMan;
    private int ManTimer;
    SharedPreferences sharedPref ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        AcuaDroidStatus = new AcuaDroidStatus();

        isMan = false;
        ManTimer=0;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        setSupportActionBar(toolbar);

        // Procesar valores actuales de las preferencias.
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);// Cargar valores por defecto
        //PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, R.string.txt_FloatingButton, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        txStatusA = (TextView) findViewById(R.id.txStatusA);
        txStatusB = (TextView) findViewById(R.id.txStatusB);
        txTemp = (TextView) findViewById(R.id.txTemp);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(sharedPref.getInt("ManTime", 60));

        //TODO Que no salga mas de un hilo
        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                UpdateAcuaDroidStatus();
                                ResfreshScreen();
                            }
                        });
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        FragmentManager fragmentManager = getSupportFragmentManager();
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.man_luxA) {
            ManLuxA dialogo = new ManLuxA();
            dialogo.show(fragmentManager, "statusA");
            return true;
        }
        if (id == R.id.man_luxB) {
            ManLuxB dialogo = new ManLuxB();
            dialogo.show(fragmentManager, "statusB");
            return true;
        }

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onResume() {
        super.onResume();

        RunTimeChequer();
    }

    public void ResfreshScreen(){
        txStatusA.setText(String.valueOf(AcuaDroidStatus.getLuxA()));
        txStatusB.setText(String.valueOf(AcuaDroidStatus.getLuxB()));
        txTemp.setText(String.valueOf(AcuaDroidStatus.getTemp())+"º");
        if(AcuaDroidStatus.getTemp()>sharedPref.getInt("temp_min", 0))
        {
            txTemp.setTextColor(ContextCompat.getColor(this, R.color.colorCold));
        }else if(AcuaDroidStatus.getTemp()<sharedPref.getInt("temp_max", 0)){
            txTemp.setTextColor(ContextCompat.getColor(this, R.color.colorHot));
        }else {
            txTemp.setTextColor(ContextCompat.getColor(this, R.color.colorWarm));
        }
        progressBar.setProgress(ManTimer);
    };

    public void UpdateAcuaDroidStatus(){
        if(isMan){
            if(ManTimer>0){
                ManTimer--;
                Log.d("AcuaDroid", "Modo Manual: " + ManTimer+ "sec ");
            }else{
                SetManOff();
            }
        }else{
            txStatusA.setText(String.valueOf(AcuaDroidStatus.getLuxA()));
            txStatusB.setText(String.valueOf(AcuaDroidStatus.getLuxB()));
      //      AcuaDroidStatus.setBatteryLevel(sharedPref.getInt("Temp", 0));
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        StopTimeChequer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StopTimeChequer();
    }



    private void RunTimeChequer(){
        Intent i = new Intent(this, TimeChecker.class);
//        Bundle bundle = new Bundle();
//        bundle.pu
//        i.putExtras(AcuaDroidStatus);
        startService(i);
    }
    private void StopTimeChequer(){
        stopService(new Intent(this, TimeChecker.class));
    }


    //TODO Mantener valores al girar pantalla
    @Override
    public void onPossitiveLuxAButtonClick(int pow) {
        Log.d("AcuaDroid", "Modo Manual Azul MA: " + pow+ "pow ");
        AcuaDroidStatus.setLuxA(pow);
        SetManOn();
        txStatusA.setText(String.valueOf(AcuaDroidStatus.getLuxA()));
    }
    @Override
    public void onPossitiveLuxBButtonClick(int pow) {
        AcuaDroidStatus.setLuxB(pow);
        SetManOn();
        txStatusB.setText(String.valueOf(AcuaDroidStatus.getLuxB()));
    }


    public void SetManOn(){
        ManTimer=sharedPref.getInt("ManTime", 60);
        isMan=true;
        StopTimeChequer();
        Log.d("AcuaDroid", "Modo Manual ON");
    }
    public void SetManOff(){
        ManTimer=0;
        isMan=false;
        RunTimeChequer();
        Log.d("AcuaDroid", "Modo Manual OFF");
    }


}
