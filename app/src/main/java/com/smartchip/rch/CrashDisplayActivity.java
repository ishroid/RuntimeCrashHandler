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
    private Button buttonSend;

    private String exceptionContent;
    private final String APP_NAME = "UncaughtExceptionHandlerMy";
    private final String EMAIL_HEADER = "\n Please describe what you were doing when this crash happended: \n";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Config.IS_SEND_EMAIL=false;
        Config.IS_WRITE_TO_SDCARD=true;
        Config.STORAGE_PATH= Environment.getExternalStorageDirectory()+"/"+"zxy";
        Utils.register(this);

        exceptionContent = getIntent().getStringExtra("err");
        textViewCont.setText(exceptionContent);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(android.content.Intent.ACTION_SEND);
                i.setType("plain/text");
                i.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"toci1@abv.bg"});
                i.putExtra(android.content.Intent.EXTRA_SUBJECT, APP_NAME);
                i.putExtra(android.content.Intent.EXTRA_TEXT, exceptionContent + EMAIL_HEADER);
                startActivity(Intent.createChooser(i, "Send e-mail"));
            }
        });
    }
}
