package com.d4n1.acuadroid.actividades;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.d4n1.acuadroid.R;
import com.d4n1.acuadroid.auxiliares.BluetoothDeviceArrayAdapter;
import com.d4n1.acuadroid.auxiliares.BluetoothService;
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

import java.util.ArrayList;

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


    private BluetoothAdapter bAdapter;					// Adapter para uso del Bluetooth
    private ArrayList<BluetoothDevice> arrayDevices;	// Listado de dispositivos
    private ArrayAdapter arrayAdapter;					// Adaptador para el listado de dispositivos
    private static final int    REQUEST_ENABLE_BT   = 1;
    private static final int    REQUEST_DEVICE_BT   = 1;
    private BluetoothService 	servicio;				// Servicio de mensajes de Bluetooth
    private BluetoothDevice		ultimoDispositivo;		// Ultimo dispositivo conectado
    private ListView lvDispositivos;


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

        //btnBuscarDispositivo = new Button(this);

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

        Log.w("Acuadroid BT", "Activamos BlueTooth");
        bAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bAdapter != null)
        {
            if(bAdapter.isEnabled()) {
                Log.w("Acuadroid BT", "BT Activo");
            }else{
                Log.w("Acuadroid BT", "BT Apagado");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

            }
        }
        configurarControles();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Make sure that the loginButton hears the result from any
        // Activity that it triggered.

        switch(requestCode)
        {
            case REQUEST_ENABLE_BT:
            {
                if(resultCode == RESULT_OK)
                {
                    // Acciones adicionales a realizar si el usuario activa el Bluetooth
                    if(servicio != null)
                    {
                        servicio.finalizarServicio();
                        servicio.iniciarServicio();
                        Log.w("Acuadroid BT", "Reiniciando el servicio");
                    }
                    else {
                        servicio = new BluetoothService(this, handler, bAdapter);
                        Log.w("Acuadroid BT", "Arrancando el servicio");
                    }
                }
                else
                {
                    // Acciones adicionales a realizar si el usuario no activa el Bluetooth
                }
                break;
            }

            default:
                loginButton.onActivityResult(requestCode, resultCode, data);
                break;
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





    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        StopTimeChequer();
        this.unregisterReceiver(bReceiver);
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
        //this.unregisterReceiver(bReceiver);
        super.onDestroy();
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


    //============================================================================================================================
    //===============================        FUNCIONES AUXILIARES             ====================================================
    //============================================================================================================================

    String sLux(int pow) {
        return pow + getString(R.string.PercentSign);
    }

    String sTemp(int temp) {
        return temp + getString(R.string.grados);
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

    //============================================================================================================================
    //===============================                  TWITTER                ====================================================
    //============================================================================================================================

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



    //============================================================================================================================
    //===============================                 BLUETOOTH               ====================================================
    //============================================================================================================================

    // Instanciamos un BroadcastReceiver que se encargara de detectar si el estado
// del Bluetooth del dispositivo ha cambiado mediante su handler onReceive
    private final BroadcastReceiver bReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();

            // BluetoothAdapter.ACTION_STATE_CHANGED
            // Codigo que se ejecutara cuando el Bluetooth cambie su estado.
            // Manejaremos los siguientes estados:
            //		- STATE_OFF: El Bluetooth se desactiva
            //		- STATE ON: El Bluetooth se activa
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action))
            {
                final int estado = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (estado)
                {
                    // Apagado
                    case BluetoothAdapter.STATE_OFF:
                    {
                        Log.v("AcuaDroidBT", "onReceive: Apagando");
                        break;
                    }

                    // Encendido
                    case BluetoothAdapter.STATE_ON:
                    {
                        Log.v("AcuaDroidBT", "onReceive: Encendiendo");

                        //Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                        //discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
                        //startActivity(discoverableIntent);

                        break;
                    }
                    default:
                        break;
                } // Fin switch

            } // Fin if

            // BluetoothDevice.ACTION_FOUND
            // Cada vez que se descubra un nuevo dispositivo por Bluetooth, se ejecutara
            // este fragmento de codigo
            else if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                lvDispositivos.setVisibility(View.VISIBLE);
                Log.w("Acuadroid BT", "lvDispositivos.VISIBLE");
                if(arrayDevices == null)
                    arrayDevices = new ArrayList<BluetoothDevice>();

                BluetoothDevice dispositivo = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                arrayDevices.add(dispositivo);
                String descripcionDispositivo = dispositivo.getName() + " [" + dispositivo.getAddress() + "]";
                //Toast.makeText(getBaseContext(), getString(R.string.DetectadoDispositivo) + ": " + descripcionDispositivo, Toast.LENGTH_SHORT).show();
                Log.v("AcuaDroidBT", "ACTION_FOUND: Dispositivo encontrado: " + descripcionDispositivo);
            }

            // BluetoothAdapter.ACTION_DISCOVERY_FINISHED
            // Codigo que se ejecutara cuando el Bluetooth finalice la busqueda de dispositivos.
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                // Instanciamos un nuevo adapter para el ListView
                arrayAdapter = new BluetoothDeviceArrayAdapter(getBaseContext(), android.R.layout.simple_list_item_2, arrayDevices);
                lvDispositivos.setAdapter(arrayAdapter);
                //Toast.makeText(getBaseContext(), R.string.FinBusqueda, Toast.LENGTH_SHORT).show();
                Log.v("AcuaDroidBT", "Búsqueda terminada ");
            }

        } // Fin onReceive
    };

    // Handler que obtendrá informacion de BluetoothService
    private final Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg)
        {
            byte[] buffer 	= null;
            String mensaje 	= null;

            // Atendemos al tipo de mensaje
            switch(msg.what)
            {
                // Mensaje de lectura: se mostrara en el TextView
                case BluetoothService.MSG_LEER:
                {
                    buffer = (byte[])msg.obj;
                    mensaje = new String(buffer, 0, msg.arg1);
                    Log.e("AcuaDroid_BT", "MSG_LEER: "+mensaje);
                    break;
                }

                // Mensaje de escritura: se mostrara en el Toast
                case BluetoothService.MSG_ESCRIBIR:
                {
                    buffer = (byte[])msg.obj;
                    mensaje = new String(buffer);
                    mensaje = "EnviandoMensaje: " + mensaje;
                    Log.e("AcuaDroid_BT", "MSG_ESCRIBIR: "+mensaje);
                    break;
                }

                // Mensaje de cambio de estado
                case BluetoothService.MSG_CAMBIO_ESTADO:
                {
                    switch(msg.arg1)
                    {
                        case BluetoothService.ESTADO_ATENDIENDO_PETICIONES:
                            break;

                        // CONECTADO: Se muestra el dispositivo al que se ha conectado y se activa el boton de enviar
                        case BluetoothService.ESTADO_CONECTADO:
                        {
                            mensaje = "Conexion Actual: " + servicio.getNombreDispositivo();
                            Log.e("AcuaDroid_BT", "ESTADO_CONECTADO: "+mensaje);
                            //todo lvDispositivos.setVisibility(View.INVISIBLE);
                            Log.w("Acuadroid BT", "lvDispositivos.INVISIBLE");
                            //tvConexion.setText(mensaje);
                            //btnEnviar.setEnabled(true);
                            break;
                        }

                        // REALIZANDO CONEXION: Se muestra el dispositivo al que se esta conectando
                        case BluetoothService.ESTADO_REALIZANDO_CONEXION:
                        {
                            mensaje = "Conectando A " + ultimoDispositivo.getName() + " [" + ultimoDispositivo.getAddress() + "]";
                            Log.e("AcuaDroid_BT", "ESTADO_REALIZANDO_CONEXION: "+mensaje);
                            // btnEnviar.setEnabled(false);
                            break;
                        }

                        // NINGUNO: Mensaje por defecto. Desactivacion del boton de enviar
                        case BluetoothService.ESTADO_NINGUNO:
                        {
                            mensaje = "Sin Conexion";
                            Log.e("AcuaDroid_BT", "ESTADO_NINGUNO: "+mensaje);
                            //tvConexion.setText(mensaje);
                            //btnEnviar.setEnabled(false);
                            break;
                        }
                        default:
                            break;
                    }
                    break;
                }

                // Mensaje de alerta: se mostrara en el Toast
                case BluetoothService.MSG_ALERTA:
                {
                    mensaje = msg.getData().getString("alerta");
                    //Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
                    Log.e("AcuaDroid_BT", "MSG_ALERTA: "+mensaje);
                    break;
                }

                default:
                    break;
            }
        }
    };

    private void configurarControles()
    {

        // Instanciamos el array de dispositivos
        arrayDevices = new ArrayList<BluetoothDevice>();
        lvDispositivos = (ListView)findViewById(R.id.lvDispositivos);
        //todo lvDispositivos.setVisibility(View.INVISIBLE);
        Log.w("Acuadroid BT", "lvDispositivos.INVISIBLE");
        configurarListaDispositivos();
        bAdapter = BluetoothAdapter.getDefaultAdapter();
        registrarEventosBluetooth();
    }
    /**
     * Configura el ListView para que responda a los eventos de pulsacion
     */
    private void configurarListaDispositivos()
    {
        lvDispositivos.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView adapter, View view, int position, long arg)
            {
                // El ListView tiene un adaptador de tipo BluetoothDeviceArrayAdapter.
                // Invocamos el metodo getItem() del adaptador para recibir el dispositivo
                // bluetooth y realizar la conexion.
                BluetoothDevice dispositivo = (BluetoothDevice)lvDispositivos.getAdapter().getItem(position);

                AlertDialog dialog = crearDialogoConexion(getString(R.string.Conectar),
                        getString(R.string.MsgConfirmarConexion) + " " + dispositivo.getName() + "?",
                        dispositivo.getAddress());

                dialog.show();
            }
        });
    }

    private AlertDialog crearDialogoConexion(String titulo, String mensaje, final String direccion)
    {
        // Instanciamos un nuevo AlertDialog Builder y le asociamos titulo y mensaje
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(titulo);
        alertDialogBuilder.setMessage(mensaje);

        // Creamos un nuevo OnClickListener para el boton OK que realice la conexion
        DialogInterface.OnClickListener listenerOk = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                conectarDispositivo(direccion);
            }
        };

        // Creamos un nuevo OnClickListener para el boton Cancelar
        DialogInterface.OnClickListener listenerCancelar = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        };

        // Asignamos los botones positivo y negativo a sus respectivos listeners
        alertDialogBuilder.setPositiveButton(R.string.Conectar, listenerOk);
        alertDialogBuilder.setNegativeButton(R.string.Cancelar, listenerCancelar);

        return alertDialogBuilder.create();
    }

    /**
     * Configura el BluetoothAdapter y los botones asociados
     */


    /**
     * Suscribe el BroadcastReceiver a los eventos relacionados con Bluetooth que queremos
     * controlar.
     */
    private void registrarEventosBluetooth()
    {
        // Registramos el BroadcastReceiver que instanciamos previamente para
        // detectar los distintos eventos que queremos recibir
        IntentFilter filtro = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        filtro.addAction(BluetoothDevice.ACTION_FOUND);
        filtro.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        this.registerReceiver(bReceiver, filtro);
    }

    public void conectarDispositivo(String direccion)
    {
        Log.e("AcuaDroid_BT", "Conectando a : "+direccion);
        if(servicio != null)
        {
            BluetoothDevice dispositivoRemoto = bAdapter.getRemoteDevice(direccion);
            servicio.solicitarConexion(dispositivoRemoto);
            this.ultimoDispositivo = dispositivoRemoto;
        }
    }

    public void enviarMensaje(String mensaje)
    {
        if(servicio.getEstado() != BluetoothService.ESTADO_CONECTADO)
        {
            Log.e("AcuaDroid_BT", "Error de Conexión al BT");
            return;
        }

        if(mensaje.length() > 0)
        {
            byte[] buffer = mensaje.getBytes();
            servicio.enviar(buffer);
        }
    }

}