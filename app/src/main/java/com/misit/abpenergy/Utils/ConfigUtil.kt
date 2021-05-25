package com.misit.abpenergy.Utilsimport android.app.Activityimport android.app.DatePickerDialogimport android.app.TimePickerDialogimport android.content.Contextimport android.content.DialogInterfaceimport android.content.Intentimport android.graphics.Bitmapimport android.graphics.BitmapFactoryimport android.provider.MediaStoreimport android.view.Windowimport android.view.WindowManagerimport android.widget.DatePickerimport android.widget.Toastimport androidx.appcompat.app.AlertDialogimport androidx.core.content.ContextCompatimport com.google.android.material.textfield.TextInputEditTextimport com.misit.abpenergy.Api.ApiClientimport com.misit.abpenergy.Api.ApiEndPointimport com.misit.abpenergy.HazardReport.PhotoHazardActivityimport com.misit.abpenergy.Login.UbahDataActivityimport com.misit.abpenergy.Master.PerusahaanActivityimport com.misit.abpenergy.Rimport com.misit.abpenergy.Rkb.Response.CsrfTokenResponseimport com.misit.abpenergy.Service.ChangePWDActivityimport kotlinx.android.synthetic.main.activity_new_hazard.*import org.joda.time.LocalDateimport org.joda.time.format.DateTimeFormatimport org.joda.time.format.DateTimeFormatterimport retrofit2.Callimport retrofit2.Callbackimport retrofit2.Responseimport java.io.Fileimport java.io.FileOutputStreamimport java.io.IOExceptionimport java.io.OutputStreamimport java.text.SimpleDateFormatimport java.util.*object ConfigUtil {    fun changeColor(activity: Activity){        val window: Window = activity.window        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)        window.statusBarColor = ContextCompat.getColor(activity, R.color.skyBlue)    }    fun dMY(tanggal:String):String{        val fmt: DateTimeFormatter = DateTimeFormat.forPattern("d MMMM, yyyy")        return LocalDate.parse(tanggal).toString(fmt)    }    fun resultIntent(activity: Activity){        val intent = Intent()        activity.setResult(Activity.RESULT_OK,intent)        activity.finish()    }    fun streamFoto(bitmap: Bitmap,file:File):File{        try {            // Get the file output stream            val stream: OutputStream = FileOutputStream(file)            //var uri = Uri.parse(file.absolutePath)            // Compress bitmap            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 20, stream)            // Flush the stream            stream.flush()            // Close stream            stream.close()        } catch (e: IOException){ // Catch the exception            e.printStackTrace()        }        return file    }    fun getToken(context: Context):String {        var csrf_token:String?=null        val apiEndPoint = ApiClient.getClient(context)!!.create(ApiEndPoint::class.java)        val call = apiEndPoint.getToken("csrf_token")        call?.enqueue(object : Callback<CsrfTokenResponse> {            override fun onFailure(call: Call<CsrfTokenResponse>, t: Throwable) {                Toast.makeText(context, "Error : $t", Toast.LENGTH_SHORT).show()                csrf_token=null            }            override fun onResponse(                call: Call<CsrfTokenResponse>,                response: Response<CsrfTokenResponse>            ) {                csrf_token= response.body()?.csrfToken            }        })        return csrf_token!!    }    //    DIALOG TANGGAL    fun showDialogTgl(inTgl: TextInputEditText,c: Context) {        val now = Calendar.getInstance()        val datePicker =            DatePickerDialog.OnDateSetListener { view: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->                now.set(Calendar.YEAR, year)                now.set(Calendar.MONTH, month)                now.set(Calendar.DAY_OF_MONTH, dayOfMonth)                inTgl.setText(SimpleDateFormat("dd MMMM yyyy", Locale.US).format(now.time))            }        DatePickerDialog(            c,            datePicker,            now.get(Calendar.YEAR),            now.get(Calendar.MONTH),            now.get(Calendar.DAY_OF_MONTH)        ).show()    }    //    DIALOG TANGGAL    //    DIALOG JAM    fun showDialogTime(inTime: TextInputEditText,c: Context) {        val now = Calendar.getInstance()        val timePicker = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute: Int ->            now.set(Calendar.HOUR_OF_DAY, hour)            now.set(Calendar.MINUTE, minute)            inTime.setText(SimpleDateFormat("HH:mm", Locale.US).format(now.time))        }        TimePickerDialog(            c,            timePicker,            now.get(Calendar.HOUR_OF_DAY),            now.get(Calendar.MINUTE),            true        ).show()    }//    DIALOG JAM//Change Passfun changePassword(c:Context,username:String){    val intent = Intent(c, ChangePWDActivity::class.java)    intent.putExtra("USERNAME",username)    c.startActivity(intent)}//Change Pass//Update Datafun updateData(c:Context,username:String){    val intent = Intent(c, UbahDataActivity::class.java)    intent.putExtra("USERNAME",username)    c.startActivity(intent)}//Update Data//masterPerusahaan    fun masterPerusahaan(c:Context){        val intent = Intent(c, PerusahaanActivity::class.java)        c.startActivity(intent)    }//masterPerusahaan//    Dialog PICK PICTUREfun showDialogOption(c:Activity,camera:Int,galery:Int){    val alertDialog = AlertDialog.Builder(c)    alertDialog.setTitle("Silahkan Pilih")    val animals = arrayOf<String>(        "Ambil Sebuah Gambar",        "Pilih Gambar dari galery"    )    alertDialog!!.setItems(animals, DialogInterface.OnClickListener{ dialog, which ->        when (which) {            0 -> openCamera(c,camera)            1 -> openGalleryForImage(c,galery)        }    })    alertDialog.create()    alertDialog.show()}    //    Dialog PICK PICTURE    fun openCamera(c:Activity,codeRequest:Int){        var intent = Intent(c, PhotoHazardActivity::class.java)        c.startActivityForResult(intent,codeRequest)    }    //OPEN GALERY    private fun openGalleryForImage(c:Activity,codeRequest: Int) {        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)        intent.type = "image/*"        c.startActivityForResult(intent, codeRequest)    }    //OPEN GALERY}