package com.daisy.activity.onBoarding.slider.slides.addScreen;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.daisy.R;
import com.daisy.activity.base.BaseFragment;
import com.daisy.activity.onBoarding.slider.OnBoarding;
import com.daisy.common.session.SessionManager;
import com.daisy.databinding.AddScreenBinding;
import com.daisy.pojo.response.Carrier;
import com.daisy.pojo.response.GeneralResponse;
import com.daisy.pojo.response.GlobalResponse;
import com.daisy.pojo.response.Manufacture;
import com.daisy.pojo.response.Product;
import com.daisy.utils.Constraint;
import com.daisy.utils.Utils;
import com.daisy.utils.ValidationHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Purpose -  AddScreen is an fragment that help to show all carrier and product and orientation
 * Responsibility - Its show latest carrier , product name , orientation and manufacture to user for selection
 **/
public class AddScreen extends BaseFragment implements View.OnClickListener {

    public AddScreenBinding mBinding;
    public AddScreenViewModel mViewModel;
    private Context context;
    private SessionManager sessionManager;
    final int sdk = android.os.Build.VERSION.SDK_INT;
    private static OnBoarding baording;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.add_screen, container, false);
        initView();
        initClick();
        return mBinding.getRoot();
    }


    /**
     * Responsibility - initView method is used for initiate all object and perform some initial level task
     * Parameters - No parameter
     **/
    private void initView() {
        context = requireContext();
        mViewModel = new ViewModelProvider(this).get(AddScreenViewModel.class);
        addOrientationData();
        ArrayAdapter<String> orientationAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, mViewModel.getOrientation());
        orientationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mBinding.webkitOrientation.setAdapter(orientationAdapter);


    }


    /**
     * Responsibility - addOrientationData method is used for add data in orientation list set it to view model
     * Parameters - No parameter
     **/
    private void addOrientationData() {
        ArrayList<String> orientation = new ArrayList<>();
        orientation.add(getString(R.string.landscape));
        orientation.add(getString(R.string.defaultt));

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
    }


    /**
     * onResume handler
     */
    @Override
    public void onResume() {
        super.onResume();
        handleResumeWork();
    }

    /**
     * Purpose - handleResumeWork method handles all design work according to page and set adaptors
     */
    private void handleResumeWork() {
        sessionManager = SessionManager.get();
        designWork();
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
     * Responsibility - designWork method is used for run time color change at bottom next button
     * Parameters - No parameter
     **/
    private void designWork() {
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            if (Locale.getDefault().getLanguage().equals(Constraint.AR))
                baording.mBinding.nextSlide.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ovel_purple_rtl));
            else

                baording.mBinding.nextSlide.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ovel_purple));
        } else {
            if (Locale.getDefault().getLanguage().equals(Constraint.AR))
                baording.mBinding.nextSlide.setBackground(ContextCompat.getDrawable(context, R.drawable.ovel_purple_rtl));
            else

                baording.mBinding.nextSlide.setBackground(ContextCompat.getDrawable(context, R.drawable.ovel_purple));
        }
        baording.mBinding.tabDotsLayout.getTabAt(0).setIcon(getResources().getDrawable(R.drawable.default_dot));
        baording.mBinding.tabDotsLayout.getTabAt(1).setIcon(getResources().getDrawable(R.drawable.selected_purple));
//        baording.mBinding.tabDotsLayout.getTabAt(2).setIcon(getResources().getDrawable(R.drawable.selected_purple));
    }


    /**
     * Responsibility - getProductNameListener method is used for item selection of product
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
     * Responsibility - getCarrierListener method is used for item selection of carrier
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
     * Responsibility - getManufactureListener method is used for item selection of manufacture
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
     * Responsibility - getGeneralResponse method is used for fire general api and get response and send response to handleResponse method
     * Parameters - Its takes Carrier,Manufacture object as parameter
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
     * Responsibility - handleResponse method is called by getGeneralResponse method its check if response is correct then set value in adaptor
     * Parameters - Its takes GlobalResponse<GeneralResponse> object as parameter
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
     * Responsibility - getGeneralRequest method is used for create general api request
     * Parameters - Its takes Carrier,Manufacture object as parameter
     **/
    private HashMap<String, String> getGeneralRequest(Carrier carrier, Manufacture manufacture) {
        HashMap<String, String> hashMap = new HashMap<>();
        if (carrier != null)
            hashMap.put(Constraint.CARRIER_ID, carrier.getIdcarrier() + "");
        if (manufacture != null)
            hashMap.put(Constraint.MANUFACTURE_ID, manufacture.getIdterm());
        return hashMap;
    }

    public static AddScreen getInstance(OnBoarding bording) {
        baording = bording;
        return new AddScreen();

    }


    /**
     * Responsibility - onClick is an predefine method that calls when any click perform
     * Parameters - Its takes view that contains if from which we can know which item is clicked
     **/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel: {
                getActivity().onBackPressed();
                break;
            }
        }
    }
}
