package com.d4n1.acuadroid.actividades;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
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
import com.d4n1.acuadroid.auxiliares.TwitterText;
import com.d4n1.acuadroid.dialogos.ManLuxA;
import com.d4n1.acuadroid.dialogos.ManLuxB;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import io.fabric.sdk.android.Fabric;

public class AcuaDroid extends AppCompatActivity implements
        ManLuxA.LuxADialogListener, ManLuxB.LuxBDialogListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    //private static final String TWITTER_KEY = "IOeAvbTyModB6Vg2RmxaD3r3K";
    //private static final String TWITTER_SECRET = "O15G4qgDKTIWInPuzXdGFQ4VYCC3SbDBGi2M9guJMEwnWaXtuK";

    private static final String TWITTER_KEY = "5u1x09N5I6JECefgQhh7XEPzP";
    private static final String TWITTER_SECRET = "BKXwGZo3u8PADnn7rBMwNnSTzM3cXr3nkmQtudUm3vyqrEydfE";
    private TwitterLoginButton loginButton;
    private Boolean isTwitterLoggedIn=false;
    private TwitterSession session;


    private TwitterText tt;


    //AcuaDroidStatus AcuaDroidStatus;

    //Variables de estado de Acuadroid
    private int LuxA, LuxB, Temp, BatteryLevel;
    static final String Status_LuxA = "StatusLuxA",
            Status_LuxB = "StatusLuxB",
            Status_Temp = "StatusTemp",
            Status_BatteryLevel = "StatusBattery",
            Status_ManTimer = "StatusManTimer",
            Status_Man = "StatusMan",
            Status_twitter = "Status_twitter";

    TextView txStatusA, txStatusB, txTemp;
    ProgressBar progressBar;
    RelativeLayout RelLay;
    int orientation;
    String Fase;

    Thread t;
    private boolean isMan;
    private int ManTimer;
    SharedPreferences sharedPref;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        tt = new TwitterText(this);

        isMan = false;
        ManTimer = 0;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);


        setSupportActionBar(toolbar);

        // Procesar valores actuales de las preferencias.
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);// Cargar valores por defecto
        //PreferenceManager.setDefaultValues(this, R.xml.settings_otros, false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Tweet(tt.getFeed());
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
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Make sure that the loginButton hears the result from any
        // Activity that it triggered.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

    public void Tweet(String txt){
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(session);
        StatusesService statusesService = twitterApiClient.getStatusesService();
        statusesService.update(txt, null, false, null, null, null,null, null, null, new Callback<Tweet>() {
                    @Override
                    public void success(Result<Tweet> result) {
                        //Do something with result, which provides a Tweet inside of result.data
                        Log.d("TwitterKit", "Tweet enviado");
                    }

                    public void failure(TwitterException exception) {
                        //Do something on failure
                        Log.e("TwitterKit", "Tweet NO enviado, estas loggeado?");
                    }
                });
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
        savedInstanceState.putBoolean(Status_twitter, isTwitterLoggedIn);

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
        isTwitterLoggedIn = savedInstanceState.getBoolean(Status_twitter);
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
        Log.w("TwitterKit", "usar_twitter: " + String.valueOf(sharedPref.getBoolean("usar_twitter", false))+ " isTwitterLoggedIn: " + String.valueOf(isTwitterLoggedIn));
        if(sharedPref.getBoolean("usar_twitter", false) && !isTwitterLoggedIn){
            loginButton = new TwitterLoginButton(this);
            loginButton.setCallback(new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    // The TwitterSession is also available through:
                    // Twitter.getInstance().core.getSessionManager().getActiveSession()
                    session = result.data;
                    // TODO: Remove toast and use the TwitterSession's userID
                    // with your app's user model
                    isTwitterLoggedIn=true;
                    String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";
                    Log.d("TwitterKit", msg);
                }
                @Override
                public void failure(TwitterException exception) {
                    Log.d("TwitterKit", "Login with Twitter failure", exception);
                }
            });
            loginButton.performClick();
        }
        // Register mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("AcuaDroid"));
    }



    public void UpdateAcuaDroidStatus() {
        if (isMan) {
            if (ManTimer > 0) {
                progressBar.setProgress(ManTimer);
                ManTimer--;
                Log.d("AcuaDroid", "Modo Manual: " + ManTimer + "sec ");
            } else {
                SetManOff();
            }
        }
    }

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        StopTimeChequer();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        StopTimeChequer();
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        if(t != null){
            if (t.isAlive()) {
                Log.d("AcuaDroid", "onDestroy");
                t.interrupt();
            }
        }
        super.onDestroy();
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
        SetManOn();
        txStatusA.setText(sLux(pow));
    }

    @Override
    public void onPossitiveLuxBButtonClick(int pow) {
        SetManOn();
        txStatusB.setText(sLux(pow));
    }


    public void SetManOn() {
        if (!isMan) {
            ManTimer = Integer.valueOf(sharedPref.getString("TiempoManual", "10")) * 60;
            progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorWarm), PorterDuff.Mode.SRC_IN);
            isMan = true;
            StopTimeChequer();
            Log.d("AcuaDroid", "Modo Manual ON");

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
    }

    public void SetManOff() {
        if (isMan) {
            ManTimer = 0;
            t.interrupt();
            isMan = false;
            RunTimeChequer();
            Log.d("AcuaDroid", "Modo Manual OFF");
        }
    }

    String sLux(int pow) {
        return pow + getString(R.string.PercentSign);
    }

    String sTemp(int temp) {
        return temp + getString(R.string.grados);
    }

    // handler for received Intents for the "my-event" event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);
            switch (message.substring(0,4)){
                case "Noch":
                    setBackground(0);
                    break;
                case "Dawn":
                    setBackground(1);
                    break;
                case "Dia_":
                    setBackground(2);
                    break;
                case "Dusk":
                    setBackground(3);
                    break;
                case "Cold":
                    setCold(Integer.valueOf(message.substring(5)));
                    break;
                case "Heat":
                    setHeat(Integer.valueOf(message.substring(5)));
                break;
                case "Warm":
                    setWarm(Integer.valueOf(message.substring(5)));
                    break;
                case "LLvl":
                    setLowLevel(Integer.valueOf(message.substring(5)));
                    break;
                case "LvOk":
                    setLevelOK(Integer.valueOf(message.substring(5)));
                    break;
                case "LuxA":
                    setLuxA(Integer.valueOf(message.substring(5)));
                    break;
                case "LuxB":
                    setLuxB(Integer.valueOf(message.substring(5)));
                    break;
                default:
                    break;
            }
        }
    };


    private void setBackground(int Fase){
        switch (Fase) {
            case 0:
                    RelLay.setBackgroundResource(R.drawable.noche);
                break;
            case 1:
                    RelLay.setBackgroundResource(R.drawable.amanecer);
                break;
            case 2:
               RelLay.setBackgroundResource(R.drawable.dia);
                break;
            case 3:
                    RelLay.setBackgroundResource(R.drawable.amanecer);
                break;
            default:
                break;
        }
    }
