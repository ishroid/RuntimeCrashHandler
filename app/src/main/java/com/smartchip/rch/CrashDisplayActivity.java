package com.smartchip.rch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Ishrat Khan on 2/16/2015.
 */

public class CrashDisplayActivity extends Activity{
    private TextView textViewCont;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**Config*/
        Config.IS_SEND_EMAIL = false;
        Config.IS_WRITE_TO_SDCARD = true;
        Config.STORAGE_PATH = Environment.getExternalStorageDirectory() + "/" + "zxy";
        Config.EMAIL_ID = "ishrat.khan@morpho.com";
        Config.EMAIL_HEADER = "\n Please describe what you were doing when this crash happended: \n";

        /**Register exception receiver*/
        Utils.register(this);


        /**Exception generate here to test code*/
        textViewCont.setText("Exception");

    }
}
