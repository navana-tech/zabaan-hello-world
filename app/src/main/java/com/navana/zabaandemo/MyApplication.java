package com.navana.zabaandemo;

import android.app.Application;

import com.zabaan.sdk.InitializeParams;
import com.zabaan.sdk.Zabaan;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJmcmVzaCI6ZmFsc2UsImlhdCI6MTYyNzYyMjA0NiwianRpIjoiNGJiNzU2MzQtMDE1NS00ODI0LTg0MTAtODk1YjMxMTA2NzRiIiwidHlwZSI6ImFjY2VzcyIsInN1YiI6IjE3NzBhMTJmLWM3ODItNDlmZi1iMDNiLWZmNTM0OTEwODEzNyIsIm5iZiI6MTYyNzYyMjA0NiwiZGVwbG95bWVudF9pZCI6IjkwMmUwNWI1LWMzODMtNGNiYi1hZjQzLTc0MTQ5NmRjYzk0MCIsInByb2plY3RfaWQiOiJhYWY2YWUwNi1hMTFkLTQwM2YtOTljNC0zY2FiY2M5MThjN2YiLCJyb2xlIjoic2RrX3VzZXIifQ.vIgHRMDwdwmsQj915SaFN4oNJW7y-nfxqmz8891RXUqFxuTsejer9N4g6r8mRkDS3EY0uD46PAdmHtXvoDc3OQ";
        try {
            InitializeParams params = new InitializeParams.Builder()
                    .context(this)
                    .accessToken(token)
                    .build();
            Zabaan.setDebug(true);
            Zabaan.initialize(params);
        } catch (Exception e) {
            //Handle the exception here.
        }
    }
}
