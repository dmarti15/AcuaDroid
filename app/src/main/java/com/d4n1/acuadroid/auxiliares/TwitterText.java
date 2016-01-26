package com.d4n1.acuadroid.auxiliares;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.d4n1.acuadroid.R;

import java.util.Random;

/**
 * Created by dmartinm on 26/01/2016.
 */
public class TwitterText {
    Context ctx;
    SharedPreferences sharedPref;
    public TwitterText(Context context){
        ctx=context;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    //=================================  TEMPERATURA  =======================================//
    public String getHighTemp(int temp){
        String t="";
        Random r = new Random();
        switch (r.nextInt(2)){
            case 1:
                t=ctx.getResources().getString(R.string.HiTemp_1a)
                        +temp
                        +ctx.getResources().getString(R.string.HiTemp_1b);
                break;
            case 2:
                t=ctx.getResources().getString(R.string.HiTemp_2a)
                        +temp
                        +ctx.getResources().getString(R.string.HiTemp_2b);
                break;
            default:
                t=ctx.getResources().getString(R.string.HiTemp_3a)
                        +sharedPref.getString("usuario_twitter", "")
                        +ctx.getResources().getString(R.string.HiTemp_3b)
                        +temp
                        +ctx.getResources().getString(R.string.HiTemp_3c);
                break;
        }
        return t;
    }

    public String getLowTemp(int temp){
        String t="";
        Random r = new Random();
        switch (r.nextInt(2)){
            case 1:
                t=ctx.getResources().getString(R.string.LowTemp_1a)
                        +temp
                        +ctx.getResources().getString(R.string.LowTemp_1b);
                break;
            case 2:
                t=ctx.getResources().getString(R.string.LowTemp_2a)
                        +temp
                        +ctx.getResources().getString(R.string.LowTemp_2b);
                break;
            default:
                t=ctx.getResources().getString(R.string.LowTemp_3a)
                        +sharedPref.getString("usuario_twitter", "")
                        +ctx.getResources().getString(R.string.LowTemp_3b)
                        +temp
                        +ctx.getResources().getString(R.string.LowTemp_3c);
                break;
        }
        return t;
    }

    //=================================  Nivel  =======================================//
    public String getNivel(int ltrs, int pcnt){
        String t="";
        Random r = new Random();
        switch (r.nextInt(2)){
            case 1:
                t=ctx.getResources().getString(R.string.LowLevl_1a)
                        +sharedPref.getString("usuario_twitter", "")
                        +ctx.getResources().getString(R.string.LowLevl_1b)
                        +pcnt
                        +ctx.getResources().getString(R.string.LowLevl_1c);
                break;
            case 2:
                t=ctx.getResources().getString(R.string.LowLevl_2a)
                        +sharedPref.getString("usuario_twitter", "")
                        +ctx.getResources().getString(R.string.LowLevl_2b)
                        +ltrs
                        +ctx.getResources().getString(R.string.LowLevl_2c);
                break;
            default:
                t=ctx.getResources().getString(R.string.LowLevl_3a)
                        +sharedPref.getString("usuario_twitter", "")
                        +ctx.getResources().getString(R.string.LowLevl_3b)
                        +ltrs
                        +ctx.getResources().getString(R.string.LowLevl_3c);
                break;
        }
        return t;
    }

    //=================================  Nivel  =======================================//
    public String getFeed(){
        String t="";
        Random r = new Random();
        switch (r.nextInt(8)){
            case 1:
                t=ctx.getResources().getString(R.string.Feed_1)+ctx.getResources().getString(R.string.Feed_4);
                break;
            case 2:
                t=ctx.getResources().getString(R.string.Feed_1)+ctx.getResources().getString(R.string.Feed_5);
                break;
            case 3:
                t=ctx.getResources().getString(R.string.Feed_1)+ctx.getResources().getString(R.string.Feed_6);
                break;
            case 4:
                t=ctx.getResources().getString(R.string.Feed_2)+ctx.getResources().getString(R.string.Feed_4);
                break;
            case 5:
                t=ctx.getResources().getString(R.string.Feed_2)+ctx.getResources().getString(R.string.Feed_5);
                break;
            case 6:
                t=ctx.getResources().getString(R.string.Feed_2)+ctx.getResources().getString(R.string.Feed_6);
                break;
            case 7:
                t=ctx.getResources().getString(R.string.Feed_3)+ctx.getResources().getString(R.string.Feed_4);
                break;
            case 8:
                t=ctx.getResources().getString(R.string.Feed_3)+ctx.getResources().getString(R.string.Feed_5);
                break;
            default:
                t=ctx.getResources().getString(R.string.Feed_3)+ctx.getResources().getString(R.string.Feed_6);
                break;
        }
        return t;
    }

    public String getResumen(boolean isDawn, int ltrs, int pcnt, int temp) {
        String tr, tt, tn, te;
        Boolean bTemp, bLevl, bTempHi;
        Random r = new Random();
        if(pcnt<=Integer.valueOf(sharedPref.getString("LevlAlarm", "15"))){
            bLevl=true;
        }else{
            bLevl=false;
        }
        if(temp>=Integer.valueOf(sharedPref.getString("temp_max", "27"))){
            bTemp=true;
            bTempHi=true;
        }else{
            bTemp=false;
            bTempHi=false;
        }
        if(temp<Integer.valueOf(sharedPref.getString("temp_min", "22"))){
            bLevl=true;
        }else{
            bLevl=false;
        }

        if(isDawn){
            switch (r.nextInt(2)) {
                case 1:
                    tr = ctx.getResources().getString(R.string.Dawn_1);
                    break;
                case 2:
                    tr = ctx.getResources().getString(R.string.Dawn_2);
                    break;
                default:
                    tr = ctx.getResources().getString(R.string.Dawn_3);
                    break;
            }
        }else{
            switch (r.nextInt(2)) {
                case 1:
                    tr = ctx.getResources().getString(R.string.Dusk_1);
                    break;
                case 2:
                    tr = ctx.getResources().getString(R.string.Dusk_2);
                    break;
                default:
                    tr = ctx.getResources().getString(R.string.Dusk_3);
                    break;
            }
        }
        if(bTemp){
            if(bTempHi){
                switch (r.nextInt(1)) {
                    case 1:
                        tt = ctx.getResources().getString(R.string.Temp_Ha)
                             +temp
                             +ctx.getResources().getString(R.string.Temp_G);
                        break;
                    default:
                        tt = ctx.getResources().getString(R.string.Temp_Hb)
                             +temp
                             +ctx.getResources().getString(R.string.Temp_G);
                        break;
                }
            }else{
                switch (r.nextInt(1)) {
                    case 1:
                        tt = ctx.getResources().getString(R.string.Temp_Fa)
                                +temp
                                +ctx.getResources().getString(R.string.Temp_G);
                        break;
                    default:
                        tt = ctx.getResources().getString(R.string.Temp_Fb)
                                +temp
                                +ctx.getResources().getString(R.string.Temp_G);
                        break;
                }
            }
        }else{
            switch (r.nextInt(1)) {
                case 1:
                    tt = ctx.getResources().getString(R.string.Temp_Ba)
                            +temp
                            +ctx.getResources().getString(R.string.Temp_G);
                    break;
                default:
                    tt = ctx.getResources().getString(R.string.Temp_Bb)
                            +temp
                            +ctx.getResources().getString(R.string.Temp_G);
                    break;
            }
        }

        if(bLevl==bTemp){
            te=ctx.getResources().getString(R.string.Link_Y);
        }else{
            te=ctx.getResources().getString(R.string.Link_P);
        }

        if(bTemp){
            switch (r.nextInt(1)) {
                case 1:
                    tn = ctx.getResources().getString(R.string.Level_Ma)
                            +ltrs
                            +ctx.getResources().getString(R.string.Level_L);
                    break;
                default:
                    tn = ctx.getResources().getString(R.string.Level_Mb)
                            +temp
                            +ctx.getResources().getString(R.string.Level_P);
                    break;
            }
        }else{
            switch (r.nextInt(1)) {
                case 1:
                    tn = ctx.getResources().getString(R.string.Level_Ba)
                            +ltrs
                            +ctx.getResources().getString(R.string.Level_L);
                    break;
                default:
                    tn = ctx.getResources().getString(R.string.Level_Bb)
                            +pcnt
                            +ctx.getResources().getString(R.string.Level_P);
                    break;
            }
        }
        return tr+tt+te+tn;
    }

    public String getAmanecer(int ltrs, int pcnt, int temp) {
        return getResumen(true, ltrs, pcnt, temp);
    }
    public String getAnochecer(int ltrs, int pcnt, int temp) {
        return getResumen(false, ltrs, pcnt, temp);
    }
}
