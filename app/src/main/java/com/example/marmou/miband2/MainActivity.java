package com.example.marmou.miband2;


import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.EditText;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.UUID;



public class MainActivity extends AppCompatActivity implements BLEMiBand2Helper.BLEAction{

    public static final String LOG_TAG = "Mario";

    public EditText textCall;
    public EditText textSms;
    public String part1="";
    public String part2="";
    public String part3="";
   /* public String part4="";
    public String part5="";
    public String part6="";
    public String part7="";*/

    public static String MAC;

    static int POS=0;

    Handler handler = new Handler(Looper.getMainLooper());
    BLEMiBand2Helper helper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textCall = (EditText) findViewById(R.id.textoLlamada);
        textSms = (EditText) findViewById(R.id.mensaje);


        EditText mac=(EditText) findViewById(R.id.txMac);
        MAC=mac.getText().toString();

        helper = new BLEMiBand2Helper(MainActivity.this, handler);
        helper.addListener(this);


        // Setup Bluetooth:
        helper.connect();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        getTouchNotifications();

    }


    @Override
    protected void onDestroy() {
        if (helper != null)
            helper.DisconnectGatt();
        super.onDestroy();
    }


    /**
     * //button for Connect to miBand
     * @param view
     */
    public void btnRun(View view) {
        EditText mac=(EditText) findViewById(R.id.txMac);
        MAC=mac.getText().toString();
        helper.connect();

    }
    /**
     * button for Disconnect to miBand
     */
     public void btnDescon(View view){
        helper.DisconnectGatt();
    }


    /**
     * Get notifications of the button
     */
    public void getTouchNotifications() {
        helper.getNotifications(
                Consts.UUID_SERVICE_MIBAND_SERVICE,
                Consts.UUID_BUTTON_TOUCH);
        Toast.makeText(MainActivity.this, "Botao ativado", Toast.LENGTH_SHORT).show();
    }
    /**
     * button to collect notifications of the button
     */
    public void btnTest(View view) throws InterruptedException {
        getTouchNotifications();
    }

    /**
     * function btnEnviar, Send text as sms
     * @param view
     */
    public void btnEnviar(View view) throws InterruptedException {

        /*
        ANOTACION: LLAMAR AQUI A SENDCALL("MANSAJE") PARA ASEGURARNOS QUE EL MENSAJE VA A SER LEIDO
        Y AL TOCAR EL BOTON QUE SE MANDE EL SMS
         */

        sendText();
    }

    /**
     * Send text as sms, separating the message in 15 characters
     * @throws InterruptedException
     */
    public void sendText() throws InterruptedException {

        String value= textSms.getText().toString();
        helper.alerta();
        java.lang.Thread.sleep(2000);

        aadSplit(value);

        helper.sendSms(part1);
        part1="";
        if(part2.isEmpty()){
            POS=0;
        }else{
            POS=1;
        }

    }

    /**
     * Send text as call
     * @param view
     */
    public void call(View view){
        Log.v(LOG_TAG,"SACI");
        String value= textCall.getText().toString();
        if(value.length()>18){
            String cut=value.substring(0,18);
            Log.v(LOG_TAG,cut);
            helper.sendCall(cut);
            Toast.makeText(MainActivity.this, "No se puede enviar el texto entero en forma de llamada,\n" +
                    "pruebe a hacerlo en forma de sms. Se ha enviado: "+cut, Toast.LENGTH_LONG).show();
        }
        else{
            Log.v(LOG_TAG,value);
            helper.sendCall(value);
        }
    }


    /* ===========  EVENTS (background thread) =============== */

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onConnect() {
        Log.v(LOG_TAG,"CONNECTED");
    }

    @Override
    public void onRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        Log.v(LOG_TAG,"DISCONNECTED");
    }

    @Override
    public void onWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

    }

    /**
     * Notification from miBand, in this case from the button
     * @param gatt
     * @param characteristic
     */
    @Override
    public void onNotification(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        UUID alertUUID = characteristic.getUuid();
        if (alertUUID.equals(Consts.UUID_BUTTON_TOUCH)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try{
                        functionButton();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

        }
    }

    /**
     * Function of the Button from miBand2
     *
     */
    public void functionButton() {
        if(POS==0){
            Toast.makeText(MainActivity.this, "Ok, Mensagem toda recebida", Toast.LENGTH_LONG).show();
        }

        enviarPartSms();


    }

    /**
     *
     *Method to receive the following parts of the sms and say: "okay received"
     *send more than one sms
     */
    private void enviarPartSms(){
        switch (POS){
            case 1:
                Toast.makeText(MainActivity.this, "Ok, Recebida primera parte", Toast.LENGTH_LONG).show();
                helper.sendSms(part2);
                part2="";
                POS=2;
                if(part3.isEmpty()){
                    POS=0;
                }
                break;

            case 2:
                Toast.makeText(MainActivity.this, "Ok, Recebida segunda parte", Toast.LENGTH_LONG).show();
                helper.sendSms(part3);
                part3="";
                POS=0;
                break;

        }
    }

    /**
     * It is added "_" every 15 characters to be able to use split to separate the sms and it is added -> when there are more parts of the sms to read
     *  NOTE: THE XIAOMI MIBAND2 ONLY ALLOWS 18 CHARACTERS FOR EVERY MESSAGE
     * @param messaje sms
     */
    private void aadSplit(String messaje){

        String textfin="";
        ArrayList<String> letters=  new ArrayList<>();

        for (int i=0;i<=messaje.length();i++){
            try {
                letters.add(String.valueOf(messaje.charAt(i)));
            }catch (Exception e){
                System.out.println("Salta exception pero lo almacena bien");
            }
        }
//limited for 45 characteres
        for (int i=0;i<letters.size();i++) {
            if(i==15){
                letters.add(i,"->");
                letters.add(i+1,"_");
            }else if(i==30){
                letters.add(i,"->");
                letters.add(i+1,"_");
            }/*else if(i==45){
                letters.add(i,"->");
                letters.add(i+1,"_");
            }else if(i==60){
                letters.add(i,"->");
                letters.add(i+1,"_");
            }else if(i==75){
                letters.add(i,"->");
                letters.add(i+1,"_");
            }*/
        }

        for (String s : letters) {
            textfin+=s;
        }

        fragmentarSms(textfin);

    }

    /**
     * fragment the sms in parts to send them separately
     * @param textfin final text with the splits
     */
    private void fragmentarSms(String textfin){
        String[] parts = textfin.split("_");
        try{
            part1 = parts[0];
            part2 = parts[1];
            part3 = parts[2];
            /*part4 = parts[3];
            part5 = parts[4];
            part6 = parts[5];
            part7 = parts[6];*/

        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }
}



/*
Credit and thanks:
https://github.com/lwis/miband-notifier/
http://allmydroids.blogspot.co.il/2014/12/xiaomi-mi-band-ble-protocol-reverse.html
https://github.com/Freeyourgadget/Gadgetbridge
http://stackoverflow.com/questions/20043388/working-with-ble-android-4-3-how-to-write-characteristics
https://github.com/yonixw/mi-band-2
https://github.com/ZenGod16/unreademailsformiband
*/