package com.misit.abpenergy.Modelimport io.realm.RealmObjectimport io.realm.annotations.PrimaryKeyopen class InitModel():RealmObject() {    @PrimaryKey    var id:Long=0    var dataHazard:Int=0    var dataInspeksi:Int=0}