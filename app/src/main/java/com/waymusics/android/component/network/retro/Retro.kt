package com.waymusics.android.component.network.retro

import androidx.annotation.Keep
import com.google.gson.GsonBuilder
import com.waymusics.android.Constant
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Keep
class Retro {
    fun getRetrofitInstance(): Retrofit {
        val okHttpClient: OkHttpClient? = UnsafeOkHttpClient().getUnsafeOkHttpClient()


        val interceptor = Interceptor { chain ->
            var request: Request = chain.request()
            val builder: Request.Builder =
                request.newBuilder()
            request = builder.build()
            chain.proceed(request)
        }


        val client = OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.MINUTES)
            .addNetworkInterceptor(interceptor)
            .build()


        val gson = GsonBuilder().setLenient().create()
        val host = Constant.API_URL +"/"
        return Retrofit.Builder()
            .baseUrl(host).client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()


    }
}