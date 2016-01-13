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
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
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
        mTimer.scheduleAtFixedRate(new AcuariumCheckerTimerTask(), 0, NOTIFY_INTERVAL);
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
            default:
                i=0;
        }
        i++;
        //batteryLevel();
    };
    private void LuxAzulChecker(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        int amazi = (Integer.valueOf(sharedPref.getString("amazi", "15")))*60;
        int amaze = (Integer.valueOf(sharedPref.getString("amaze", "17")))*60;
        int pmazi = (Integer.valueOf(sharedPref.getString("pmazi", "22")))*60;
        int pmaze = (Integer.valueOf(sharedPref.getString("pmaze", "24")))*60;
        int powerA =(Integer.valueOf(sharedPref.getString("powerA","70")));
        int TiempoActual = Integer.parseInt(getHourTime())*60+Integer.parseInt(getMinutTime());


        if(amazi>TiempoActual){
            editor.putInt("statusA",0);
            editor.putInt("FaseA", 0);
        }else if (amazi<=TiempoActual && amaze>TiempoActual){
            editor.putInt("statusA",map(TiempoActual,amazi,amaze,0,powerA));
            editor.putInt("FaseA", 1);
        }else if (amaze<=TiempoActual && pmazi>TiempoActual){
            editor.putInt("statusA",powerA);
            editor.putInt("FaseA", 2);
        }else if (pmazi<=TiempoActual && pmaze>TiempoActual){
            editor.putInt("statusA",map(TiempoActual,amazi,amaze,0,powerA));
            editor.putInt("FaseA", 3);
        }else if (pmaze<=TiempoActual){
            editor.putInt("statusA",0);
            editor.putInt("FaseA", 0);
        };
        editor.apply();

        Log.d("TimeChecker", "LuzAzul="+sharedPref.getInt("statusA",-1));
    }

    private void LuxBlancaChecker(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        int ambli = (Integer.valueOf(sharedPref.getString("ambli", "15")))*60;
        int amble = (Integer.valueOf(sharedPref.getString("amble", "17")))*60;
        int pmbli = (Integer.valueOf(sharedPref.getString("pmbli", "22")))*60;
        int pmble = (Integer.valueOf(sharedPref.getString("pmble", "24")))*60;
        int powerB =(Integer.valueOf(sharedPref.getString("powerB","70")));
        int TiempoActual = Integer.parseInt(getHourTime())*60+Integer.parseInt(getMinutTime());


        if(ambli>TiempoActual){
            editor.putInt("statusB", 0);
            editor.putInt("FaseB", 0);
        }else if (ambli<=TiempoActual && amble>TiempoActual){
            editor.putInt("statusB",map(TiempoActual,ambli,amble,0,powerB));
            editor.putInt("FaseB", 1);
        }else if (amble<=TiempoActual && pmbli>TiempoActual){
            editor.putInt("statusB",powerB);
            editor.putInt("FaseB", 2);
        }else if (pmbli<=TiempoActual && pmble>TiempoActual){
            editor.putInt("statusB",map(TiempoActual,ambli,amble,0,powerB));
            editor.putInt("FaseB", 3);
        }else if (pmble<=TiempoActual){
            editor.putInt("statusB",0);
            editor.putInt("FaseB", 0);
        };
        editor.apply();
        Log.d("TimeChecker", "LuzBlanca="+sharedPref.getInt("statusB",-1));
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
}
