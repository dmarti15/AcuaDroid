package com.d4n1.acuadroid.actividades;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.d4n1.acuadroid.R;
import com.d4n1.acuadroid.auxiliares.TimeChecker;
import com.d4n1.acuadroid.auxiliares.TweetHelper;
import com.d4n1.acuadroid.auxiliares.TwitterWebActivity;
import com.d4n1.acuadroid.auxiliares.Twitter_Statics;
import com.d4n1.acuadroid.dialogos.ManLuxA;
import com.d4n1.acuadroid.dialogos.ManLuxB;

import twitter4j.auth.RequestToken;

public class AcuaDroid extends AppCompatActivity implements
        ManLuxA.LuxADialogListener, ManLuxB.LuxBDialogListener {


    //AcuaDroidStatus AcuaDroidStatus;

    //Variables de estado de Acuadroid
    private int LuxA, LuxB, Temp, BatteryLevel;
    static final String Status_LuxA = "StatusLuxA",
            Status_LuxB = "StatusLuxB",
            Status_Temp = "StatusTemp",
            Status_BatteryLevel = "StatusBattery",
            Status_ManTimer = "StatusManTimer",
            Status_Man = "StatusMan";

    TextView txStatusA, txStatusB, txTemp;
    ProgressBar progressBar;
    RelativeLayout RelLay;
    int orientation;
    String Fase;

    Thread t;
    private boolean isMan;
    private int ManTimer;
    SharedPreferences sharedPref;


    Twitter_Statics ts = new Twitter_Statics();
    private RequestToken mRequestToken = null;

    private TweetHelper tweet_hlp;
    private int TWITTER_AUTH=1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        isMan = false;
        ManTimer = 0;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        // Creo instancia a clase para interactuar con twitter.
        tweet_hlp = new TweetHelper(this);
        //Verificamos que halla alguna key guardada en el telefono si no
        //llamamos a un webview para obtenerlas
        validar_login();

        setSupportActionBar(toolbar);

        // Procesar valores actuales de las preferencias.
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);// Cargar valores por defecto
        //PreferenceManager.setDefaultValues(this, R.xml.settings_otros, false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //validamos login de nuevo
                    validar_login();
                    // obtenemos texto de componente de texto
                    String texto = "Tweet de prueba de la nieva version de AcuadRoid";
                    // enviamos tweet y obtenemos estado de envio
                    boolean twt_snd_status = tweet_hlp.Send_Tweet(texto);
                    if (twt_snd_status)// OK enviado
                    { // cambiamos imagen de estad a twitter_OK
                        Snackbar.make(view, "Tu tweet ha sido enviado", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                    } else {
                        // cambiamos imagen de estad a twitter_fial
                        Snackbar.make(view, "Hubo un Error no se pudo eviar tu tweet.", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                    }

                    Snackbar.make(view, R.string.txt_FloatingButton, Snackbar.LENGTH_LONG).setAction("Action", null).show();

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        RelLay = (RelativeLayout) findViewById(R.id.LayoutFondo);
        orientation = getResources().getConfiguration().orientation;


        txStatusA = (TextView) findViewById(R.id.txStatusA);
        txStatusA.setTextColor(getResources().getColor(R.color.LuxA));
        txStatusB = (TextView) findViewById(R.id.txStatusB);
        txStatusB.setTextColor(getResources().getColor(R.color.LuxB));
        txTemp = (TextView) findViewById(R.id.txTemp);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(Integer.valueOf(sharedPref.getString("TiempoManual", "10")) * 60);

        t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                UpdateAcuaDroidStatus();
                            }
                        });
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    Log.d("AcuaDroid", "Thread Error: " + e.getMessage());
                }
            }
        };
        if (!t.isAlive()) {
            Log.d("AcuaDroid", "Arranca Ticker");
            t.start();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save variables on screen orientation change. Save the user's current game state
        savedInstanceState.putInt(Status_LuxA, LuxA);
        savedInstanceState.putInt(Status_LuxB, LuxB);
        savedInstanceState.putInt(Status_Temp, Temp);
        savedInstanceState.putInt(Status_BatteryLevel, BatteryLevel);
        savedInstanceState.putInt(Status_ManTimer, ManTimer);
        savedInstanceState.putBoolean(Status_Man, isMan);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore variables on screen orientation change. Restore state members from saved instance
        LuxA = savedInstanceState.getInt(Status_LuxA);
        LuxB = savedInstanceState.getInt(Status_LuxB);
        Temp = savedInstanceState.getInt(Status_Temp);
        BatteryLevel = savedInstanceState.getInt(Status_BatteryLevel);
        ManTimer = savedInstanceState.getInt(Status_ManTimer);
        isMan = savedInstanceState.getBoolean(Status_Man);
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

    public void ResfreshScreen() {
        txStatusA.setText(sLuxA());
        txStatusB.setText(sLuxB());
        txTemp.setText(sTemp());
        int fA = sharedPref.getInt("FaseA", -1);
        int fB = sharedPref.getInt("FaseB", -1);
        int fX = -1;
        if (fA == 2 && fB == 2) fX = 2;
        else if (fA == 1 || fB == 1 || fA == 2) fX = 1;
        else if (fA == 3 || fB == 3 || fB == 2) fX = 3;
        else if (fA == 0 && fB == 0) fX = 0;

        switch (fX) {
            case 0:
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    RelLay.setBackgroundResource(R.drawable.noche);
                } else {
                    RelLay.setBackgroundColor(getResources().getColor(R.color.cNoche));
                }
                Fase = getResources().getString(R.string.Fase0);
                break;
            case 1:
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    RelLay.setBackgroundResource(R.drawable.amanecer);
                } else {
                    RelLay.setBackgroundColor(getResources().getColor(R.color.cAmanecer));
                }
                Fase = getResources().getString(R.string.Fase1);
                break;
            case 2:
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    RelLay.setBackgroundResource(R.drawable.dia);
                } else {
                    RelLay.setBackgroundColor(getResources().getColor(R.color.cDia));
                }
                Fase = getResources().getString(R.string.Fase2);
                break;
            case 3:
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    RelLay.setBackgroundResource(R.drawable.amanecer);
                } else {
                    RelLay.setBackgroundColor(getResources().getColor(R.color.cAmanecer));
                }
                Fase = getResources().getString(R.string.Fase3);
                break;
            default:
                RelLay.setBackgroundColor(getResources().getColor(R.color.cError));
                Fase = getResources().getString(R.string.FaseX);
                Log.d("AcuaDroid", Fase + ":" + fX);
                break;
        }
        if (Temp < Integer.valueOf(sharedPref.getString("temp_min", "0"))) {
            txTemp.setTextColor(ContextCompat.getColor(this, R.color.colorCold));
            //Log.d("AcuaDroid", "Hace frio "+AcuaDroidStatus.getTemp()+">"+sharedPref.getString("temp_min", "0"));
        } else if (Temp > Integer.valueOf(sharedPref.getString("temp_max", "0"))) {
            txTemp.setTextColor(ContextCompat.getColor(this, R.color.colorHot));
            //Log.d("AcuaDroid", "Hace calor");
        } else {
            txTemp.setTextColor(ContextCompat.getColor(this, R.color.colorWarm));
            //Log.d("AcuaDroid", "Hace bueno");
        }
        progressBar.setProgress(ManTimer);
    }

    public void UpdateAcuaDroidStatus() {
        if (isMan) {
            if (ManTimer > 0) {
                ManTimer--;
                Log.d("AcuaDroid", "Modo Manual: " + ManTimer + "sec ");
            } else {
                SetManOff();
            }
        } else {
            LuxA = sharedPref.getInt("statusA", 0);
            LuxB = sharedPref.getInt("statusB", 0);
            //      AcuaDroidStatus.setBatteryLevel(sharedPref.getInt("Temp", 0));
            //Log.d("AcuaDroid", "Modo Auto");
        }
        ResfreshScreen();
    }

    @Override
    protected void onPause() {
        super.onPause();
        StopTimeChequer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StopTimeChequer();
        t.interrupt();
    }


    private void RunTimeChequer() {
        Intent i = new Intent(this, TimeChecker.class);
        startService(i);
    }

    private void StopTimeChequer() {
        stopService(new Intent(this, TimeChecker.class));
    }


    @Override
    public void onPossitiveLuxAButtonClick(int pow) {
        Log.d("AcuaDroid", "Modo Manual Azul MA: " + pow + "pow ");
        LuxA = pow;
        SetManOn();
        txStatusA.setText(sLuxA());
    }

    @Override
    public void onPossitiveLuxBButtonClick(int pow) {
        LuxB = pow;
        SetManOn();
        txStatusB.setText(sLuxB());
    }


    public void SetManOn() {
        if (!isMan) {
            ManTimer = Integer.valueOf(sharedPref.getString("TiempoManual", "10")) * 60;
            isMan = true;
            StopTimeChequer();
            Log.d("AcuaDroid", "Modo Manual ON");
        }
    }

    public void SetManOff() {
        if (isMan) {
            ManTimer = 0;
            isMan = false;
            RunTimeChequer();
            Log.d("AcuaDroid", "Modo Manual OFF");
        }
    }

    String sLuxA() {
        return LuxA + getString(R.string.PercentSign);
    }

    String sLuxB() {
        return LuxB + getString(R.string.PercentSign);
    }

    String sTemp() {
        return Temp + getString(R.string.grados);
    }

    public void validar_login(){
        //Verificar si existen claves alamacenadas en el telefono si no llamar al webview.
        if(!tweet_hlp.verify_logindata())
        {
            //Show_Toast("No se han encontrado claves en el telefono las descargaremos.");
            //creamos intent y pasamos get_AuthenticationURL
            Intent i = new Intent(AcuaDroid.this, TwitterWebActivity.class);
            i.putExtra("URL", tweet_hlp.get_AuthenticationURL());
            startActivityForResult(i, TWITTER_AUTH);

        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //si el codigo obtenido es ok
        if (resultCode == Activity.RESULT_OK) {
            // Obtenemos oauth_verifier pasado por el webview
            String oauthVerifier = (String) data.getExtras().get("oauth_verifier");
            Log.e("oauthVerifier ->", oauthVerifier);

            // Grabamos el valor de oauthVerifier en el shared preferences
            tweet_hlp.Store_OAuth_verifier(oauthVerifier);

        }

    }
}