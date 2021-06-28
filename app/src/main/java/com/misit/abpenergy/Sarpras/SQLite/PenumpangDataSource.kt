package com.misit.abpenergy.Sarpras.SQLiteimport android.content.ContentValuesimport android.content.Contextimport android.database.Cursorimport android.database.sqlite.SQLiteDatabaseimport com.misit.abpenergy.SQLite.DbHelperimport es.dmoral.toasty.Toastyclass PenumpangDataSource(val c:Context) {    var dbHelper : DbHelper    var sqlDatabase : SQLiteDatabase?=null    var listItem :ArrayList<PenumpangModel>?=null    init {        listItem = ArrayList()        dbHelper = DbHelper(c)    }    private fun openAccess(){        sqlDatabase = dbHelper.writableDatabase    }    private fun closeAccess(){        sqlDatabase?.close()        dbHelper?.close()    }    fun insertItem(item: PenumpangModel):Long{        openAccess()        var cv = createCV(item)        var hasil = sqlDatabase?.insertOrThrow("${tbItem}",null,cv)        closeAccess()        return hasil!!    }    fun getItem(nik: String): PenumpangModel {        openAccess()        val c = sqlDatabase?.rawQuery("SELECT * FROM "+                "${tbItem} WHERE nik = ?", arrayOf(nik))        c?.moveToFirst()        var itemModels = PenumpangModel()        c?.let {            itemModels = fetchRow(it)        }        c?.close()        closeAccess()        return itemModels    }    fun getAll(): ArrayList<PenumpangModel> {        openAccess()        val c = sqlDatabase?.rawQuery("SELECT * FROM "+                "${tbItem} ",null)        if(c!!.moveToFirst()){            while (c.isAfterLast){                listItem?.add(fetchRow(c))            }        }        c?.close()        closeAccess()        return listItem!!    }    private fun fetchRow(cursor: Cursor): PenumpangModel {        val id = cursor.getInt(cursor.getColumnIndex("id"))        val nik = cursor.getString(cursor.getColumnIndex("nik"))        val nama = cursor.getString(cursor.getColumnIndex("nama"))        val jabatan = cursor.getString(cursor.getColumnIndex("jabatan"))        val penumpangModel = PenumpangModel()        penumpangModel.id = id.toLong()        penumpangModel.nik = nik        penumpangModel.nama = nama        penumpangModel.jabatan = jabatan        return penumpangModel    }    fun deleteItem(item:Int){        openAccess()        val hasil = sqlDatabase?.delete("${tbItem}","nik = ? ", arrayOf(item.toString()))        if(hasil!! <0 ){            Toasty.error(c!!,"Gagal Hapus").show()        }else{            Toasty.success(c!!,"Hapus Berhasil").show()        }        closeAccess()    }    fun deleteAll():Boolean{        openAccess()        val hasil = sqlDatabase?.delete("${tbItem}","1",null)        if(hasil!! <0 ){            return false        }        closeAccess()        return true    }    fun updateItem(item: PenumpangModel, nik:String):Boolean{        openAccess()        val items = ContentValues()        items.put("nik",item.nik)        items.put("nama",item.nama)        items.put("jabatan",item.jabatan)        val hasil = sqlDatabase?.update("${tbItem}",items,"nik = ?", arrayOf("${nik}"))        if(hasil!! < 0){            return false        }        closeAccess()        return true    }    private fun createCV(item : PenumpangModel): ContentValues {        var cv = ContentValues()        cv.put("id",item.id)        cv.put("nik",item.nik)        cv.put("nama",item.nama)        cv.put("jabatan",item.jabatan)        return cv    }    companion object{        val tbItem = "PENUMPANG"    }}