package com.example.marmou.miband2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/**
 * Created by marmou on 12/06/2017.
 */

public class BLEMiBand2Helper {

    public static final String TAG = "Mario-MI-2";

    private Context myContext = null;
    private android.os.Handler myHandler = null;

    private BluetoothDevice activeDevice = null; // The mi band
    private boolean isConnectedToGatt = false; // the gatt connection
    private BluetoothGatt myGatBand = null;
    private BluetoothGattCharacteristic characteristic;


    private BLEMiBand2Helper() {
    }

    public BLEMiBand2Helper(Context context, android.os.Handler handler) {
        myContext = context;
        myHandler = handler;
    }

    public boolean isConnected() {
        return isConnectedToGatt;
    }

    /* =========  Handling Initializing  ============== */


    public void connect() {
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            return;
        }
        isConnectedToGatt = false;

        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        try {
            activeDevice = mBluetoothAdapter.getRemoteDevice("D4:4E:7D:8E:26:28");
        } catch (Exception e) {
            activeDevice = null;
            e.printStackTrace();
        }
        if (activeDevice != null) {
            if (!connectGatt()) {
                isConnectedToGatt = false;
                Log.w(TAG, "No se puede conectar con mi Band 2");
            }
        }
    }

    public boolean connectGatt() {
        if (activeDevice == null)
            return false;

        // closeGatt();//older gatt??
        myGatBand = activeDevice.connectGatt(myContext, false, myGattCallback);//false

        if (myGatBand == null)
            return false;

        return myGatBand.connect();
    }


    public void DisconnectGatt() {
        if (myGatBand != null && isConnectedToGatt) {
            myHandler.post(new Runnable() {
                @Override
                public void run() {
                    myGatBand.disconnect();
                    myGatBand.close();
                    myGatBand = null;
                    isConnectedToGatt = false;
                }
            });
        }
    }

    private BluetoothGattCallback myGattCallback = new BluetoothGattCallback() {
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                characteristic = gatt.getService(Consts.UUID_SERVICE_1802).getCharacteristic(Consts.UUID_CHARACTERISTIC_2A06);

                isConnectedToGatt = true;
            }
            Log.d(TAG, "Service discovered with status " + status);
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.d(TAG, "Gatt state: connected");
                    gatt.discoverServices();
                    isConnectedToGatt = true;
                    raiseonConnect();
                    break;
                default:
                    Log.d(TAG, "Gatt state: not connected");
                    isConnectedToGatt = false;
                    raiseonDisconnect();
                    break;
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "Write successful: " + Arrays.toString(characteristic.getValue()));
            raiseonWrite(gatt, characteristic, status);
            super.onCharacteristicWrite(gatt, characteristic, status);


        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "Read successful: " + Arrays.toString(characteristic.getValue()));
            raiseonRead(gatt, characteristic, status);
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.d(TAG, " - Notifiaction UUID: " + characteristic.getUuid().toString());
            Log.d(TAG, " - Notifiaction value: " + Arrays.toString(characteristic.getValue()));
            raiseonNotification(gatt, characteristic);
            super.onCharacteristicChanged(gatt, characteristic);
        }


    };
    /*
        Método que envia vibración y icono de mensaje con corazon en el medio
     */
    public void sendActions(Byte action){
        if (!isConnectedToGatt){
            connect();
        }
        try {
             characteristic.setValue(new byte[]{action,1});//vibrar o icono mensaje con corazon

            myGatBand.writeCharacteristic(characteristic);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /*
        Método para enviar datos tanto numeros como maximo una letra, pero la lectura siempre es en forma de números
    */
    public void sendData(String value, Byte func, Byte action) {
        if (!isConnectedToGatt) {
            connect();
        }
        try {
                if(isNumeric(value)==true){

                    int mens=Integer.parseInt(value);

                            /*Forma original*/
                            characteristic.setValue(new byte[]{action, func, (byte) (mens & 0xFF), (byte) ((mens >> 8) & 0xFF)});


                }else{

                    byte[] actions={action,func};
                    byte[] mens =value.getBytes(Charset.forName("UTF-8"));
                    byte[] cero={0};
                    byte[] union=new byte[actions.length+mens.length+cero.length];

                    //copy actions into start of union
                    System.arraycopy(actions,0,union,0,actions.length);
                    System.arraycopy(mens,0,union,actions.length,mens.length);
                    //copy cero into end of union
                    System.arraycopy(cero,0,union,mens.length+actions.length,cero.length);

                    characteristic.setValue(union);
                }
            myGatBand.writeCharacteristic(characteristic);

            /* characteristic.setValue(new byte[]{-3,func, mens, 0 });
            characteristic.setValue(value.getBytes(Charset.defaultCharset().forName("Utf-8"))); Esto envia pero no muestra nada en la pulsera
           Esto envia bytes tanto con numeros o letras pero la pulsera no muestra nada.
            writeData(Consts.UUID_SERVICE_MIBAND_SERVICE,Consts.UUID_CHARACTERISTIC_2A06,value.getBytes(Charset.defaultCharset().forName("Utf-8")));*/


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface BLEAction {
        void onDisconnect();

        void onConnect();

        void onRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);

        void onWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);

        void onNotification(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);
    }

    /* =========  Handling Events  ============== */

    private ArrayList<BLEAction> listeners = new ArrayList<BLEAction>();

    public void addListener(BLEAction toAdd) {
        listeners.add(toAdd);
    }

    public void removeListener(BLEAction toDel) {
        listeners.remove(toDel);
    }

    public void raiseonNotification(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        // Notify everybody that may be interested.
        for (BLEAction listener : listeners)
            listener.onNotification(gatt, characteristic);
    }

    public void raiseonRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        // Notify everybody that may be interested.
        for (BLEAction listener : listeners)
            listener.onRead(gatt, characteristic, status);
    }

    public void raiseonWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        // Notify everybody that may be interested.
        for (BLEAction listener : listeners)
            listener.onWrite(gatt, characteristic, status);
    }

    public void raiseonDisconnect() {
        // Notify everybody that may be interested.
        for (BLEAction listener : listeners)
            listener.onDisconnect();
    }

    public void raiseonConnect() {
        // Notify everybody that may be interested.
        for (BLEAction listener : listeners)
            listener.onConnect();
    }


    /* =========  Handling Data  ============== */

    public void readData(UUID service, UUID Characteristics) {
        if (!isConnectedToGatt || myGatBand == null) {
            Log.d(TAG, "Cant read from BLE, not initialized.");
            return;
        }

        Log.d(TAG, "* Getting gatt service, UUID:" + service.toString());
        BluetoothGattService myGatService =
                myGatBand.getService(service /*Consts.UUID_SERVICE_GENERIC*/);
        if (myGatService != null) {
            Log.d(TAG, "* Getting gatt Characteristic. UUID: " + Characteristics.toString());

            BluetoothGattCharacteristic myGatChar
                    = myGatService.getCharacteristic(Characteristics /*Consts.UUID_CHARACTERISTIC_DEVICE_NAME*/);
            if (myGatChar != null) {
                Log.d(TAG, "* Reading data");

                boolean status = myGatBand.readCharacteristic(myGatChar);
                Log.d(TAG, "* Read status :" + status);
            }
        }
    }

    public void writeData(UUID service, UUID Characteristics, byte[] data) {
        if (!isConnectedToGatt || myGatBand == null) {
            Log.d(TAG, "Cant read from BLE, not initialized.");
            return;
        }

        Log.d(TAG, "* Getting gatt service, UUID:" + service.toString());
        BluetoothGattService myGatService =
                myGatBand.getService(service /*Consts.UUID_SERVICE_HEARTBEAT*/);
        if (myGatService != null) {
            Log.d(TAG, "* Getting gatt Characteristic. UUID: " + Characteristics.toString());

            BluetoothGattCharacteristic myGatChar
                    = myGatService.getCharacteristic(Characteristics /*Consts.UUID_START_HEARTRATE_CONTROL_POINT*/);
            if (myGatChar != null) {
                Log.d(TAG, "* Writing trigger");
                myGatChar.setValue(data /*Consts.BYTE_NEW_HEART_RATE_SCAN*/);

                boolean status = myGatBand.writeCharacteristic(myGatChar);
                Log.d(TAG, "* Writting trigger status :" + status);
            }
        }
    }

    public void getNotifications(UUID service, UUID Characteristics) {
        if (!isConnectedToGatt || myGatBand == null) {
            Log.d(TAG, "Cant get notifications from BLE, not initialized.");
            return;
        }

        Log.d(TAG, "* Getting gatt service, UUID:" + service.toString());
        BluetoothGattService myGatService =
                myGatBand.getService(service/*Consts.UUID_SERVICE_MIBAND_SERVICE*/);
        if (myGatService != null) {
            Log.d(TAG, "* Getting gatt Characteristic. UUID: " + Characteristics.toString());

            BluetoothGattCharacteristic myGatChar
                    = myGatService.getCharacteristic(Characteristics/*Consts.UUID_BUTTON_TOUCH*/);
            if (myGatChar != null) {
                Log.d(TAG, "* Statring listening");

                // second parametes is for starting\stopping the listener.
                boolean status = myGatBand.setCharacteristicNotification(myGatChar, true);
                Log.d(TAG, "* Set notification status :" + status);
            }
        }
    }

    /**
     * Get notification but also set descriptor to Enable notification. You need to wait couple of
     * seconds before you could use it (at least in the mi band 2)
     *
     * @param service
     * @param Characteristics
     */
    public void getNotificationsWithDescriptor(UUID service, UUID Characteristics, UUID Descriptor) {
        if (!isConnectedToGatt || myGatBand == null) {
            Log.d(TAG, "Cant get notifications from BLE, not initialized.");
            return;
        }

        Log.d(TAG, "* Getting gatt service, UUID:" + service.toString());
        BluetoothGattService myGatService =
                myGatBand.getService(service/*Consts.UUID_SERVICE_MIBAND_SERVICE*/);
        if (myGatService != null) {
            Log.d(TAG, "* Getting gatt Characteristic. UUID: " + Characteristics.toString());

            BluetoothGattCharacteristic myGatChar
                    = myGatService.getCharacteristic(Characteristics/*Consts.UUID_BUTTON_TOUCH*/);
            if (myGatChar != null) {
                Log.d(TAG, "* Statring listening");

                // second parametes is for starting\stopping the listener.
                boolean status = myGatBand.setCharacteristicNotification(myGatChar, true);
                Log.d(TAG, "* Set notification status :" + status);

                BluetoothGattDescriptor myDescriptor
                        = myGatChar.getDescriptor(Descriptor/*Consts.UUID_DESCRIPTOR_UPDATE_NOTIFICATION*/);
                if (myDescriptor != null) {
                    Log.d(TAG, "Writing decriptor: " + Descriptor.toString());
                    myDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    status = myGatBand.writeDescriptor(myDescriptor);
                    Log.d(TAG, "Writing decriptors result: " + status);
                }
            }
        }
    }

    private static boolean isNumeric(String cadena){
        try {
            Integer.parseInt(cadena);
            return true;
        } catch (NumberFormatException nfe){
            return false;
        }
    }

}


