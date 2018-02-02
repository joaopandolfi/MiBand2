package com.example.marmou.miband2.apl;

import android.view.KeyEvent;

import java.io.IOException;

/**
 * Created by joaop on 02/02/2018.
 */

public class AplKeyCommand {
    public static void sendKeyCommand(String keyCommand){
        try
        {
            //String keyCommand = "input keyevent " + KeyEvent.KEYCODE_MENU;
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec(keyCommand);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void takePhoto(){
        AplKeyCommand.sendKeyCommand("input keyevent "+ KeyEvent.KEYCODE_VOLUME_DOWN);
    }
}
