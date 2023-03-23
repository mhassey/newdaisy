package com.daisy.senncoData

import com.daisy.apiService.ApiConstant
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SenncoRetrofit {

    companion object {
        var baseUrl= "https://0ddz637c90.execute-api.us-east-1.amazonaws.com/mpc"

        fun getInstance(): SenncoService {
            var interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            var httpClient = OkHttpClient.Builder().addInterceptor(interceptor).addInterceptor {
                var requestBuilder = it.request().newBuilder()
                requestBuilder.addHeader(ApiConstant.KEY_CONTENT_TYPE, ApiConstant.CONTENT_TYPE)
                requestBuilder.addHeader("username", "")
                requestBuilder.addHeader("password", "")
                var response = it.proceed(requestBuilder.build())
                if (response.code()!=200)
                {
                    // Handle error
                }
                response
            }
            return  Retrofit.Builder().baseUrl(baseUrl).client(httpClient.build()).addConverterFactory(GsonConverterFactory.create()).build().create(
                SenncoService::class.java)

        }




    }







}