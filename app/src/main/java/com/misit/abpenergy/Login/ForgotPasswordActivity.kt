package com.misit.abpenergy.Loginimport androidx.appcompat.app.AppCompatActivityimport android.os.Bundleimport com.misit.abpenergy.Rimport es.dmoral.toasty.Toastyclass ForgotPasswordActivity : AppCompatActivity() {    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)        setContentView(R.layout.activity_forgot_password)        var username = intent.getStringExtra(USERNAME)        if(username!=null){            Toasty.info(this,username).show()        }    }    companion object{      var USERNAME = null    }}