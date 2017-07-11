package com.example.marmou.miband2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements BLEMiBand2Helper.BLEAction{

    public static final String LOG_TAG = "Mario";

    public EditText number;
    public EditText texto;
    public CheckBox check;
    public String value;
    public static String MAC;

    Handler handler = new Handler(Looper.getMainLooper());
    BLEMiBand2Helper helper = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        number = (EditText) findViewById(R.id.editNumber);
        texto = (EditText) findViewById(R.id.editText);
        check = (CheckBox) findViewById(R.id.checkBox);
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
    public void btnEnviar(View view)  throws InterruptedException{


        if(check.isChecked()){
            value=texto.getText().toString();
        }else{
            value = number.getText().toString();
        }
        helper.sendData(value, (byte) 2, Consts.cronoYHearBeat);
    }
    public void btnEnvCro(View view)  throws InterruptedException{


        if(check.isChecked()){
            value=texto.getText().toString();
        }else{
            value = number.getText().toString();
        }
        helper.sendData(value, (byte) 1, Consts.cronoYHearBeat);
    }
    public void btnIcnMens(View view) throws InterruptedException{


        if(check.isChecked()){
            value=texto.getText().toString();
        }else{
            value = number.getText().toString();
        }
        helper.sendData(value, (byte) 1, Consts.mensaje);

    }
    public void btnLlamar(View view) throws InterruptedException{


        if(check.isChecked()){
            value=texto.getText().toString();
        }else{
            value = number.getText().toString();
        }
        helper.sendData(value, (byte) 1, Consts.llamada);
    }
    public void vibrar(View view) throws InterruptedException, IOException {

         helper.sendActions(Consts.vibrar);
    }
    public void iconCor(View view) throws InterruptedException{
                helper.sendActions(Consts.icon);
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

                    Toast.makeText(MainActivity.this,"Boton pulsado", Toast.LENGTH_LONG).show();
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

    public void functionButton(){

        CheckBox check = (CheckBox) findViewById(R.id.checkBox);
        if(check.isChecked()){
            check.setChecked(false);
        }else{
            check.setChecked(true);
        }}





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