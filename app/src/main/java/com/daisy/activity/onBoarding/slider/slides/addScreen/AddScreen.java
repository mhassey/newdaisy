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
import com.daisy.database.DBCaller;
import com.daisy.databinding.AddScreenBinding;
import com.daisy.pojo.response.GeneralResponse;
import com.daisy.pojo.response.GlobalResponse;
import com.daisy.pojo.response.Product;
import com.daisy.utils.Constraint;
import com.daisy.utils.Utils;
import com.daisy.utils.ValidationHelper;

import java.util.HashMap;
import java.util.List;

public class AddScreen extends BaseFragment implements AdapterView.OnItemSelectedListener {

    public  AddScreenBinding mBinding;
    private AddScreenViewModel mViewModel;
    private Context context;
    public Product selectedProduct;
    private List<Product> products;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding= DataBindingUtil.inflate(inflater, R.layout.add_screen, container, false);
        initView();
        initClick();
        return mBinding.getRoot();
    }


    private void initView() {
        mViewModel=new ViewModelProvider(this).get(AddScreenViewModel.class);

        context=requireContext();
        getGeneralResponse();
    }

    private void initClick() {
        mBinding.productName.setOnItemSelectedListener(this);
    }

    private void getGeneralResponse() {
        if (Utils.getNetworkState(context)) {
            HashMap<String,String> generalRequest=getGeneralRequest();
            mViewModel.setGeneralRequest(generalRequest);
            LiveData<GlobalResponse<GeneralResponse>> liveData=mViewModel.getGeneralResponseLiveData();
            if (!liveData.hasActiveObservers())
            {
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
        if (generalResponse!=null) {
            if (generalResponse.isApi_status()) {
                products = (generalResponse.getResult().getProducts());
                //Creating the ArrayAdapter instance having the country list
                ArrayAdapter<Product> aa = new ArrayAdapter<Product>(context, android.R.layout.simple_spinner_item, products);
                aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                //Setting the ArrayAdapter data on the Spinner
                mBinding.productName.setAdapter(aa);
            } else {
                ValidationHelper.showToast(context, generalResponse.getMessage());
            }
        }
        else
        {
            ValidationHelper.showToast(context,getString(R.string.invalid_url));
        }

    }



    private HashMap<String, String> getGeneralRequest() {
    HashMap<String,String> hashMap=new HashMap<>();
    return hashMap;
    }

    public static AddScreen getInstance() {
        return new AddScreen();
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedProduct=products.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
