package com.daisy.checkCardAvailability;

import android.os.Environment;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.daisy.activity.onBoarding.slider.getCard.GetCardViewModel;
import com.daisy.activity.onBoarding.slider.getCard.vo.GetCardResponse;
import com.daisy.apiService.ApiService;
import com.daisy.apiService.AppRetrofit;
import com.daisy.common.session.SessionManager;
import com.daisy.pojo.response.GlobalResponse;
import com.daisy.pojo.response.UpdateCards;
import com.daisy.utils.Constraint;
import com.daisy.utils.Utils;

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
        HashMap<String,String> hashMap=getCardRequest();
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
    if (liveData!=null)
    {
        if (liveData.isSuccessful()) {
            GlobalResponse<GetCardResponse> globalResponse = liveData.body();
            if (liveData.body().isApi_status()) {
                if (globalResponse.getResult() != null) {
                    sessionManager.setPriceCard(globalResponse.getResult().getPricecard());
                    sessionManager.setPromotion(globalResponse.getResult().getPromotions());
                    sessionManager.setPricing(globalResponse.getResult().getPricing());
                    String UrlPath = globalResponse.getResult().getPricecard().getFileName();
                    String configFilePath = Environment.getExternalStorageDirectory() + File.separator + Constraint.FOLDER_NAME + Constraint.SLASH;

                    try {
                        Utils.writeFile(configFilePath, UrlPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    EventBus.getDefault().post(new UpdateCards());
                }
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
