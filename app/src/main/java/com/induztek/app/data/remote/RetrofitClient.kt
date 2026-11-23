package com.induztek.app.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Recordar cambiar esto con el port de VSC.
    // const val URL_BASE = "http://10.0.2.2:8090/" //
    const val URL_BASE = "http://127.0.0.1:8090/"
    //const val URL_BASE = "https://7nkxcdld-8090.brs.devtunnels.ms/"
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

    val clienteApi: ClienteApi by lazy {
        crear(URL_BASE).create(ClienteApi::class.java)
    }
}