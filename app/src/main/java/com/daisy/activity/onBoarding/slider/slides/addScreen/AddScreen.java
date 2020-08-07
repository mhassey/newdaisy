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
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.daisy.R;
import com.daisy.activity.base.BaseFragment;
import com.daisy.common.session.SessionManager;
import com.daisy.databinding.AddScreenBinding;
import com.daisy.pojo.response.Carrier;
import com.daisy.pojo.response.GeneralResponse;
import com.daisy.pojo.response.GlobalResponse;
import com.daisy.pojo.response.Product;
import com.daisy.utils.Constraint;
import com.daisy.utils.Utils;
import com.daisy.utils.ValidationHelper;

import java.util.HashMap;
import java.util.List;

public class AddScreen extends BaseFragment {

    public AddScreenBinding mBinding;
    private AddScreenViewModel mViewModel;
    private Context context;
    public Product selectedProduct;
    public Carrier selectedCarrier;
    private List<Product> products;
    private List<Carrier> carriers;
    private SessionManager sessionManager;

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


    private void initView() {
        mViewModel = new ViewModelProvider(this).get(AddScreenViewModel.class);

        context = requireContext();
        sessionManager = SessionManager.get();
        getGeneralResponse(null);
    }

    private void initClick() {
        mBinding.productName.setOnItemSelectedListener(getProductNameListener());
        mBinding.carrierName.setOnItemSelectedListener(getCarrierListener());
    }

    private AdapterView.OnItemSelectedListener getProductNameListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedProduct = products.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    private AdapterView.OnItemSelectedListener getCarrierListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Carrier carrier = carriers.get(position);
                selectedCarrier = carrier;
                getGeneralResponse(carrier);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

    }


    private void getGeneralResponse(Carrier carrier) {
        if (Utils.getNetworkState(context)) {
            showHideProgressDialog(true);
            HashMap<String, String> generalRequest = getGeneralRequest(carrier);
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

    private void handleResponse(GlobalResponse<GeneralResponse> generalResponse) {
        showHideProgressDialog(false);
        if (generalResponse != null) {
            if (generalResponse.isApi_status()) {
                sessionManager.setOSType(generalResponse.getResult().getOsTypes());
                if (selectedCarrier == null) {
                    carriers = (generalResponse.getResult().getCarrier());
                    if (carriers != null) {
                        ArrayAdapter<Carrier> carrierArrayAdapter = new ArrayAdapter<Carrier>(context, android.R.layout.simple_spinner_item, carriers);
                        carrierArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mBinding.carrierName.setAdapter(carrierArrayAdapter);
                    }
                } else {
                    products = (generalResponse.getResult().getProducts());
                    if (products != null) {
                        ArrayAdapter<Product> productArrayAdapter = new ArrayAdapter<Product>(context, android.R.layout.simple_spinner_item, products);
                        productArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mBinding.productName.setAdapter(productArrayAdapter);
                    }
                }
            } else {
                ValidationHelper.showToast(context, generalResponse.getMessage());
            }
        } else {
            ValidationHelper.showToast(context, getString(R.string.invalid_url));
        }

    }


    private HashMap<String, String> getGeneralRequest(Carrier carrier) {
        HashMap<String, String> hashMap = new HashMap<>();
        if (carrier != null)
            hashMap.put(Constraint.CARRIER_ID, carrier.getIdcarrier() + "");
        return hashMap;
    }

    public static AddScreen getInstance() {
        return new AddScreen();
    }


}
