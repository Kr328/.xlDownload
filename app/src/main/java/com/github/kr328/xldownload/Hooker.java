package com.github.kr328.xldownload;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Hooker implements IXposedHookLoadPackage {
    private static final String TAG = "XlDownload";
    private static final String TARGET_PACKAGE = "com.android.providers.downloads";
    private static final File TARGET_PATH = new File(Environment.getExternalStorageDirectory(), ".xlDownload").getAbsoluteFile();

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        Log.d(TAG, "Injected to process = " + lpparam.packageName);

        if (!TARGET_PACKAGE.equals(lpparam.packageName)) {
            return;
        }

        Log.d(TAG, "Hooking");

        Log.d(TAG, "Target path = " + TARGET_PATH);

        XposedHelpers.findAndHookMethod(File.class, "mkdirs", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                Log.d(TAG, "path = " + param.thisObject);

                final boolean isXlDownload = ((File) param.thisObject).getAbsoluteFile().equals(TARGET_PATH);
                if (isXlDownload) {
                    Log.d(TAG, "blocked");

                    param.setThrowable(new FileNotFoundException("blocked"));
                }
            }
        });

        Log.d(TAG, "Hooked");
    }
}
