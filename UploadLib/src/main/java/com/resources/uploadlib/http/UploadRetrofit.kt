package com.resources.uploadlib.http

import com.blankj.utilcode.util.SPUtils
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


/**
 * @Author:BYZ
 * @Time:2023/10/20 14:18
 * @blame Android Team
 * @info 根据自身需求修改网络请求
 */
class UploadRetrofit {

    val okHttpClient by lazy{ OkHttpClient.Builder().apply {
        addInterceptor(object :Interceptor{

            override fun intercept(chain: Interceptor.Chain): Response {
                chain.let {
                    val original: Request = it.request()
                    val request = original.newBuilder()//getSessionId()
                        .addHeader("X_TICKET", "f3f3fd7994fb460081ff2f9fc6090f3c_2:cc67e7305b8e494b8c0d1a0060a2dbcc")
                        .method(original.method, original.body)
                        .build()
                    return it.proceed(request)
                }
            }
        })
//        setInterceptorListener(this,object : BaseInterceptListener {  })
//        if (BuildConfig.DEBUG) {
//            val httpLoggingInterceptor = HttpLoggingInterceptor()
//            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
//            addInterceptor(httpLoggingInterceptor)
//        }
    }}


    val mRetrofit by lazy{
        Retrofit.Builder().baseUrl("http://192.162.130.34:9511/")
        .addConverterFactory(GsonConverterFactory.create())
        .addConverterFactory(ScalarsConverterFactory.create())
        .client(okHttpClient.build())
        .build()
     }


    val uploadApi by lazy { mRetrofit.create( UploadApi::class.java) }

    fun getSessionId(): String {
        return SPUtils.getInstance().getString("login_token")
    }


}