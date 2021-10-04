package com.demoapp.hdfcdemo.newDemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.demoapp.hdfcdemo.R;
import com.zabaan.sdk.AssistantInteractionListener;
import com.zabaan.sdk.AssistantStateListener;
import com.zabaan.sdk.NavanaAssistantException;
import com.zabaan.sdk.Zabaan;
import com.zabaan.common.ZabaanLanguages;
import com.zabaan.common.ZabaanSpeakable;
import com.zabaan.sdk.internal.interaction.StateInteractionRequest;
import com.zabaan.sdk.internal.interaction.ViewInteractionRequest;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MpinActivity extends AppCompatActivity implements AssistantInteractionListener, AssistantStateListener {
    private static final String KEYPAD = "k";
    private static final String TICK = "t";

    Context mContext;
    ConstraintLayout dialPad;
    ConstraintLayout otpInput;
    ImageButton confirmTick;
    TextView tvPayNow;
    int mNumHolders;
    int mIndex = 0;
    ImageView imgLogout, imgHome;
    private boolean isCVAPlaying;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpin);
        initVariables();
        assignViews();
        attachListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        Zabaan.getInstance().show(this);
        Zabaan.getInstance().setScreenName("MPinActivity");
        Zabaan.getInstance().setAssistantStateListener(this);
        Zabaan.getInstance().setAssistantInteractionListener(this);
        //requestOTP(phoneNumber);
        while (mIndex > 0) {
            mIndex--;
            setCharacter("");
        }
        checkEnableConfirm();
        clearAnimation();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    public void clearAnimation() {

    }

    private void initVariables() {
        mContext = MpinActivity.this;
    }

    private void assignViews() {
        dialPad = findViewById(R.id.dialpad);
        otpInput = findViewById(R.id.mpin_input);
        mNumHolders = otpInput.getChildCount();
        confirmTick = findViewById(R.id.confirm_tick);
        imgLogout = findViewById(R.id.img_logout);
        imgHome = findViewById(R.id.img_home);
        tvPayNow = findViewById(R.id.tv_pay_now);
    }


    private void attachListeners() {
        for (int i = 0; i < dialPad.getChildCount(); i++) {
            if ("backspace".equals(dialPad.getChildAt(i).getTag())) {
                dialPad.getChildAt(i).setOnClickListener(deleteCharacterListener);
            } else if ("confirm".equals(dialPad.getChildAt(i).getTag())) {
                dialPad.getChildAt(i).setOnClickListener(goToNextFragmentListener);
            } else {
                dialPad.getChildAt(i).setOnClickListener(addCharacterListener);
            }
        }

        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MpinActivity.this, LoginActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        });

        imgHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvPayNow.setOnClickListener(goToNextFragmentListener);
    }

    View.OnClickListener goToNextFragmentListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String mPinInputted = getNumbersFromInputField();
            String mPinRegistered = "1111";
            if (mPinInputted.equals(mPinRegistered)) {
                goToNextFragment();
            } else {
                showErrorOtp(true);
                StateInteractionRequest request = new StateInteractionRequest.Builder()
                        .setState("IncorrectMpin")
                        .build();
                Zabaan.getInstance().playInteraction(request);
            }
        }
    };


    View.OnClickListener deleteCharacterListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showErrorOtp(false);
            if (mIndex > 0)
                mIndex--;
            setCharacter("");
            checkEnableConfirm();
        }
    };

    View.OnClickListener addCharacterListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!inputFieldIsFilled()) {
                String number = ((TextView) v).getText().toString();
                setCharacter(number);
                mIndex++;
                checkEnableConfirm();
            }
        }
    };

    private boolean inputFieldIsFilled() {
        return mIndex > mNumHolders - 1;
    }


    private void setCharacter(String toSet) {
        if (mIndex > mNumHolders - 1 || mIndex < 0) // ensure you cant add more numbers if whole field is filled.
            return;

        ConstraintLayout layoutHolder = (ConstraintLayout) otpInput.getChildAt(mIndex);

        final TextView numberHolder = layoutHolder.findViewById(R.id.number_holder);
        final TextView number_mask = layoutHolder.findViewById(R.id.number_mask);

        if (TextUtils.isEmpty(toSet)) {
            numberHolder.setVisibility(View.INVISIBLE);
            number_mask.setVisibility(View.GONE);
        } else {
            numberHolder.setVisibility(View.VISIBLE);
            numberHolder.setText(toSet);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    number_mask.setVisibility(View.VISIBLE);
                    numberHolder.setVisibility(View.GONE);
                }
            }, 150);

        }

        if (TextUtils.isEmpty(toSet)) {
            View cursorCurrent = layoutHolder.findViewById(R.id.cursor);
            startCursorBlink(cursorCurrent);
            if (mIndex + 1 != mNumHolders) {
                ConstraintLayout nextlayout = (ConstraintLayout) otpInput.getChildAt(mIndex + 1);
                View cursorNext = nextlayout.findViewById(R.id.cursor);
                stopCursorBlink(cursorNext);
            }
        } else {
            View cursorCurrent = layoutHolder.findViewById(R.id.cursor);
            stopCursorBlink(cursorCurrent);
            if (mIndex + 1 != mNumHolders) {
                ConstraintLayout nextlayout = (ConstraintLayout) otpInput.getChildAt(mIndex + 1);
                View cursorNext = nextlayout.findViewById(R.id.cursor);
                startCursorBlink(cursorNext);
            }
        }
    }

    void startCursorBlink(View view) {
        view.setVisibility(View.VISIBLE);
        Animation blinkAnim = new AlphaAnimation(0.0f, 1.0f);
        blinkAnim.setDuration(530);
        blinkAnim.setRepeatCount(Animation.INFINITE);
        blinkAnim.setRepeatMode(Animation.RESTART);
        view.startAnimation(blinkAnim);
    }

    void stopCursorBlink(View view) {
        view.clearAnimation();
        view.setVisibility(View.GONE);
    }

    private String getNumbersFromInputField() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < mNumHolders; i++) {
            ConstraintLayout layoutHolder = (ConstraintLayout) otpInput.getChildAt(i);
            TextView numberHolder = layoutHolder.findViewById(R.id.number_holder);
            builder.append(numberHolder.getText());
        }
        return builder.toString();

    }

    private void checkEnableConfirm() {
        if (mIndex < mNumHolders) {
            confirmTick.setBackgroundResource(R.drawable.grey_background_black_border);
            confirmTick.setEnabled(false);
            tvPayNow.setBackgroundResource(R.drawable.grey_button);
        } else {
            confirmTick.setBackgroundResource(R.drawable.green_background_black_border);
            confirmTick.setEnabled(true);
            tvPayNow.setEnabled(true);
            tvPayNow.setBackgroundResource(R.drawable.yellow_button);
            ViewInteractionRequest request = new ViewInteractionRequest.Builder()
                    .setViewId(R.id.tv_pay_now)
                    .setState("PayNow")
                    .build();
            Zabaan.getInstance().playInteraction(request);
        }
    }


    private void goToNextFragment() {
        showSuccessDialog();
    }

    public void showErrorOtp(boolean hasError) {

        for (int i = 0; i < mNumHolders; i++) {
            ConstraintLayout layoutHolder = (ConstraintLayout) otpInput.getChildAt(i);
            TextView numberHolder = (layoutHolder.findViewById(R.id.number_holder));
            TextView tv_bg = (layoutHolder.findViewById(R.id.tv_bg));

            if (hasError) {
                numberHolder.setTextColor(ContextCompat.getColor(MpinActivity.this, R.color.errorRed));
                tv_bg.setBackgroundResource(R.drawable.textview_border_error);
            } else {
                numberHolder.setTextColor(ContextCompat.getColor(MpinActivity.this, R.color.colorBlack));
                tv_bg.setBackgroundResource(R.drawable.textview_border);
            }
        }
        if (hasError) {
            Animation shake = AnimationUtils.loadAnimation(MpinActivity.this, R.anim.anim_shake);
            otpInput.startAnimation(shake);
        }
    }

    void showSuccessDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MpinActivity.this);
        LayoutInflater inflater = LayoutInflater.from(MpinActivity.this);
        View dialogView = inflater.inflate(R.layout.dialog_success, null);

        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        TextView tvMsg = dialogView.findViewById(R.id.txt_msg);
        tvMsg.setText("Thank You! \nYour EMI is successfully paid.");

        AppCompatImageView imgClose = dialogView.findViewById(R.id.img_close);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });

        StateInteractionRequest request = new StateInteractionRequest.Builder()
                .setState("MpinSuccess")
                .build();
        Zabaan.getInstance().playInteraction(request);
    }



    @Override
    public void assistantSpeechDataDownloadError() {

    }

    @Override
    public void assistantAvailable() {
        ViewInteractionRequest request = new ViewInteractionRequest.Builder()
                .setViewId(R.id.first_number)
                .setState("MPININTRO")
                .build();
        Zabaan.getInstance().playInteraction(request);
    }

    @Override
    public void assistantClicked() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isCVAPlaying) {
                    isCVAPlaying = false;
                    Zabaan.getInstance().stopZabaanInteraction();
                } else {
                    Zabaan.getInstance().stopZabaanInteraction();
                    ViewInteractionRequest request = new ViewInteractionRequest.Builder()
                            .setViewId(R.id.first_number)
                            .setState("MPININTRO")
                            .build();
                    Zabaan.getInstance().playInteraction(request);
                }
            }
        }, 200);
    }

    @Override
    public void assistantSpeechEnd(@NotNull ZabaanSpeakable zabaanSpeakable, boolean isInterrupted) {
        isCVAPlaying = false;
        if (!(TextUtils.isEmpty(zabaanSpeakable.getState())) && zabaanSpeakable.getState().equalsIgnoreCase("IncorrectMpin")) {
            ViewInteractionRequest request = new ViewInteractionRequest.Builder()
                    .setViewId(R.id.forget_mpin)
                    .setState("ResetMpin")
                    .build();
            Zabaan.getInstance().playInteraction(request);
        }
    }

    @Override
    public void assistantSpeechError(@NotNull ZabaanSpeakable zabaanSpeakable, @Nullable Exception e) {
        isCVAPlaying = false;
        Log.e("Assistant Speech Error:", "" + e.getMessage());
    }

    @Override
    public void assistantSpeechStart(@NotNull ZabaanSpeakable zabaanSpeakable) {
        isCVAPlaying = true;
    }

    @Override
    public void assistantLanguageNotSupported() {

    }

    @Override
    public void assistantSpeechDataDownloaded(@NotNull ZabaanLanguages zabaanLanguages) {

    }
}