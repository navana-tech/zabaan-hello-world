package com.demoapp.hdfcdemo.newDemo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demoapp.hdfcdemo.R;
import com.zabaan.sdk.AssistantInteractionListener;
import com.zabaan.sdk.AssistantStateListener;
import com.zabaan.sdk.Zabaan;
import com.zabaan.common.ZabaanLanguages;
import com.zabaan.common.ZabaanSpeakable;
import com.zabaan.sdk.internal.interaction.ViewInteractionRequest;
import com.zabaan.common.views.AnimationSwipeLeftNormal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

public class NewDashboardActivity extends AppCompatActivity
        implements AssistantInteractionListener {

    ArrayList<DashboardItem> dashboardItems = new ArrayList<>();
    ImageView imgLogout;
    LinearLayoutManager llm;
    ArrayList<String> statePicker = new ArrayList<String>();
    boolean isCVAPlaying = false;
    boolean isAudioDownloaded = false;
    private RecyclerView rvDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_dashboard);
        imgLogout = findViewById(R.id.img_logout);

        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NewDashboardActivity.this, LoginActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isAudioDownloaded = false;
        setUpDashboardList();
        Zabaan.getInstance().show(this);
        Zabaan.getInstance().setScreenName("DashboardActivity");
        Zabaan.getInstance().setAssistantInteractionListener(this);
        Zabaan.getInstance().setAssistantStateListener(new AssistantStateListener() {
            @Override
            public void assistantAvailable() {
                if (isAudioDownloaded)
                    playAudioForScreen();
            }

            @Override
            public void assistantClicked() {
                Zabaan.getInstance().stopZabaanInteraction();
                if (isCVAPlaying) {
                    isCVAPlaying = false;
                } else {
                    playAudioForScreen();
                }
            }

            @Override
            public void assistantLanguageNotSupported() {

            }
        });
    }

    void setUpDashboardList() {
        if (!dashboardItems.isEmpty())
            dashboardItems.clear();

        DashboardItem item1 = new DashboardItem();
        item1.setImg(R.drawable.img_borrow);
        item1.setTitle("PAY");
        item1.setDescription("UPI Payments, Money Transfer, Cards,\n Recharge, Bill Payment");
        String[] entry = new String[]{"UPI Payment", "Money Transfer", "Cards", "Pay Loan EMI", "Bill Pay"};
        ArrayList<String> subItems = new ArrayList<>(Arrays.asList(entry));
        item1.setSubItems(subItems);
        dashboardItems.add(item1);

        DashboardItem item2 = new DashboardItem();
        item2.setImg(R.drawable.img_save);
        item2.setTitle("SAVE");
        item2.setDescription("Accounts, Fixed Deposit, Recurring Deposit");
        dashboardItems.add(item2);

        DashboardItem item3 = new DashboardItem();
        item3.setImg(R.drawable.img_invest);
        item3.setTitle("INVEST");
        item3.setDescription("Demat, Mutual Fund, Bonds");
        dashboardItems.add(item3);

        DashboardItem item4 = new DashboardItem();
        item4.setImg(R.drawable.img_pay);
        item4.setTitle("BORROW");
        item4.setDescription("Personal Loan, Car Loan, Gold Loan, Educational Loan");
        dashboardItems.add(item4);

        DashboardItem item5 = new DashboardItem();
        item5.setImg(R.drawable.img_profile);
        item5.setTitle("YOUR PROFILE");
        item5.setDescription("Personal Profile, Manage Alerts, Tax, Security");
        dashboardItems.add(item5);

        DashboardItem item6 = new DashboardItem();
        item6.setImg(R.drawable.img_settings);
        item6.setTitle("SETTINGS");
        dashboardItems.add(item6);

        DashboardItem item7 = new DashboardItem();
        item7.setImg(R.drawable.img_power_off);
        item7.setTitle("LOGOUT");
        dashboardItems.add(item7);

        statePicker.clear();
        statePicker.add("PAY");
        statePicker.add("SAVE");
        statePicker.add("INVEST");
        statePicker.add("BORROW");
        statePicker.add("YOUR PROFILE");
        statePicker.add("SETTINGS");
        statePicker.add("LOGOUT");

        rvDashboard = (RecyclerView) findViewById(R.id.rv_dashboard);
        DashboardAdapter adapter = new DashboardAdapter(dashboardItems, rvDashboard);
        // Attach the adapter to the recyclerview to populate items
        rvDashboard.setAdapter(adapter);
        // Set layout manager to position the items
        llm = new LinearLayoutManager(NewDashboardActivity.this);
        rvDashboard.setLayoutManager(llm);
        rvDashboard.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isCVAPlaying)
                    Zabaan.getInstance().stopZabaanInteraction();
            }
        });
    }

    void playAudioForScreen() {
        Zabaan.getInstance().stopZabaanInteraction();
        if (rvDashboard != null && isAudioDownloaded) {
            int pos = 0;
            if (llm != null) {
                pos = llm.findFirstCompletelyVisibleItemPosition();
            }
            String state = "";
            try {
                state = statePicker.get(pos);
            } catch (IndexOutOfBoundsException iex) {
                System.out.println("" + iex.getMessage());
            }
            if (!TextUtils.isEmpty(state)) {
                RecyclerView.ViewHolder viewHolder = rvDashboard.findViewHolderForAdapterPosition(pos);
                if (viewHolder != null) {
                    ViewInteractionRequest request = new ViewInteractionRequest.Builder()
                            .setRootView(viewHolder.itemView)
                            .setViewId(R.id.tv_row_title)
                            .setState(state)
                            .build();
                    Zabaan.getInstance().playInteraction(request);
                }
            }
        }
    }

    @Override
    public void assistantSpeechDataDownloaded(ZabaanLanguages s) {
        isAudioDownloaded = true;
    }

    @Override
    public void assistantSpeechDataDownloadError() {

    }

    @Override
    public void assistantSpeechEnd(@NotNull ZabaanSpeakable zabaanSpeakable, boolean isInterrupted) {
        isCVAPlaying = false;
        if (!isInterrupted) {
            String checkState = zabaanSpeakable.getState();
            RecyclerView.ViewHolder viewHolder;
            switch (checkState) {
                case "PAY":
                    viewHolder = rvDashboard.findViewHolderForAdapterPosition(1);
                    if (viewHolder != null) {
                        System.out.println("" + viewHolder.itemView.getParent().getParent());
                        ViewInteractionRequest request = new ViewInteractionRequest.Builder()
                                .setRootView(viewHolder.itemView)
                                .setViewId(R.id.tv_row_title)
                                .setState("SAVE")
                                .build();
                        Zabaan.getInstance().playInteraction(request);
                    }
                    break;

                case "SAVE":
                    viewHolder = rvDashboard.findViewHolderForAdapterPosition(2);
                    if (viewHolder != null) {
                        ViewInteractionRequest request = new ViewInteractionRequest.Builder()
                                .setRootView(viewHolder.itemView)
                                .setViewId(R.id.tv_row_title)
                                .setState("INVEST")
                                .build();
                        Zabaan.getInstance().playInteraction(request);
                    }
                    break;

                case "INVEST":
                    viewHolder = rvDashboard.findViewHolderForAdapterPosition(3);
                    if (viewHolder != null) {
                        ViewInteractionRequest request = new ViewInteractionRequest.Builder()
                                .setRootView(viewHolder.itemView)
                                .setViewId(R.id.tv_row_title)
                                .setState("BORROW")
                                .build();
                        Zabaan.getInstance().playInteraction(request);
                    }
                    break;

                case "BORROW":
                    int lv = llm.findLastCompletelyVisibleItemPosition();
                    if (llm.findLastCompletelyVisibleItemPosition() >= 4) {
                        viewHolder = rvDashboard.findViewHolderForAdapterPosition(4);
                        if (viewHolder != null) {
                            ViewInteractionRequest request = new ViewInteractionRequest.Builder()
                                    .setRootView(viewHolder.itemView)
                                    .setViewId(R.id.tv_row_title)
                                    .setState("YOUR PROFILE")
                                    .build();
                            Zabaan.getInstance().playInteraction(request);
                        }
                    } else {
                        ViewInteractionRequest request = new ViewInteractionRequest.Builder()
                                .setViewId(R.id.img_add)
                                .setFingerAnimation(AnimationSwipeLeftNormal.INSTANCE)
                                .setState("DEAL")
                                .build();
                        Zabaan.getInstance().playInteraction(request);
                    }
                    break;

                case "YOUR PROFILE":
                    if (llm.findLastCompletelyVisibleItemPosition() >= 5) {
                        viewHolder = rvDashboard.findViewHolderForAdapterPosition(5);
                        if (viewHolder != null) {
                            ViewInteractionRequest request = new ViewInteractionRequest.Builder()
                                    .setRootView(viewHolder.itemView)
                                    .setViewId(R.id.tv_row_title)
                                    .setState("SETTINGS")
                                    .build();
                            Zabaan.getInstance().playInteraction(request);
                        }
                    } else {
                        ViewInteractionRequest request = new ViewInteractionRequest.Builder()
                                .setViewId(R.id.img_add)
                                .setFingerAnimation(AnimationSwipeLeftNormal.INSTANCE)
                                .setState("DEAL")
                                .build();
                        Zabaan.getInstance().playInteraction(request);
                    }
                    break;

                case "SETTINGS":
                    if (llm.findLastCompletelyVisibleItemPosition() == 6) {
                        viewHolder = rvDashboard.findViewHolderForAdapterPosition(6);
                        if (viewHolder != null) {
                            ViewInteractionRequest request = new ViewInteractionRequest.Builder()
                                    .setRootView(viewHolder.itemView)
                                    .setViewId(R.id.tv_row_title)
                                    .setState("LOGOUT")
                                    .build();
                            Zabaan.getInstance().playInteraction(request);
                        }
                    } else {
                        ViewInteractionRequest request = new ViewInteractionRequest.Builder()
                                .setViewId(R.id.img_add)
                                .setFingerAnimation(AnimationSwipeLeftNormal.INSTANCE)
                                .setState("DEAL")
                                .build();
                        Zabaan.getInstance().playInteraction(request);
                    }
                    break;

                case "LOGOUT":
                    ViewInteractionRequest request = new ViewInteractionRequest.Builder()
                            .setViewId(R.id.img_add)
                            .setFingerAnimation(AnimationSwipeLeftNormal.INSTANCE)
                            .setState("DEAL")
                            .build();
                    Zabaan.getInstance().playInteraction(request);
                    break;

                case "UPIPAYMENT":
                    viewHolder = rvDashboard.findViewHolderForAdapterPosition(0);
                    if (viewHolder != null) {
                        int idView = 2001;
                        if (viewHolder.itemView.findViewById(idView) != null) {
                            ViewInteractionRequest request1 = new ViewInteractionRequest.Builder()
                                    .setViewId(R.id.tv_row_title)
                                    .setViewId(2001)
                                    .setState("MONEYTRANSFER")
                                    .build();
                            Zabaan.getInstance().playInteraction(request1);
                        }
                    }
                    break;
                case "MONEYTRANSFER":
                    viewHolder = rvDashboard.findViewHolderForAdapterPosition(0);
                    if (viewHolder != null) {
                        int idView = 3002;
                        if (viewHolder.itemView.findViewById(idView) != null) {
                            ViewInteractionRequest request2 = new ViewInteractionRequest.Builder()
                                    .setViewId(3002)
                                    .setState("CARDS")
                                    .build();
                            Zabaan.getInstance().playInteraction(request2);
                        }
                    }
                    break;
                case "CARDS":
                    viewHolder = rvDashboard.findViewHolderForAdapterPosition(0);
                    if (viewHolder != null) {
                        int idView = 4003;
                        if (viewHolder.itemView.findViewById(idView) != null) {
                            ViewInteractionRequest request3 = new ViewInteractionRequest.Builder()
                                    .setViewId(4003)
                                    .setState("PAYEMI")
                                    .build();
                            Zabaan.getInstance().playInteraction(request3);
                        }
                    }
                    break;
                case "PAYEMI":
                    viewHolder = rvDashboard.findViewHolderForAdapterPosition(0);
                    if (viewHolder != null) {
                        int idView = 5004;
                        if (viewHolder.itemView.findViewById(idView) != null) {
                            ViewInteractionRequest request4 = new ViewInteractionRequest.Builder()
                                    .setViewId(5004)
                                    .setState("BILLPAY")
                                    .build();
                            Zabaan.getInstance().playInteraction(request4);
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void assistantSpeechError(@NotNull ZabaanSpeakable zabaanSpeakable, @Nullable Exception e) {

    }

    @Override
    public void assistantSpeechStart(@NotNull ZabaanSpeakable zabaanSpeakable) {
        isCVAPlaying = true;
        System.out.println(zabaanSpeakable);
    }
}