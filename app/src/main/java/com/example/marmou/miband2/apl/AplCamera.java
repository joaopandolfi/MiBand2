package com.example.marmou.miband2.apl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;

/**
 * Created by joaop on 02/02/2018.
 */

public class AplCamera {
    static final int REQUEST_IMAGE_CAPTURE = 1;

    //input keyevent 27

    public static void dispatchTakePictureIntent(Activity a) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(a.getPackageManager()) != null) {
            a.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
}
