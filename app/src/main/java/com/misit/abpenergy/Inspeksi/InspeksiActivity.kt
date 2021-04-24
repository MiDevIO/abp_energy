package com.misit.abpenergy.Inspeksi

import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.misit.abpenergy.Api.ApiClient
import com.misit.abpenergy.Api.ApiEndPoint
import com.misit.abpenergy.HazardReport.DetailHazardActivity
import com.misit.abpenergy.Inspeksi.Adapter.ListFormInspeksiAdapater
import com.misit.abpenergy.Inspeksi.Response.FormInspeksiResponse
import com.misit.abpenergy.Inspeksi.Response.FormItem
import com.misit.abpenergy.Login.LoginActivity
import com.misit.abpenergy.R
import com.misit.abpenergy.Utils.PopupUtil
import com.misit.abpenergy.Utils.PrefsUtil
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_inspeksi.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InspeksiActivity : AppCompatActivity(),ListFormInspeksiAdapater.OnItemClickListener {
    private var adapter: ListFormInspeksiAdapater? = null
    private var inspeksiList:MutableList<FormItem>?=null
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var page : Int=1
    private var visibleItem : Int=0
    private var total : Int=0
    private var pastVisibleItem : Int=0
    private var loading : Boolean=false
    var curentPosition: Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inspeksi)
        title="Form Inspeksi"
        PrefsUtil.initInstance(this)
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        if(PrefsUtil.getInstance().getBooleanState("IS_LOGGED_IN",false)){
            USERNAME = PrefsUtil.getInstance().getStringState(PrefsUtil.USER_NAME,"")
        }else{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        inspeksiList= ArrayList()
        adapter = ListFormInspeksiAdapater(this@InspeksiActivity,inspeksiList!!)
        val linearLayoutManager = LinearLayoutManager(this@InspeksiActivity)
        rvFormInspeksi?.layoutManager = linearLayoutManager
        rvFormInspeksi.adapter =adapter
        adapter?.setListener(this)


        swipeRefreshLayout = findViewById(R.id.pullRefreshInspeksi)
        swipeRefreshLayout.setOnRefreshListener(object :SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {
                rvFormInspeksi.adapter = adapter
                page=1
                inspeksiList?.clear()
                loadForm(page.toString())
            }
        })
        inspeksiList?.clear()
    }

    companion object{
        var USERNAME="USERNAME"
    }
    fun loadForm(hal:String){
        swipeRefreshLayout.isRefreshing=true
        val apiEndPoint = ApiClient.getClient(this)!!.create(ApiEndPoint::class.java)
        val call = apiEndPoint.getListFormInspeksi()
        call?.enqueue(object : Callback<FormInspeksiResponse> {
            override fun onFailure(call: Call<FormInspeksiResponse>, t: Throwable) {
                swipeRefreshLayout.isRefreshing=false
                Toasty.error(this@InspeksiActivity,"Error : $t", Toasty.LENGTH_SHORT).show()
                PopupUtil.dismissDialog()
            }

            override fun onResponse(call: Call<FormInspeksiResponse>, response: Response<FormInspeksiResponse>) {
                var listInspeksi = response.body()
                if(listInspeksi!=null){
                    if (listInspeksi.data!=null){
                        PopupUtil.showProgress(this@InspeksiActivity,"Loading...","Memuat Form Inspeksi!")
                        loading=true
                        inspeksiList!!.addAll(listInspeksi.data!!)
                        adapter?.notifyDataSetChanged()
                    }else{
                        curentPosition = (rvFormInspeksi.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                        inspeksiList!!.addAll(listInspeksi.data!!)
                        adapter?.notifyDataSetChanged()
                    }
                }
                rvFormInspeksi.addOnScrollListener(object : RecyclerView.OnScrollListener(){
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                        if (dy > 0) {
                            visibleItem = recyclerView.layoutManager!!.childCount
                            total = recyclerView.layoutManager!!.itemCount
                            pastVisibleItem =
                                (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                            if (loading) {
                                if ((visibleItem + pastVisibleItem) >= total) {
                                    loading = false
                                    page++
                                    loadForm(page.toString())
                                }
                            }
                        }
                    }
                    override fun onScrollStateChanged(
                        recyclerView: RecyclerView,
                        newState: Int
                    ) {
                        super.onScrollStateChanged(recyclerView, newState)
                    }
                })
                PopupUtil.dismissDialog()
                swipeRefreshLayout.isRefreshing=false

            }
        })
    }


    override fun onItemClick(idForm: String,nameForm: String) {
//        var intent = Intent(this@InspeksiActivity, DetailHazardActivity::class.java)
//        intent.putExtra(DetailHazardActivity.UID,uid.toString())
//        startActivity(intent)
    }
}
