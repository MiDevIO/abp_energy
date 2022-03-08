package com.misit.abpenergy.HSE.Inspeksi.Activityimport androidx.appcompat.app.AppCompatActivityimport android.os.Bundleimport android.util.Logimport android.view.Viewimport android.view.Windowimport android.view.WindowManagerimport androidx.core.content.ContextCompatimport androidx.recyclerview.widget.LinearLayoutManagerimport com.misit.abpenergy.Api.ApiClientimport com.misit.abpenergy.Api.ApiEndPointTwoimport com.misit.abpenergy.HSE.Inspeksi.Adapter.DetailInspeksiAdapterimport com.misit.abpenergy.HSE.Inspeksi.Adapter.PicaDetailAdapterimport com.misit.abpenergy.HSE.Inspeksi.Adapter.TeamDetailAdapterimport com.misit.abpenergy.HSE.Inspeksi.Response.*import com.misit.abpenergy.HSE.Inspeksi.SQLite.Model.ItemsInspeksiModelsimport com.misit.abpenergy.HSE.Inspeksi.SQLite.Model.SubItemsModelsimport com.misit.abpenergy.Rimport com.misit.abpenergy.Utils.ConfigUtilimport com.misit.abpenergy.Utils.PopupUtilimport com.misit.abpenergy.Utils.PrefsUtilimport kotlinx.android.synthetic.main.activity_detail_inspeksi.*import retrofit2.Callimport retrofit2.Callbackimport retrofit2.Responseclass DetailInspeksiActivity : AppCompatActivity() {    private var teamAdapter : TeamDetailAdapter?=null    private var picaAdapter : PicaDetailAdapter?=null    private var detailInspeksiAdapater : DetailInspeksiAdapter?=null    private var subItems:MutableList<SubItemsModels>?=null    private var teamList:MutableList<TeamInspeksiItem>?=null    private var picaList:MutableList<InspeksiPicaDetailItem>?=null    private var idInspeksi:String?=null    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)        setContentView(R.layout.activity_detail_inspeksi)        PrefsUtil.initInstance(this)        val window: Window = this.window        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)        var actionBar = supportActionBar        actionBar?.setDisplayHomeAsUpEnabled(true)        idInspeksi = intent.getStringExtra("idInspeksi")        var tglInspeksi = intent.getStringExtra("tglInspeksi")        var perusahaanInspeksi = intent.getStringExtra("perusahaanInspeksi")        var lokasiInspeksi = intent.getStringExtra("lokasiInspeksi")        var saranInspeksi = intent.getStringExtra("saranInspeksi")        var namaInspeksi = intent.getStringExtra("namaInspeksi")        var formId = intent.getStringExtra("formId")        title = namaInspeksi        tvTanggalInspeksi.text = ConfigUtil.dMY(tglInspeksi!!)        tvInspeksiPerusahaan.text = perusahaanInspeksi        tvInspeksiLokasi.text = lokasiInspeksi        tvInspeksiSaran.text = saranInspeksi        teamList=ArrayList()        picaList=ArrayList()        subItems = ArrayList()        pullRefreshNewInspeksi.visibility = View.GONE        lnLoadingNewInspeksi.visibility = View.VISIBLE        val linearLayoutManager = LinearLayoutManager(this@DetailInspeksiActivity)        val linearLayoutManager1 = LinearLayoutManager(this@DetailInspeksiActivity)        val linearLayoutManager2 = LinearLayoutManager(this@DetailInspeksiActivity)        teamAdapter = TeamDetailAdapter(this@DetailInspeksiActivity,teamList!!)        rvTeamInspeksiDetail?.layoutManager = linearLayoutManager2        rvTeamInspeksiDetail.adapter=teamAdapter        picaAdapter = PicaDetailAdapter(this@DetailInspeksiActivity,picaList!!)        rvPicaInspeksiDetail?.layoutManager = linearLayoutManager1        rvPicaInspeksiDetail.adapter=picaAdapter!!        detailInspeksiAdapater =            formId?.let {                DetailInspeksiAdapter(this@DetailInspeksiActivity,                    it,idInspeksi!!,subItems!!)            }        rvListInspeksiDetail?.layoutManager = linearLayoutManager        rvListInspeksiDetail.adapter = detailInspeksiAdapater        loadTeamInspeksi(idInspeksi!!)        if (formId != null) {            loadDetail(formId,idInspeksi!!)        }    }    private fun loadDetail(idforms:String,idInspeksi: String){        val apiEndPoint = ApiClient.getClient(this)!!.create(ApiEndPointTwo::class.java)        val call = apiEndPoint.getListDetInspeksi(idforms,idInspeksi)        call?.enqueue(object : Callback<ItemDetailInspeksiResponse> {            override fun onFailure(call: Call<ItemDetailInspeksiResponse>, t: Throwable) {                Log.d("ERRORLOG",t.toString())                PopupUtil.dismissDialog()            }            override fun onResponse(call: Call<ItemDetailInspeksiResponse>, response: Response<ItemDetailInspeksiResponse>) {                var listInspeksi = response.body()                if (listInspeksi != null) {                    if (listInspeksi != null) {                        listInspeksi.itemDetailInspeksi!!.forEach {                            val subItem = SubItemsModels()                            var listItemArr: ArrayList<ItemsInspeksiModels>? = null                            listItemArr = ArrayList()                            subItem.nameSub = it.nameSub                            subItem.numSub = it.numSub                            it.items!!.forEach {                                val itemInspeksi = ItemsInspeksiModels()                                itemInspeksi.idForm = it.idForm                                itemInspeksi.idList = it.idList                                itemInspeksi.idSub = it.idSub                                itemInspeksi.listInspeksi = it.listInspeksi                                itemInspeksi.flag = it.flag                                itemInspeksi.tglInput = it.tglInput                                itemInspeksi.answere = it.answer                                listItemArr.add(itemInspeksi)                            }                            subItem.items = listItemArr                            subItems!!.add(subItem)                        }                        detailInspeksiAdapater?.notifyDataSetChanged()                        pullRefreshNewInspeksi.visibility = View.VISIBLE                    }                }                PopupUtil.dismissDialog()                lnLoadingNewInspeksi.visibility = View.GONE            }        })    }    private fun listPica(idInspeksi:String){        picaList?.clear()//        PopupUtil.showProgress(this@DetailInspeksiActivity,"Loading...","Sedang Memproses . . .!")        val apiEndPoint = ApiClient.getClient(this@DetailInspeksiActivity)!!.create(ApiEndPointTwo::class.java)        val call = apiEndPoint.listInspeksiPica(idInspeksi)        call?.enqueue(object : Callback<InspeksiPicaDetailResponse> {            override fun onResponse(                call: Call<InspeksiPicaDetailResponse>,                response: Response<InspeksiPicaDetailResponse>            ) {                pullRefreshNewInspeksi.visibility = View.VISIBLE                lnLoadingNewInspeksi.visibility = View.GONE                var r = response.body()                if(r!=null){                    picaList?.addAll(r!!.inspeksiPicaDetail!!)                    picaAdapter?.notifyDataSetChanged()                    PopupUtil.dismissDialog()                }else{                    PopupUtil.dismissDialog()                }            }            override fun onFailure(call: Call<InspeksiPicaDetailResponse>, t: Throwable) {                PopupUtil.dismissDialog()                Log.d("ErrorPica",t.toString())            }        })    }    private fun loadTeamInspeksi(inpeksiId:String){//        PopupUtil.showProgress(this@DetailInspeksiActivity,"Loading...","Memuat Team Inspeksi!")        val apiEndPoint = ApiClient.getClient(this@DetailInspeksiActivity)!!.create(ApiEndPointTwo::class.java)        val call = apiEndPoint.teamInspeksi(inpeksiId)        call?.enqueue(object : Callback<TeamDetailResponse> {            override fun onResponse(                call: Call<TeamDetailResponse>,                response: Response<TeamDetailResponse>            ) {                var r = response.body()                if(r!=null){                    if(r.teamInspeksi!=null)                    {                        var team = r!!.teamInspeksi                        teamList?.addAll(team!!)                        listPica(idInspeksi!!)                        teamAdapter?.notifyDataSetChanged()                        PopupUtil.dismissDialog()                    }else{                        Log.d("ErrorLoadTeam",r.teamInspeksi.toString())                        PopupUtil.dismissDialog()                    }                    PopupUtil.dismissDialog()                }else{                    Log.d("ErrorLoadTeam",r.toString())                    PopupUtil.dismissDialog()                }                PopupUtil.dismissDialog()            }            override fun onFailure(call: Call<TeamDetailResponse>, t: Throwable) {                Log.d("ErrorLoadTeam",t.toString())                PopupUtil.dismissDialog()            }        })    }}