package com.misit.abpenergy.HazardReport.Activity

import android.app.Activity
import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.misit.abpenergy.HazardReport.Adapter.ListHazardReportAdapter
import com.misit.abpenergy.HazardReport.Response.HazardItem
import com.misit.abpenergy.HazardReport.Service.HazardService
import com.misit.abpenergy.HazardReport.ViewModel.HeaderViewModel
import com.misit.abpenergy.Login.LoginActivity
import com.misit.abpenergy.R
import com.misit.abpenergy.Service.ConnectionService
import com.misit.abpenergy.Utils.*
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_hazard_report.*
import kotlinx.android.synthetic.main.activity_hazard_report.internetConnection
import kotlinx.android.synthetic.main.activity_main_page.*
import kotlinx.android.synthetic.main.index_new.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.joda.time.format.DateTimeFormat
import java.util.*
import kotlin.collections.ArrayList

class HazardReportActivity : AppCompatActivity(), ListHazardReportAdapter.OnItemClickListener,View.OnClickListener {

    private var adapter: ListHazardReportAdapter? = null
    private var hazardList:MutableList<HazardItem>?=null
    private var displayList:MutableList<HazardItem>?=null
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var page : Int=1
    private var visibleItem : Int=0
    private var total : Int=0
    private var pastVisibleItem : Int=0
    private var loading : Boolean=false
    var curentPosition: Int=0
    private var halamanTotal=1
    private lateinit var cld: ConnectionLiveData
    lateinit var viewModel: HeaderViewModel
    var tokenPassingReceiver : BroadcastReceiver?=null
    lateinit var connectionService:Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hazard_report)
        title="HAZARD REPORT"
        PrefsUtil.initInstance(this)
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        if(PrefsUtil.getInstance().getBooleanState("IS_LOGGED_IN",false)){
            USERNAME = PrefsUtil.getInstance().getStringState(PrefsUtil.USER_NAME,"")
            RULE = PrefsUtil.getInstance().getStringState(PrefsUtil.RULE,"")
        }else{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        reciever()
        connectionService = Intent(this@HazardReportActivity, ConnectionService::class.java)
//        startService(connectionService)
        viewModel = ViewModelProvider(this@HazardReportActivity).get(HeaderViewModel::class.java)
        hazardList= ArrayList()
        displayList = ArrayList()
        adapter = ListHazardReportAdapter(this@HazardReportActivity, RULE,"",displayList!!)
        val linearLayoutManager = LinearLayoutManager(this@HazardReportActivity)
        rvHazardList?.layoutManager = linearLayoutManager
        rvHazardList.adapter =adapter
        adapter?.setListener(this)
        DARI =PrefsUtil.getInstance().getStringState(PrefsUtil.AWAL_BULAN,"")
        SAMPAI = PrefsUtil.getInstance().getStringState(PrefsUtil.AKHIR_BULAN,"")
//        TOTAL_HAZARD_USER = PrefsUtil.getInstance().getStringState(PrefsUtil.TOTAL_HAZARD_USER!!,"0")
//        hazardVerify.text= TOTAL_HAZARD_USER
        txtTglDari.setText(DARI)
        txtTglSampai.setText(SAMPAI)
        swipeRefreshLayout = findViewById(R.id.pullRefreshHazard)
        swipeRefreshLayout.setOnRefreshListener(object :SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {
                rvHazardList.adapter = adapter
                page=1
                hazardList?.clear()
                displayList?.clear()
                startService(connectionService)

//                load(page.toString(), DARI, SAMPAI)
                pullRefreshHazard.visibility=View.VISIBLE
                shimmerHazard.visibility = View.GONE
            }
        })
        floatingNewHazard.setOnClickListener {
            var intent = Intent(this@HazardReportActivity, NewHazardActivity::class.java)
            startActivityForResult(intent,101)
        }
