package com.demoapp.hdfcdemo.newDemo

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import com.demoapp.hdfcdemo.R
import android.widget.LinearLayout
import android.widget.TextView
import android.util.TypedValue
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.zabaan.sdk.internal.interaction.ViewInteractionRequest
import com.zabaan.sdk.Zabaan
import java.util.ArrayList

class DashboardAdapter(listItems: ArrayList<DashboardItem>, rv: RecyclerView) :
    RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {
    private val listItems: List<DashboardItem>
    var context: Context? = null
    var rvType: RecyclerView
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView =
            inflater.inflate(R.layout.row_dashboard, parent, false)

        // Return a new holder instance
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cardListItem = listItems[position]
        holder.tvTitle.text = cardListItem.title

        holder.tvDescription.visibility = View.GONE
        cardListItem.description.let {
            holder.tvDescription.text = "" + cardListItem.description
        }

        holder.imgMain.setImageDrawable(ContextCompat.getDrawable(context!!, cardListItem.img))

        if (cardListItem.isOpenSublayout) {
            holder.llDynamic.visibility = View.VISIBLE
            if (cardListItem.subItems != null && cardListItem.subItems!!.isNotEmpty()) {
                holder.llDynamic.removeAllViews()
                addAllChild(holder.llDynamic, cardListItem.subItems!!)
            }
            cardListItem.isOpenSublayout = true
        } else {
            holder.llDynamic.visibility = View.GONE
            cardListItem.isOpenSublayout = false
        }
    }

    fun addAllChild(llView: LinearLayout, data: ArrayList<String>) {
        for (i in data.indices) {
            val tv = TextView(context)
            tv.text = "" + data[i]
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f)
            var lp = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val factor = context!!.resources.displayMetrics.density
            val marginVal = (16 * factor).toInt()
            lp.setMargins(marginVal, marginVal, marginVal, marginVal)
            tv.layoutParams = lp
            tv.id = (i + 1) * 1000 + i
            Log.e("Text ID", "" + ((i + 1) * 1000 + i))
            llView.addView(tv)
            val view = View(context)
            lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (1 * factor).toInt())
            view.layoutParams = lp
            view.setBackgroundColor(Color.parseColor("#666666"))
            llView.addView(view)
            if (tv.text.toString().contains("EMI")) {
                tv.setOnClickListener {
                    context!!.startActivity(
                        Intent(
                            context,
                            DetailActivity::class.java
                        )
                    )
                }
            }
        }
        val request: ViewInteractionRequest = ViewInteractionRequest.Builder()
            .setViewId(1000)
            .setState("UPIPAYMENT")
            .build()
        Zabaan.getInstance().playInteraction(request)
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        var tvTitle: TextView
        var tvDescription: TextView
        var imgMain: ImageView
        var llMain: LinearLayout
        var llDynamic: LinearLayout

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        init {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            tvTitle = itemView.findViewById(R.id.tv_row_title)
            tvDescription = itemView.findViewById(R.id.tv_row_description)
            imgMain = itemView.findViewById(R.id.row_main_img)
            llMain = itemView.findViewById(R.id.ll_main)
            llDynamic = itemView.findViewById(R.id.ll_dynamic)
            llMain.setOnClickListener {
                llDynamic.visibility = View.VISIBLE
                val cardListItem = listItems[adapterPosition]
                if (cardListItem.isOpenSublayout) {
                    Zabaan.getInstance().stopZabaanInteraction()
                    Handler().postDelayed({
                        llDynamic.removeAllViews()
                        llDynamic.visibility = View.GONE
                    }, 100)
                    cardListItem.isOpenSublayout = false
                } else if (cardListItem.subItems != null && cardListItem.subItems!!.isNotEmpty()) {
                    llDynamic.removeAllViews()
                    addAllChild(llDynamic, cardListItem.subItems!!)
                    cardListItem.isOpenSublayout = true
                }
            }
        }
    }

    // Pass in the contact array into the constructor
    init {
        this.listItems = listItems
        rvType = rv
    }
}