package com.demoapp.hdfcdemo.newDemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.demoapp.hdfcdemo.R;
import com.google.android.material.textfield.TextInputEditText;
import com.zabaan.sdk.AssistantInteractionListener;
import com.zabaan.sdk.AssistantStateListener;
import com.zabaan.sdk.AssistantUi;
import com.zabaan.sdk.Zabaan;
import com.zabaan.common.ZabaanLanguages;
import com.zabaan.common.ZabaanSpeakable;
import com.zabaan.sdk.internal.interaction.ViewInteractionRequest;
import com.zabaan.common.views.AnimationClickNormal;
import com.zabaan.common.views.AssistantFingerAnimation;
import com.zabaan.common.views.CustomAnimation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText etCustomerId, etPassword;
    TextView btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etCustomerId = findViewById(R.id.et_customer_id);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(etCustomerId.getText())
                    && etCustomerId.getText().toString().equalsIgnoreCase("demouser")
                    && !TextUtils.isEmpty(etPassword.getText())
                    && etPassword.getText().toString().equalsIgnoreCase("1111")) {
                startActivity(new Intent(LoginActivity.this, NewDashboardActivity.class));
                finish();
            } else {
                try {
                    ViewInteractionRequest request = new ViewInteractionRequest.Builder()
                            .setViewId(R.id.tv_forgot_pass_new)
                            .setState("IncorrectPassword")
                            .build();
                    Zabaan.getInstance().playInteraction(request);
                } catch (Exception nex) {
                    Log.e("Login", "Error playing interaction");
                }
            }
        });

        etCustomerId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Zabaan.getInstance().stopZabaanInteraction();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateLoginButton();
            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Zabaan.getInstance().stopZabaanInteraction();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateLoginButton();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Zabaan.getInstance().show(this);
        Zabaan.getInstance().setAssistantStateListener(new AssistantStateListener() {
            @Override
            public void assistantAvailable() {
                Zabaan.getInstance().promptIdleAnimation(5000);
                Zabaan.getInstance().setDefaultFingerAnimation(AnimationClickNormal.INSTANCE);
                if (PreferenceManager.getDefaultSharedPreferences(LoginActivity.this)
                        .getBoolean("isFirstTime", false)) {
                    Zabaan.getInstance().setCurrentState("NORMALASSISTANTCLICK");
                } else {
                    PreferenceManager.getDefaultSharedPreferences(LoginActivity.this)
                            .edit()
                            .putBoolean("isFirstTime", true).apply();
                    Zabaan.getInstance().setCurrentState("FIRSTTIMEUSER");
                }
            }

            @Override
            public void assistantClicked() {

            }

            @Override
            public void assistantLanguageNotSupported() {

            }
        });

        Zabaan.getInstance().setAssistantInteractionListener(new AssistantInteractionListener() {


            @Override
            public void assistantSpeechDataDownloaded(@NotNull ZabaanLanguages zabaanLanguage) {

            }

            @Override
            public void assistantSpeechDataDownloadError() {

            }

            @Override
            public void assistantSpeechStart(@NotNull ZabaanSpeakable zabaanSpeakable) {
                if (zabaanSpeakable.getState().equalsIgnoreCase("FIRSTTIMEUSER"))
                    Zabaan.getInstance().setCurrentState("NORMALASSISTANTCLICK");
            }

            @Override
            public void assistantSpeechEnd(@NotNull ZabaanSpeakable zabaanSpeakable, boolean isInterrupted) {

            }

            @Override
            public void assistantSpeechError(@NotNull ZabaanSpeakable zabaanSpeakable, @Nullable Exception e) {

            }
        });

        Zabaan.getInstance().setScreenName("LoginActivity");
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        Zabaan.getInstance().stopIdleAnimation();
    }

    void updateLoginButton() {
        String username, pass;

        username = etCustomerId.getText().toString();
        pass = etPassword.getText().toString();

        if (username != null && pass != null && username.length() >= 4 && pass.length() >= 4) {
            btnLogin.setBackgroundResource(R.drawable.yellow_button);
            btnLogin.setEnabled(true);
        } else {
            btnLogin.setBackgroundResource(R.drawable.grey_button);
            btnLogin.setEnabled(false);
        }
    }

    public Uri getRawUri(String filename) {
        int aVal = LoginActivity.this.getResources().getIdentifier(filename, "raw", this.getPackageName());
        return Uri.parse(String.valueOf(aVal));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Zabaan.getInstance().clearZabaan();
    }
}