package com.d4n1.acuadroid.auxiliares;

/**
 * Created by dmartinm on 21/12/2015.
 */
public class AcuaDroidStatus {

    //Variables de estado de Acuadroid
    private int LuxA, LuxB, Temp, BatteryLevel;

    public AcuaDroidStatus() {
        Temp=25;
    }

    public int getTemp() {
        return Temp;
    }

    public void setTemp(int temp) {
        Temp = temp;
    }

    public int getLuxB() {
        return LuxB;
    }

    public void setLuxB(int luxB) {
        LuxB = luxB;
    }

    public int getLuxA() {
        return LuxA;
    }

    public void setLuxA(int luxA) {
        LuxA = luxA;
    }

    public int getBatteryLevel() {
        return BatteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        BatteryLevel = batteryLevel;
    }


}
