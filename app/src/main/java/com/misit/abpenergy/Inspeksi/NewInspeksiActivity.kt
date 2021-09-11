package com.misit.abpenergy.Inspeksiimport android.Manifestimport android.app.Activityimport android.content.Contextimport android.content.Intentimport android.content.pm.PackageManagerimport androidx.appcompat.app.AppCompatActivityimport android.os.Bundleimport android.util.Logimport android.view.Viewimport androidx.appcompat.app.AlertDialogimport androidx.core.app.ActivityCompatimport androidx.core.content.ContextCompatimport androidx.recyclerview.widget.LinearLayoutManagerimport com.misit.abpenergy.Api.ApiClientimport com.misit.abpenergy.Api.ApiEndPointimport com.misit.abpenergy.Api.ApiEndPointTwoimport com.misit.abpenergy.HazardReport.Response.HazardReportResponseimport com.misit.abpenergy.Inspeksi.Adapter.ListInspeksiAdapterimport com.misit.abpenergy.Inspeksi.Adapter.PicaAdapterimport com.misit.abpenergy.Inspeksi.Adapter.TeamInspeksiAdapterimport com.misit.abpenergy.Inspeksi.Response.*import com.misit.abpenergy.Inspeksi.SQLite.ItemDataSourceimport com.misit.abpenergy.Inspeksi.SQLite.ItemModelsimport com.misit.abpenergy.Login.CompanyActivityimport com.misit.abpenergy.Login.LoginActivityimport com.misit.abpenergy.Rimport com.misit.abpenergy.Rkb.Response.CsrfTokenResponseimport com.misit.abpenergy.Service.BarcodeScannerActivityimport com.misit.abpenergy.Utils.ConfigUtilimport com.misit.abpenergy.Utils.PopupUtilimport com.misit.abpenergy.Utils.PrefsUtilimport es.dmoral.toasty.Toastyimport kotlinx.android.synthetic.main.activity_new_inspeksi.*import okhttp3.MultipartBodyimport okhttp3.RequestBody.Companion.toRequestBodyimport retrofit2.Callimport retrofit2.Callbackimport retrofit2.Responseimport java.util.*import kotlin.collections.ArrayListclass NewInspeksiActivity : AppCompatActivity(),View.OnClickListener {    private var adapter: ListInspeksiAdapter? = null    private var teamAdapter : TeamInspeksiAdapter?=null    private var picaAdapter : PicaAdapter?=null    private var subItems:MutableList<SubItemsModels>?=null    private var teamList:MutableList<TeamInspeksiTempItem>?=null    private var picaList:MutableList<InspeksiPicaItem>?=null    private var companyDipilih:String?=null    private var idCompany:String?=null    private var page : Int=1    private var loading : Boolean=false    private var uniqueID:String?=null    private var formId:String?=null    private var csrf_token:String?=null    private var itemCounter = ItemDataSource(this@NewInspeksiActivity)    private var itemModels= ItemModels()    var curentPosition: Int=0    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)        setContentView(R.layout.activity_new_inspeksi)        val actionBar = supportActionBar        actionBar?.setDisplayHomeAsUpEnabled(true)        verifyStoragePermissions(this,this)        uniqueID = UUID.randomUUID().toString()        formId = intent.getStringExtra(IDFORM)        var nameForm = intent.getStringExtra(NAMEFORM)        companyDipilih = ""        idCompany = ""        title = "FORM ${nameForm}".capitalize()        PrefsUtil.initInstance(this)        if(PrefsUtil.getInstance().getBooleanState("IS_LOGGED_IN",false)){            USERNAME = PrefsUtil.getInstance().getStringState(PrefsUtil.USER_NAME,"")            var nikTeam = PrefsUtil.getInstance().getStringState(PrefsUtil.NIK,"")            addTeamInspeksi(uniqueID!!, formId!!,nikTeam)            PopupUtil.dismissDialog()        }else{            val intent = Intent(this, LoginActivity::class.java)            startActivity(intent)            finish()        }        var delete = itemCounter.deleteAll()        if(delete){            Log.d("DeleteItem","Berhasil")        }else{            Log.d("DeleteItem","Gagal")        }        itemModels.UNIQUEID = uniqueID        itemModels.YES =0        itemModels.NO = 0        itemModels.TOTAL = 0        var insert = itemCounter.insertItem(itemModels)        if (insert<0){            Log.d("Insert_Item","Gagal")        }else{            Log.d("Insert_Item","Berhasil")        }        subItems= ArrayList()        teamList = ArrayList()        picaList = ArrayList()        adapter = ListInspeksiAdapter(this@NewInspeksiActivity,formId!!,uniqueID!!,            USERNAME,subItems!!)        val linearLayoutManager = LinearLayoutManager(this@NewInspeksiActivity)        val linearLayoutManager1 = LinearLayoutManager(this@NewInspeksiActivity)        val linearLayoutManager2 = LinearLayoutManager(this@NewInspeksiActivity)        rvListInspeksi?.layoutManager = linearLayoutManager        rvListInspeksi.adapter =adapter//        adapter?.setListener(this)        teamAdapter = TeamInspeksiAdapter(this@NewInspeksiActivity,teamList!!)        rvTeamInspeksi?.layoutManager = linearLayoutManager2        rvTeamInspeksi.adapter=teamAdapter        picaAdapter = PicaAdapter(this@NewInspeksiActivity,picaList!!)        rvPicaInspeksi?.layoutManager = linearLayoutManager1        rvPicaInspeksi.adapter=picaAdapter!!        teamList?.clear()        subItems?.clear()        picaList?.clear()        pullRefreshNewInspeksi.visibility = View.GONE        lnLoadingNewInspeksi.visibility = View.VISIBLE        loadInspeksi(formId!!,page.toString())        addTeamQrCode.setOnClickListener(this)        btnBatalInspeksi.setOnClickListener(this)        inTglInspeksi.setOnClickListener(this)        addPicaInspeksi.setOnClickListener(this)        inInspeksiPerusahaan.setOnClickListener(this)        btnSimpanInspeksi.setOnClickListener(this)    }    private fun verifyStoragePermissions(context: Context, activity: Activity) {        val permission = ContextCompat.checkSelfPermission(context,            Manifest.permission.READ_EXTERNAL_STORAGE)        val permission1 = ContextCompat.checkSelfPermission(context,            Manifest.permission.WRITE_EXTERNAL_STORAGE)        val permission2 = ContextCompat.checkSelfPermission(context,            Manifest.permission.READ_PHONE_STATE)        val permission3 = ContextCompat.checkSelfPermission(context,            Manifest.permission.CAMERA)        if (permission != PackageManager.PERMISSION_GRANTED) {            Log.i("FaceId", "READ_EXTERNAL_STORAGE Permission to record denied")            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),11)//            finish()        }        if (permission1 != PackageManager.PERMISSION_GRANTED) {            Log.i("FaceId", "WRITE_EXTERNAL_STORAGE Permission to record denied")            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),12)//            finish()        }        if (permission2 != PackageManager.PERMISSION_GRANTED) {            Log.i("FaceId", "READ_PHONE_STATE Permission to record denied")            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE),13)//            finish()        }        if (permission3 != PackageManager.PERMISSION_GRANTED) {            Log.i("FaceId", "READ_PHONE_STATE Permission to record denied")            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA),13)//            finish()        }    }    override fun onClick(v: View?) {        if(v?.id==R.id.addTeamQrCode){            var intent = Intent(this@NewInspeksiActivity,BarcodeScannerActivity::class.java)            intent.putExtra("aktivitas","addTeam")            startActivityForResult(intent,100)        }        if(v?.id==R.id.btnBatalInspeksi){            onBackPressed()        }        if(v?.id==R.id.inTglInspeksi){            ConfigUtil.showDialogTgl(inTglInspeksi,this@NewInspeksiActivity)        }        if(v?.id==R.id.addPicaInspeksi){            var intent = Intent(this@NewInspeksiActivity,PicaInspeksiActivity::class.java)            intent.putExtra("formId",formId)            intent.putExtra("uniqueID",uniqueID)            startActivityForResult(intent,101)        }        if(v?.id==R.id.inInspeksiPerusahaan){            var intent = Intent(this@NewInspeksiActivity,CompanyActivity::class.java)            intent.putExtra("companyDipilih",companyDipilih)            startActivityForResult(intent,102)        }        if (v?.id==R.id.btnSimpanInspeksi){            simpanInspeksi()        }    }    private fun simpanInspeksi() {        val getItem = itemCounter.getItem(uniqueID!!)        if(getItem!=null){            var total = getItem.YES+getItem.NO            if(total<getItem.TOTAL){                Toasty.info(this@NewInspeksiActivity,"Harap Periksa Isian Anda!").show()            }else{                if (!isValidate()) {                    return                }                PopupUtil.showProgress(this@NewInspeksiActivity,"Loading...","Sedang Memproses . . .!")                var idUnik = uniqueID.toString().toRequestBody(MultipartBody.FORM)                var formId = formId.toString().toRequestBody(MultipartBody.FORM)                var inTglInspeksi = inTglInspeksi.text.toString().toRequestBody(MultipartBody.FORM)                var inInspeksiPerusahaan = idCompany.toString().toRequestBody(MultipartBody.FORM)                var inInspeksiLokasi = inInspeksiLokasi.text.toString().toRequestBody(MultipartBody.FORM)                var inInspeksiSaran = inInspeksiSaran.text.toString().toRequestBody(MultipartBody.FORM)                var csrf = csrf_token!!.toRequestBody(MultipartBody.FORM)                var username = USERNAME.toRequestBody(MultipartBody.FORM)                val apiEndPoint = ApiClient.getClient(this@NewInspeksiActivity )!!.create(ApiEndPointTwo::class.java)                var call= apiEndPoint.inspeksiSave(idUnik,formId,username,inTglInspeksi,inInspeksiPerusahaan,inInspeksiLokasi,inInspeksiSaran,csrf)                call?.enqueue(object:Callback<ItemTempResponse>{                    override fun onResponse(                        call: Call<ItemTempResponse>,                        response: Response<ItemTempResponse>                    ) {                        var r = response.body()                        if(r!=null){                            if(r.success){                                Toasty.success(this@NewInspeksiActivity,"Berhasil Membuat Inspeksi!").show()                                deleteInspeksiTemp(uniqueID!!)                                PopupUtil.dismissDialog()                            }else{                                Toasty.error(this@NewInspeksiActivity,"Error! Harap Menyimpan Ulang").show()                                PopupUtil.dismissDialog()                            }                        }else{                            Toasty.error(this@NewInspeksiActivity,"Error! Harap Menyimpan Ulang").show()                            PopupUtil.dismissDialog()                        }                    }                    override fun onFailure(call: Call<ItemTempResponse>, t: Throwable) {                        Log.d("ErrorSaveInspeksi",t.toString())                        PopupUtil.dismissDialog()                    }                })            }        }else{            Toasty.error(this@NewInspeksiActivity,"NOT OK").show()        }    }    fun isValidate():Boolean {        clearError()        if (inTglInspeksi.text!!.isEmpty()) {            tilTglInspeksi.error = "Please Input Someting"            inTglInspeksi.requestFocus()            return false        }        if (inInspeksiPerusahaan.text!!.isEmpty()) {            tilInspeksiPerusahaan.error = "Please Input Someting"            inInspeksiPerusahaan.requestFocus()            return false        }        if (inInspeksiLokasi.text!!.isEmpty()) {            tilInspeksiLokasi.error = "Please Input Someting"            inInspeksiLokasi.requestFocus()            return false        }        if (inInspeksiSaran.text!!.isEmpty()) {            tilInspeksiSaran.error = "Please Input Someting"            inInspeksiSaran.requestFocus()            return false        }        return true    }    private fun clearError() {        tilTglInspeksi.error=null        tilInspeksiPerusahaan.error=null        tilInspeksiLokasi.error=null        tilInspeksiSaran.error=null    }    override fun onResume() {        getToken()        super.onResume()    }    fun loadInspeksi(idforms:String,hal:String){        ITEMCOUNTER=0        PopupUtil.showProgress(            this@NewInspeksiActivity,"Loading...","Memuat Form Inspeksi!")        val apiEndPoint = ApiClient.getClient(this)!!.create(ApiEndPoint::class.java)        val call = apiEndPoint.getListSubInspeksi(idforms)        call?.enqueue(object : Callback<InspeksiGroupsResponse> {            override fun onFailure(call: Call<InspeksiGroupsResponse>, t: Throwable) {                Log.d("ERRORLOG",t.toString())                PopupUtil.dismissDialog()            }            override fun onResponse(call: Call<InspeksiGroupsResponse>, response: Response<InspeksiGroupsResponse>) {                var listInspeksi = response.body()                if (listInspeksi != null) {                    if (listInspeksi != null) {                        loading = true                        listInspeksi.itemInspeksi!!.forEach {                            val subItem = SubItemsModels()                            var listItemArr: ArrayList<ItemsInspeksiModels>? = null                            listItemArr = ArrayList()                            subItem.nameSub = it.nameSub                            subItem.numSub = it.numSub                            it.items!!.forEach {                                val itemInspeksi = ItemsInspeksiModels()                                itemInspeksi.idForm = it.idForm                                itemInspeksi.idList = it.idList                                itemInspeksi.idSub = it.idSub                                itemInspeksi.listInspeksi = it.listInspeksi                                itemInspeksi.flag = it.flag                                itemInspeksi.tglInput = it.tglInput                                listItemArr.add(itemInspeksi)                            }                            subItem.items = listItemArr                            subItems!!.add(subItem)                        }                        adapter?.notifyDataSetChanged()                        pullRefreshNewInspeksi.visibility = View.VISIBLE                    }                }                PopupUtil.dismissDialog()                lnLoadingNewInspeksi.visibility = View.GONE            }        })    }    private fun getToken() {        val apiEndPoint = ApiClient.getClient(this)!!.create(ApiEndPoint::class.java)        val call = apiEndPoint.getToken("csrf_token")        call?.enqueue(object : Callback<CsrfTokenResponse> {            override fun onResponse(                call: Call<CsrfTokenResponse>,                response: Response<CsrfTokenResponse>            ) {                csrf_token = response.body()?.csrfToken            }            override fun onFailure(call: Call<CsrfTokenResponse>, t: Throwable) {                Log.d("csrf_token",t.toString())            }        })    }    override fun onSupportNavigateUp(): Boolean {        onBackPressed()        return super.onSupportNavigateUp()    }    companion object{        var USERNAME="USERNAME"        var IDFORM = "IDFORM"        var NAMEFORM = "NAMEFORM"        var ITEMCOUNTER = 0    }    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {        if(resultCode==Activity.RESULT_OK && requestCode==100){            var nikTeam = data!!.getStringExtra("NikTeam")            addTeamInspeksi(uniqueID!!,formId!!,nikTeam)            Log.d("addTeam",nikTeam)        }        if(resultCode==Activity.RESULT_OK && requestCode==101){            listPica(uniqueID!!)        }        if(resultCode==Activity.RESULT_OK && requestCode==102){            companyDipilih = data?.getStringExtra("companyDipilih")            idCompany = data?.getStringExtra("idCompany")            inInspeksiPerusahaan.setText(companyDipilih)        }        super.onActivityResult(requestCode, resultCode, data)    }    override fun onBackPressed() {        areYouSure("Informasi","Apakah anda yakin?")    }    private fun areYouSure(titleDialog:String,msgDialog:String){        val builder = AlertDialog.Builder(this)        builder.setTitle(titleDialog)        builder.setMessage(msgDialog)        builder.setPositiveButton("Tidak") { dialog, which ->        }        builder.setNegativeButton("Ya") { dialog, which ->            deleteInspeksiTemp(uniqueID!!)        }        builder.show()    }    private fun addTeamInspeksi(idTemp:String,idForm:String,nikTeam:String){        PopupUtil.showProgress(this@NewInspeksiActivity,"Loading...","Menambah Team Inspeksi!")        val apiEndPoint = ApiClient.getClient(this@NewInspeksiActivity)!!.create(ApiEndPoint::class.java)        val call = apiEndPoint.addTeamInspeksi(idTemp,idForm,nikTeam)        call?.enqueue(object : Callback<ItemTempResponse> {            override fun onResponse(                call: Call<ItemTempResponse>,                response: Response<ItemTempResponse>            ) {                var r = response.body()                if(r!=null){                    teamList?.clear()                    loadTeamInspeksi(uniqueID!!)                    PopupUtil.dismissDialog()                }else{                    PopupUtil.dismissDialog()                }                PopupUtil.dismissDialog()            }            override fun onFailure(call: Call<ItemTempResponse>, t: Throwable) {                Log.d("ErrAddTeam",t.toString())                PopupUtil.dismissDialog()            }        })    }    private fun loadTeamInspeksi(idTemp:String){//        PopupUtil.showProgress(this@NewInspeksiActivity,"Loading...","Memuat Team Inspeksi!")        val apiEndPoint = ApiClient.getClient(this@NewInspeksiActivity)!!.create(ApiEndPoint::class.java)        val call = apiEndPoint.teamInspeksiTemp(idTemp)        call?.enqueue(object : Callback<TeamInspeksiTempResponse> {            override fun onResponse(                call: Call<TeamInspeksiTempResponse>,                response: Response<TeamInspeksiTempResponse>            ) {                var r = response.body()                if(r!=null){                    if(r.teamInspeksiTemp!=null)                    {                        var team = r!!.teamInspeksiTemp                        teamList?.addAll(team!!)                        teamAdapter?.notifyDataSetChanged()                        PopupUtil.dismissDialog()                    }else{                        Log.d("ErrorLoadTeam",r.toString())                        PopupUtil.dismissDialog()                    }                    PopupUtil.dismissDialog()                }else{                    Log.d("ErrorLoadTeam",r.toString())                    PopupUtil.dismissDialog()                }            }            override fun onFailure(call: Call<TeamInspeksiTempResponse>, t: Throwable) {                Log.d("ErrorLoadTeam",t.toString())                PopupUtil.dismissDialog()            }        })    }    private fun deleteInspeksiTemp(idTemp:String){        PopupUtil.showProgress(this@NewInspeksiActivity,"Loading...","Sedang Memproses . . .!")        val apiEndPoint = ApiClient.getClient(this@NewInspeksiActivity)!!.create(ApiEndPoint::class.java)        val call = apiEndPoint.deleteInspeksiTemp(idTemp)        call?.enqueue(object : Callback<ItemTempResponse> {            override fun onResponse(                call: Call<ItemTempResponse>,                response: Response<ItemTempResponse>            ) {                var r = response.body()                if(r!=null){                        finish()                    PopupUtil.dismissDialog()                }else{                    deleteInspeksiTemp(uniqueID!!)                    PopupUtil.dismissDialog()                }            }            override fun onFailure(call: Call<ItemTempResponse>, t: Throwable) {                Log.d("deleteTempInspeksi",t.toString())                PopupUtil.dismissDialog()            }        })    }    private fun listPica(idTemp:String){        picaList?.clear()        PopupUtil.showProgress(this@NewInspeksiActivity,"Loading...","Sedang Memproses . . .!")        val apiEndPoint = ApiClient.getClient(this@NewInspeksiActivity)!!.create(ApiEndPoint::class.java)        val call = apiEndPoint.listInspeksiPica(idTemp)        call?.enqueue(object : Callback<ListInspeksiPicaResponse> {            override fun onResponse(                call: Call<ListInspeksiPicaResponse>,                response: Response<ListInspeksiPicaResponse>            ) {                var r = response.body()                if(r!=null){                    picaList?.addAll(r!!.inspeksiPica!!)                    picaAdapter?.notifyDataSetChanged()                    PopupUtil.dismissDialog()                }else{                    PopupUtil.dismissDialog()                }            }            override fun onFailure(call: Call<ListInspeksiPicaResponse>, t: Throwable) {                PopupUtil.dismissDialog()                Log.d("ErrorPica",t.toString())            }        })    }}