package com.daisy.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.daisy.R
import com.daisy.activity.base.BaseActivity
import com.daisy.activity.onBoarding.slider.screenAdd.ScreenAddViewModel
import com.daisy.activity.onBoarding.slider.slides.signup.SignUp
import com.daisy.activity.onBoarding.slider.slides.signup.SignUpViewModel
import com.daisy.activity.onBoarding.slider.slides.signup.vo.SignUpResponse
import com.daisy.common.session.SessionManager
import com.daisy.databinding.ActivityAutoOnboardingWithPermissionBinding
import com.daisy.utils.Constraint
import com.daisy.utils.Utils
import com.daisy.utils.ValidationHelper

class AutoOnboardingWithPermission : BaseActivity() {
     val mBinding: ActivityAutoOnboardingWithPermissionBinding by lazy {
         DataBindingUtil.setContentView(this, R.layout.activity_auto_onboarding_with_permission)
     }
     val signUpViewModel: SignUpViewModel by lazy {
         ViewModelProvider(this)[SignUpViewModel::class.java]
     }
    val sessionManager:SessionManager by lazy { SessionManager.get() }
    val screenAddViewModel:ScreenAddViewModel by lazy{
        ViewModelProvider(this)[ScreenAddViewModel::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        defineObserver()
        permissionChecker()


    }


    private fun permissionChecker() {
        hasSecurity()


    }

    private fun hasSecurity() {
        val hasFeature: Boolean = packageManager
            .hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
        if (!hasFeature) {
            askForPopUpPermission()
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun askForPopUpPermission() {
       if(!Settings.canDrawOverlays(this)) {
            Utils.showAlertDialog(
                this, getString(R.string.display_over_the_app), "Ok",
                { dialog, which ->
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                    this.startActivityForResult(intent, Constraint.POP_UP_RESPONSE)
                }, false
            )

        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constraint.POP_UP_RESPONSE) {
            if (Settings.canDrawOverlays(this)) {
                hitSignUpApi()
            } else {
                askForPopUpPermission()
            }
        }


    }

    private fun hitSignUpApi() {
        if (Utils.getNetworkState(this)) {
            showHideProgressDialog(true)
            val signUpRequest: HashMap<String, String> = getSignUpRequest()
            signUpViewModel.setSignUpRequestMutableLiveData(signUpRequest)
        }
        else
        {
            ValidationHelper.showToast(this, getString(R.string.no_internet_available))
        }
    }

    private fun getSignUpRequest(): java.util.HashMap<String, String> {
        val hashMap = java.util.HashMap<String, String>()
        hashMap[Constraint.STORE_CODE] = Constraint.STORE_CODE_VALUE
        hashMap[Constraint.PASSWORD_ID] = Constraint.PASSWORD_VALUE
        hashMap[Constraint.DEVICENAME] = Utils.ModelNumber()
        return hashMap
    }


    private fun defineObserver() {
        var liveData= signUpViewModel.responseLiveData
        if (!liveData.hasActiveObservers())
        {
            liveData.observe(this){
                handleResponse(it)
            }
        }


    }


    /**
     * Purpose - handleResponse method handle sign up response
     *
     * @param signUpResponse
     */
    private fun handleResponse(signUpResponse: SignUpResponse?) {
        showHideProgressDialog(false)
       signUpResponse?.let {
            if (signUpResponse.isApi_status) {
                sessionManager.setPasswordForLock(SignUp.loginBinding.password.text.toString())
                sessionManager.setOpenTime(signUpResponse.data.open)
                sessionManager.setCloseTime(signUpResponse.data.closed)
                sessionManager.setOffset(signUpResponse.data.utcOffset)
                sessionManager.setSenitized(signUpResponse.data.deviceSanitize)
                sessionManager.deviceSecurity = signUpResponse.data.deviceSecurity
                sessionManager.pricingPlainId = signUpResponse.data.pricingPlanID
                sessionManager.serverTime = signUpResponse.data.currentTime
                sessionManager.setSignUpData(signUpResponse.data)
                checkIdExsists( signUpResponse.data.deviceId)
            } else {
                ValidationHelper.showToast(this, signUpResponse.message)
            }
        } ?: kotlin.run {
            ValidationHelper.showToast(this, getString(R.string.no_internet_available))
        }
    }

    private fun checkIdExsists(deviceId:String) {
        screenAddViewModel.deviceId = deviceId

    }


}
