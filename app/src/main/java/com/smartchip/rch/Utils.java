package com.smartchip.rch;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;

/**
 * Created by Ishrat Khan on 2/16/2015.
 */
public class Utils {

   // static final String TAG = ExceptionHandler.class.getSimpleName();
    static final String UNKNOWN = "unknown";

   // static String sStoragePath = null;
    static String sAppVersionName = UNKNOWN;
    static String sAppVersionCode = UNKNOWN;
    static String sAppPackage = UNKNOWN;
    static String sDeviceModel = UNKNOWN;
    static String sAndroidVersion = UNKNOWN;

    public static void register(Context context) {

        // Get information about the Package
        final PackageManager pm = context.getPackageManager();
        try {
            // Get package informations
            final PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);

            // Get application informations
            sAppVersionName = pi.versionName;
            sAppVersionCode = String.valueOf(pi.versionCode);
            sAppPackage = pi.packageName;

            // Get device informations
            sDeviceModel = android.os.Build.MODEL;
            sAndroidVersion = String.valueOf(android.os.Build.VERSION.SDK_INT);
        } catch (PackageManager.NameNotFoundException e) {
            //Log.e(TAG, "Impossible to grab application informations", e);
        }

        // Manage Uncaught Exception Handler
        final Thread.UncaughtExceptionHandler currentHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (currentHandler != null) {
            //Log.d(TAG, "current handler class=" + currentHandler.getClass().getName());
        }
        if (!(currentHandler instanceof RuntimeCrashHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new RuntimeCrashHandler(context,currentHandler));
        }

        // Send stored exception
        sendStoredStackTraces();
    }


    public static void sendStoredStackTraces() {
        new Thread() {
            @Override
            public void run() {
                try {
                    // Open stored path
                  //  Log.d(TAG, "Looking for exceptions in: " + sStoragePath);
                    final File dir = new File(Config.STORAGE_PATH + "/");
                    dir.mkdir();

                    // Search files
                    final FilenameFilter filter = new FilenameFilter() {
                        public boolean accept(File dir, String name) {
                            return name.endsWith(".stacktrace");
                        }
                    };

                    final String[] exceptionList = dir.list(filter);
                   // Log.d(TAG, "Found " + exceptionList.length + " stacktrace(s)");

                    // Send
                    for (int i = 0; i < exceptionList.length; i++) {
                        final String filePath = Config.STORAGE_PATH + "/" + exceptionList[i];
                        final String version = exceptionList[i].split("-")[0];
                       // Log.d(TAG, "Stacktrace in file '" + filePath + "' belongs to version " + version);
                        final StringBuilder contents = new StringBuilder();
                        final BufferedReader input = new BufferedReader(new FileReader(filePath));
                        String line = null;
                        String appPackage = null;
                        String appVersionName = null;
                        String deviceModel = null;
                        String androidVersion = null;
                        while ((line = input.readLine()) != null) {
                            if (appPackage == null) {
                                appPackage = line;
                                continue;
                            } else if (appVersionName == null) {
                                appVersionName = line;
                                continue;
                            } else if (deviceModel == null) {
                                deviceModel = line;
                                continue;
                            } else if (androidVersion == null) {
                                androidVersion = line;
                                continue;
                            }
                            contents.append(line);
                            contents.append(System.getProperty("line.separator"));
                        }
                        input.close();
                        String stacktrace = contents.toString();

                     /*Send report to server...Write code here...*/

                    }

                } catch (Exception e) {
                   // Log.w(TAG, "Impossible to send stored exception");
                }
            }
        }.start();
    }
}
