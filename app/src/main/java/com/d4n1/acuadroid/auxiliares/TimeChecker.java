package com.d4n1.acuadroid.auxiliares;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by dmartinm on 17/12/2015.
 */
public class TimeChecker extends Service {
    // constant
    public static final long NOTIFY_INTERVAL = 10 * 1000; // 10 seconds

    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;

    int i=1;

    int FaseAzul, FaseBlanca, FaseTotal, FasePrevia=-1;
    SharedPreferences sharedPref;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // cancel if already existed
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
            Log.d("TimeChecker", "Run");
        }
        // schedule task
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mTimer.scheduleAtFixedRate(new AcuariumCheckerTimerTask(), 0, NOTIFY_INTERVAL);
        FaseAzul        =sharedPref.getInt("FaseA", 0);
        FaseBlanca      =sharedPref.getInt("FaseB", 0);

        //Checkeo Inicial
        LuxAzulChecker();
        LuxBlancaChecker();
        TempChecher();
        LevelChecher();
        CheckFase();
    }
    public void onDestroy() {
        super.onDestroy();
        // cancel if already existed
        if (mTimer != null) {
            mTimer.cancel();
        }
    }
    class AcuariumCheckerTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    AcuariumChecker();
                }

            });
        }

    }
    private String getHourTime() {
        // get date time in custom format
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        return sdf.format(new Date());
    }
    private String getMinutTime() {
        // get date time in custom format
        SimpleDateFormat sdf = new SimpleDateFormat("mm");
        return sdf.format(new Date());
    }

    private void AcuariumChecker(){
        switch (i){
            case 1:
                LuxAzulChecker();
                break;
            case 2:
                LuxBlancaChecker();
                break;
            case 3:
                CheckFase();
                break;
            case 4:
                TempChecher();
                break;
            case 5:
                LevelChecher();
                break;
            default:
                i=0;
        }
        i++;
        //batteryLevel();
    };
    private void LuxAzulChecker(){
        int amazi = (Integer.valueOf(sharedPref.getString("amazi", "15")))*60;
        int amaze = (Integer.valueOf(sharedPref.getString("amaze", "17")))*60;
        int pmazi = (Integer.valueOf(sharedPref.getString("pmazi", "22")))*60;
        int pmaze = (Integer.valueOf(sharedPref.getString("pmaze", "24")))*60;
        int powerA =(Integer.valueOf(sharedPref.getString("powerA","70")));
        int TiempoActual = Integer.parseInt(getHourTime())*60+Integer.parseInt(getMinutTime());

        if(amazi>TiempoActual){
            sendMessage("LuxA_0");
            FaseAzul=0;
        }else if (amazi<=TiempoActual && amaze>TiempoActual){
            sendMessage("LuxA_"+map(TiempoActual,amazi,amaze,0,powerA));
            FaseAzul=1;
        }else if (amaze<=TiempoActual && pmazi>TiempoActual){
            sendMessage("LuxA_"+powerA);
            FaseAzul=2;
        }else if (pmazi<=TiempoActual && pmaze>TiempoActual){
            sendMessage("LuxA_"+map(TiempoActual,amazi,amaze,0,powerA));
            FaseAzul=3;
        }else if (pmaze<=TiempoActual){
            sendMessage("LuxA_0");
            FaseAzul=0;
        }
    }

    private void LuxBlancaChecker(){
        int ambli = (Integer.valueOf(sharedPref.getString("ambli", "15")))*60;
        int amble = (Integer.valueOf(sharedPref.getString("amble", "17")))*60;
        int pmbli = (Integer.valueOf(sharedPref.getString("pmbli", "22")))*60;
        int pmble = (Integer.valueOf(sharedPref.getString("pmble", "24")))*60;
        int powerB =(Integer.valueOf(sharedPref.getString("powerB","70")));
        int TiempoActual = Integer.parseInt(getHourTime())*60+Integer.parseInt(getMinutTime());


        if(ambli>TiempoActual){
            sendMessage("LuxB_0");
            FaseBlanca=0;
        }else if (ambli<=TiempoActual && amble>TiempoActual){
            sendMessage("LuxB_"+map(TiempoActual,ambli,amble,0,powerB));
            FaseBlanca=1;
        }else if (amble<=TiempoActual && pmbli>TiempoActual){
            sendMessage("LuxB_"+powerB);
            FaseBlanca=2;
        }else if (pmbli<=TiempoActual && pmble>TiempoActual){
            sendMessage("LuxB_"+map(TiempoActual,ambli,amble,0,powerB));
            FaseBlanca=3;
        }else if (pmble<=TiempoActual){
            sendMessage("LuxB_0");
            FaseBlanca=0;
        }
    }

    private void TempChecher(){
        //TODO Cambiar el random por la lectura del termometro
        Random r = new Random();
        int Temp=r.nextInt(10)+20;

        if (Temp < Integer.valueOf(sharedPref.getString("temp_min", "0"))) {
            sendMessage("Cold_"+Temp);
            //Log.d("AcuaDroid", "Hace frio "+AcuaDroidStatus.getTemp()+">"+sharedPref.getString("temp_min", "0"));
        } else if (Temp > Integer.valueOf(sharedPref.getString("temp_max", "0"))) {
            sendMessage("Heat_"+Temp);
            //Log.d("AcuaDroid", "Hace calor");
        } else {
            sendMessage("Warm_"+Temp);
            //Log.d("AcuaDroid", "Hace bueno");
        }

    }

    private void LevelChecher(){
        //TODO Cambiar el random por la lectura del termometro
        Random r = new Random();
        int Levl=r.nextInt(40);

        if (Levl < Integer.valueOf(sharedPref.getString("LevlAlarm", "15"))) {
            sendMessage("LowL_"+Levl);
            //Log.d("AcuaDroid", "Hace frio "+AcuaDroidStatus.getTemp()+">"+sharedPref.getString("temp_min", "0"));
        } else {
            sendMessage("LvOk_"+Levl);
            //Log.d("AcuaDroid", "Hace bueno");
        }

    }


    //TODO Error al instanciar varios receiver
    private void batteryLevel() {
        BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this);
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sharedPref.edit();
                int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int level = -1;
                if (rawlevel >= 0 && scale > 0) {
                    level = (rawlevel * 100) / scale;
                }
                editor.putInt("BatteryLevel", level);
                editor.apply();
            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelReceiver, batteryLevelFilter);
    }

    int map(int x, int in_min, int in_max, int out_min, int out_max)
    {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    // Send an Intent with an action named "my-event".
    private void sendMessage(String Valor) {
        Intent intent = new Intent("AcuaDroid");
        // add data
        intent.putExtra("message", Valor);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void CheckFase(){
        if (FaseAzul == 2 && FaseBlanca == 2) FaseTotal = 2;
        else if (FaseAzul == 1 || FaseBlanca == 1 || FaseAzul == 2) FaseTotal = 1;
        else if (FaseAzul == 3 || FaseBlanca == 3 || FaseBlanca == 2) FaseTotal = 3;
        else if (FaseAzul == 0 && FaseBlanca == 0) FaseTotal = 0;

        if(FaseTotal!=FasePrevia){
            switch (FaseTotal){
                case 0:
                    sendMessage("Noch");
                    break;
                case 1:
                    sendMessage("Dawn");
                    break;
                case 2:
                    sendMessage("Dia_");
                    break;
                case 3:
                    sendMessage("Dusk");
                    break;
            }
        }
    }
}