//        hazardList?.clear()
//        load("1",DARI, SAMPAI)
        txtTglDari.setOnClickListener(this)
        txtTglSampai.setOnClickListener(this)
        btnLoad.setOnClickListener(this)
        rvHazardList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItem = recyclerView.layoutManager!!.childCount
                    total = recyclerView.layoutManager!!.itemCount
                    pastVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    if(loading){
                        if ((visibleItem + pastVisibleItem) >= total) {
                            if(page < halamanTotal){
//                                pullRefreshHazard.visibility=View.GONE
//                                shimmerHazard.visibility = View.VISIBLE
                                loading = false
                                page =  page + 1
                                    viewModel.offlineHazard(this@HazardReportActivity,page, DARI, SAMPAI)
                            }
                            Log.d("TotalHalaman","visible : $visibleItem | Total : $total | Past : $pastVisibleItem | total halaman : $halamanTotal | page : $page")
                        }

                    }
                }
            }
        })
        hazardViewModel()
        checkNetworkConnection()

    }
    private fun checkNetworkConnection() {
        startService(connectionService)
        cld = ConnectionLiveData(application)
        cld.observe(this@HazardReportActivity,Observer{ isConnected->
            if (isConnected){
                startService(connectionService)
                shimmerHazard.visibility = View.GONE
                internetConnection.visibility = View.GONE
            }else{
                startService(connectionService)
                shimmerHazard.visibility = View.GONE
                internetConnection.visibility= View.VISIBLE
            }
        })
    }
        private fun hazardViewModel() {
            viewModel.hazardsObserver().observe(this@HazardReportActivity,Observer{
                if(it.size>0){
                    hazardList?.clear()
                    if(displayList!!.size==0){
                        displayList?.clear()
                        hazardList?.addAll(it)
                        hazardList?.let { it1 -> displayList?.addAll(it1) }
                        loading=true
                        adapter?.notifyDataSetChanged()
                        btnLoad.isEnabled = true
                        swipeRefreshLayout.isRefreshing=false

                        pullRefreshHazard.visibility = View.VISIBLE
                        shimmerHazard.visibility = View.GONE
                    }else{
                        curentPosition = (rvHazardList.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                        hazardList?.addAll(it)
                        hazardList?.let { it1 -> displayList?.addAll(it1) }
                        adapter?.notifyDataSetChanged()
                        loading=true
                        btnLoad.isEnabled = true
                        swipeRefreshLayout.isRefreshing=false

                        pullRefreshHazard.visibility = View.VISIBLE
                        shimmerHazard.visibility = View.GONE
                    }
                    Log.d("SetStatus","hazardList $it")
                }else{

                    pullRefreshHazard.visibility = View.VISIBLE
                    shimmerHazard.visibility = View.GONE
                    swipeRefreshLayout.isRefreshing=false
                    loading=false
                    btnLoad.isEnabled = true
                }
                Log.d("SetStatus","hazardList $hazardList")
                Log.d("SetStatus","displayList $displayList")

            })
            viewModel.hazardPaginate().observe(this@HazardReportActivity,Observer{
                halamanTotal = it
                Log.d("SetStatus","$halamanTotal")

            })
            viewModel.setStatus().observe(this@HazardReportActivity, Observer{
                swipeRefreshLayout.isRefreshing=true
                if(it){
                    viewModel.offlineHazard(this@HazardReportActivity,page, DARI, SAMPAI)
                    btnLoad.isEnabled = true
                }else{
                    pullRefreshHazard.visibility = View.VISIBLE
                    shimmerHazard.visibility = View.GONE
                    btnLoad.isEnabled = true
                }
                Log.d("SetStatus","$it")
            })
            viewModel.totalHazardUsers.observe(this@HazardReportActivity,Observer{
                if(it!=null){
                    totalHazard.text=it
                }else{
                    totalHazard.text="0"
                }
            })
            viewModel.hazardUserVerify.observe(this@HazardReportActivity, Observer{
                if(it!=null){
                    hazardVerify.text = it
                }else{
                    hazardVerify.text = "0"
                }
            })
        }
    override fun onResume() {
        Locale.setDefault(Locale.US)
        LocalBroadcastManager.getInstance(this@HazardReportActivity).registerReceiver(tokenPassingReceiver!!, IntentFilter("com.misit.abpenergy"))
        pullRefreshHazard.visibility=View.GONE
        shimmerHazard.visibility = View.VISIBLE
        super.onResume()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.newForm){
            var intent = Intent(this@HazardReportActivity, NewHazardActivity::class.java)
            startActivityForResult(intent,101)
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    companion object{
        var USERNAME="USERNAME"
        private  var DARI="01 January 2021"
        private  var SAMPAI="31 January 2021"
        private  var TOTAL_HAZARD_USER = "TOTAL_HAZARD_USER"
        private var RULE ="RULE"
    }
    override fun onItemClick(uid: String?) {
        var intent = Intent(this@HazardReportActivity, DetailHazardActivity::class.java)
        intent.putExtra(DetailHazardActivity.UID,uid.toString())
        intent.putExtra("Method","Offline")
        startActivity(intent)
    }
    override fun onUpdateClick(uid: String?) {
        Toasty.info(this@HazardReportActivity,uid!!).show()
    }
    override fun onVerify(uid: String?, option: Int?) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode==Activity.RESULT_OK && requestCode==101){
            Log.d("connectionService","Start")
            sendMessageToActivity(this,"HazardLoading","Loading")

            startService(connectionService)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onClick(v: View?) {
        hazardList?.clear()
        val fmt = DateTimeFormat.forPattern("d MMMM yyyy")
        if(v?.id==R.id.txtTglDari){
            var dari = fmt.parseLocalDate(DARI).toString()
            ConfigUtil.showDialogTgl(txtTglDari,this@HazardReportActivity)
        }
        if(v?.id==R.id.txtTglSampai){
            var sampai = fmt.parseLocalDate(SAMPAI).toString()
            ConfigUtil.showDialogTgl(txtTglSampai,this@HazardReportActivity)
        }
        if(v?.id==R.id.btnLoad){
            btnLoad.isEnabled = false
            page=1
            hazardList?.clear()
            displayList?.clear()
            pullRefreshHazard.visibility=View.GONE
            shimmerHazard.visibility = View.VISIBLE
            var dari = txtTglDari.text.toString()
            var sampai = txtTglSampai.text.toString()
            DARI = dari
            SAMPAI = sampai
            startService(connectionService)
        }
    }
    private fun reciever() {
        tokenPassingReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val bundle = intent.extras
                if (bundle != null) {
                    if (bundle.containsKey("bsConnection")) {
                        hazardList?.clear()
                        displayList?.clear()
                        val tokenData = bundle.getString("bsConnection")
                        Log.d("ServiceName","${tokenData} Index")
                        if(tokenData=="Online"){
                            page = 1
                            GlobalScope.launch(Dispatchers.IO) {
                                viewModel.onlineHazard(this@HazardReportActivity, DARI, SAMPAI)

                            }
                            btnLoad.isEnabled = true
                            internetConnection.visibility= View.GONE

                            Log.d("ConnectionCheck",tokenData)
                        }else if(tokenData=="Offline"){
                            hazardList?.clear()
                            displayList?.clear()
                                viewModel.offlineHazard(this@HazardReportActivity,page, DARI, SAMPAI)
                            btnLoad.isEnabled = true
                            Log.d("ConnectionCheck",tokenData)
                            Toasty.error(this@HazardReportActivity,"No Internet Connection").show()
                            internetConnection.visibility= View.VISIBLE
                        }else if(tokenData=="Disabled"){
                                viewModel.offlineHazard(this@HazardReportActivity,page, DARI, SAMPAI)
                            btnLoad.isEnabled = true
                            Log.d("ConnectionCheck",tokenData)
                            internetConnection.visibility= View.VISIBLE
                            Toasty.error(this@HazardReportActivity,"Network Disabled").show()
                        }
                    }
                    if(bundle.containsKey("SavingHazard")){
                        val tokenData = bundle.getString("SavingHazard")
                        if(tokenData=="HAZARD_DIBUAT"){
                            hazardList?.clear()
                            displayList?.clear()
                            startService(connectionService)
                        }
                    }
                    if(bundle.containsKey("FgHazard")){
                        val tokenData = bundle.getString("FgHazard")
                        if(tokenData=="FgHazardDone"){
                            stopService(Intent(this@HazardReportActivity, HazardService::class.java).apply {
                                this.action = Constants.SERVICE_STOP
                            })
                            hazardList?.clear()
                            displayList?.clear()
                            startService(connectionService)
                            cvLoadingSaving.visibility = View.GONE
                            Log.d("FgHazard","${tokenData}")
                        }
                        if(tokenData=="FgHazardSaving"){
                            cvLoadingSaving.visibility = View.VISIBLE
                            Glide.with(this@HazardReportActivity).load(R.drawable.abp).into(imgLoadingSaving)
                        }
                    }
                    if(bundle.containsKey("HazardLoading")){
                        val tokenData = bundle.getString("HazardLoading")
                        Log.d("BroadcastMessage","$tokenData")
                        if(tokenData=="Loading"){
                            cvLoadingSaving.visibility = View.VISIBLE
                            Glide.with(this@HazardReportActivity).load(R.drawable.abp).into(imgLoadingSaving)
                        }
                    }

                }
            }
        }
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(this@HazardReportActivity).unregisterReceiver(tokenPassingReceiver!!)
        super.onStop()
    }

    private fun sendMessageToActivity(c: Context, name:String, msg: String) {
        Log.d("BroadcastMessage","$msg")
        val intent = Intent()
        intent.action = "com.misit.abpenergy"
        intent.putExtra(name, msg)
        LocalBroadcastManager.getInstance(c).sendBroadcast(intent)
    }
}