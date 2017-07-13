package com.example.marmou.miband2;


import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import android.widget.EditText;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.UUID;



public class MainActivity extends AppCompatActivity implements BLEMiBand2Helper.BLEAction{

    public static final String LOG_TAG = "Mario";

    public EditText textoLlamada;
    public EditText textoMensaje;
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

        textoLlamada = (EditText) findViewById(R.id.textoLlamada);
        textoMensaje = (EditText) findViewById(R.id.mensaje);


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



    public void btnRun(View view) {
        EditText mac=(EditText) findViewById(R.id.txMac);
        MAC=mac.getText().toString();
        helper.connect();

    }
    public void btnDescon(View view){
        helper.DisconnectGatt();
    }



    public void getTouchNotifications() {
        helper.getNotifications(
                Consts.UUID_SERVICE_MIBAND_SERVICE,
                Consts.UUID_BUTTON_TOUCH);
        Toast.makeText(MainActivity.this, "Botón activado", Toast.LENGTH_SHORT).show();
    }

    public void btnTest(View view) throws InterruptedException {
        getTouchNotifications();
    }

    /**
     * Enviar texto como sms
     * @param view
     */
    public void btnEnviar(View view) throws InterruptedException {

        /*
        ANOTACION: LLAMAR AQUI A SENDCALL("MANSAJE") PARA ASEGURARNOS QUE EL MENSAJE VA A SER LEIDO
        Y AL TOCAR EL BOTON QUE SE MANDE EL SMS
         */

        enviarTexto();
    }
    public void enviarTexto() throws InterruptedException {

        String value=textoMensaje.getText().toString();
        helper.alerta();
        java.lang.Thread.sleep(2000);

        añadirSplit(value);

        helper.sendSms(part1);
        part1="";
        if(part2.isEmpty()){
            POS=0;
        }else{
            POS=1;
        }

    }



    /**
     * Enviar texto como llamada
     * @param view
     */
    public void llamar (View view){
        String value=textoLlamada.getText().toString();
        if(value.length()>18){
            String cortado=value.substring(0,18);
            helper.sendCall(cortado);
            Toast.makeText(MainActivity.this, "No se puede enviar el texto entero en forma de llamada,\n" +
                    "pruebe a hacerlo en forma de sms. Se ha enviado: "+cortado, Toast.LENGTH_LONG).show();
        }
        else{
            helper.sendCall(value);
        }
    }


    /* ===========  EVENTS (background thread) =============== */

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onConnect() {

    }

    @Override
    public void onRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

    }

    @Override
    public void onWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

    }

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
     * Funcionalidad que se le dará al botón
     *
     */
    public void functionButton() {
        if(POS==0){
            Toast.makeText(MainActivity.this, "Ok, Recibido todo el mensaje", Toast.LENGTH_LONG).show();
        }

        enviarPartSms();


    }

    /**
     * Metodo para recibir las siguinetes partes del sms y decir okay recibido
     */
    public void enviarPartSms(){
        switch (POS){
            case 1:
                Toast.makeText(MainActivity.this, "Ok, Recibida primera parte", Toast.LENGTH_LONG).show();
                helper.sendSms(part2);
                part2="";
                POS=2;
                if(part3.isEmpty()){
                    POS=0;
                }
                break;

            case 2:
                Toast.makeText(MainActivity.this, "Ok, Recibida segunda parte", Toast.LENGTH_LONG).show();
                helper.sendSms(part3);
                part3="";
                POS=0;
                break;

        }
    }

    /**
     * Sele añade "_" cada 15 caracteres para poder utilizar split para separar el sms y se le añade -> cuando hay más partes del sms por leer
     * NOTA: LA XIAOMI MIBAND2 SOLO ADMITE 18 CARACTERES POR CADA MENSAJE
     * @param mensaje sms que queremos mandar
     */
    public void añadirSplit(String mensaje){

        String textofin="";
        ArrayList<String> letras=  new ArrayList<>();

        for (int i=0;i<=mensaje.length();i++){
            try {
                letras.add(String.valueOf(mensaje.charAt(i)));
            }catch (Exception e){
                System.out.println("Salta exception pero lo almacena bien");

            }
        }

        for (int i=0;i<letras.size();i++) {
            if(i==15){
                letras.add(i,"->");
                letras.add(i+1,"_");
            }else if(i==30){
                letras.add(i,"->");
                letras.add(i+1,"_");
            }/*else if(i==45){
                letras.add(i,"->");
                letras.add(i+1,"_");
            }else if(i==60){
                letras.add(i,"->");
                letras.add(i+1,"_");
            }else if(i==75){
                letras.add(i,"->");
                letras.add(i+1,"_");
            }*/
        }

        for (String s : letras) {
            textofin+=s;
        }

        fragmentarSms(textofin);



    }

    /**
     * Metodo que fragmeta el sms en varios String para enviarlos por separado
     * @param textofin Texto final con los splits añadidos
     */
    public void fragmentarSms(String textofin){
        String[] parts = textofin.split("_");
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