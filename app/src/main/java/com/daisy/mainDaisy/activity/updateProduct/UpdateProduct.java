package com.daisy.mainDaisy.activity.updateProduct;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.daisy.R;
import com.daisy.mainDaisy.activity.base.BaseActivity;
import com.daisy.mainDaisy.activity.editorTool.EditorTool;
import com.daisy.mainDaisy.activity.mainActivity.MainActivity;
import com.daisy.mainDaisy.activity.onBoarding.slider.getCard.GetCardViewModel;
import com.daisy.mainDaisy.activity.onBoarding.slider.getCard.vo.GetCardResponse;
import com.daisy.mainDaisy.activity.onBoarding.slider.slides.addScreen.AddScreenViewModel;
import com.daisy.mainDaisy.common.session.SessionManager;
import com.daisy.mainDaisy.database.DBCaller;
import com.daisy.databinding.ActivityUpdateProductBinding;
import com.daisy.mainDaisy.pojo.response.Carrier;
import com.daisy.mainDaisy.pojo.response.GeneralResponse;
import com.daisy.mainDaisy.pojo.response.GlobalResponse;
import com.daisy.mainDaisy.pojo.response.Manufacture;
import com.daisy.mainDaisy.pojo.response.Product;
import com.daisy.mainDaisy.utils.Constraint;
import com.daisy.mainDaisy.utils.Utils;
import com.daisy.mainDaisy.utils.ValidationHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Purpose -  UpdateProduct is an activity that helps to update product in app
 * Responsibility - Its shows all carriers product and orientation user select any one then activity tells server to update product to particular screen id then we got update
 **/
