package com.misit.abpenergy.Serviceimport android.content.Contextimport android.content.Intentimport android.graphics.Bitmapimport android.graphics.BitmapFactoryimport android.net.Uriimport android.util.Logimport androidx.localbroadcastmanager.content.LocalBroadcastManagerimport com.misit.abpenergy.Api.ApiClientimport com.misit.abpenergy.Api.ApiEndPointimport com.misit.abpenergy.Api.ApiEndPointTwoimport com.misit.abpenergy.DataSource.*import com.misit.abpenergy.HazardReport.SQLite.DataSource.RiskDataSourceimport com.misit.abpenergy.HazardReport.SQLite.DataSource.PengendalianDataSourceimport com.misit.abpenergy.HazardReport.SQLite.Model.RiskModelimport com.misit.abpenergy.HazardReport.SQLite.Model.PengendalianModelimport com.misit.abpenergy.Model.*import com.misit.abpenergy.NewIndexActivityimport com.misit.abpenergy.Utils.ConfigUtilimport com.misit.abpenergy.Utils.PrefsUtilimport kotlinx.coroutines.*import java.io.IOExceptionimport java.net.URLclass GetToken{    var TAG ="BgToken"    lateinit var kemungkinanDatasource: KemungkinanDataSource    lateinit var keparahanDatasource: KeparahanDataSource    lateinit var perusahaanDataSource: PerusahaanDataSource    lateinit var lokasiDatasource: LokasiDataSource    lateinit var riskDataSource: RiskDataSource    lateinit var pengendalianDataSource: PengendalianDataSource    lateinit var usersDataSource: UsersDataSource    lateinit var dataUserDatasource: DataUsersSource    suspend fun getToken(c:Context,username:String,name: String,msg: String) {        kemungkinanDatasource = KemungkinanDataSource(c)        keparahanDatasource = KeparahanDataSource(c)        perusahaanDataSource = PerusahaanDataSource(c)        lokasiDatasource = LokasiDataSource(c)        riskDataSource = RiskDataSource(c)        pengendalianDataSource = PengendalianDataSource(c)        usersDataSource = UsersDataSource(c)        dataUserDatasource = DataUsersSource(c)        GlobalScope.launch(Dispatchers.IO) {            Log.d("DataSave","Ok4")            val deferred=async {//                dataUserDatasource.deleteAll()                true            }            if(deferred.await()){                Log.d("DataSave","Ok3")                val apiEndPoint = ApiClient.getClient(c)?.create(ApiEndPoint::class.java)                val response = apiEndPoint?.dataUserCorutine(username)                if(response!=null){                    val usersModel = DataUsersModel()                    if(response.isSuccessful) {                        var res = response.body()                        if (res != null) {                            Log.d("DataSave",res.toString())                            if (res.dataUser != null) {                                Log.d("DataSave","Ok2")                                val dt = res.dataUser                                var dataHazard = if(res.dataHazard!=null){ res.dataHazard} else { 0 }                                var datInspeksi = if(res.datInspeksi!=null){ res.datInspeksi} else { 0 }                                usersModel.compString = 0                                usersModel.department = dt?.department                                usersModel.dept = dt?.dept                                usersModel.email = dt?.email                                usersModel.flag = dt?.flag                                usersModel.id_dept = dt?.idDept                                usersModel.id_perusahaan = dt?.idPerusahaan                                usersModel.id_sect = dt?.idSect                                usersModel.id_session = dt?.idSession                                usersModel.id_user = dt?.idUser//                                usersModel.inc = dt?.inc                                usersModel.level = dt?.level                                usersModel.nama_perusahaan = dt?.namaPerusahaan                                usersModel.nik = dt?.nik                                usersModel.password = dt?.password                                usersModel.perusahaan = dt?.perusahaan                                usersModel.photo_profile = dt?.photoProfile                                usersModel.rule = dt?.rule                                usersModel.sect = dt?.sect                                usersModel.section = dt?.section                                usersModel.status = dt?.status                                usersModel.tglentry = dt?.tglentry                                usersModel.time_in = dt?.timeIn                                usersModel.timelog = dt?.timelog                                usersModel.ttd = dt?.ttd                                usersModel.user_entry = dt?.userEntry                                usersModel.username = username                                usersModel.dataHazard = dataHazard                                usersModel.dataInspeksi = datInspeksi                                dataUserDatasource.insertItem(usersModel)                                loadKemungkinan(c,name,msg)                                Log.d("DataSave","Ok")                                var r = res.dataUser!!                                if (r.photoProfile != null) {                                    PrefsUtil.getInstance()                                        .setBooleanState(PrefsUtil.PHOTO_PROFILE, true)                                    PrefsUtil.getInstance()                                        .setStringState(PrefsUtil.PHOTO_URL, r.photoProfile)                                } else {                                    PrefsUtil.getInstance()                                        .setBooleanState(                                            PrefsUtil.PHOTO_PROFILE,                                            false                                        )                                }                                if (r.rule != null) {                                    NewIndexActivity.RULE = r.rule!!                                    PrefsUtil.getInstance()                                        .setStringState(                                            PrefsUtil.TOTAL_HAZARD_USER,                                            res!!.dataHazard!!.toString()                                        )                                    PrefsUtil.getInstance()                                        .setStringState("COMPANY_NAME", res!!.dataUser!!.namaPerusahaan)                                }                            }                        }                    }                }else{                    getToken(c,username, name, msg)                }            }            }    }    private fun sendMessageToActivity(c:Context,name:String,msg: String) {        Log.d(TAG,"sendMessageToActivity")        val intent = Intent()        intent.action = "com.misit.abpenergy"        intent.putExtra(name, msg)        LocalBroadcastManager.getInstance(c).sendBroadcast(intent)    }    private suspend fun loadKemungkinan(c: Context,name:String,msg: String) {        val apiEndPoint = ApiClient.getClient(c)?.create(ApiEndPoint::class.java)        var kemungkinanModel = KemungkinanModel()        kemungkinanDatasource.deleteAll()        CoroutineScope(Dispatchers.IO).launch {            val deferred = async {                val response = apiEndPoint?.kemungkinanCorutine()                if(response!=null) {                    if (response.isSuccessful) {                        val kemungkinanRes = response.body()?.kemungkinan                        if (kemungkinanRes != null) {                            kemungkinanRes.forEach {                                kemungkinanModel.idKemungkinan = it.idKemungkinan                                kemungkinanModel.kemungkinan = it.kemungkinan                                kemungkinanModel.flag = it.flag                                kemungkinanModel.nilai = it.nilai                                kemungkinanDatasource.insertItem(kemungkinanModel)                            }                        }                    }                    loadKeparahan(c, name, msg)                }else{                    loadKemungkinan(c, name, msg)                }            }            deferred.await()        }    }    private suspend fun loadKeparahan(c: Context,name:String,msg: String) {        CoroutineScope(Dispatchers.IO).launch {            val deferred = async {                val apiEndPoint = ApiClient.getClient(c)?.create(ApiEndPoint::class.java)                var keparahanModel = KeparahanModel()                keparahanDatasource.deleteAll()                val response = apiEndPoint?.keparahanCorutine()                if(response!=null) {                    if (response!!.isSuccessful) {                        val keparahanRes = response.body()?.keparahan                        if (keparahanRes != null) {                            keparahanRes.forEach {                                keparahanModel.idKeparahan = it.idKeparahan                                keparahanModel.keparahan = it.keparahan                                keparahanModel.flag = it.flag                                keparahanModel.nilai = it.nilai                                keparahanDatasource.insertItem(keparahanModel)                            }                        }                        loadPerusahaan(c, name, msg)                    }                }else{                    loadKeparahan(c, name, msg)                }            }            deferred.await()        }    }     private suspend fun loadPerusahaan(c: Context,name:String,msg: String) {         CoroutineScope(Dispatchers.IO).launch {             async {                 val apiEndPoint = ApiClient.getClient(c)?.create(ApiEndPointTwo::class.java)                 var perusahaanModel = PerusahaanModel()                 perusahaanDataSource.deleteAll()                 val response = apiEndPoint?.perusahaanCorutine()                 if(response!=null){                     if(response.isSuccessful){                         val perusahaanRes = response.body()?.company                         if(perusahaanRes!=null){                             perusahaanRes.forEach {                                 perusahaanModel.idPerusahaan = it.idPerusahaan                                 perusahaanModel.namaPerusahaan = it.namaPerusahaan                                 perusahaanModel.flag = it.flag                                 perusahaanModel.timeIn = it.timeIn                                 perusahaanDataSource.insertItem(perusahaanModel)                             }                         }                         loadLokasi(c,name,msg)                     }                 }else{                     loadPerusahaan(c, name, msg)                 }             }.await()        }    }    suspend private fun loadLokasi(c: Context,name:String,msg: String) {        CoroutineScope(Dispatchers.IO).launch {        async {            val apiEndPoint = ApiClient.getClient(c)?.create(ApiEndPoint::class.java)            var lokasiModel = LokasiModel()            lokasiDatasource.deleteAll()            val response = apiEndPoint?.lokasiCorutine()            if(response!=null){                if(response.isSuccessful){                    val lokasiRes = response.body()?.lokasi                    if(lokasiRes!=null){                        lokasiRes.forEach {                            lokasiModel.idLok = it.idLok                            lokasiModel.lokasi = it.lokasi                            lokasiModel.userInput = it.userInput                            lokasiModel.tglInput = it.tglInput                            lokasiDatasource.insertItem(lokasiModel)                        }                    }                    loadPengendalian(c,name,msg)                }            }else{                loadLokasi(c, name, msg)            }        }.await()        }    }    suspend private fun loadPengendalian(c: Context,name:String,msg: String) {        CoroutineScope(Dispatchers.IO).launch {        async {            val apiEndPoint = ApiClient.getClient(c)?.create(ApiEndPoint::class.java)            var pengendalianModel =                PengendalianModel()            pengendalianDataSource.deleteAll()            val response = apiEndPoint?.hirarkiCorutine()            if(response!=null){                if(response.isSuccessful){                    val lokasiRes = response.body()?.hirarki                    if(lokasiRes!=null){                        lokasiRes.forEach {                            pengendalianModel.idHirarki = it.idHirarki                            pengendalianModel.namaPengendalian = it.namaPengendalian                            pengendalianModel.userInput = it.userInput                            pengendalianModel.tglInput = it.tglInput                            pengendalianModel.flag = it.flag                            pengendalianDataSource.insertItem(pengendalianModel)                        }                    }                    loadRisk(c,name,msg)                }            }else{                loadPengendalian(c, name, msg)            }        }.await()        }    }    suspend private fun loadRisk(c: Context,name:String,msg: String) {        CoroutineScope(Dispatchers.IO).launch {            async {                val apiEndPoint = ApiClient.getClient(c)?.create(ApiEndPoint::class.java)                var riskModel =                    RiskModel()                riskDataSource.deleteAll()                val response = apiEndPoint?.riskCorutine()                if(response!=null){                    if (response.isSuccessful) {                        val riskRes = response.body()?.risk                        if (riskRes != null) {                            riskRes.forEach {                                riskModel.idRisk = it.idRisk                                riskModel.bgColor = it.bgColor                                riskModel.txtColor = it.txtColor                                riskModel.risk = it.risk                                riskModel.descRisk = it.descRisk                                riskModel.userInput = it.userInput                                riskModel.tglInput = it.tglInput                                riskDataSource.insertItem(riskModel)                            }                        }                        async {                            deleteImage(c, name, msg)                        }.await()                    }                }else{                    loadRisk(c, name, msg)                }            }.await()        }    }    suspend fun deleteImage(c: Context,name:String,msg: String){        CoroutineScope(Dispatchers.IO).launch {            async {                if( ConfigUtil.deleteInABPIMAGES(c,"PROFILE_IMAGE")){                    loadUsers(c,name,msg)                }            }.await()        }    }    suspend private fun loadUsers(c: Context,name:String,msg: String) {        CoroutineScope(Dispatchers.IO).launch {            val deferred = async {                usersDataSource.deleteAll()            }            val result = deferred.await()            Log.d("GETUSER","${result}")            if(result){                async {                    usersDataSource.openAccess()                    val apiEndPoint = ApiClient.getClient(c)?.create(ApiEndPointTwo::class.java)                    var usersModel = UsersModel()                    val response = apiEndPoint?.userAllCorutine()                    if(response!=null){                        if (response.isSuccessful) {                            val listUserAll = response.body()?.usersList                            if (listUserAll != null) {                                var deffered = async {                                listUserAll.forEach {                                        if (it.photoProfile != null) {                                            downloadImage(c, it.photoProfile!!, it.nik!!)                                            usersModel.offlinePhoto = "${it.nik!!}.jpg"                                        } else {                                            usersModel.offlinePhoto = null                                        }                                        Log.d("UsersList", "${it.nik} = ${it.photoProfile}")                                        usersModel.tglentry = it.tglentry                                        usersModel.level = it.level                                        usersModel.ttd = it.ttd                                        usersModel.photoProfile = it.photoProfile                                        usersModel.namaLengkap = it.namaLengkap                                        usersModel.idSession = it.idSession                                        usersModel.rule = it.rule                                        usersModel.perusahaan = it.perusahaan                                        usersModel.section = it.section                                        usersModel.namaPerusahaan = it.namaPerusahaan                                        usersModel.idUser = it.idUser                                        usersModel.dept = it.dept                                        usersModel.nik = it.nik                                        usersModel.password = it.password                                        usersModel.sect = it.sect                                        usersModel.department = it.department                                        usersModel.email = it.email                                        usersModel.username = it.username                                        usersModel.status = it.status                                            usersDataSource.insertItem(usersModel)                                }                            }                                deffered.await()                            }                        }                        GlobalScope.launch {                            sendMessageToActivity(c, name, msg)                        }                    }else{                        async {                            deleteImage(c, name, msg)                        }.await()                    }                }.await()                usersDataSource.closeAccess()            }        }    }    suspend fun downloadImage(c:Context,imagesUrl:String,fileName:String){        val url = URL(imagesUrl)        var dataRes:String?=null        val result: Deferred<Bitmap?> = GlobalScope.async {            url.toBitmap()        }        GlobalScope.launch(Dispatchers.IO) {            dataRes=  resDownload(result.await(),fileName,c)            Log.d("CorutineResult","${dataRes}")        }    }    fun URL.toBitmap(): Bitmap?{        return try {            BitmapFactory.decodeStream(openStream())        }catch (e: IOException){            null        }    }    suspend fun resDownload(result:Bitmap?,fileName:String,c: Context):String{        var fileUri:Uri?=null        try {            var bitmapPJ = result            GlobalScope.launch {                fileUri = ConfigUtil.downloadFile(bitmapPJ!!,fileName,c,"PROFILE_IMAGE")            }            Log.d("ErrorDowloadFile",fileUri.toString())        }catch (e:Exception){            Log.d("ErrorDowloadFile",e.toString())        }        return "${fileUri}"    }}