package com.demoapp.hdfcdemo.newDemo

import androidx.appcompat.app.AppCompatActivity
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import android.widget.TextView
import android.os.Bundle
import com.demoapp.hdfcdemo.R
import android.content.Intent
import android.view.View
import android.widget.ImageView
import com.zabaan.sdk.Zabaan

class DetailActivity : AppCompatActivity() {
    var ll_sub: LinearLayout? = null
    var ll_acc: ConstraintLayout? = null
    var imgLogout: ImageView? = null
    var imgHome: ImageView? = null
    var img_select: ImageView? = null
    var tv_acc1: TextView? = null
    var tv_acc2: TextView? = null
    var tv_acc: TextView? = null
    var tv_bal: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        ll_acc = findViewById(R.id.ll_acc)
        ll_sub = findViewById(R.id.ll_sub)
        imgLogout = findViewById(R.id.img_logout)
        imgHome = findViewById(R.id.img_home)
        img_select = findViewById(R.id.img_select)
        tv_acc1 = findViewById(R.id.tv_acc1)
        tv_acc2 = findViewById(R.id.tv_acc2)
        tv_acc = findViewById(R.id.tv_acc)
        tv_bal = findViewById(R.id.tv_bal)

        ll_acc?.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    this@DetailActivity,
                    MpinActivity::class.java
                )
            )
        })

        imgLogout?.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(this@DetailActivity, LoginActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
            finish()
        })

        imgHome?.setOnClickListener(View.OnClickListener { view: View? -> finish() })

        tv_acc1?.setOnClickListener(View.OnClickListener { view: View? ->
            ll_sub?.setVisibility(View.GONE)
            tv_acc?.setText("Account No: 1232xxxxx5112")
            tv_bal?.setText("BALANCE: RS 25,347.00")
        })

        tv_acc2?.setOnClickListener(View.OnClickListener { view: View? ->
            ll_sub?.setVisibility(View.GONE)
            tv_acc?.setText("Account No: 1163xxxxx5463")
            tv_bal?.setText("BALANCE: RS 11,533.00")
        })

        img_select?.setOnClickListener(View.OnClickListener { view: View? ->
            if (ll_sub?.getVisibility() == View.INVISIBLE || ll_sub?.getVisibility() == View.GONE) {
                ll_sub?.setVisibility(View.VISIBLE)
            } else {
                ll_sub?.setVisibility(View.GONE)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        Zabaan.getInstance().show(this)
        Zabaan.getInstance().setScreenName("DetailActivity")
        Zabaan.getInstance().setCurrentState("details_1")
    }
}