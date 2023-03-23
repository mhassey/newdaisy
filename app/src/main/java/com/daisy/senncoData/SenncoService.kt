package com.daisy.senncoData

import com.daisy.pojo.response.SenncoDeviceResponse
import retrofit2.Response
import retrofit2.http.GET

interface SenncoService {

    @GET("Devices/")
    suspend fun getSenncoDevices(): Response<SenncoDeviceResponse>
}