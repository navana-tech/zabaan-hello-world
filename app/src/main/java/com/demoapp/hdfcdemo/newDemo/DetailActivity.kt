package com.demoapp.hdfcdemo.newDemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.demoapp.hdfcdemo.R;
import com.zabaan.sdk.Zabaan;

public class DetailActivity extends AppCompatActivity {
    LinearLayout ll_sub;
    ConstraintLayout ll_acc;
    ImageView imgLogout, imgHome, img_select;
    TextView tv_acc1, tv_acc2, tv_acc, tv_bal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ll_acc = findViewById(R.id.ll_acc);
        ll_sub = findViewById(R.id.ll_sub);

        imgLogout = findViewById(R.id.img_logout);
        imgHome = findViewById(R.id.img_home);
        img_select = findViewById(R.id.img_select);

        tv_acc1 = findViewById(R.id.tv_acc1);
        tv_acc2 = findViewById(R.id.tv_acc2);
        tv_acc = findViewById(R.id.tv_acc);
        tv_bal = findViewById(R.id.tv_bal);

        ll_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DetailActivity.this, MpinActivity.class));
            }
        });

        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DetailActivity.this, LoginActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        });

        imgHome.setOnClickListener(view -> finish());

        tv_acc1.setOnClickListener(view -> {
            ll_sub.setVisibility(View.GONE);
            tv_acc.setText("Account No: 1232xxxxx5112");
            tv_bal.setText("BALANCE: RS 25,347.00");
        });

        tv_acc2.setOnClickListener(view -> {
            ll_sub.setVisibility(View.GONE);
            tv_acc.setText("Account No: 1163xxxxx5463");
            tv_bal.setText("BALANCE: RS 11,533.00");
        });

        img_select.setOnClickListener(view -> {
            if (ll_sub.getVisibility() == View.INVISIBLE || ll_sub.getVisibility() == View.GONE) {
                ll_sub.setVisibility(View.VISIBLE);
            } else {
                ll_sub.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Zabaan.getInstance().show(this);
        Zabaan.getInstance().setScreenName("DetailActivity");
        Zabaan.getInstance().setCurrentState("details_1");
    }
}