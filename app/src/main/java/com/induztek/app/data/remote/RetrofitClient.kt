package com.induztek.app.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Recordar cambiar esto con el port de VSC.
    const val URL_BASE = "http://10.0.2.2:8081/"

    private fun crear(urlBase: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(urlBase)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val autenticacionApi: AutenticacionApi by lazy {
        crear(URL_BASE).create(AutenticacionApi::class.java)
    }

    val avatarApi: AvatarApi by lazy {
        crear(URL_BASE).create(AvatarApi::class.java)
    }
}