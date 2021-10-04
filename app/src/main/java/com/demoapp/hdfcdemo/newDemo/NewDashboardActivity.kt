package com.demoapp.hdfcdemo.newDemo
import androidx.appcompat.app.AppCompatActivity
import com.zabaan.sdk.AssistantInteractionListener
import com.demoapp.hdfcdemo.newDemo.DashboardItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import com.demoapp.hdfcdemo.R
import android.content.Intent
import com.demoapp.hdfcdemo.newDemo.LoginActivity
import com.zabaan.sdk.Zabaan
import com.zabaan.sdk.AssistantStateListener
import com.demoapp.hdfcdemo.newDemo.DashboardAdapter
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import com.zabaan.sdk.internal.interaction.ViewInteractionRequest
import com.zabaan.common.ZabaanLanguages
import com.zabaan.common.ZabaanSpeakable
import com.zabaan.common.views.AnimationSwipeLeftNormal
import java.lang.Exception
import java.lang.IndexOutOfBoundsException
import java.util.*

class NewDashboardActivity : AppCompatActivity(), AssistantInteractionListener {
    var dashboardItems = ArrayList<DashboardItem>()
    var imgLogout: ImageView? = null
    var llm: LinearLayoutManager? = null
    var statePicker = ArrayList<String>()
    var isCVAPlaying = false
    var isAudioDownloaded = false
    private var rvDashboard: RecyclerView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_dashboard)
        imgLogout = findViewById(R.id.img_logout)
        imgLogout?.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(this@NewDashboardActivity, LoginActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
            finish()
        })
    }

    override fun onResume() {
        super.onResume()
        isAudioDownloaded = false
        setUpDashboardList()
        Zabaan.getInstance().show(this)
        Zabaan.getInstance().setScreenName("DashboardActivity")
        Zabaan.getInstance().setAssistantInteractionListener(this)
        Zabaan.getInstance().setAssistantStateListener(object : AssistantStateListener {
            override fun assistantAvailable() {
                if (isAudioDownloaded) playAudioForScreen()
            }

            override fun assistantClicked() {
                Zabaan.getInstance().stopZabaanInteraction()
                if (isCVAPlaying) {
                    isCVAPlaying = false
                } else {
                    playAudioForScreen()
                }
            }

            override fun assistantLanguageNotSupported() {}
        })
    }

    fun setUpDashboardList() {
        if (!dashboardItems.isEmpty()) dashboardItems.clear()
        val item1 = DashboardItem()
        item1.img = R.drawable.img_borrow
        item1.title = "PAY"
        item1.description = "UPI Payments, Money Transfer, Cards,\n Recharge, Bill Payment"
        val entry = arrayOf("UPI Payment", "Money Transfer", "Cards", "Pay Loan EMI", "Bill Pay")
        val subItems = ArrayList(Arrays.asList(*entry))
        item1.subItems = subItems
        dashboardItems.add(item1)
        val item2 = DashboardItem()
        item2.img = R.drawable.img_save
        item2.title = "SAVE"
        item2.description = "Accounts, Fixed Deposit, Recurring Deposit"
        dashboardItems.add(item2)
        val item3 = DashboardItem()
        item3.img = R.drawable.img_invest
        item3.title = "INVEST"
        item3.description = "Demat, Mutual Fund, Bonds"
        dashboardItems.add(item3)
        val item4 = DashboardItem()
        item4.img = R.drawable.img_pay
        item4.title = "BORROW"
        item4.description = "Personal Loan, Car Loan, Gold Loan, Educational Loan"
        dashboardItems.add(item4)
        val item5 = DashboardItem()
        item5.img = R.drawable.img_profile
        item5.title = "YOUR PROFILE"
        item5.description = "Personal Profile, Manage Alerts, Tax, Security"
        dashboardItems.add(item5)
        val item6 = DashboardItem()
        item6.img = R.drawable.img_settings
        item6.title = "SETTINGS"
        dashboardItems.add(item6)
        val item7 = DashboardItem()
        item7.img = R.drawable.img_power_off
        item7.title = "LOGOUT"
        dashboardItems.add(item7)
        statePicker.clear()
        statePicker.add("PAY")
        statePicker.add("SAVE")
        statePicker.add("INVEST")
        statePicker.add("BORROW")
        statePicker.add("YOUR PROFILE")
        statePicker.add("SETTINGS")
        statePicker.add("LOGOUT")
        rvDashboard = findViewById<View>(R.id.rv_dashboard) as RecyclerView
        val adapter = DashboardAdapter(dashboardItems, rvDashboard!!)
        // Attach the adapter to the recyclerview to populate items
        rvDashboard!!.adapter = adapter
        // Set layout manager to position the items
        llm = LinearLayoutManager(this@NewDashboardActivity)
        rvDashboard!!.layoutManager = llm
        rvDashboard!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isCVAPlaying) Zabaan.getInstance().stopZabaanInteraction()
            }
        })
    }

    fun playAudioForScreen() {
        Zabaan.getInstance().stopZabaanInteraction()
        if (rvDashboard != null && isAudioDownloaded) {
            var pos = 0
            if (llm != null) {
                pos = llm!!.findFirstCompletelyVisibleItemPosition()
            }
            var state = ""
            try {
                state = statePicker[pos]
            } catch (iex: IndexOutOfBoundsException) {
                println("" + iex.message)
            }
            if (!TextUtils.isEmpty(state)) {
                val viewHolder = rvDashboard!!.findViewHolderForAdapterPosition(pos)
                if (viewHolder != null) {
                    val request: ViewInteractionRequest = ViewInteractionRequest.Builder()
                        .setRootView(viewHolder.itemView)
                        .setViewId(R.id.tv_row_title)
                        .setState(state)
                        .build()
                    Zabaan.getInstance().playInteraction(request)
                }
            }
        }
    }

    override fun assistantSpeechDataDownloaded(s: ZabaanLanguages) {
        isAudioDownloaded = true
    }

    override fun assistantSpeechDataDownloadError() {}
    override fun assistantSpeechEnd(zabaanSpeakable: ZabaanSpeakable, isInterrupted: Boolean) {
        isCVAPlaying = false
        if (!isInterrupted) {
            val checkState = zabaanSpeakable.state
            val viewHolder: RecyclerView.ViewHolder?
            when (checkState) {
                "PAY" -> {
                    viewHolder = rvDashboard!!.findViewHolderForAdapterPosition(1)
                    if (viewHolder != null) {
                        println("" + viewHolder.itemView.parent.parent)
                        val request: ViewInteractionRequest =  ViewInteractionRequest.Builder()
                            .setRootView(viewHolder.itemView)
                            .setViewId(R.id.tv_row_title)
                            .setState("SAVE")
                            .build()
                        Zabaan.getInstance().playInteraction(request)
                    }
                }
                "SAVE" -> {
                    viewHolder = rvDashboard!!.findViewHolderForAdapterPosition(2)
                    if (viewHolder != null) {
                        val request: ViewInteractionRequest =  ViewInteractionRequest.Builder()
                            .setRootView(viewHolder.itemView)
                            .setViewId(R.id.tv_row_title)
                            .setState("INVEST")
                            .build()
                        Zabaan.getInstance().playInteraction(request)
                    }
                }
                "INVEST" -> {
                    viewHolder = rvDashboard!!.findViewHolderForAdapterPosition(3)
                    if (viewHolder != null) {
                        val request: ViewInteractionRequest =  ViewInteractionRequest.Builder()
                            .setRootView(viewHolder.itemView)
                            .setViewId(R.id.tv_row_title)
                            .setState("BORROW")
                            .build()
                        Zabaan.getInstance().playInteraction(request)
                    }
                }
                "BORROW" -> {
                    val lv = llm!!.findLastCompletelyVisibleItemPosition()
                    if (llm!!.findLastCompletelyVisibleItemPosition() >= 4) {
                        viewHolder = rvDashboard!!.findViewHolderForAdapterPosition(4)
                        if (viewHolder != null) {
                            val request: ViewInteractionRequest =  ViewInteractionRequest.Builder()
                                .setRootView(viewHolder.itemView)
                                .setViewId(R.id.tv_row_title)
                                .setState("YOUR PROFILE")
                                .build()
                            Zabaan.getInstance().playInteraction(request)
                        }
                    } else {
                        val request: ViewInteractionRequest =  ViewInteractionRequest.Builder()
                            .setViewId(R.id.img_add)
                            .setFingerAnimation(AnimationSwipeLeftNormal)
                            .setState("DEAL")
                            .build()
                        Zabaan.getInstance().playInteraction(request)
                    }
                }
                "YOUR PROFILE" -> if (llm!!.findLastCompletelyVisibleItemPosition() >= 5) {
                    viewHolder = rvDashboard!!.findViewHolderForAdapterPosition(5)
                    if (viewHolder != null) {
                        val request: ViewInteractionRequest =  ViewInteractionRequest.Builder()
                            .setRootView(viewHolder.itemView)
                            .setViewId(R.id.tv_row_title)
                            .setState("SETTINGS")
                            .build()
                        Zabaan.getInstance().playInteraction(request)
                    }
                } else {
                    val request: ViewInteractionRequest =  ViewInteractionRequest.Builder()
                        .setViewId(R.id.img_add)
                        .setFingerAnimation(AnimationSwipeLeftNormal)
                        .setState("DEAL")
                        .build()
                    Zabaan.getInstance().playInteraction(request)
                }
                "SETTINGS" -> if (llm!!.findLastCompletelyVisibleItemPosition() == 6) {
                    viewHolder = rvDashboard!!.findViewHolderForAdapterPosition(6)
                    if (viewHolder != null) {
                        val request: ViewInteractionRequest =  ViewInteractionRequest.Builder()
                            .setRootView(viewHolder.itemView)
                            .setViewId(R.id.tv_row_title)
                            .setState("LOGOUT")
                            .build()
                        Zabaan.getInstance().playInteraction(request)
                    }
                } else {
                    val request: ViewInteractionRequest =  ViewInteractionRequest.Builder()
                        .setViewId(R.id.img_add)
                        .setFingerAnimation(AnimationSwipeLeftNormal)
                        .setState("DEAL")
                        .build()
                    Zabaan.getInstance().playInteraction(request)
                }
                "LOGOUT" -> {
                    val request: ViewInteractionRequest =  ViewInteractionRequest.Builder()
                        .setViewId(R.id.img_add)
                        .setFingerAnimation(AnimationSwipeLeftNormal)
                        .setState("DEAL")
                        .build()
                    Zabaan.getInstance().playInteraction(request)
                }
                "UPIPAYMENT" -> {
                    viewHolder = rvDashboard!!.findViewHolderForAdapterPosition(0)
                    if (viewHolder != null) {
                        val idView = 2001
                        if (viewHolder.itemView.findViewById<View?>(idView) != null) {
                            val request1: ViewInteractionRequest =  ViewInteractionRequest.Builder()
                                .setViewId(R.id.tv_row_title)
                                .setViewId(2001)
                                .setState("MONEYTRANSFER")
                                .build()
                            Zabaan.getInstance().playInteraction(request1)
                        }
                    }
                }
                "MONEYTRANSFER" -> {
                    viewHolder = rvDashboard!!.findViewHolderForAdapterPosition(0)
                    if (viewHolder != null) {
                        val idView = 3002
                        if (viewHolder.itemView.findViewById<View?>(idView) != null) {
                            val request2: ViewInteractionRequest =  ViewInteractionRequest.Builder()
                                .setViewId(3002)
                                .setState("CARDS")
                                .build()
                            Zabaan.getInstance().playInteraction(request2)
                        }
                    }
                }
                "CARDS" -> {
                    viewHolder = rvDashboard!!.findViewHolderForAdapterPosition(0)
                    if (viewHolder != null) {
                        val idView = 4003
                        if (viewHolder.itemView.findViewById<View?>(idView) != null) {
                            val request3: ViewInteractionRequest =  ViewInteractionRequest.Builder()
                                .setViewId(4003)
                                .setState("PAYEMI")
                                .build()
                            Zabaan.getInstance().playInteraction(request3)
                        }
                    }
                }
                "PAYEMI" -> {
                    viewHolder = rvDashboard!!.findViewHolderForAdapterPosition(0)
                    if (viewHolder != null) {
                        val idView = 5004
                        if (viewHolder.itemView.findViewById<View?>(idView) != null) {
                            val request4: ViewInteractionRequest =  ViewInteractionRequest.Builder()
                                .setViewId(5004)
                                .setState("BILLPAY")
                                .build()
                            Zabaan.getInstance().playInteraction(request4)
                        }
                    }
                }
            }
        }
    }

    override fun assistantSpeechError(zabaanSpeakable: ZabaanSpeakable, e: Exception?) {}
    override fun assistantSpeechStart(zabaanSpeakable: ZabaanSpeakable) {
        isCVAPlaying = true
        println(zabaanSpeakable)
    }
}