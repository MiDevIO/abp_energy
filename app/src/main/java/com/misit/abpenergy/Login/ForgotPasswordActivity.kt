package com.misit.abpenergy.Loginimport android.app.Activityimport android.content.Intentimport androidx.appcompat.app.AppCompatActivityimport android.os.Bundleimport android.view.Viewimport com.misit.abpenergy.Rimport com.misit.abpenergy.Utils.ConfigUtilimport com.misit.abpenergy.Utils.ConfigUtil.resultIntentimport es.dmoral.toasty.Toastyimport kotlinx.android.synthetic.main.activity_forgot_password.*class ForgotPasswordActivity : AppCompatActivity(),View.OnClickListener {    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)        setContentView(R.layout.activity_forgot_password)        ConfigUtil.changeColor(this)        var username = intent.getStringExtra(USERNAME)        if(username!=null){            Toasty.info(this,username).show()        }        btnHaveAccount.setOnClickListener(this)        btnNewUser.setOnClickListener(this)    }    companion object{      var USERNAME = null    }    override fun onClick(v: View?) {        if(v?.id==R.id.btnHaveAccount){            finish()        }        if(v?.id==R.id.btnNewUser){            resultIntent(this@ForgotPasswordActivity)        }    }}