//TODO No se ve la temperatura
    private void setCold(int T){
        txTemp.setTextColor(getResources().getColor(R.color.colorCold));
        txTemp.setText(sTemp(T));
        Log.d("AcuaDroid", "Frio: "+sTemp(T));
    }
    private void setHeat(int T){
        txTemp.setTextColor(getResources().getColor(R.color.colorHot));
        txTemp.setText(sTemp(T));
        Log.d("AcuaDroid", "Calor: "+sTemp(T));
    }
    private void setWarm(int T){
        txTemp.setTextColor(getResources().getColor(R.color.colorWarm));
        txTemp.setText(sTemp(T));
        Log.d("AcuaDroid", "Bien: "+sTemp(T));
    }

    private void setLowLevel(int L){
        progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorHot), PorterDuff.Mode.SRC_IN);
        progressBar.setProgress(L);
        Log.d("AcuaDroid", "Low Level: "+L);
    }

    private void setLevelOK(int L){
        progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorCold), PorterDuff.Mode.SRC_IN);
        progressBar.setProgress(L);
        Log.d("AcuaDroid", "OK Level: "+L);
    }

    private void setLuxA(int L){
        txStatusA.setText(sLux(L));
        Log.d("AcuaDroid", "LuxA: "+sLux(L));
    }
    private void setLuxB(int L){
        txStatusB.setText(sLux(L));
        Log.d("AcuaDroid", "LuxB: "+sLux(L));
    }


}