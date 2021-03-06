package com.misit.abpenergy.HSE.Inspeksi.Response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ItemsInspeksiResponse(

	@field:SerializedName("dataItems")
	var dataItems: List<DataItemsInspeksi>? = null
)

@Keep
data class DataItemsInspeksi(

	@field:SerializedName("idForm")
	var idForm: Int? = null,

	@field:SerializedName("tgl_input")
	var tglInput: String? = null,

	@field:SerializedName("idSub")
	var idSub: Int? = null,

	@field:SerializedName("flag")
	var flag: Int? = null,

	@field:SerializedName("user_input")
	var userInput: String? = null,

	@field:SerializedName("listInspeksi")
	var listInspeksi: String? = null,

	@field:SerializedName("idList")
	var idList: Int? = null
)
