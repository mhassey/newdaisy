package com.daisy.activity.updateProduct;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.daisy.BuildConfig;
import com.daisy.R;
import com.daisy.activity.base.BaseActivity;
import com.daisy.activity.editorTool.EditorTool;
import com.daisy.activity.mainActivity.MainActivity;
import com.daisy.activity.onBoarding.slider.getCard.GetCardViewModel;
import com.daisy.activity.onBoarding.slider.getCard.vo.GetCardResponse;
import com.daisy.activity.onBoarding.slider.slides.addScreen.AddScreenViewModel;
import com.daisy.common.session.SessionManager;
import com.daisy.database.DBCaller;
import com.daisy.databinding.ActivityUpdateProductBinding;
import com.daisy.pojo.response.Carrier;
import com.daisy.pojo.response.GeneralResponse;
import com.daisy.pojo.response.GlobalResponse;
import com.daisy.pojo.response.LoginResponse;
import com.daisy.pojo.response.Manufacture;
import com.daisy.pojo.response.Product;
import com.daisy.utils.Constraint;
import com.daisy.utils.Utils;
import com.daisy.utils.ValidationHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
         * Initiate objects
     */
    private void initView() {
        context = this;
        setNoTitleBar(this);
        mBinding=DataBindingUtil.setContentView(this,R.layout.activity_update_product);
        mViewModel = new ViewModelProvider(this).get(AddScreenViewModel.class);
        updateProductViewModel=new ViewModelProvider(this).get(UpdateProductViewModel.class);
        getCardViewModel = new ViewModelProvider(this).get(GetCardViewModel.class);
        addOrientationData();
        ArrayAdapter<String> orientationAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, mViewModel.getOrientation());
        orientationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.webkitOrientation.setAdapter(orientationAdapter);
    }


    /**
     * Add orientation
     */
    private void addOrientationData() {
        ArrayList<String> orientation=new ArrayList<>();
        orientation.add(getString(R.string.defaultt));
        orientation.add(getString(R.string.landscape));
        mViewModel.setOrientation(orientation);
    }


    /**
     * Initiate all listener
     */
    private void initClick() {
        mBinding.cancel.setOnClickListener(this);
        mBinding.productName.setOnItemSelectedListener(getProductNameListener());
        mBinding.carrierName.setOnItemSelectedListener(getCarrierListener());
        mBinding.manufactureList.setOnItemSelectedListener(getManufactireListner());
        mBinding.nextSlide.setOnClickListener(this::onClick);
    }



    /**
     * Change system ui to full screen when any change perform in activity
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }

    }



    /**
     * Handle full screen mode
     */
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
     * Handle product name selection
     */
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
     * Handle carrier selection listener
     */
    private AdapterView.OnItemSelectedListener getCarrierListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Carrier carrier = mViewModel.getCarriers().get(position);
                mViewModel.setSelectedCarrier(carrier);
                Manufacture manufacture=(Manufacture)mBinding.manufactureList.getSelectedItem();
                mViewModel.setSelectedManufacture(manufacture);
                getGeneralResponse(carrier,manufacture);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

    }


    /**
     * Handle Manufacture selection listener
     */
    private AdapterView.OnItemSelectedListener getManufactireListner() {
        return  new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Manufacture manufacture = mViewModel.getManufactures().get(position);
                mViewModel.setSelectedManufacture(manufacture);
                Carrier carrier=(Carrier)mBinding.carrierName.getSelectedItem();
                mViewModel.setSelectedCarrier(carrier);

                getGeneralResponse(carrier,manufacture);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    /**
     * Fire general api
     */
    private void getGeneralResponse(Carrier carrier,Manufacture manufacture) {

        if (Utils.getNetworkState(context)) {
            showHideProgressDialog(true);
            HashMap<String, String> generalRequest = getGeneralRequest(carrier,manufacture);
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
     * Handle general response
     */
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
     * Create general request
     */
    private HashMap<String, String> getGeneralRequest(Carrier carrier,Manufacture manufacture) {
        HashMap<String, String> hashMap = new HashMap<>();
        if (carrier != null)
            hashMap.put(Constraint.CARRIER_ID, carrier.getIdcarrier() + "");
        if (manufacture!=null)
            hashMap.put(Constraint.MANUFACTURE_ID,manufacture.getIdterm());
        return hashMap;
    }




    /**
     * Handle click listener
     */
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.cancel:
            {
                onBackPressed();
                break;
            }
            case R.id.nextSlide:
            {
                handlePriceCardGettingHandler();
                break;
            }
        }
    }

    private void handlePriceCardGettingHandler() {
        showHideProgressDialog(true);
        updateProductViewModel.setMutableLiveData(getAddScreenRequest());
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

    private void handleScreenAddResponse(GlobalResponse screenAddResponseGlobalResponse) {
        if (screenAddResponseGlobalResponse.isApi_status()) {
            mBinding.nextSlide.setVisibility(View.GONE);
            sessionManager.setOrientation(mBinding.webkitOrientation.getSelectedItem().toString());
            getCardData();
        } else {
            ValidationHelper.showToast(context, screenAddResponseGlobalResponse.getMessage());
        }
    }



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
                            DBCaller.storeLogInDatabase(context,getCardResponseGlobalResponse.getResult().getPricecard().getPriceCardName()+getString(R.string.data_store),"","",Constraint.APPLICATION_LOGS);
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

    private void handleCardGetResponse(GlobalResponse<GetCardResponse> getCardResponseGlobalResponse) throws IOException {
        showHideProgressDialog(false);
        if (getCardResponseGlobalResponse.isApi_status()) {
            sessionManager.setPriceCard(getCardResponseGlobalResponse.getResult().getPricecard());
            sessionManager.setPromotion(getCardResponseGlobalResponse.getResult().getPromotions());
            sessionManager.setPricing(getCardResponseGlobalResponse.getResult().getPricing());
            redirectToMainHandler(getCardResponseGlobalResponse);

        } else {
            if (getCardResponseGlobalResponse.getResult().getDefaultPriceCard()!=null && !getCardResponseGlobalResponse.getResult().getDefaultPriceCard().equals(""))
            {
                redirectToMainHandler(getCardResponseGlobalResponse);
            }
            else
                ValidationHelper.showToast(context, getCardResponseGlobalResponse.getMessage());
        }
    }



    private void redirectToMainHandler(GlobalResponse<GetCardResponse> response) throws IOException {
        Utils.deleteDaisy();
        String UrlPath;

        if (response.getResult().getPricecard().getFileName1()!=null && !response.getResult().getPricecard().getFileName1().equals(""))
        {
            UrlPath= response.getResult().getPricecard().getFileName1();
        }
        else
        {
            UrlPath= response.getResult().getPricecard().getFileName();
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
                    Utils.writeFile(configFilePath,UrlPath);
                    sessionManager.deleteLocation();

                    DBCaller.storeLogInDatabase(context, Constraint.CHANGE_BASE_URL, Constraint.CHANGE_BASE_URL_DESCRIPTION, UrlPath, Constraint.APPLICATION_LOGS);

                }
            } else {
                Utils.writeFile(configFilePath, UrlPath);
            }

            redirectToMain();

        } else if (response.getResult().getDefaultPriceCard()!=null && !response.getResult().getDefaultPriceCard().equals("")) {
            UrlPath= response.getResult().getDefaultPriceCard();
            String configFilePath = Environment.getExternalStorageDirectory() + File.separator + Constraint.FOLDER_NAME + Constraint.SLASH;
            File directory = new File(configFilePath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String path = Utils.getPath();
            if (path != null) {
                if (!path.equals(UrlPath)) {
                    Utils.deleteCardFolder();
                    Utils.writeFile(configFilePath,UrlPath);
                    sessionManager.deleteLocation();

                    DBCaller.storeLogInDatabase(context, Constraint.CHANGE_BASE_URL, Constraint.CHANGE_BASE_URL_DESCRIPTION, UrlPath, Constraint.APPLICATION_LOGS);

                }
            } else {
                Utils.writeFile(configFilePath, UrlPath);
            }

            redirectToMain();
        }
        else{
            ValidationHelper.showToast(context, getString(R.string.invalid_url));
        }

        Intent intent = new Intent(this, EditorTool.class);
        startActivity(intent);
        finish();
    }

    private void redirectToMain() {
        sessionManager.onBoarding(Constraint.TRUE);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    private HashMap<String, String> getCardRequest() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constraint.SCREEN_ID, sessionManager.getScreenId() + "");
        hashMap.put(Constraint.TOKEN,sessionManager.getDeviceToken());
        return hashMap;
    }


    private HashMap<String, String> getAddScreenRequest() {
        HashMap<String, String> hashMap = new HashMap<>();
           if (mViewModel.getSelectedProduct()!=null) {
            if (mViewModel.getSelectedProduct().getIdproductStatic() != null)
                hashMap.put(Constraint.ID_PRODUCT_STATIC, mViewModel.getSelectedProduct().getIdproductStatic());
        }
        else
        {
            ValidationHelper.showToast(context,getString(R.string.product_not_available));

        }
        hashMap.put(Constraint.TOKEN,sessionManager.getDeviceToken());
        return hashMap;
    }
}
