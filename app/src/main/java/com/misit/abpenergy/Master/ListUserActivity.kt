package com.misit.abpenergy.Masterimport android.app.Activityimport android.content.Contextimport android.content.Intentimport androidx.appcompat.app.AppCompatActivityimport android.os.Bundleimport android.util.Logimport android.view.Menuimport android.view.MenuItemimport androidx.appcompat.widget.SearchViewimport androidx.recyclerview.widget.LinearLayoutManagerimport androidx.recyclerview.widget.RecyclerViewimport androidx.swiperefreshlayout.widget.SwipeRefreshLayoutimport com.misit.abpenergy.Api.ApiClientimport com.misit.abpenergy.Api.ApiEndPointTwoimport com.misit.abpenergy.DataSource.UsersDataSourceimport com.misit.abpenergy.HazardReport.NewHazardActivityimport com.misit.abpenergy.Master.Adapter.ListUserAdapterimport com.misit.abpenergy.Master.Response.UsersListItemimport com.misit.abpenergy.Rimport com.misit.abpenergy.Response.UserListResponseimport es.dmoral.toasty.Toastyimport kotlinx.android.synthetic.main.activity_list_user.*import retrofit2.Callimport retrofit2.Callbackimport retrofit2.Responseimport java.sql.SQLExceptionimport kotlin.collections.ArrayListclass ListUserActivity : AppCompatActivity(),ListUserAdapter.OnItemClickListener {    private var adapter: ListUserAdapter? = null    private var userList:MutableList<UsersListItem>?=null    private var userPick:String?=null    private var page:Int=1    private var call: Call<UserListResponse>?=null    private var visibleItem : Int=0    private var total : Int=0    private var pastVisibleItem : Int=0//    lateinit var swipeRefreshLayout: SwipeRefreshLayout    private var loading : Boolean=false    private var search:String?=null    var curentPosition: Int=0    var dataIntent:String?=null    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)        setContentView(R.layout.activity_list_user)        title="List User"        search=""        userPick=""        page=1        dataIntent=""        visibleItem=0        total=0        pastVisibleItem=0        curentPosition=0        userList = ArrayList()        userPick = intent.getStringExtra(NewHazardActivity.USEPICK)        dataIntent = intent.getStringExtra(DataExtra)        var actionBar = supportActionBar        actionBar?.setDisplayHomeAsUpEnabled(true)        adapter = ListUserAdapter(            this@ListUserActivity,            userPick,            userList!!        )        val linearLayoutManager = LinearLayoutManager(this@ListUserActivity)        rvListUser?.layoutManager = linearLayoutManager        rvListUser?.adapter =adapter        adapter?.setListener(this@ListUserActivity)//        swipeRefreshLayout = findViewById(R.id.pullRefreshUser)//        swipeRefreshLayout.setOnRefreshListener(object :SwipeRefreshLayout.OnRefreshListener{//            override fun onRefresh() {//                page=1//                userList?.clear()//                userListSQL(this@ListUserActivity,search!!)//                adapter?.notifyDataSetChanged()//            }//        })//        loadData(search,page)        userListSQL(this@ListUserActivity,search!!)        Log.d("KemungkinanSQL","OK")    }//    private fun loadData(cari:String?,hal:Int) {//        swipeRefreshLayout.isRefreshing=true//        val apiEndPoint = ApiClient.getClient(this@ListUserActivity)!!.create(ApiEndPointTwo::class.java)//        call = apiEndPoint.getUsersList(cari,hal)//        call?.enqueue(object : Callback<UserListResponse?> {//            override fun onFailure(call: Call<UserListResponse?>, t: Throwable) {//                Toasty.error(this@ListUserActivity,"Error : "+ t).show()//            }//            override fun onResponse(//                call: Call<UserListResponse?>,//                response: Response<UserListResponse?>//            ) {//                var r= response.body()//                if(r!=null){//                    Log.d("response",r.toString())//                    if(r!=null){//                            loading=true//                            if(userList?.size==0) {//                                userList!!.addAll(r!!.userItem!!)//                                swipeRefreshLayout.isRefreshing=false//                                adapter!!.notifyDataSetChanged()//                            }else{//                                curentPosition = (rvListUser?.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()//                                userList?.addAll(r?.userItem!!)//                                swipeRefreshLayout.isRefreshing=false//                                adapter?.notifyDataSetChanged()//                            }////                        rvListUser?.addOnScrollListener(object : RecyclerView.OnScrollListener(){//                            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {////                                if (dy > 0) {//                                    visibleItem = recyclerView.layoutManager!!.childCount//                                    total = recyclerView.layoutManager!!.itemCount//                                    pastVisibleItem =//                                        (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()//                                    if (loading) {//                                        if ((visibleItem + pastVisibleItem) >= total) {//                                            loading = false//                                            page++////                                            loadData(search,page)//                                        }//                                    }//                                }//                            }//                            override fun onScrollStateChanged(//                                recyclerView: RecyclerView,//                                newState: Int//                            ) {//                                Log.d("newState",newState.toString())//                                super.onScrollStateChanged(recyclerView, newState)//                            }//                        })//                    }//                }//            }////        })//    }    override fun onCreateOptionsMenu(menu: Menu?): Boolean {        menuInflater.inflate(R.menu.menu_cari,menu)        val menuItem = menu!!.findItem(R.id.searchUser)        val searchView = menuItem.actionView as SearchView        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{            override fun onQueryTextSubmit(query: String?): Boolean {                Log.d("query",query)                return true            }            override fun onQueryTextChange(newText: String?): Boolean {                userList?.clear()                Log.d("query",newText)                search=newText                userListSQL(this@ListUserActivity,search)                return true            }        })        return super.onCreateOptionsMenu(menu)    }    override fun onOptionsItemSelected(item: MenuItem): Boolean {        return super.onOptionsItemSelected(item)    }    override fun onSupportNavigateUp(): Boolean {        onBackPressed()        return super.onSupportNavigateUp()    }    override fun onItemClick(id_user: Int, nama: String, nik: String, profileIMG: String?) {        if(dataIntent=="Hazard"){            val intent = Intent()            intent.putExtra("nama",nama)            intent.putExtra("nik",nik)            intent.putExtra("profileIMG",profileIMG)            intent.putExtra(userPick,id_user.toString())            setResult(Activity.RESULT_OK,intent)            finish()        }    }    companion object{        var DataExtra = "DataExtra"    }    private fun userListSQL(c: Context,cari:String?){        val userDataSource = UsersDataSource(c)        Log.d("KemungkinanSQL",cari.toString())        try {            val usersRow=userDataSource.searchItems(cari)            usersRow.forEach{                Log.d("KemungkinanSQL",usersRow.toString())                userList?.add(                    UsersListItem(                    it.tglentry,                    it.level,                    it.ttd,                    it.photoProfile,                    it.namaLengkap,                    it.idSession,                    it.rule,                    it.perusahaan,                    it.section,                    it.namaPerusahaan,                    it.idUser,                    it.dept,                    it.nik,                    it.password,                    it.sect,                    it.department,                    it.email,                    it.username,                    it.status                )                )            }            adapter?.notifyDataSetChanged()//            rvListUser?.addOnScrollListener(object : RecyclerView.OnScrollListener(){//                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {////                    if (dy > 0) {//                        visibleItem = recyclerView.layoutManager!!.childCount//                        total = recyclerView.layoutManager!!.itemCount//                        pastVisibleItem =//                            (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()//                        if (loading) {//                            if ((visibleItem + pastVisibleItem) >= total) {//                                loading = false//                                page++////                                loadData(search,page)//                                userListSQL(this@ListUserActivity,search)//                            }//                        }//                    }//                }//                override fun onScrollStateChanged(//                    recyclerView: RecyclerView,//                    newState: Int//                ) {//                    Log.d("newState",newState.toString())//                    super.onScrollStateChanged(recyclerView, newState)//                }//            })        }catch (e: SQLException){            Log.d("KemungkinanSQL",e.toString())        }    }}