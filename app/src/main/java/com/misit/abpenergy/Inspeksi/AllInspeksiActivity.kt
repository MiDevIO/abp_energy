package com.misit.abpenergy.Inspeksiimport android.content.Contextimport android.content.Intentimport androidx.appcompat.app.AppCompatActivityimport android.os.Bundleimport android.util.Logimport android.view.Viewimport android.view.Windowimport android.view.WindowManagerimport androidx.core.content.ContextCompatimport androidx.recyclerview.widget.LinearLayoutManagerimport androidx.recyclerview.widget.RecyclerViewimport androidx.swiperefreshlayout.widget.SwipeRefreshLayoutimport com.misit.abpenergy.Api.ApiClientimport com.misit.abpenergy.Api.ApiEndPointTwoimport com.misit.abpenergy.Inspeksi.Adapter.ALLInspectionAdapterimport com.misit.abpenergy.Inspeksi.Response.InpeksiItemimport com.misit.abpenergy.Login.LoginActivityimport com.misit.abpenergy.Rimport com.misit.abpenergy.Utils.PrefsUtilimport kotlinx.android.synthetic.main.activity_all_inspeksi.*import kotlinx.coroutines.Dispatchersimport kotlinx.coroutines.GlobalScopeimport kotlinx.coroutines.launchclass AllInspeksiActivity : AppCompatActivity() ,ALLInspectionAdapter.OnItemsClickListener, View.OnClickListener {    private var adapter: ALLInspectionAdapter?=null    private var listInspection:MutableList<InpeksiItem>?=null    private var page=0    private var visibleItem : Int=0    private var total : Int=0    private var pastVisibleItem : Int=0    var curentPosition: Int=0    lateinit var swipeRefreshLayout: SwipeRefreshLayout    private var loading : Boolean=false    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)        setContentView(R.layout.activity_all_inspeksi)        title ="Semua Inspeksi"        var actionBar = supportActionBar        actionBar?.setDisplayHomeAsUpEnabled(true)        PrefsUtil.initInstance(this)        val window: Window = this.window        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)        if(PrefsUtil.getInstance().getBooleanState("IS_LOGGED_IN",false)){            USERNAME = PrefsUtil.getInstance().getStringState(PrefsUtil.USER_NAME,"")        }else{            val intent = Intent(this, LoginActivity::class.java)            startActivity(intent)            finish()        }        listInspection = ArrayList()        adapter = ALLInspectionAdapter(this@AllInspeksiActivity,listInspection!!)        val linearLayoutManager = LinearLayoutManager(this)        rvAllInspeksiList?.layoutManager = linearLayoutManager        rvAllInspeksiList.adapter =adapter        adapter?.setListener(this@AllInspeksiActivity)        swipeRefreshLayout = findViewById(R.id.pullRefreshInspeksiList)        swipeRefreshLayout.setOnRefreshListener(object :SwipeRefreshLayout.OnRefreshListener{            override fun onRefresh() {                rvAllInspeksiList.adapter = adapter                page=1                listInspection?.clear()                loadInspeksi(page,this@AllInspeksiActivity)//                swipeRefreshLayout.isRefreshing=false                //PopupUtil.dismissDialog()            }        })        listInspection?.clear()        loadInspeksi(page,this@AllInspeksiActivity)        floatingNewInspeksi.setOnClickListener(this@AllInspeksiActivity)    }    private fun loadInspeksi(hal: Int,c:Context) {        swipeRefreshLayout.isRefreshing=true        GlobalScope.launch(Dispatchers.Main) {            val apiEndPoint = ApiClient.getClient(c)!!.create(ApiEndPointTwo::class.java)            val allInspeksi = apiEndPoint.allInspectionCorutine(hal)            if(allInspeksi!!.isSuccessful){                val res = allInspeksi.body()                if(res!=null){                    val allInspeksi = res.allInspection!!.inpeksiItem                    if(res!!.allInspection!=null){                        if(allInspeksi!=null){                            listInspection?.addAll(allInspeksi!!)                            adapter?.notifyDataSetChanged()                            Log.d("INSPEKSIALL",listInspection.toString())                        }else{                            curentPosition = (rvAllInspeksiList.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()                            listInspection!!.addAll(allInspeksi!!)                            adapter?.notifyDataSetChanged()                            Log.d("INSPEKSIALL","Inspeksi Null")                        }                    }else{                        Log.d("INSPEKSIALL","ALL Inspeksi Null")                    }                }else{                    Log.d("INSPEKSIALL","Result Null")                }            }else{                Log.d("INSPEKSIALL",allInspeksi.message().toString())            }            swipeRefreshLayout.isRefreshing=false            rvAllInspeksiList.addOnScrollListener(object : RecyclerView.OnScrollListener(){                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {                    if (dy > 0) {                        visibleItem = recyclerView.layoutManager!!.childCount                        total = recyclerView.layoutManager!!.itemCount                        pastVisibleItem =                            (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()                        if (loading) {                            if ((visibleItem + pastVisibleItem) >= total) {                                loading = false                                page++                                loadInspeksi(page,this@AllInspeksiActivity)                            }                        }                    }                }                override fun onScrollStateChanged(                    recyclerView: RecyclerView,                    newState: Int                ) {                    super.onScrollStateChanged(recyclerView, newState)                }            })        }    }    override fun onSupportNavigateUp(): Boolean {        onBackPressed()        return super.onSupportNavigateUp()    }    companion object{        var USERNAME = "USERNAME"    }    override fun onItemClick(idInspeksi: String) {    }    override fun onClick(v: View?) {        val c = this@AllInspeksiActivity        if(v?.id == R.id.floatingNewInspeksi){            var intent = Intent(c, InspeksiActivity::class.java)            startActivity(intent)        }    }}