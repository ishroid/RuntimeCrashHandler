package com.smartchip.rch;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Random;

/**
 * Created by Ishrat Khan on 2/16/2015.
 */

public class RuntimeCrashHandler  implements Thread.UncaughtExceptionHandler {

    private static final String  TAG=RuntimeCrashHandler.class.getSimpleName();
    private Context ctx;
	private Thread.UncaughtExceptionHandler defaultExceptionHandler;

    public RuntimeCrashHandler(Context aCtx,Thread.UncaughtExceptionHandler pDefaultExceptionHandler) {
        ctx = aCtx;
        defaultExceptionHandler = pDefaultExceptionHandler;
    }

    @Override
    public void uncaughtException(final Thread t, final Throwable e) {
        // Debug
        Log.w(TAG, "Thread crashed: "+ t.hashCode());
        Log.w(TAG, "Uncaught exception receive", e);

        if(Config.IS_WRITE_TO_SDCARD){
            // Submit StackTraces
            new Thread() {
                @Override
                public void run() {
                    // Extract StackTraces
                    final Writer result = new StringWriter();
                    final PrintWriter printWriter = new PrintWriter(result);
                    e.printStackTrace(printWriter);

                    // Store exception
                    Log.d(TAG, "Try to store in local");
                    try {
                        final Random generator = new Random();
                        final int random = generator.nextInt(99999);
                        final String filename = Config.FILE_PREFIX+Utils.sAppVersionName +"-"+ Integer.toString(random);
                        final String path =Config.STORAGE_PATH +"/"+ filename +".stacktrace";

                        Log.d(TAG, "Writing unhandled exception to: "+ path);

                        BufferedWriter bos = new BufferedWriter(new FileWriter(path));
                        bos.write(Utils.sAppPackage + "\n");
                        bos.write(Utils.sAppVersionName + "\n");
                        bos.write(Utils.sDeviceModel + "\n");
                        bos.write(Utils.sAndroidVersion + "\n");
                        bos.write(result.toString());
                        bos.flush();
                        // Close up everything
                        bos.close();
                    } catch (Exception e) {
                        Log.w(TAG, "Impossible to store this crash", e);
                    }
                }
            }.start();
        }

        if(Config.IS_SEND_EMAIL){
            sendEmailStackTrace(e);
        }

        // Call original handler
        defaultExceptionHandler.uncaughtException(t, e);
    }

    public void sendEmailStackTrace(Throwable ex) {

        StringWriter exception = new StringWriter();
        ex.printStackTrace(new PrintWriter(exception));

        Intent i = new Intent(android.content.Intent.ACTION_SEND);
        i.setType("plain/text");
        i.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{Config.EMAIL_ID});
        i.putExtra(android.content.Intent.EXTRA_SUBJECT, Utils.sAppPackage);
        i.putExtra(android.content.Intent.EXTRA_TEXT, exception.toString() + Config.EMAIL_HEADER);
        ctx.startActivity(Intent.createChooser(i, "Send e-mail"));
    }
}
