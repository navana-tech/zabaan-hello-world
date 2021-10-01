package com.demoapp.hdfcdemo;

import android.content.Context;

import androidx.multidex.MultiDexApplication;

import com.zabaan.sdk.InitializeParams;
import com.zabaan.sdk.Zabaan;

public class MyApplication extends MultiDexApplication {
    private static Context mContext;
    public static boolean isIntroPlayed = false;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        try {
            InitializeParams params = new InitializeParams.Builder()
                    .context(this)
                    .accessToken(BuildConfig.ACCESS_TOKEN)
                    .build();
            Zabaan.setDebug(true);
            Zabaan.setSandbox(true);
            Zabaan zabaan = Zabaan.init(params);
        } catch (Exception e) {
//            NavanaUtils.log('e', "Application", ""+e.getMessage());
        }
        /*ViewPump.init(ViewPump.builder()
                .addInterceptor(new CustomTextViewInterceptor())
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/mukta.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());*/
    }

    public static Context getAppContext() {
        return mContext;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
