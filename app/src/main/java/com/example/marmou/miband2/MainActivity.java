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


import java.util.UUID;

import static com.example.marmou.miband2.BLEMiBand2Helper.CONTADOR;

public class MainActivity extends AppCompatActivity implements BLEMiBand2Helper.BLEAction{

    public static final String LOG_TAG = "Mario";

    public EditText texto;
    public static String MAC;

    Handler handler = new Handler(Looper.getMainLooper());
    BLEMiBand2Helper helper = null;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        texto = (EditText) findViewById(R.id.texto);
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
    public void btnEnviar(View view){

       enviarTexto();
    }
    public void enviarTexto(){
        String value=texto.getText().toString();
        helper.sendSms(value);
    }

    /**
     * Enviar texto como llamada
     * @param view
     */
    public void llamar (View view){
        String value=texto.getText().toString();
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
     */
    public void functionButton() {
        Toast.makeText(MainActivity.this, "Ok, Recibido", Toast.LENGTH_LONG).show();

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