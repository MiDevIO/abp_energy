package com.misit.abpenergy.Sarpras.Serviceimport android.content.Contextimport android.content.Intentimport android.util.Logimport androidx.appcompat.app.AlertDialogimport androidx.localbroadcastmanager.content.LocalBroadcastManagerimport com.misit.abpenergy.Api.ApiClientimport com.misit.abpenergy.Api.ApiEndPointimport com.misit.abpenergy.Model.KaryawanModelimport com.misit.abpenergy.Sarpras.SQLite.PenumpangDataSourceimport com.misit.abpenergy.Sarpras.SQLite.PenumpangModelimport com.misit.abpenergy.Sarpras.SaranaResponse.PenumpangListModelimport com.misit.abpenergy.Utils.PrefsUtilimport kotlinx.coroutines.*class LoadSarana {    var listKaryawan : ArrayList<KaryawanModel>? = null    private var penumpangDataSource:PenumpangDataSource?=null    private var penumpangModel:ArrayList<PenumpangListModel>?=null    fun run(c:Context,name:String,msg:String){        penumpangDataSource = PenumpangDataSource(c)        penumpangModel = ArrayList()        listKaryawan = ArrayList()        PrefsUtil.initInstance(c)        corutineSarana(c,name,msg)    }    fun corutineSarana(c:Context,name: String,msg: String){        penumpangDataSource!!.deleteAll()        var len =0        listKaryawan?.clear()        var i = 1        val apiEndPoint = ApiClient.getClient(c)!!.create(ApiEndPoint::class.java)        GlobalScope.launch(Dispatchers.IO){            val response = apiEndPoint.corutineAllSarana()            if (response.isSuccessful) {                val r= response.body()?.karyawan                r.let {                    len = r?.size!!                    r?.forEach {                        penumpangModel?.add(                            PenumpangListModel(i.toLong(),it!!.nik!!,it!!.nama!!,it!!.jabatan!!)                        )                        i++                    }                    PrefsUtil.getInstance()                        .setStringState(                            PrefsUtil.AWAL_BULAN,                            response.body()?.awalBulan                        )                    PrefsUtil.getInstance()                        .setStringState(                            PrefsUtil.AKHIR_BULAN,                            response.body()?.akhirBulan                        )                }                copyPenumpang(penumpangModel!!)//                listPenumpang(c,name,msg)            }else{                koneksiInActive(c)            }            if(i>=len){                sendMessageToActivity(name,msg,c)            }        }    }    private fun copyPenumpang(penumpang: ArrayList<PenumpangListModel>){        CoroutineScope(Dispatchers.IO).launch{            var p = PenumpangModel()            penumpang.forEach {                p.id = it.id                p.nik = it.nik                p.nama = it.nama                p.jabatan = it.jabatan                penumpangDataSource?.insertItem(p)            }        }    }    fun koneksiInActive(c:Context){        AlertDialog.Builder(c)            .setTitle("Maaf Koneksi Internet Tidak Ada!")            .setPositiveButton("OK, Keluar", { dialog,                                               which ->            }).show()    }    private fun sendMessageToActivity(name: String,msg: String,c: Context) {        val intent = Intent()        intent.action = "com.misit.abpenergy"        intent.putExtra(name, msg)        LocalBroadcastManager.getInstance(c).sendBroadcast(intent)    }}