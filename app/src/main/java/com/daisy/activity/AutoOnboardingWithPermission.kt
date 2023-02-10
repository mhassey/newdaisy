package com.daisy.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.daisy.BuildConfig
import com.daisy.R
import com.daisy.activity.base.BaseActivity
import com.daisy.activity.editorTool.EditorTool
import com.daisy.activity.mainActivity.MainActivity
import com.daisy.activity.onBoarding.slider.OnBoarding
import com.daisy.activity.onBoarding.slider.getCard.GetCardViewModel
import com.daisy.activity.onBoarding.slider.getCard.vo.GetCardResponse
import com.daisy.activity.onBoarding.slider.screenAdd.ScreenAddViewModel
import com.daisy.activity.onBoarding.slider.screenAdd.vo.ScreenAddResponse
import com.daisy.activity.onBoarding.slider.slides.addScreen.AddScreenViewModel
import com.daisy.activity.onBoarding.slider.slides.signup.SignUpViewModel
import com.daisy.activity.onBoarding.slider.slides.signup.vo.SignUpResponse
import com.daisy.common.session.SessionManager
import com.daisy.databinding.ActivityAutoOnboardingWithPermissionBinding
import com.daisy.pojo.response.*
import com.daisy.utils.Constraint
import com.daisy.utils.Utils
import com.daisy.utils.ValidationHelper
import java.io.File
import java.io.IOException

class AutoOnboardingWithPermission : BaseActivity() {
     lateinit var mBinding: ActivityAutoOnboardingWithPermissionBinding
     val signUpViewModel: SignUpViewModel by lazy {
         ViewModelProvider(this)[SignUpViewModel::class.java]
     }
    val sessionManager:SessionManager by lazy { SessionManager.get() }
    val screenAddViewModel:ScreenAddViewModel by lazy{
        ViewModelProvider(this)[ScreenAddViewModel::class.java]
    }
    val  mViewModel:AddScreenViewModel by lazy {
        ViewModelProvider(this)[AddScreenViewModel::class.java]

    }
    val getCardViewModel : GetCardViewModel by lazy {
        ViewModelProvider(this)[GetCardViewModel::class.java]
    }

    var id:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding=  DataBindingUtil.setContentView(this, R.layout.activity_auto_onboarding_with_permission)
        setNoTitleBar(this)

