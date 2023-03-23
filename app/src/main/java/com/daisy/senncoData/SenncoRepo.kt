package com.daisy.senncoData

import androidx.lifecycle.MutableLiveData
import com.daisy.senncoData.SenncoRetrofit
import com.daisy.pojo.response.SenncoDeviceResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SenncoRepo  {

    var senncoResponseLiveData: MutableLiveData<SenncoDeviceResponse>  = MutableLiveData()
    val senncoService by lazy {
        SenncoRetrofit.getInstance()
    }
    fun getSenncoDeviceData(deviceId:String,brictechagenttype:String): MutableLiveData<SenncoDeviceResponse>
    {
        CoroutineScope(Dispatchers.IO).launch {
         val senncoDevices =   senncoService.getSenncoDevices()
            withContext(Dispatchers.Main)
            {
                if (senncoDevices.isSuccessful)
                {
                    senncoResponseLiveData.value= senncoDevices.body()
                }

            }
        }
        return senncoResponseLiveData
    }

}