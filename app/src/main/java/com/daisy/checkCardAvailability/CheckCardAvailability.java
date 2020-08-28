package com.daisy.checkCardAvailability;

import android.content.Intent;
import android.os.Environment;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.daisy.R;
import com.daisy.activity.mainActivity.MainActivity;
import com.daisy.activity.onBoarding.slider.getCard.GetCardViewModel;
import com.daisy.activity.onBoarding.slider.getCard.vo.GetCardResponse;
import com.daisy.activity.refreshTimer.RefreshTimer;
import com.daisy.apiService.ApiService;
import com.daisy.apiService.AppRetrofit;
import com.daisy.common.session.SessionManager;
import com.daisy.database.DBCaller;
import com.daisy.pojo.response.GlobalResponse;
import com.daisy.pojo.response.Pricing;
import com.daisy.pojo.response.Promotion;
import com.daisy.pojo.response.UpdateCards;
import com.daisy.utils.Constraint;
import com.daisy.utils.Utils;
import com.daisy.utils.ValidationHelper;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckCardAvailability {
    private GetCardViewModel getCardViewModel;
    private SessionManager sessionManager;

    public void checkCard() {
        sessionManager = SessionManager.get();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getCard();
            }
        }).start();
    }

    private void getCard() {
        MutableLiveData<GlobalResponse<GetCardResponse>> liveData = new MutableLiveData<>();
        ApiService apiService = AppRetrofit.getInstance().getApiService();
        HashMap<String, String> hashMap = getCardRequest();
        Call<GlobalResponse<GetCardResponse>> globalResponseCall = apiService.getCard(hashMap, hashMap.get(Constraint.TOKEN));
        globalResponseCall.enqueue(new Callback<GlobalResponse<GetCardResponse>>() {
            @Override
            public void onResponse(Call<GlobalResponse<GetCardResponse>> call, Response<GlobalResponse<GetCardResponse>> response) {

                handleResponse(response);

            }

            @Override
            public void onFailure(Call<GlobalResponse<GetCardResponse>> call, Throwable t) {
                handleResponse(null);
            }
        });

    }

    private void handleResponse(Response<GlobalResponse<GetCardResponse>> liveData) {
        if (liveData != null) {
            if (liveData.isSuccessful()) {
                GlobalResponse<GetCardResponse> response = liveData.body();
                if (response.isApi_status()) {
                    if (response.getResult() != null) {
                        if (!response.getResult().isDefault()) {
                            if (response.getResult().getPricecard() != null && response.getResult().getPricecard().getFileName() != null) {
                                sessionManager.deleteLocation();
                                sessionManager.setPriceCard(response.getResult().getPricecard());
                                sessionManager.setPromotion(response.getResult().getPromotions());
                                sessionManager.setPricing(response.getResult().getPricing());
                                sessionManager.setCardDeleted(false);
                                redirectToMain(response);

                            } else if (response.getResult().getPromotions() != null) {
                                sessionManager.setPromotion(response.getResult().getPromotions());

                                if (response.getResult().getPricing() != null) {
                                    sessionManager.setPricing(response.getResult().getPricing());
                                }
                                EventBus.getDefault().post(new Promotion());
                            }

                        } else {
                            if (response.getResult().getPromotions() != null && !response.getResult().getPromotions().isEmpty()) {
                                sessionManager.setPromotion(response.getResult().getPromotions());
                                EventBus.getDefault().post(new Promotion());
                            }
                        }

                    } else {

                    }
                } else {

                }
            }

        }
    }

    private void redirectToMain(GlobalResponse<GetCardResponse> response) {
        Utils.deleteDaisy();
        String UrlPath = response.getResult().getPricecard().getFileName();
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
                    try {
                        Utils.writeFile(configFilePath, UrlPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sessionManager.deleteLocation();
                    EventBus.getDefault().post(new Pricing());
                }
            } else {
                try {
                    Utils.writeFile(configFilePath, UrlPath);
                    sessionManager.deleteLocation();
                    EventBus.getDefault().post(new Pricing());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private HashMap<String, String> getCardRequest() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constraint.SCREEN_ID, sessionManager.getScreenId() + "");
        hashMap.put(Constraint.TOKEN, sessionManager.getDeviceToken());
//        hashMap.put(Constraint.SCREEN_ID, "47");
        return hashMap;
    }

}