        defineObserver()
        permissionChecker()


    }


    private fun permissionChecker() {
        if (!Settings.canDrawOverlays(this)) {
            askForPopUpPermission()
        }
        else if (!Utils.isAccessGranted(this)) {
        askForUsagesPermission()
        }
        else{
            hitSignUpApi()
        }


    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    fun askForPopUpPermission() {

        Utils.showAlertDialog(
                this, getString(R.string.display_over_the_app), "Ok",
                { _, _ ->
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                    this.startActivityForResult(intent, Constraint.POP_UP_RESPONSE)
                }, false
            )


    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constraint.POP_UP_RESPONSE) {
            if (Settings.canDrawOverlays(this)) {
                checkAccessUsage()
            }
            else
                askForPopUpPermission()

        }
        else if (requestCode== Constraint.RETURN) {
            if (!Utils.isAccessGranted(this)) {
                askForUsagesPermission()
            } else {
                checkAccessUsage()
            }
        }
        }




    private fun askForUsagesPermission() {
        Utils.showAlertDialog(this, getString(R.string.allow_data_access), "Ok",
            { dialog, which ->
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                startActivityForResult(intent, Constraint.RETURN)
            }, false
        )
    }


    /**
     * Responsibility - check for access usage  permission
     * Parameters - No parameter
     */
    private fun checkAccessUsage() {
        if (Utils.isAccessGranted(this)) {
            hitSignUpApi()
        } else {
            askForUsagesPermission()
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
                sessionManager.setPasswordForLock(Constraint.PASSWORD_VALUE)
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
        val carriers: List<Carrier> = sessionManager.loginResponse.carrier

        if (screenAddViewModel.deviceId != null && !screenAddViewModel.deviceId.equals("") && !screenAddViewModel.deviceId.equals("0")
        ) {
            getGeneralResponseForProductSelection(screenAddViewModel.deviceId, carriers[0])
        } else {
            redirectToOnBoardingProcess()
        }

    }

    private fun getGeneralResponseForProductSelection(deviceId: String, carrier: Carrier) {
        if (Utils.getNetworkState(this)) {
            showHideProgressDialog(true)
            val generalRequest: java.util.HashMap<String, String> =
                getGeneralRequest(deviceId, carrier)
            mViewModel.setGeneralRequestForDeviceSpecific(generalRequest)
            val liveData: LiveData<GlobalResponse<GeneralResponse>> =
                mViewModel.generalResponseLiveDataForDeviceSpecific
            if (!liveData.hasActiveObservers()) {
                liveData.observe(
                    this
                ) { generalResponseGlobalResponse ->
                    showHideProgressDialog(false)
                    if (generalResponseGlobalResponse.isApi_status) {
                        handleProductListData(generalResponseGlobalResponse, deviceId)
                    }
                }
            }
        } else {
            ValidationHelper.showToast(this, getString(R.string.no_internet_available))
        }
    }

    private fun handleProductListData(
        generalResponseGlobalResponse: GlobalResponse<GeneralResponse>?,
        deviceId: String
    ) {
        generalResponseGlobalResponse?.let {
            generalResponseGlobalResponse.result.products?.let {
                val products: List<Product>? = generalResponseGlobalResponse.result.products
                sessionManager.setOSType(generalResponseGlobalResponse.result.osTypes)
                if (products != null && products.isNotEmpty()) {
                    mViewModel.isManufactureSelected = false
                    val product: Product = products[0]
                    mViewModel.setAutoSelectProduct(product)
                    if (mViewModel.autoSelctedProduct != null) {
                        callAddScreen()
                    }
                } else {
                    redirectToOnBoardingProcess()
                }
            }
        }

    }

    private fun redirectToOnBoardingProcess() {

        var intent:Intent = Intent(this,OnBoarding::class.java)
        var bundle=Bundle()
        bundle.putBoolean(Constraint.OPEN_SELECT_PRODUCT,true)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun handleCreateScreen() {

    }

    private fun callAddScreen() {
        showHideProgressDialog(true)
        screenAddViewModel.setMutableLiveData(getAddScreenRequest())
        val liveData: LiveData<GlobalResponse<ScreenAddResponse>> = screenAddViewModel.liveData
        if (!liveData.hasActiveObservers()) {

            liveData.observe(
                this
            ) { screenAddResponseGlobalResponse ->
                showHideProgressDialog(false)
                handleScreenAddResponse(screenAddResponseGlobalResponse)
            }
        }
    }

    private fun handleScreenAddResponse(screenAddResponseGlobalResponse: GlobalResponse<ScreenAddResponse>?) {
       screenAddResponseGlobalResponse?.let {
           if (screenAddResponseGlobalResponse.isApi_status) {
               sessionManager.setDeviceId(screenAddResponseGlobalResponse.result.iddevice)
               sessionManager.setScreenID(screenAddResponseGlobalResponse.result.id)
               sessionManager.deviceToken = screenAddResponseGlobalResponse.result.token
               sessionManager.setScreenPosition(screenAddResponseGlobalResponse.result.screenPosition)
               sessionManager.orientation =getString(R.string.defaultt)
               getCardData()
           } else {
               ValidationHelper.showToast(this, screenAddResponseGlobalResponse.message)
           }
       }

    }


    /**
     * Responsibility -getCardData method is used for sending card request and accessing response
     * Parameters - No parameter
     */
    private fun getCardData() {
        if (Utils.getNetworkState(this)) {
            showHideProgressDialog(true)
            getCardViewModel.setMutableLiveData(getCardRequest())
            val liveData: LiveData<GlobalResponse<GetCardResponse>> = getCardViewModel.liveData
            if (!liveData.hasActiveObservers()) {
                liveData.observe(
                    this
                ) { getCardResponseGlobalResponse ->
                    try {
                        handleCardGetResponse(getCardResponseGlobalResponse)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        } else {
            ValidationHelper.showToast(this, getString(R.string.no_internet_available))
        }
    }

    private fun handleCardGetResponse(getCardResponseGlobalResponse: GlobalResponse<GetCardResponse>?) {
        showHideProgressDialog(false)
        if (getCardResponseGlobalResponse?.isApi_status == true) {
            sessionManager.priceCard = getCardResponseGlobalResponse.result.pricecard
            sessionManager.promotion = getCardResponseGlobalResponse.result.promotions
            sessionManager.pricing = getCardResponseGlobalResponse.result.pricing
            redirectToMainHandler(getCardResponseGlobalResponse)
        } else {
            if (getCardResponseGlobalResponse?.result
                    ?.defaultPriceCard != null && !getCardResponseGlobalResponse.result
                    ?.defaultPriceCard.equals("")
            ) {
                redirectToMainHandler(getCardResponseGlobalResponse)
            } else ValidationHelper.showToast(this, getCardResponseGlobalResponse?.message)
        }
    }


    /**
     * Responsibility -  Its delete daisy data and check if new price card is available in response then add new file path and call redirectToMain method
     * * Parameters -  Its takes GlobalResponse<GetCardResponse> object as parameter
    </GetCardResponse> */
    @Throws(IOException::class)
    private fun redirectToMainHandler(response: GlobalResponse<GetCardResponse>) {
        Utils.deleteDaisy()
        var UrlPath: String
        UrlPath =
            if (response.result.pricecard.fileName1 != null && !response.result.pricecard.fileName1.equals(
                    ""
                )
            ) {
                response.result.pricecard.fileName1
            } else {
                response.result.pricecard.fileName
            }
        if (response.result.pricecard.fileName != null) {
            val configFilePath = Constraint.FOLDER_NAME + Constraint.SLASH
            val directory: File = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                File(getExternalFilesDir(""), configFilePath)
            } else {
                File(configFilePath)
            }
            if (!directory.exists()) {
                directory.mkdirs()
            }
            val path = Utils.getPath()
            if (path != null) {
                if (path != UrlPath) {
                    Utils.deleteCardFolder()
                    Utils.writeFile(configFilePath, UrlPath)
                    sessionManager.deleteLocation()
                }
            } else {
                Utils.writeFile(configFilePath, UrlPath)
            }
            redirectToMain()
        } else if (response.result.defaultPriceCard != null && !response.result.defaultPriceCard.equals(
                ""
            )
        ) {
            UrlPath = response.result.defaultPriceCard
            val configFilePath = Constraint.FOLDER_NAME + Constraint.SLASH
            val directory = File(getExternalFilesDir(""), configFilePath)
            if (!directory.exists()) {
                directory.mkdirs()
            }
            val path = Utils.getPath()
            if (path != null) {
                if (path != UrlPath) {
                    Utils.deleteCardFolder()
                    Utils.writeFile(configFilePath, UrlPath)
                    sessionManager.deleteLocation()
                }
            } else {
                Utils.writeFile(configFilePath, UrlPath)
            }
            redirectToMain()
        } else {
            ValidationHelper.showToast(this, getString(R.string.invalid_url))
        }
        val intent = Intent(this, EditorTool::class.java)
        startActivity(intent)
        finish()
    }


    /**
     * Responsibility - redirectToMain method is used for call Main Activity
     * Parameters - No parameter
     */
    private fun redirectToMain() {
        sessionManager.onBoarding(Constraint.TRUE)
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    /**
     * Responsibility -getCardRequest method create card request
     * Parameters - No parameter
     */
    private fun getCardRequest(): java.util.HashMap<String, String> {
        val hashMap = java.util.HashMap<String, String>()
        hashMap[Constraint.SCREEN_ID] = "${sessionManager.screenId}"
        hashMap[Constraint.TOKEN] = sessionManager.deviceToken
        return hashMap
    }
    /**
     * Responsibility - Create add screen request
     * Parameters - Its takes AddScreen object as parameter
     */
    private fun getAddScreenRequest(): java.util.HashMap<String, String> {

                val hashMap = java.util.HashMap<String, String>()
                hashMap[Constraint.ISLE] = Constraint.ONE_STRING
                hashMap[Constraint.SHELF] = Constraint.ONE_STRING
                hashMap[Constraint.POSITION] = Constraint.ONE_STRING
                    if (screenAddViewModel.deviceId != null && !screenAddViewModel.deviceId.equals("") && !screenAddViewModel.deviceId.equals(
                            "0"
                        )
                    ) {
                        hashMap[Constraint.DEVICEID] = screenAddViewModel.deviceId
                    } else {
                        hashMap[Constraint.DEVICEID] = "0"
                        hashMap[Constraint.DEVICE_NAME] = Utils.ModelNumber()
                    }

                        if (mViewModel.getSelectedProduct() != null) {
                            if (mViewModel.getSelectedProduct().idproductStatic != null) hashMap[Constraint.ID_PRODUCT_STATIC] =
                                mViewModel.getSelectedProduct().idproductStatic
                        } else if (mViewModel.autoSelctedProduct != null) {
                            if (mViewModel.autoSelctedProduct.idproductStatic != null) hashMap[Constraint.ID_PRODUCT_STATIC] =
                                mViewModel.autoSelctedProduct.idproductStatic
                        } else {
                            ValidationHelper.showToast(
                                this,
                                getString(R.string.product_not_available)
                            )
                        }
                hashMap[Constraint.BUILD_VERSION] = BuildConfig.VERSION_NAME
                val loginResponse: LoginResponse = sessionManager.loginResponse
                hashMap[Constraint.IDSTORE] = loginResponse.idstore
                hashMap[Constraint.MAC_ADDRESS] = Utils.getMacAddress(applicationContext)
                hashMap[Constraint.DEVICE_TOKEN] = SessionManager.get().fcmToken
                for (osType in SessionManager.get().osType) {
                    if (osType.osName.equals(Constraint.ANDROID)) {
                        hashMap[Constraint.DEVICE_TYPE] = "${osType.osID}"
                    }
                }
                return hashMap


    }

    /**
     * Responsibility - getGeneralRequest method is used for create general api request
     */
    private fun getGeneralRequest(
        deviceId: String,
        carrier: Carrier
    ): java.util.HashMap<String, String> {
        val hashMap = java.util.HashMap<String, String>()
        hashMap[Constraint.DEVICE_ID] = deviceId
        hashMap[Constraint.CARRIER_ID] = "${carrier.idcarrier}"
        return hashMap
    }


}
