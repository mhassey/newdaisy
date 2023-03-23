package com.daisy.activity.onBoarding.slider.slides.addScreen;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.daisy.pojo.response.Carrier;
import com.daisy.pojo.response.GeneralResponse;
import com.daisy.pojo.response.GlobalResponse;
import com.daisy.pojo.response.Manufacture;
import com.daisy.pojo.response.Product;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class AddScreenViewModel extends AndroidViewModel {

    public Product selctedProduct;
    public Product autoselctedProduct;
    private MutableLiveData<HashMap<String, String>> generalRequest = new MutableLiveData<>();
    private LiveData<GlobalResponse<GeneralResponse>> generalResponseLiveData;
    private MutableLiveData<HashMap<String, String>> generalRequestForDeviceSpecific = new MutableLiveData<>();
    public boolean isManufactureSelected = false;
    private LiveData<GlobalResponse<GeneralResponse>> generalResponseLiveDataForDeviceSpecific;

    private AddScreenRepo addScreenRepo;
    public Product selectedProduct;
    public Carrier selectedCarrier;
    private List<Product> products;
    private List<Carrier> carriers;
    private List<String> orientation;
    private List<Manufacture> manufactures;
    private List<Product> autoSelectedProduct;
    private Manufacture manufacture;
    public String storeCode = null;

    public AddScreenViewModel(@NonNull Application application) {
        super(application);
        addScreenRepo = new AddScreenRepo();
        generalResponseLiveData = Transformations.switchMap(generalRequest, new Function<HashMap<String, String>, LiveData<GlobalResponse<GeneralResponse>>>() {
            @Override
            public LiveData<GlobalResponse<GeneralResponse>> apply(HashMap<String, String> input) {
                return addScreenRepo.getGeneralResponse(input);
            }
        });
        generalResponseLiveDataForDeviceSpecific = Transformations.switchMap(generalRequestForDeviceSpecific, new Function<HashMap<String, String>, LiveData<GlobalResponse<GeneralResponse>>>() {
            @Override
            public LiveData<GlobalResponse<GeneralResponse>> apply(HashMap<String, String> input) {
                return new AddScreenRepo().getGeneralResponse(input);
            }
        });
    }

    public MutableLiveData<HashMap<String, String>> getGeneralRequestForDeviceSpecific() {
        return generalRequestForDeviceSpecific;
    }

    public LiveData<GlobalResponse<GeneralResponse>> getGeneralResponseLiveDataForDeviceSpecific() {
        return generalResponseLiveDataForDeviceSpecific;
    }

    public void setGeneralResponseLiveDataForDeviceSpecific(LiveData<GlobalResponse<GeneralResponse>> generalResponseLiveDataForDeviceSpecific) {
        this.generalResponseLiveDataForDeviceSpecific = generalResponseLiveDataForDeviceSpecific;
    }

    public void setGeneralRequestForDeviceSpecific(HashMap<String, String> generalRequestForDeviceSpecific) {
        this.generalRequestForDeviceSpecific.setValue(generalRequestForDeviceSpecific);
    }

    public List<Manufacture> getManufactures() {
        return manufactures;
    }

    public void setManufactures(List<Manufacture> manufactures) {
        this.manufactures = manufactures;
    }

    public void setGeneralRequest(HashMap<String, String> request) {
        generalRequest.setValue(request);
    }

    public LiveData<GlobalResponse<GeneralResponse>> getGeneralResponseLiveData() {
        return generalResponseLiveData;
    }

    public Product getSelectedProduct() {
        return selectedProduct;
    }

    public void setSelectedProduct(Product selectedProduct) {
        this.selectedProduct = selectedProduct;
    }

    public Carrier getSelectedCarrier() {
        return selectedCarrier;
    }

    public void setSelectedCarrier(Carrier selectedCarrier) {
        this.selectedCarrier = selectedCarrier;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<Carrier> getCarriers() {
        return carriers;
    }

    public void setCarriers(List<Carrier> carriers) {
        this.carriers = carriers;
    }

    public List<String> getOrientation() {
        return orientation;
    }

    public void setOrientation(List<String> orientation) {
        this.orientation = orientation;
    }

    public void setSelectedManufacture(Manufacture selectedItem) {

    }

    public Manufacture getManufacture() {
        return manufacture;
    }

    public void setManufacture(Manufacture manufacture) {
        this.manufacture = manufacture;
    }

    public void setAutoDetectProduct(List<Product> products) {
        this.autoSelectedProduct = products;
    }

    public List<Product> getAutoSelectedProduct() {
        return autoSelectedProduct;
    }

    public void setAutoSelectProduct(Product product) {
        autoselctedProduct = product;
    }

    public Product getAutoSelctedProduct() {
        return autoselctedProduct;
    }
}
