package com.misit.abpenergy.Utilsimport android.app.*import android.app.job.JobInfoimport android.app.job.JobSchedulerimport android.content.*import android.graphics.Bitmapimport android.graphics.BitmapFactoryimport android.media.RingtoneManagerimport android.net.ConnectivityManagerimport android.net.NetworkCapabilitiesimport android.net.Uriimport android.os.Buildimport android.provider.MediaStoreimport android.provider.Settingsimport android.util.Logimport android.view.Windowimport android.view.WindowManagerimport android.widget.*import androidx.appcompat.app.AlertDialogimport androidx.core.app.NotificationCompatimport androidx.core.content.ContextCompatimport androidx.localbroadcastmanager.content.LocalBroadcastManagerimport com.google.android.gms.tasks.OnCompleteListenerimport com.google.android.material.textfield.TextInputEditTextimport com.google.firebase.messaging.FirebaseMessagingimport com.misit.abpenergy.Api.ApiClientimport com.misit.abpenergy.Api.ApiEndPointimport com.misit.abpenergy.HazardReport.PhotoHazardActivityimport com.misit.abpenergy.HomePage.IndexActivityimport com.misit.abpenergy.Login.LoginActivityimport com.misit.abpenergy.Login.UbahDataActivityimport com.misit.abpenergy.Master.PerusahaanActivityimport com.misit.abpenergy.OlderClass.NewIndexActivityimport com.misit.abpenergy.Rimport com.misit.abpenergy.Rkb.DetailRkbActivityimport com.misit.abpenergy.Rkb.Response.CsrfTokenResponseimport com.misit.abpenergy.Rkb.RkbActivityimport com.misit.abpenergy.Sarpras.SarprasActivityimport com.misit.abpenergy.Service.ChangePWDActivityimport com.misit.abpenergy.Service.JobServicesimport kotlinx.android.synthetic.main.index_new.*import kotlinx.coroutines.*import org.joda.time.LocalDateimport org.joda.time.format.DateTimeFormatimport org.joda.time.format.DateTimeFormatterimport retrofit2.Callimport retrofit2.Callbackimport retrofit2.Responseimport java.io.*import java.net.*import java.text.SimpleDateFormatimport java.util.*object ConfigUtil {    lateinit var notificationManager: NotificationManager    lateinit var builder : NotificationCompat.Builder    var channelId = "com.misit.faceidchecklogptabp.notification"    fun deviceId(c:Context):String{        return Settings.Secure.getString(c.contentResolver, Settings.Secure.ANDROID_ID);    }    fun changeColor(activity: Activity){        val window: Window = activity.window        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)        window.statusBarColor = ContextCompat.getColor(activity, R.color.skyBlue)    }    fun dMY(tanggal: String):String{        val fmt: DateTimeFormatter = DateTimeFormat.forPattern("d MMMM, yyyy")        return LocalDate.parse(tanggal).toString(fmt)    }    fun resultIntent(activity: Activity){        val intent = Intent()        activity.setResult(Activity.RESULT_OK, intent)        activity.finish()    }    fun streamFoto(bitmap: Bitmap, file: File):File{        try {            // Get the file output stream            val stream: OutputStream = FileOutputStream(file)            //var uri = Uri.parse(file.absolutePath)            // Compress bitmap            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 30, stream)            // Flush the stream            stream.flush()            // Close stream            stream.close()        } catch (e: IOException){ // Catch the exception            e.printStackTrace()        }        return file    }    fun streamFotoCorutine(bitmap: Bitmap, file: File):File{        GlobalScope.launch {            try {                // Get the file output stream                val stream: OutputStream = FileOutputStream(file)                //var uri = Uri.parse(file.absolutePath)                // Compress bitmap                bitmap!!.compress(Bitmap.CompressFormat.JPEG, 30, stream)                // Flush the stream                stream.flush()                // Close stream                stream.close()            } catch (e: IOException){ // Catch the exception                e.printStackTrace()            }        }        return file    }    fun getToken(context: Context):String {        var csrf_token:String?=null        val apiEndPoint = ApiClient.getClient(context)!!.create(ApiEndPoint::class.java)        val call = apiEndPoint.getToken("csrf_token")        call?.enqueue(object : Callback<CsrfTokenResponse> {            override fun onFailure(call: Call<CsrfTokenResponse>, t: Throwable) {                Toast.makeText(context, "Error : $t", Toast.LENGTH_SHORT).show()                csrf_token = null            }            override fun onResponse(                call: Call<CsrfTokenResponse>,                response: Response<CsrfTokenResponse>            ) {                csrf_token = response.body()?.csrfToken            }        })        return csrf_token!!    }    //    DIALOG TANGGAL    fun showDialogTgl(inTgl: TextInputEditText, c: Context) {        val now = Calendar.getInstance()        val datePicker =            DatePickerDialog.OnDateSetListener { view: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->                now.set(Calendar.YEAR, year)                now.set(Calendar.MONTH, month)                now.set(Calendar.DAY_OF_MONTH, dayOfMonth)                inTgl.setText(SimpleDateFormat("dd MMMM yyyy", Locale.US).format(now.time))            }        DatePickerDialog(            c,            datePicker,            now.get(Calendar.YEAR),            now.get(Calendar.MONTH),            now.get(Calendar.DAY_OF_MONTH)        ).show()    }    //    DIALOG TANGGAL    //    DIALOG TANGGAL    fun dialogTglCurdate(inTgl: TextInputEditText, c: Context, curdate: String) {        val now = Calendar.getInstance()//        var dtNow =  SimpleDateFormat("dd/MM/yyyy").parse(curdate)//        now.set(dtNow.year,dtNow.month,dtNow.day)        val datePicker =            DatePickerDialog.OnDateSetListener { view: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->                now.set(Calendar.YEAR, year)                now.set(Calendar.MONTH, month)                now.set(Calendar.DAY_OF_MONTH, dayOfMonth)                inTgl.setText(SimpleDateFormat("dd MMMM yyyy", Locale.US).format(now.time))            }        DatePickerDialog(            c,            datePicker,            now.get(Calendar.YEAR),            now.get(Calendar.MONTH),            now.get(Calendar.DAY_OF_MONTH)        ).show()    }    //    DIALOG TANGGAL    //    DIALOG JAM    fun showDialogTime(inTime: TextInputEditText, c: Context) {        val now = Calendar.getInstance()        val timePicker = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute: Int ->            now.set(Calendar.HOUR_OF_DAY, hour)            now.set(Calendar.MINUTE, minute)            inTime.setText(SimpleDateFormat("HH:mm", Locale.US).format(now.time))        }        TimePickerDialog(            c,            timePicker,            now.get(Calendar.HOUR_OF_DAY),            now.get(Calendar.MINUTE),            true        ).show()    }//    DIALOG JAM//Change Passfun changePassword(c: Context, username: String){    val intent = Intent(c, ChangePWDActivity::class.java)    intent.putExtra("USERNAME", username)    c.startActivity(intent)}//Change Pass//Update Datafun updateData(c: Context, username: String){    val intent = Intent(c, UbahDataActivity::class.java)    intent.putExtra("USERNAME", username)    c.startActivity(intent)}//Update Data//masterPerusahaan    fun masterPerusahaan(c: Context){        val intent = Intent(c, PerusahaanActivity::class.java)        c.startActivity(intent)    }//masterPerusahaan//    Dialog PICK PICTUREfun showDialogOption(c: Activity, camera: Int, galery: Int){    val alertDialog = AlertDialog.Builder(c)    alertDialog.setTitle("Silahkan Pilih")    val animals = arrayOf<String>(        "Ambil Sebuah Gambar",        "Pilih Gambar dari galery"    )    alertDialog!!.setItems(animals, { dialog, which ->        when (which) {            0 -> openCamera(c, camera)            1 -> openGalleryForImage(c, galery)        }    })    alertDialog.create()    alertDialog.show()}    //    Dialog PICK PICTURE    fun openCamera(c: Activity, codeRequest: Int){        var intent = Intent(c, PhotoHazardActivity::class.java)        c.startActivityForResult(intent, codeRequest)    }    //OPEN GALERY    private fun openGalleryForImage(c: Activity, codeRequest: Int) {        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)        intent.type = "image/*"        c.startActivityForResult(intent, codeRequest)    }    //OPEN GALERY    // Method to save an bitmap to a file    fun bitmapToFile(bitmap: Bitmap, applicationContext: Context): Uri {        // Get the context wrapper        val wrapper = ContextWrapper(applicationContext)        // Initialize a new file instance to save bitmap object        var file = wrapper.getDir("Images", Context.MODE_PRIVATE)        file = File(file, "${UUID.randomUUID()}.jpg")        try{            // Compress the bitmap and save in jpg format            val stream:OutputStream = FileOutputStream(file)            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)            stream.flush()            stream.close()        }catch (e: IOException){            e.printStackTrace()        }        // Return the saved bitmap uri        return Uri.fromFile(File(file.absolutePath))    }    // Method to save an bitmap to a file//    Save File    fun saveFile(bitmap: Bitmap, c: Context, dir: String, fileName: String): Uri {        // Get the context wrapper        val dir = c.getExternalFilesDir(dir)        // Initialize a new file instance to save bitmap object        val file = File(dir, fileName)        try{            // Compress the bitmap and save in jpg format            val stream:OutputStream = FileOutputStream(file)            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)            stream.flush()            stream.close()        }catch (e: IOException){            e.printStackTrace()        }        // Return the saved bitmap uri        return Uri.fromFile(File(file.absolutePath))    }//    Save File    suspend fun downloadFile(    bitmap: Bitmap,    fileName: String,    applicationContext: Context,    dir: String): Uri {        var file = applicationContext.getExternalFilesDir(dir)        coroutineScope {            val deferred = async {                file = File(file, "${fileName}.jpg")                try{                    // Compress the bitmap and save in jpg format                    val stream:OutputStream = FileOutputStream(file)                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)                    stream.flush()                    stream.close()                }catch (e: IOException){                    e.printStackTrace()                }            }            deferred.await()        }        val uri =Uri.fromFile(File(file!!.absolutePath))    Log.d("dataSaveImage", "${uri}")        // Return the saved bitmap uri        return uri    }    fun createFolder(c: Context, folderName: String){        var file = File(c.getExternalFilesDir(null), folderName)        if (file.exists()){            Log.d("File_Dir", "Sudah Ada Dir")        }else{            file.mkdirs()            if(file.isDirectory){                Log.d("File_Dir", "Dir Berhasil Di Buat")            }else{                Log.d("File_Dir", "Dir Gagal Dibuat")            }        }    }    fun deleteInABPIMAGES(c: Context, dir: String):Boolean {        var children:Array<String>?=null        var dir = File(c.getExternalFilesDir(null), dir)        var z = 0        if (dir.isDirectory) {            children = dir.list()            for (i in children.indices) {                File(dir, children[i]).delete()                z++            }        }        if(z==children?.size){            return true        }else{            return false        }    }    fun getBitmapFromURL(src: String?): Bitmap? {        return try {            val url = URL(src)            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection            connection.setDoInput(true)            connection.connect()            val input: InputStream = connection.getInputStream()            BitmapFactory.decodeStream(input)        } catch (e: IOException) {            // Log exception            null        }    }    fun fileToBitmap(c: Context, dir: String, fileStr: String):Bitmap{        val dir = c!!.getExternalFilesDir(dir)        val file = File(dir, fileStr)        val filePath: String = file.absolutePath        val bitmap = BitmapFactory.decodeFile(filePath)        return bitmap    }    fun cekKoneksi(context: Context):Boolean{        var result = false        val connectivityManager =            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {            val networkCapabilities = connectivityManager.activeNetwork ?: return false            val actNw =                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false            result = when {                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true                else -> false            }        } else {            connectivityManager.run {                connectivityManager.activeNetworkInfo?.run {                    result = when (type) {                        ConnectivityManager.TYPE_WIFI -> true                        ConnectivityManager.TYPE_MOBILE -> true                        ConnectivityManager.TYPE_ETHERNET -> true                        else -> false                    }                }            }        }        return result    }    fun koneksiInActive(c: Activity){        AlertDialog.Builder(c)            .setTitle("Maaf Koneksi Internet Tidak Ada!")            .setPositiveButton("OK, Keluar", { dialog,                                               which ->                c.finish()            }).show()    }    fun playNotificationSound(c: Context, method: String) {        try {            val defSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)            val r = RingtoneManager.getRingtone(c, defSoundURI)            if(method=="Play"){                r.play()            }else if(method=="Stop"){                r.stop()            }        }catch (e: Exception){            Log.d("ER_Ringtone", e.toString())        }    }    @Throws(InterruptedException::class, IOException::class)    fun isConnected(): Boolean {        val command = "ping -c 1 abpjobsite.com"        return Runtime.getRuntime().exec(command).waitFor() == 0    }    suspend fun isOnline(): Boolean {        return try {            val timeoutMs = 1500            val sock = Socket()            val sockaddr: SocketAddress = InetSocketAddress("8.8.8.8", 53)            sock.connect(sockaddr, timeoutMs)            sock.close()            true        } catch (e: IOException) {            false        }    }    fun isJobServiceOn(context: Context, JOB_ID: Int): Boolean {        val scheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler        var hasBeenScheduled = false        for (jobInfo in scheduler.allPendingJobs) {            if (jobInfo.id == JOB_ID) {                hasBeenScheduled = true                break            }        }        return hasBeenScheduled    }    //    LogOut    fun logOut(a: Activity) {        AlertDialog.Builder(a)            .setTitle("Confirmation")            .setPositiveButton("OK , Sign Out", { dialog,                                                  which ->                if (PrefsUtil.getInstance().getBooleanState(                        "IS_LOGGED_IN", true                    )                ) {                    PrefsUtil.getInstance().setBooleanState(                        "IS_LOGGED_IN", false                    )                    PrefsUtil.getInstance().setStringState(                        PrefsUtil.USER_NAME, null                    )                    val intent = Intent(a, LoginActivity::class.java)                    a.startActivity(intent)                    a.finish()                }            })            .setNegativeButton("Cancel",                { dialog,                  which ->                    dialog.dismiss()                })            .show()    }    //LogOut    fun isMyServiceRunning(mClass: Class<*>, c: Context): Boolean {        val manager: ActivityManager = c.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager        for (service: ActivityManager.RunningServiceInfo in manager.getRunningServices(Integer.MAX_VALUE)){            if(mClass.name.equals(service.service.className)){                return true            }        }        return false    }    fun startStopService(        jvClass: Class<*>,        c: Context,        USERNAME: String,        tokenPassingReceiver: BroadcastReceiver    ) {        if(isMyServiceRunning(jvClass, c)){            LocalBroadcastManager.getInstance(c).unregisterReceiver(tokenPassingReceiver)            var intent = Intent(c, jvClass).apply {                this.action = Constants.SERVICE_STOP            }            c.stopService(intent)            Log.d("ServiceName", "${jvClass} Stop")        }else{            var intent = Intent(c, jvClass).apply {                this.action = Constants.SERVICE_START            }            intent.putExtra("username", USERNAME)            c.startService(intent)            Log.d("ServiceName", "${jvClass} Start")        }    }    suspend fun downloadImage(c: Context, imagesUrl: String, fileName: String, dir: String) {        Log.d("DownloadResult", "Download Start")        val url = URL(imagesUrl)        var dataRes: String? = null        GlobalScope.launch(Dispatchers.IO) {            val result: Deferred<Bitmap?> = GlobalScope.async {                url.toBitmap()            }            dataRes = resDownload(result.await(), fileName, c, dir)            Log.d("DownloadResult", "${dataRes}")        }        }        fun URL.toBitmap(): Bitmap?{            return try {                BitmapFactory.decodeStream(openStream())            }catch (e: IOException){                null            }        }    suspend fun resDownload(result: Bitmap?, fileName: String, c: Context, dir: String):String{        var fileUri:Uri?=null        GlobalScope.launch(Dispatchers.IO) {            try {                var bitmapPJ = result                    fileUri = downloadFile(bitmapPJ!!, fileName, c, dir)                Log.d("ErrorDowloadFile", fileUri.toString())            }catch (e: Exception){                Log.d("ErrorDowloadFile", e.toString())            }        }        return "${fileUri}"    }    fun firebase():String{        var token=""        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->            if (!task.isSuccessful) {                Log.w("FIREBASE", "Fetching FCM registration token failed", task.exception)                return@OnCompleteListener            }            // Get new FCM registration token            token = task.result        })    return token    }    //    NotifRkb    fun rkbNotif(tabindex: String?, c: Context,noRkb:String?){//        var intent = Intent(c, RkbActivity::class.java)//        intent.putExtra(RkbActivity.USERNAME, IndexActivity.USERNAME)//        intent.putExtra(RkbActivity.DEPARTMENT, IndexActivity.DEPARTMENT)//        intent.putExtra(RkbActivity.SECTON, IndexActivity.SECTON)//        intent.putExtra(RkbActivity.LEVEL, IndexActivity.LEVEL)//        intent.putExtra(RkbActivity.Tab_INDEX, tabindex)//        intent.putExtra(RkbActivity.TIPE, "notif")        val intent=Intent(c, DetailRkbActivity::class.java)        intent.putExtra(DetailRkbActivity.NO_RKB,noRkb)        c.startActivity(intent)    }//    NotifRkb//    Sarpras Notif  fun sarprasNotif(c: Context){    var intent = Intent(        c,        SarprasActivity::class.java    )    intent.putExtra(RkbActivity.TIPE, "notif")    c.startActivity(intent)}    //    Sarpras Notif    fun jobScheduler(c:Context,scheduler: JobScheduler?){        Log.d("Createing","Job Service")        val componentName = ComponentName(c, JobServices::class.java)        val jobInfo = JobInfo.Builder(Constants.JOB_SERVICE_ID,componentName)            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)            .setPersisted(true)            .setPeriodic(900000)            .build()        val resultCode = scheduler?.schedule(jobInfo)        if(resultCode == JobScheduler.RESULT_SUCCESS){            Log.d("JobScheduler","Job Scheduled")        }else{            Log.d("JobScheduler","Job Scheduled Failed")        }    }    fun stopJobScheduler(scheduler: JobScheduler?){        scheduler?.cancel(Constants.JOB_SERVICE_ID)        scheduler?.cancelAll()        PrefsUtil.getInstance().setBooleanState("StartScheduler",false)    }    fun checkJobPending(scheduler: JobScheduler?){        scheduler?.allPendingJobs?.forEach {            Log.d("allPendingJobs","${it.id}")        }    }    private fun playNotificationSound(c:Context) {        try {            val defSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)            val r = RingtoneManager.getRingtone(c,defSoundURI)            r.play()        }catch (e: java.lang.Exception){            Log.d("ER_Ringtone",e.toString())        }    }    fun showNotification(c: Context,title: String?, body: String?,intent: Intent,requestCode:Int,notifName:String) {        var pendingIntent = PendingIntent.getActivity(c,requestCode,intent,PendingIntent.FLAG_UPDATE_CURRENT)        var dSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)        notificationManager = c.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){            var oChannel = NotificationChannel(notifName,title,NotificationManager.IMPORTANCE_HIGH)            oChannel.enableVibration(true)            oChannel.enableLights(true)            notificationManager.createNotificationChannel(oChannel)            builder = NotificationCompat.Builder(c,notifName)                .setSmallIcon(R.drawable.abp_white)                .setColor(R.drawable.abp_blue)                .setContentTitle(title)                .setContentText(body)                .setSound(dSoundUri)                .setAutoCancel(true)                .setGroup(notifName)                .setPriority(NotificationCompat.PRIORITY_HIGH)                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)                .setStyle(NotificationCompat.BigTextStyle().bigText(body))                .setContentIntent(pendingIntent)        }else{            builder = NotificationCompat.Builder(c,notifName)                .setSmallIcon(R.drawable.abp_white)                .setColor(R.drawable.abp_blue)                .setContentTitle(title)                .setContentText(body)                .setGroup(notifName)                .setPriority(NotificationCompat.PRIORITY_HIGH)                .setAutoCancel(true)                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)                .setSound(dSoundUri)                .setStyle(NotificationCompat.BigTextStyle().bigText(body))                .setContentIntent(pendingIntent)        }        var idRand = (0 until 100).random()        notificationManager.notify(idRand,builder.build())        playNotificationSound(c)    }    fun showNotificationGroup(c: Context,title: String?, body: String?,intent: Intent,requestCode:Int,notifName:String) {        var pendingIntent = PendingIntent.getActivity(c,requestCode,intent,PendingIntent.FLAG_UPDATE_CURRENT)        var dSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)        notificationManager = c.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){            var oChannel = NotificationChannel(notifName,title,NotificationManager.IMPORTANCE_HIGH)            oChannel.enableVibration(true)            oChannel.enableLights(true)            notificationManager.createNotificationChannel(oChannel)            builder = NotificationCompat.Builder(c,notifName)                .setSmallIcon(R.drawable.abp_white)                .setColor(R.drawable.abp_blue)                .setContentTitle(title)                .setContentText(body)                .setSound(dSoundUri)                .setAutoCancel(true)                .setGroup(notifName)                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)                .setGroupSummary(true)                .setPriority(NotificationCompat.PRIORITY_HIGH)                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)                .setStyle(NotificationCompat.BigTextStyle().bigText(body))                .setContentIntent(pendingIntent)        }else{            builder = NotificationCompat.Builder(c,notifName)                .setSmallIcon(R.drawable.abp_white)                .setColor(R.drawable.abp_blue)                .setContentTitle(title)                .setContentText(body)                .setGroup(notifName)                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)                .setGroupSummary(true)                .setPriority(NotificationCompat.PRIORITY_HIGH)                .setAutoCancel(true)                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)                .setSound(dSoundUri)                .setStyle(NotificationCompat.BigTextStyle().bigText(body))                .setContentIntent(pendingIntent)        }        for (i:Int in 1..5){            notificationManager.notify(i,builder.build())        }//        var idRand = (1 until 100).random()//        notificationManager.notify(idRand,builder.build())//        playNotificationSound(c)    }}