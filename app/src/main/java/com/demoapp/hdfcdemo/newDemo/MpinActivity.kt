package com.demoapp.hdfcdemo.newDemo

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.zabaan.sdk.AssistantInteractionListener
import com.zabaan.sdk.AssistantStateListener
import androidx.constraintlayout.widget.ConstraintLayout
import android.widget.ImageButton
import android.widget.TextView
import android.os.Bundle
import com.demoapp.hdfcdemo.R
import com.zabaan.sdk.Zabaan
import android.content.Intent
import android.os.Handler
import com.demoapp.hdfcdemo.newDemo.LoginActivity
import com.zabaan.sdk.internal.interaction.StateInteractionRequest
import android.text.TextUtils
import android.view.animation.Animation
import android.view.animation.AlphaAnimation
import com.zabaan.sdk.internal.interaction.ViewInteractionRequest
import android.view.LayoutInflater
import androidx.appcompat.widget.AppCompatImageView
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.zabaan.common.ZabaanSpeakable
import com.zabaan.common.ZabaanLanguages
import java.lang.Exception
import java.lang.StringBuilder

class MpinActivity : AppCompatActivity(), AssistantInteractionListener, AssistantStateListener {
    var mContext: Context? = null
    var dialPad: ConstraintLayout? = null
    var otpInput: ConstraintLayout? = null
    var confirmTick: ImageButton? = null
    var tvPayNow: TextView? = null
    var mNumHolders = 0
    var mIndex = 0
    var imgLogout: ImageView? = null
    var imgHome: ImageView? = null
    private var isCVAPlaying = false
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mpin)
        initVariables()
        assignViews()
        attachListeners()
    }

    public override fun onResume() {
        super.onResume()
        Zabaan.getInstance().show(this)
        Zabaan.getInstance().setScreenName("MPinActivity")
        Zabaan.getInstance().setAssistantStateListener(this)
        Zabaan.getInstance().setAssistantInteractionListener(this)
        //requestOTP(phoneNumber);
        while (mIndex > 0) {
            mIndex--
            setCharacter("")
        }
        checkEnableConfirm()
        clearAnimation()
    }

    public override fun onPause() {
        super.onPause()
    }

    fun clearAnimation() {}
    private fun initVariables() {
        mContext = this@MpinActivity
    }

    private fun assignViews() {
        dialPad = findViewById(R.id.dialpad)
        otpInput = findViewById(R.id.mpin_input)
        otpInput?.let {
            mNumHolders = it.getChildCount()
        }
        confirmTick = findViewById(R.id.confirm_tick)
        imgLogout = findViewById(R.id.img_logout)
        imgHome = findViewById(R.id.img_home)
        tvPayNow = findViewById(R.id.tv_pay_now)
    }

    private fun attachListeners() {
        for (i in 0 until dialPad!!.childCount) {
            if ("backspace" == dialPad!!.getChildAt(i).tag) {
                dialPad!!.getChildAt(i).setOnClickListener(deleteCharacterListener)
            } else if ("confirm" == dialPad!!.getChildAt(i).tag) {
                dialPad!!.getChildAt(i).setOnClickListener(goToNextFragmentListener)
            } else {
                dialPad!!.getChildAt(i).setOnClickListener(addCharacterListener)
            }
        }
        imgLogout!!.setOnClickListener {
            startActivity(
                Intent(this@MpinActivity, LoginActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
            finish()
        }
        imgHome!!.setOnClickListener { finish() }
        tvPayNow!!.setOnClickListener(goToNextFragmentListener)
    }

    var goToNextFragmentListener = View.OnClickListener {
        val mPinInputted = numbersFromInputField
        val mPinRegistered = "1111"
        if (mPinInputted == mPinRegistered) {
            goToNextFragment()
        } else {
            showErrorOtp(true)
            val request: StateInteractionRequest = StateInteractionRequest.Builder()
                .setState("IncorrectMpin")
                .build()
            Zabaan.getInstance().playInteraction(request)
        }
    }
    var deleteCharacterListener = View.OnClickListener {
        showErrorOtp(false)
        if (mIndex > 0) mIndex--
        setCharacter("")
        checkEnableConfirm()
    }
    var addCharacterListener = View.OnClickListener { v ->
        if (!inputFieldIsFilled()) {
            val number = (v as TextView).text.toString()
            setCharacter(number)
            mIndex++
            checkEnableConfirm()
        }
    }

    private fun inputFieldIsFilled(): Boolean {
        return mIndex > mNumHolders - 1
    }

    private fun setCharacter(toSet: String) {
        if (mIndex > mNumHolders - 1 || mIndex < 0) // ensure you cant add more numbers if whole field is filled.
            return
        val layoutHolder = otpInput!!.getChildAt(mIndex) as ConstraintLayout
        val numberHolder = layoutHolder.findViewById<TextView>(R.id.number_holder)
        val number_mask = layoutHolder.findViewById<TextView>(R.id.number_mask)
        if (TextUtils.isEmpty(toSet)) {
            numberHolder.visibility = View.INVISIBLE
            number_mask.visibility = View.GONE
        } else {
            numberHolder.visibility = View.VISIBLE
            numberHolder.text = toSet
            val handler = Handler()
            handler.postDelayed({
                number_mask.visibility = View.VISIBLE
                numberHolder.visibility = View.GONE
            }, 150)
        }
        if (TextUtils.isEmpty(toSet)) {
            val cursorCurrent = layoutHolder.findViewById<View>(R.id.cursor)
            startCursorBlink(cursorCurrent)
            if (mIndex + 1 != mNumHolders) {
                val nextlayout = otpInput!!.getChildAt(mIndex + 1) as ConstraintLayout
                val cursorNext = nextlayout.findViewById<View>(R.id.cursor)
                stopCursorBlink(cursorNext)
            }
        } else {
            val cursorCurrent = layoutHolder.findViewById<View>(R.id.cursor)
            stopCursorBlink(cursorCurrent)
            if (mIndex + 1 != mNumHolders) {
                val nextlayout = otpInput!!.getChildAt(mIndex + 1) as ConstraintLayout
                val cursorNext = nextlayout.findViewById<View>(R.id.cursor)
                startCursorBlink(cursorNext)
            }
        }
    }

    fun startCursorBlink(view: View) {
        view.visibility = View.VISIBLE
        val blinkAnim: Animation = AlphaAnimation(0.0f, 1.0f)
        blinkAnim.duration = 530
        blinkAnim.repeatCount = Animation.INFINITE
        blinkAnim.repeatMode = Animation.RESTART
        view.startAnimation(blinkAnim)
    }

    fun stopCursorBlink(view: View) {
        view.clearAnimation()
        view.visibility = View.GONE
    }

    private val numbersFromInputField: String
        private get() {
            val builder = StringBuilder()
            for (i in 0 until mNumHolders) {
                val layoutHolder = otpInput!!.getChildAt(i) as ConstraintLayout
                val numberHolder = layoutHolder.findViewById<TextView>(R.id.number_holder)
                builder.append(numberHolder.text)
            }
            return builder.toString()
        }

    private fun checkEnableConfirm() {
        if (mIndex < mNumHolders) {
            confirmTick!!.setBackgroundResource(R.drawable.grey_background_black_border)
            confirmTick!!.isEnabled = false
            tvPayNow!!.setBackgroundResource(R.drawable.grey_button)
        } else {
            confirmTick!!.setBackgroundResource(R.drawable.green_background_black_border)
            confirmTick!!.isEnabled = true
            tvPayNow!!.isEnabled = true
            tvPayNow!!.setBackgroundResource(R.drawable.yellow_button)
            val request: ViewInteractionRequest = ViewInteractionRequest.Builder()
                .setViewId(R.id.tv_pay_now)
                .setState("PayNow")
                .build()
            Zabaan.getInstance().playInteraction(request)
        }
    }

    private fun goToNextFragment() {
        showSuccessDialog()
    }

    fun showErrorOtp(hasError: Boolean) {
        for (i in 0 until mNumHolders) {
            val layoutHolder = otpInput!!.getChildAt(i) as ConstraintLayout
            val numberHolder = layoutHolder.findViewById<TextView>(R.id.number_holder)
            val tv_bg = layoutHolder.findViewById<TextView>(R.id.tv_bg)
            if (hasError) {
                numberHolder.setTextColor(
                    ContextCompat.getColor(
                        this@MpinActivity,
                        R.color.errorRed
                    )
                )
                tv_bg.setBackgroundResource(R.drawable.textview_border_error)
            } else {
                numberHolder.setTextColor(
                    ContextCompat.getColor(
                        this@MpinActivity,
                        R.color.colorBlack
                    )
                )
                tv_bg.setBackgroundResource(R.drawable.textview_border)
            }
        }
        if (hasError) {
            val shake = AnimationUtils.loadAnimation(this@MpinActivity, R.anim.anim_shake)
            otpInput!!.startAnimation(shake)
        }
    }

    fun showSuccessDialog() {
        val dialogBuilder = AlertDialog.Builder(this@MpinActivity)
        val inflater = LayoutInflater.from(this@MpinActivity)
        val dialogView = inflater.inflate(R.layout.dialog_success, null)
        dialogBuilder.setView(dialogView)
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
        val tvMsg = dialogView.findViewById<TextView>(R.id.txt_msg)
        tvMsg.text = "Thank You! \nYour EMI is successfully paid."
        val imgClose: AppCompatImageView = dialogView.findViewById(R.id.img_close)
        imgClose.setOnClickListener { alertDialog.cancel() }
        val request: StateInteractionRequest = StateInteractionRequest.Builder()
            .setState("MpinSuccess")
            .build()
        Zabaan.getInstance().playInteraction(request)
    }

    override fun assistantSpeechDataDownloadError() {}
    override fun assistantAvailable() {
        val request: ViewInteractionRequest = ViewInteractionRequest.Builder()
            .setViewId(R.id.first_number)
            .setState("MPININTRO")
            .build()
        Zabaan.getInstance().playInteraction(request)
    }

    override fun assistantClicked() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (isCVAPlaying) {
                isCVAPlaying = false
                Zabaan.getInstance().stopZabaanInteraction()
            } else {
                Zabaan.getInstance().stopZabaanInteraction()
                val request: ViewInteractionRequest = ViewInteractionRequest.Builder()
                    .setViewId(R.id.first_number)
                    .setState("MPININTRO")
                    .build()
                Zabaan.getInstance().playInteraction(request)
            }
        }, 200)
    }

    override fun assistantSpeechEnd(zabaanSpeakable: ZabaanSpeakable, isInterrupted: Boolean) {
        isCVAPlaying = false
        if (!TextUtils.isEmpty(zabaanSpeakable.state) && zabaanSpeakable.state.equals(
                "IncorrectMpin",
                ignoreCase = true
            )
        ) {
            val request: ViewInteractionRequest = ViewInteractionRequest.Builder()
                .setViewId(R.id.forget_mpin)
                .setState("ResetMpin")
                .build()
            Zabaan.getInstance().playInteraction(request)
        }
    }

    override fun assistantSpeechError(zabaanSpeakable: ZabaanSpeakable, e: Exception?) {
        isCVAPlaying = false
        Log.e("Assistant Speech Error:", "" + e!!.message)
    }

    override fun assistantSpeechStart(zabaanSpeakable: ZabaanSpeakable) {
        isCVAPlaying = true
    }

    override fun assistantLanguageNotSupported() {}
    override fun assistantSpeechDataDownloaded(zabaanLanguages: ZabaanLanguages) {}

    companion object {
        private const val KEYPAD = "k"
        private const val TICK = "t"
    }
}