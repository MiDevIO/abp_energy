package com.misit.abpenergy.SQLiteimport android.content.Contextimport android.database.sqlite.SQLiteDatabaseimport android.database.sqlite.SQLiteOpenHelperclass DbHelper(c:Context):SQLiteOpenHelper(c,DB_NAME,null,15) {    companion object{        val DB_NAME = "abp.db"        val tb = arrayOf("INSPEKSI_ITEM_COUNTER","PENUMPANG","KEMUNGKINAN","KEPARAHAN",                            "PERUSAHAAN","LOKASI","PENGENDALIAN","USERS",                            "HAZARD_HEADER","HAZARD_DETAIL","HAZARD_VALIDATION",                            "RISK")    }    override fun onCreate(db: SQLiteDatabase?) {        dbQuery.tbpenumpang(db)        dbQuery.tbUsers(db)        dbQuery.tbPerusahaan(db)        dbQuery.tbItemInspeksi(db)        dbQuery.tbPengendalian(db)        dbQuery.tbKkeparahan(db)        dbQuery.tbKemungkinan(db)        dbQuery.tbLokasi(db)        dbQuery.tbHazardHeader(db)        dbQuery.tbHazardDetail(db)        dbQuery.tbHazardValidation(db)        dbQuery.tbRISK(db)    }    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {        tb.forEach {            db?.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + it + "'")        }        tb.forEach {            db?.execSQL("DROP TABLE IF EXISTS ${it}")        }        onCreate(db)    }}