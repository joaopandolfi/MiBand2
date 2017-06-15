package com.example.marmou.miband2;

import java.util.UUID;

/**
 * Created by marmou on 12/06/2017.
 */

public class Consts {//this is common for all BTLE devices. see http://stackoverflow.com/questions/18699251/finding-out-android-bluetooth-le-gatt-profiles
    public static final String BASE_UUID = "0000%s-0000-1000-8000-00805f9b34fb";

    public static final UUID UUID_SERVICE_GENERIC = UUID.fromString(String.format(BASE_UUID, "1800"));
    public static final UUID UUID_SERVICE_1802 = UUID.fromString(String.format(BASE_UUID, "1802"));
    public static final UUID UUID_SERVICE_MIBAND_SERVICE = UUID.fromString(String.format(BASE_UUID, "FEE0"));
    public static final UUID UUID_SERVICE_MIBAND2_SERVICE = UUID.fromString(String.format(BASE_UUID, "FEE1"));
    public static final UUID UUID_SERVICE_HEARTBEAT = UUID.fromString(String.format(BASE_UUID, "180D"));
    public static final UUID UUID_CHARACTERISTIC_2A06 = UUID.fromString(String.format(BASE_UUID, "2A06"));//caracteristica nivel de alerta
    // General service
    public static final UUID UUID_CHARACTERISTIC_DEVICE_NAME = UUID.fromString(String.format(BASE_UUID, "2A00"));

    public static final UUID UUID_UNKNOWN_CHARACTERISTIC0 = UUID.fromString("00000000-0000-3512-2118-0009af100700");
    public static final UUID UUID_UNKNOWN_CHARACTERISTIC1 = UUID.fromString("00000001-0000-3512-2118-0009af100700");
    public static final UUID UUID_UNKNOWN_CHARACTERISTIC2 = UUID.fromString("00000002-0000-3512-2118-0009af100700");
    /**
     * Alarms, Display and other configuration.
     */
    public static final UUID UUID_CHARACTERISTIC_3_CONFIGURATION = UUID.fromString("00000003-0000-3512-2118-0009af100700");
    public static final UUID UUID_UNKNOWN_CHARACTERISTIC4 = UUID.fromString("00000004-0000-3512-2118-0009af100700");
    public static final UUID UUID_CHARACTERISTIC_5_ACTIVITY_DATA = UUID.fromString("00000005-0000-3512-2118-0009af100700");
    public static final UUID UUID_CHARACTERISTIC_6_BATTERY_INFO = UUID.fromString("00000006-0000-3512-2118-0009af100700");
    public static final UUID UUID_CHARACTERISTIC_7_REALTIME_STEPS = UUID.fromString("00000007-0000-3512-2118-0009af100700");
    public static final UUID UUID_UNKNOWN_CHARACTERISTIC8 = UUID.fromString("00000008-0000-3512-2118-0009af100700");
    // service uuid fee1
    public static final UUID UUID_CHARACTERISTIC_AUTH = UUID.fromString("00000009-0000-3512-2118-0009af100700");
    public static final UUID UUID_BUTTON_TOUCH = UUID.fromString("00000010-0000-3512-2118-0009af100700");

    //Heart beat service
    public static final UUID UUID_START_HEARTRATE_CONTROL_POINT = UUID.fromString("00002a39-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_NOTIFICATION_HEARTRATE = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");
    public static final byte[] BYTE_LAST_HEART_RATE_SCAN = {21, 1, 1};
    public static final byte[] BYTE_NEW_HEART_RATE_SCAN = {21, 2, 1};
    public static final UUID UUID_DESCRIPTOR_UPDATE_NOTIFICATION = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    //Actions
    public static final Byte mensaje=1;
    public static final Byte llamada=2;
    public static final Byte vibrar=3;
    public static final Byte icon=-2;
    public static final Byte cronoYHearBeat=-3;

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        //http://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}