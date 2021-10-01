package com.demoapp.hdfcdemo.newDemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.demoapp.hdfcdemo.R;
import com.zabaan.sdk.Zabaan;
import com.zabaan.sdk.internal.interaction.ViewInteractionRequest;

import java.util.ArrayList;
import java.util.List;

public class DashboardAdapter extends
        RecyclerView.Adapter<DashboardAdapter.ViewHolder> {

    private List<DashboardItem> listItems;
    Context context;
    RecyclerView rvType;

    // Pass in the contact array into the constructor
    public DashboardAdapter(ArrayList<DashboardItem> listItems, RecyclerView rv) {
        this.listItems = listItems;
        this.rvType = rv;
    }

    @NonNull
    @Override
    public DashboardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.row_dashboard, parent, false);

        // Return a new holder instance
        DashboardAdapter.ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardAdapter.ViewHolder holder, int position) {
        DashboardItem cardListItem = listItems.get(position);

        holder.tvTitle.setText(cardListItem.getTitle());
        if (!TextUtils.isEmpty(cardListItem.getDescription())) {
            holder.tvDescription.setText("" + cardListItem.getDescription());
        } else {
            holder.tvDescription.setVisibility(View.GONE);
        }

        holder.imgMain.setImageDrawable(ContextCompat.getDrawable(context, cardListItem.img));
        if (cardListItem.openSublayout) {
            holder.llDynamic.setVisibility(View.VISIBLE);
            if (cardListItem.getSubItems() != null && !cardListItem.getSubItems().isEmpty()) {
                holder.llDynamic.removeAllViews();
                addAllChild(holder.llDynamic, cardListItem.getSubItems());
            }
            cardListItem.openSublayout = true;
        } else {
            holder.llDynamic.setVisibility(View.GONE);
            cardListItem.openSublayout = false;
        }
    }

    public void addAllChild(LinearLayout llView, ArrayList<String> data) {

        for (int i = 0; i < data.size() ; i++) {
            TextView tv = new TextView(context);
            tv.setText(""+data.get(i));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,18);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            float factor = context.getResources().getDisplayMetrics().density;
            int marginVal = (int) (16 * factor);
            lp.setMargins(marginVal,marginVal,marginVal,marginVal);
            tv.setLayoutParams(lp);
            tv.setId(((i+1)*1000)+i);
            Log.e("Text ID", ""+ (((i+1)*1000)+i));
            llView.addView(tv);

            View view = new View(context);
            lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) ((int)1*factor));
            view.setLayoutParams(lp);
            view.setBackgroundColor(Color.parseColor("#666666"));
            llView.addView(view);

            if (tv.getText().toString().contains("EMI")) {
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        context.startActivity(new Intent(context, DetailActivity.class));
                    }
                });
            } else if (tv.getText().toString().contains("Bill Pay")) {
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        context.startActivity(new Intent(context, WebDemoActivity.class));
                    }
                });
            }
        }
        ViewInteractionRequest request = new ViewInteractionRequest.Builder()
                .setViewId(1000)
                .setState("UPIPAYMENT")
                .build();
        Zabaan.getInstance().playInteraction(request);
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView tvTitle;
        public TextView tvDescription;
        public ImageView imgMain;
        public LinearLayout llMain;
        public LinearLayout llDynamic;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_row_title);
            tvDescription = itemView.findViewById(R.id.tv_row_description);
            imgMain = itemView.findViewById(R.id.row_main_img);
            llMain = itemView.findViewById(R.id.ll_main);
            llDynamic = itemView.findViewById(R.id.ll_dynamic);
            llMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    llDynamic.setVisibility(View.VISIBLE);
                    DashboardItem cardListItem = listItems.get(getAdapterPosition());
                    if (cardListItem.openSublayout) {
                        Zabaan.getInstance().stopZabaanInteraction();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                llDynamic.removeAllViews();
                                llDynamic.setVisibility(View.GONE);
                            }
                        },100);
                        cardListItem.openSublayout = false;
                    } else if (cardListItem.getSubItems() != null && !cardListItem.getSubItems().isEmpty()) {
                        llDynamic.removeAllViews();
                        addAllChild(llDynamic, cardListItem.getSubItems());
                        cardListItem.openSublayout = true;
                    }
                }
            });
        }
    }
}
