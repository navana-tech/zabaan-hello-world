package com.demoapp.hdfcdemo.newDemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.demoapp.hdfcdemo.R;
import com.zabaan.sdk.AssistantInteractionListener;
import com.zabaan.sdk.Zabaan;
import com.zabaan.common.ZabaanLanguages;
import com.zabaan.common.ZabaanSpeakable;
import com.zabaan.sdk.internal.interaction.CoordinateInteractionRequest;
import com.zabaan.common.views.AnimationClickNormal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

public class WebDemoActivity extends AppCompatActivity implements AssistantInteractionListener {

    WebView webView;
    GridLayout gridLayout;

    Integer[] xCoordinateForHome = {45, 180};
    Integer[] yCoordinateForHome = {120, 120};
    Integer[] xCoordinateForPrepaid = {200, 200};
    Integer[] yCoordinateForPrepaid = {200, 280};

    ArrayList<Integer> index = new ArrayList<>(Arrays.asList(0, 1));

    String stateHome = "home";
    String statePrepaid = "prepaid";
    String stateElectricity = "electricity";
    boolean isFirstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_demo);
        webView = findViewById(R.id.wv_main);
        gridLayout = findViewById(R.id.gridview);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                Log.e("Loading", url);
                if (!isFirstLoad) {
                    if (url.contains(statePrepaid) || url.contains(stateElectricity)) {
                        Zabaan.getInstance().show(WebDemoActivity.this);
                        Zabaan.getInstance().setCurrentState(statePrepaid);
                        queueInteraction(statePrepaid, xCoordinateForPrepaid[0], yCoordinateForPrepaid[0], index.get(0));
                    } else {
                        Zabaan.getInstance().hide();
//                        Zabaan.getInstance().setCurrentState(stateHome);
//                        queueInteraction(stateHome, xCoordinateForHome[0], yCoordinateForHome[0], index.get(0));
                    }
                } else {
                    isFirstLoad = false;
                    Zabaan.getInstance().show(WebDemoActivity.this);
                    Zabaan.getInstance().setScreenName("webview");
                    Zabaan.getInstance().setAssistantInteractionListener(WebDemoActivity.this);
                }
                super.doUpdateVisitedHistory(view, url, isReload);
            }
        });
        webView.loadUrl("https://www.freecharge.in/");
        //fillGridView();

    }

    void fillGridView() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WebDemoActivity.this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;


        int rowCount = 10;
        int columnCount = 10;
        gridLayout.setRowCount(rowCount);
        gridLayout.setColumnCount(columnCount);

        final int MAX_COLUMN = gridLayout.getColumnCount(); //5
        final int MAX_ROW = gridLayout.getRowCount(); //7
        final int itemsCount = MAX_ROW * MAX_COLUMN; //35

        int row = 0, column = 0;


        for (int i = 1; i <= itemsCount; i++) {
            ImageView view = new ImageView(this);

            //Just to provide alternate colors
            //view.setBackgroundColor(colarr[i % colarr.length]);

            GridLayout.LayoutParams params = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                params = new GridLayout.LayoutParams(GridLayout.spec(row, 1F), GridLayout.spec(column, 1F));
            }
            view.setLayoutParams(params);
            view.setId(i);
            view.setBackgroundResource(R.drawable.black_border);
            gridLayout.addView(view);

            column++;

            if (column >= MAX_COLUMN) {
                column = 0;
                row++;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void assistantSpeechDataDownloaded(@NotNull ZabaanLanguages languageCode) {

    }

    @Override
    public void assistantSpeechDataDownloadError() {

    }

    @Override
    public void assistantSpeechStart(@NotNull ZabaanSpeakable interaction) {

    }

    @Override
    public void assistantSpeechEnd(@NotNull ZabaanSpeakable interaction, boolean isInteractionInterrupted) {
        Integer indexValue = interaction.getIndex();
        Integer position = index.indexOf(indexValue);
        Log.e("Speakable", "Index" + position + interaction.getState());
        if (interaction.getState().equals(stateHome)) {
            if (position < index.size() - 1) {
                position++;
                queueInteraction(stateHome, xCoordinateForHome[position], yCoordinateForHome[position], position);
            }
        } else {
            if (position < index.size() - 1) {
                position++;
                queueInteraction(statePrepaid, xCoordinateForPrepaid[position], yCoordinateForPrepaid[position], position);
            }
        }
    }

    @Override
    public void assistantSpeechError(@NotNull ZabaanSpeakable interaction, @Nullable Exception ex) {

    }

    public void queueInteraction(String state, Integer x, Integer y, Integer index) {
        Zabaan.getInstance().setScreenName("webview");
        CoordinateInteractionRequest cir = new CoordinateInteractionRequest.Builder()
                .setIndex(index)
                .setX(x)
                .setY(y)
                .setState(state)
                .setFingerAnimation(AnimationClickNormal.INSTANCE)
                .build();
        Zabaan.getInstance().playInteraction(cir);
    }

    public WebDemoActivity instance() {
        return this;
    }
}