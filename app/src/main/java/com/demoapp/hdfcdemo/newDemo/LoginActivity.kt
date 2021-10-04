package com.demoapp.hdfcdemo.newDemo

import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import android.widget.TextView
import android.os.Bundle
import com.demoapp.hdfcdemo.R
import android.text.TextUtils
import android.content.Intent
import android.net.Uri
import com.demoapp.hdfcdemo.newDemo.NewDashboardActivity
import com.zabaan.sdk.internal.interaction.ViewInteractionRequest
import com.zabaan.sdk.Zabaan
import android.text.TextWatcher
import android.text.Editable
import com.zabaan.sdk.AssistantStateListener
import com.zabaan.common.views.AnimationClickNormal
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import com.zabaan.sdk.AssistantInteractionListener
import com.zabaan.common.ZabaanLanguages
import com.zabaan.common.ZabaanSpeakable
import java.lang.Exception

class LoginActivity : AppCompatActivity() {
    var etCustomerId: TextInputEditText? = null
    var etPassword: TextInputEditText? = null
    var btnLogin: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        etCustomerId = findViewById(R.id.et_customer_id)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)
        btnLogin?.setOnClickListener(View.OnClickListener { v: View? ->
            if (!TextUtils.isEmpty(etCustomerId?.getText())
                && etCustomerId?.getText().toString().equals("demouser", ignoreCase = true)
                && !TextUtils.isEmpty(etPassword?.getText())
                && etPassword?.getText().toString().equals("1111", ignoreCase = true)
            ) {
                startActivity(Intent(this@LoginActivity, NewDashboardActivity::class.java))
                finish()
            } else {
                try {
                    val request: ViewInteractionRequest = ViewInteractionRequest.Builder()
                        .setViewId(R.id.tv_forgot_pass_new)
                        .setState("IncorrectPassword")
                        .build()
                    Zabaan.getInstance().playInteraction(request)
                } catch (nex: Exception) {
                    Log.e("Login", "Error playing interaction")
                }
            }
        })
        etCustomerId?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                Zabaan.getInstance().stopZabaanInteraction()
            }

            override fun afterTextChanged(editable: Editable) {
                updateLoginButton()
            }
        })

        etPassword?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                Zabaan.getInstance().stopZabaanInteraction()
            }

            override fun afterTextChanged(editable: Editable) {
                updateLoginButton()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        Zabaan.getInstance().show(this)
        Zabaan.getInstance().setAssistantStateListener(object : AssistantStateListener {
            override fun assistantAvailable() {
                Zabaan.getInstance().promptIdleAnimation(5000)
                Zabaan.getInstance().setDefaultFingerAnimation(AnimationClickNormal)
                if (PreferenceManager.getDefaultSharedPreferences(this@LoginActivity)
                        .getBoolean("isFirstTime", false)
                ) {
                    Zabaan.getInstance().setCurrentState("NORMALASSISTANTCLICK")
                } else {
                    PreferenceManager.getDefaultSharedPreferences(this@LoginActivity)
                        .edit()
                        .putBoolean("isFirstTime", true).apply()
                    Zabaan.getInstance().setCurrentState("FIRSTTIMEUSER")
                }
            }

            override fun assistantClicked() {}
            override fun assistantLanguageNotSupported() {}
        })
        Zabaan.getInstance().setAssistantInteractionListener(object : AssistantInteractionListener {
            override fun assistantSpeechDataDownloaded(zabaanLanguage: ZabaanLanguages) {}
            override fun assistantSpeechDataDownloadError() {}
            override fun assistantSpeechStart(zabaanSpeakable: ZabaanSpeakable) {
                if (zabaanSpeakable.state.equals(
                        "FIRSTTIMEUSER",
                        ignoreCase = true
                    )
                ) Zabaan.getInstance().setCurrentState("NORMALASSISTANTCLICK")
            }

            override fun assistantSpeechEnd(
                zabaanSpeakable: ZabaanSpeakable,
                isInterrupted: Boolean
            ) {
            }

            override fun assistantSpeechError(zabaanSpeakable: ZabaanSpeakable, e: Exception?) {}
        })
        Zabaan.getInstance().setScreenName("LoginActivity")
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        Zabaan.getInstance().stopIdleAnimation()
    }

    fun updateLoginButton() {
        val username: String
        val pass: String
        username = etCustomerId!!.text.toString()
        pass = etPassword!!.text.toString()
        if (username != null && pass != null && username.length >= 4 && pass.length >= 4) {
            btnLogin!!.setBackgroundResource(R.drawable.yellow_button)
            btnLogin!!.isEnabled = true
        } else {
            btnLogin!!.setBackgroundResource(R.drawable.grey_button)
            btnLogin!!.isEnabled = false
        }
    }

    fun getRawUri(filename: String?): Uri {
        val aVal = this@LoginActivity.resources.getIdentifier(filename, "raw", this.packageName)
        return Uri.parse(aVal.toString())
    }

    override fun onPause() {
        super.onPause()
        Zabaan.getInstance().clearZabaan()
    }
}