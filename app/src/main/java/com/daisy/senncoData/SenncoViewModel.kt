package com.daisy.senncoData

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.daisy.pojo.response.SenncoDeviceResponse

class SenncoViewModel: ViewModel() {
    val welcomeRepo: SenncoRepo by lazy {
        SenncoRepo()
    }


    fun sendDeviceDetails(deviceId:String,brictechagenttype:String) : MutableLiveData<SenncoDeviceResponse>
    {
       return welcomeRepo.getSenncoDeviceData(deviceId,brictechagenttype)


    }



}