public class UpdateProduct extends BaseActivity implements View.OnClickListener {
    private Context context;
    public ActivityUpdateProductBinding mBinding;
    public AddScreenViewModel mViewModel;
    private UpdateProductViewModel updateProductViewModel;
    private SessionManager sessionManager;
    final int sdk = android.os.Build.VERSION.SDK_INT;
    private GetCardViewModel getCardViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initClick();
    }

    /**
     * Responsibility - initView method is used for initiate all object and perform some initial level task
     * Parameters - No parameter
     **/
    private void initView() {
        context = this;
        setNoTitleBar(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_update_product);
        mViewModel = new ViewModelProvider(this).get(AddScreenViewModel.class);
        updateProductViewModel = new ViewModelProvider(this).get(UpdateProductViewModel.class);
        getCardViewModel = new ViewModelProvider(this).get(GetCardViewModel.class);
        addOrientationData();
        ArrayAdapter<String> orientationAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, mViewModel.getOrientation());
        orientationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.webkitOrientation.setAdapter(orientationAdapter);
    }


    /**
     * Responsibility - addOrientationData method is used for add orientation in array and pass it to view model
     * Parameters - No parameter
     **/
    private void addOrientationData() {
        ArrayList<String> orientation = new ArrayList<>();
        orientation.add(getString(R.string.defaultt));
        orientation.add(getString(R.string.landscape));
        mViewModel.setOrientation(orientation);
    }


    /**
     * Responsibility - initClick is an method that used for initiate clicks
     * Parameters - No parameter
     **/
    private void initClick() {
        mBinding.cancel.setOnClickListener(this);
        mBinding.productName.setOnItemSelectedListener(getProductNameListener());
        mBinding.carrierName.setOnItemSelectedListener(getCarrierListener());
        mBinding.manufactureList.setOnItemSelectedListener(getManufactureListener());
        mBinding.nextSlide.setOnClickListener(this::onClick);
    }


    /**
     * Responsibility - onWindowFocusChanged method is an override function that call when any changes perform on ui
     * Parameters - its take boolean hasFocus that help to know out app is in focused or not
     **/
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }

    }


    /**
     * Responsibility - hideSystemUI method is an default method that help to change app ui to full screen when any change perform in activity
     * Parameters - No parameter
     **/
    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

        ViewGroup.LayoutParams params = mBinding.rootLayout.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        mBinding.rootLayout.requestLayout();

    }

    /**
     * onResume handler
     */
    @Override
    public void onResume() {
        super.onResume();
        resumeWork();
    }

    /**
     * Responsibility - When we resume the activity then we need to get all data again so  we again set carrier and product
     * Parameters - No parameter
     **/
    private void resumeWork() {
        sessionManager = SessionManager.get();
        if (sessionManager.getLoginResponse() != null) {
            List<Carrier> carriers = sessionManager.getLoginResponse().getCarrier();
            if (carriers != null) {
                mViewModel.setCarriers(carriers);
                ArrayAdapter<Carrier> carrierArrayAdapter = new ArrayAdapter<Carrier>(context, android.R.layout.simple_spinner_item, mViewModel.getCarriers());
                carrierArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mBinding.carrierName.setAdapter(carrierArrayAdapter);

            }
            List<Manufacture> manufactures = sessionManager.getLoginResponse().getManufacturers();
            if (manufactures != null) {
                mViewModel.setManufactures(manufactures);
                ArrayAdapter<Manufacture> manufactureArrayAdapter = new ArrayAdapter<Manufacture>(context, android.R.layout.simple_spinner_item, mViewModel.getManufactures());
                manufactureArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mBinding.manufactureList.setAdapter(manufactureArrayAdapter);

            }

        }
    }


    /**
     * Responsibility - getProductNameListener method is used when we select any new product in that case we need to change  selected product value in session
     * Parameters - No parameter
     **/
    private AdapterView.OnItemSelectedListener getProductNameListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mViewModel.setSelectedProduct(mViewModel.getProducts().get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }


    /**
     * Responsibility - getCarrierListener method is used when we select any new carrier in that case we need to change  selected carrier value in session
     * Parameters - No parameter
     **/
    private AdapterView.OnItemSelectedListener getCarrierListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Carrier carrier = mViewModel.getCarriers().get(position);
                mViewModel.setSelectedCarrier(carrier);
                Manufacture manufacture = (Manufacture) mBinding.manufactureList.getSelectedItem();
                mViewModel.setSelectedManufacture(manufacture);
                getGeneralResponse(carrier, manufacture);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

    }


    /**
     * Responsibility - getManufactureListener method is used when we select any new manufacture in that case we need to change  selected manufacture value in session
     * Parameters - No parameter
     **/
    private AdapterView.OnItemSelectedListener getManufactureListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Manufacture manufacture = mViewModel.getManufactures().get(position);
                mViewModel.setSelectedManufacture(manufacture);
                Carrier carrier = (Carrier) mBinding.carrierName.getSelectedItem();
                mViewModel.setSelectedCarrier(carrier);

                getGeneralResponse(carrier, manufacture);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    /**
     * Responsibility - getGeneralResponse method is used to get general response by firing general api
     * Parameters - Its take carrier and manufacture object
     **/
    private void getGeneralResponse(Carrier carrier, Manufacture manufacture) {

        if (Utils.getNetworkState(context)) {
            showHideProgressDialog(true);
            HashMap<String, String> generalRequest = getGeneralRequest(carrier, manufacture);
            mViewModel.setGeneralRequest(generalRequest);
            LiveData<GlobalResponse<GeneralResponse>> liveData = mViewModel.getGeneralResponseLiveData();
            if (!liveData.hasActiveObservers()) {
                liveData.observe(this, new Observer<GlobalResponse<GeneralResponse>>() {
                    @Override
                    public void onChanged(GlobalResponse<GeneralResponse> generalResponseGlobalResponse) {

                        handleResponse(generalResponseGlobalResponse);
                    }
                });
            }
        } else {
            ValidationHelper.showToast(context, getString(R.string.no_internet_available));
        }
    }


    /**
     * Responsibility - handleResponse method  takes GlobalResponse<GeneralResponse> as parameter and check if response is ok then set set value in product name adaptor
     * Parameters - Its takes GlobalResponse<GeneralResponse> response that help to set value in product name adaptor
     **/
    private void handleResponse(GlobalResponse<GeneralResponse> generalResponse) {
        showHideProgressDialog(false);
        if (generalResponse != null) {
            if (generalResponse.isApi_status()) {

                sessionManager.setOSType(generalResponse.getResult().getOsTypes());
                mViewModel.setProducts(generalResponse.getResult().getProducts());
                if (mViewModel.getProducts() != null) {
                    ArrayAdapter<Product> productArrayAdapter = new ArrayAdapter<Product>(context, android.R.layout.simple_spinner_item, mViewModel.getProducts());
                    productArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mBinding.productName.setAdapter(productArrayAdapter);
                }

            } else {
                ValidationHelper.showToast(context, generalResponse.getMessage());
            }
        } else {
            ValidationHelper.showToast(context, getString(R.string.invalid_url));
        }

    }


    /**
     * Responsibility - getGeneralRequest method  takes Carrier and Manufacture as parameter and create a request for general api
     * Parameters - Its takes Carrier and Manufacture object as an parameter
     **/
    private HashMap<String, String> getGeneralRequest(Carrier carrier, Manufacture manufacture) {
        HashMap<String, String> hashMap = new HashMap<>();
        if (carrier != null)
            hashMap.put(Constraint.CARRIER_ID, carrier.getIdcarrier() + "");
        if (manufacture != null)
            hashMap.put(Constraint.MANUFACTURE_ID, manufacture.getIdterm());
        return hashMap;
    }


    /**
     * Responsibility - onClick is an predefine method that calls when any click perform
     * Parameters - Its takes view that contains if from which we can know which item is clicked
     **/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel: {
                onBackPressed();
                break;
            }
            case R.id.nextSlide: {
                handlePriceCardGettingHandler();
                break;
            }
        }
    }

    /**
     * Responsibility - handlePriceCardGettingHandler method helps  to fire update screen api and send response in handleScreenAddResponse  method
     * Parameters - No parameter
     **/
    private void handlePriceCardGettingHandler() {
        showHideProgressDialog(true);
        updateProductViewModel.setMutableLiveData(getUpdateScreenRequest());
        LiveData<GlobalResponse> liveData = updateProductViewModel.getLiveData();
        if (!liveData.hasActiveObservers()) {
            liveData.observe(this, new Observer<GlobalResponse>() {
                @Override
                public void onChanged(GlobalResponse globalResponse) {
                    showHideProgressDialog(false);
                    handleScreenAddResponse(globalResponse);
                }
            });
        }

    }

    /**
     * Responsibility - handleScreenAddResponse is an method that check update screen response is ok if yes then call getCardData method
     * Parameters - Its takes GlobalResponse object as an parameter
     **/
    private void handleScreenAddResponse(GlobalResponse screenAddResponseGlobalResponse) {
        if (screenAddResponseGlobalResponse.isApi_status()) {
            mBinding.nextSlide.setVisibility(View.GONE);
            sessionManager.setOrientation(mBinding.webkitOrientation.getSelectedItem().toString());
            getCardData();
        } else {
            ValidationHelper.showToast(context, screenAddResponseGlobalResponse.getMessage());
        }
    }


    /**
     * Responsibility - getCardData method fire the getCard api and pass the response to handleCardGetResponse method
     * Parameters - No parameter
     **/
    private void getCardData() {
        if (Utils.getNetworkState(context)) {
            showHideProgressDialog(true);
            getCardViewModel.setMutableLiveData(getCardRequest());
            LiveData<GlobalResponse<GetCardResponse>> liveData = getCardViewModel.getLiveData();
            if (!liveData.hasActiveObservers()) {
                liveData.observe(this, new Observer<GlobalResponse<GetCardResponse>>() {
                    @Override
                    public void onChanged(GlobalResponse<GetCardResponse> getCardResponseGlobalResponse) {
                        try {
                            DBCaller.storeLogInDatabase(context, getCardResponseGlobalResponse.getResult().getPricecard().getPriceCardName() + Constraint.DATA_STORE, "", "", Constraint.APPLICATION_LOGS);
                            handleCardGetResponse(getCardResponseGlobalResponse);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } else {
            ValidationHelper.showToast(context, getString(R.string.no_internet_available));
        }
    }

    /**
     * Responsibility - handleCardGetResponse method handle response provided by getCardData method if response is ok then set new value of price card promotion and pricing in session and call redirectToMainHandler method
     * Parameters - No parameter
     **/
    private void handleCardGetResponse(GlobalResponse<GetCardResponse> getCardResponseGlobalResponse) throws IOException {
        showHideProgressDialog(false);
        if (getCardResponseGlobalResponse.isApi_status()) {
            try {

                sessionManager.setPriceCard(getCardResponseGlobalResponse.getResult().getPricecard());
                sessionManager.setPromotion(getCardResponseGlobalResponse.getResult().getPromotions());
                sessionManager.setPricing(getCardResponseGlobalResponse.getResult().getPricing());
                redirectToMainHandler(getCardResponseGlobalResponse);
            } catch (Exception e) {

            }

        } else {
            if (getCardResponseGlobalResponse.getResult().getDefaultPriceCard() != null && !getCardResponseGlobalResponse.getResult().getDefaultPriceCard().equals("")) {
                redirectToMainHandler(getCardResponseGlobalResponse);
            } else
                ValidationHelper.showToast(context, getCardResponseGlobalResponse.getMessage());
        }
    }


    /**
     * Responsibility - redirectToMainHandler method  delete daisy older data and set new file path from which app download the price card  and  call redirectToMain method
     * Parameters - Its takes GlobalResponse response object
     **/
    private void redirectToMainHandler(GlobalResponse<GetCardResponse> response) throws IOException {
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.Q) {
            Utils.deleteDaisy();
            String UrlPath;

            if (response.getResult().getPricecard().getFileName1() != null && !response.getResult().getPricecard().getFileName1().equals("")) {
                UrlPath = response.getResult().getPricecard().getFileName1();
            } else {
                UrlPath = response.getResult().getPricecard().getFileName();
            }

            if (response.getResult().getPricecard().getFileName() != null) {
                String configFilePath = Constraint.FOLDER_NAME + Constraint.SLASH;
                File directory = new File(getExternalFilesDir(""), configFilePath);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                String path = Utils.getPath();
                if (path != null) {
                    if (!path.equals(UrlPath)) {
                        Utils.deleteCardFolder();
                        Utils.writeFile(configFilePath, UrlPath);
                        sessionManager.deleteLocation();

                        DBCaller.storeLogInDatabase(context, Constraint.CHANGE_BASE_URL, Constraint.CHANGE_BASE_URL_DESCRIPTION, UrlPath, Constraint.APPLICATION_LOGS);

                    }
                } else {
                    Utils.writeFile(configFilePath, UrlPath);
                }

                redirectToMain();

            } else if (response.getResult().getDefaultPriceCard() != null && !response.getResult().getDefaultPriceCard().equals("")) {
                UrlPath = response.getResult().getDefaultPriceCard();
                String configFilePath = Constraint.FOLDER_NAME + Constraint.SLASH;
                File directory = new File(getExternalFilesDir(""), configFilePath);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                String path = Utils.getPath();
                if (path != null) {
                    if (!path.equals(UrlPath)) {
                        Utils.deleteCardFolder();
                        Utils.writeFile(configFilePath, UrlPath);
                        sessionManager.deleteLocation();

                        DBCaller.storeLogInDatabase(context, Constraint.CHANGE_BASE_URL, Constraint.CHANGE_BASE_URL_DESCRIPTION, UrlPath, Constraint.APPLICATION_LOGS);

                    }
                } else {
                    Utils.deleteCardFolder();
                    sessionManager.deletePriceCard();
                    sessionManager.deletePromotions();
                    sessionManager.setPricing(null);
                    sessionManager.deleteLocation();

                    Utils.writeFile(configFilePath, UrlPath);

                }

                redirectToMain();
            } else {
                ValidationHelper.showToast(context, getString(R.string.invalid_url));
            }

            Intent intent = new Intent(this, EditorTool.class);
            startActivity(intent);
            finish();
        } else {
            Utils.deleteDaisy();
            String UrlPath;

            if (response.getResult().getPricecard().getFileName1() != null && !response.getResult().getPricecard().getFileName1().equals("")) {
                UrlPath = response.getResult().getPricecard().getFileName1();
            } else {
                UrlPath = response.getResult().getPricecard().getFileName();
            }

            if (response.getResult().getPricecard().getFileName() != null) {
                String configFilePath = Environment.getExternalStorageDirectory() + File.separator + Constraint.FOLDER_NAME + Constraint.SLASH;
                File directory = new File(configFilePath);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                String path = Utils.getPath();
                if (path != null) {
                    if (!path.equals(UrlPath)) {
                        Utils.deleteCardFolder();
                        Utils.writeFile(configFilePath, UrlPath);
                        sessionManager.deleteLocation();

                        DBCaller.storeLogInDatabase(context, Constraint.CHANGE_BASE_URL, Constraint.CHANGE_BASE_URL_DESCRIPTION, UrlPath, Constraint.APPLICATION_LOGS);

                    }
                } else {
                    Utils.writeFile(configFilePath, UrlPath);
                }

                redirectToMain();

            } else if (response.getResult().getDefaultPriceCard() != null && !response.getResult().getDefaultPriceCard().equals("")) {
                UrlPath = response.getResult().getDefaultPriceCard();
                String configFilePath = Environment.getExternalStorageDirectory() + File.separator + Constraint.FOLDER_NAME + Constraint.SLASH;
                File directory = new File(configFilePath);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                String path = Utils.getPath();
                if (path != null) {
                    if (!path.equals(UrlPath)) {
                        Utils.deleteCardFolder();
                        Utils.writeFile(configFilePath, UrlPath);
                        sessionManager.deleteLocation();

                        DBCaller.storeLogInDatabase(context, Constraint.CHANGE_BASE_URL, Constraint.CHANGE_BASE_URL_DESCRIPTION, UrlPath, Constraint.APPLICATION_LOGS);

                    }
                } else {
                    Utils.deleteCardFolder();
                    sessionManager.deletePriceCard();
                    sessionManager.deletePromotions();
                    sessionManager.setPricing(null);
                    sessionManager.deleteLocation();

                    Utils.writeFile(configFilePath, UrlPath);

                }

                redirectToMain();
            } else {
                ValidationHelper.showToast(context, getString(R.string.invalid_url));
            }

            Intent intent = new Intent(this, EditorTool.class);
            startActivity(intent);
            finish();
        }
    }


    /**
     * Responsibility -  redirectToMain method redirect screen to MainActivity
     * Parameters - No parameter
     **/
    private void redirectToMain() {
        sessionManager.onBoarding(Constraint.TRUE);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    /**
     * Responsibility -  getCardRequest method create card request
     * Parameters - No parameter
     **/
    private HashMap<String, String> getCardRequest() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constraint.SCREEN_ID, sessionManager.getScreenId() + "");
        hashMap.put(Constraint.TOKEN, sessionManager.getDeviceToken());
        return hashMap;
    }


    /**
     * Responsibility -  getUpdateScreenRequest method create update screen request
     * Parameters - No parameter
     **/
    private HashMap<String, String> getUpdateScreenRequest() {
        HashMap<String, String> hashMap = new HashMap<>();
        if (mViewModel.getSelectedProduct() != null) {
            if (mViewModel.getSelectedProduct().getIdproductStatic() != null)
                hashMap.put(Constraint.ID_PRODUCT_STATIC, mViewModel.getSelectedProduct().getIdproductStatic());
        } else {
            ValidationHelper.showToast(context, getString(R.string.product_not_available));

        }
        hashMap.put(Constraint.TOKEN, sessionManager.getDeviceToken());
        return hashMap;
    }


    @Override
    protected void onStart() {
        if (Locale.getDefault().getLanguage().equals(Constraint.AR)) {
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                mBinding.nextSlide.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ovel_purple_rtl));
            } else {
                mBinding.nextSlide.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ovel_purple_rtl));
            }
        }
        super.onStart();

    }
}
