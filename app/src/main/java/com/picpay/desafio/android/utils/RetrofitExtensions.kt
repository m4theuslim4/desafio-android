package com.picpay.desafio.android.utils

import com.google.gson.Gson
import okhttp3.ResponseBody

inline fun <reified T> ResponseBody.toObject() : T {
    val errorTemp = this.string()
    return Gson().fromJson(errorTemp,T::class.java)

}