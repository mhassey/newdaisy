package com.daisy.optimalPermission.checkCardAvailability;

import android.os.Environment;

import com.daisy.optimalPermission.activity.onBoarding.slider.getCard.vo.GetCardResponse;
import com.daisy.optimalPermission.apiService.ApiService;
import com.daisy.optimalPermission.apiService.AppRetrofit;
import com.daisy.mainDaisy.app.AppController;
import com.daisy.optimalPermission.session.SessionManager;
import com.daisy.optimalPermission.pojo.response.GlobalResponse;
import com.daisy.optimalPermission.pojo.response.PriceCard;
import com.daisy.optimalPermission.pojo.response.Pricing;
import com.daisy.optimalPermission.pojo.response.Promotion;
import com.daisy.optimalPermission.utils.Constraint;
import com.daisy.optimalPermission.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * CheckCardAvailability works in background when ever service required to fire getCard api then service use CheckCardAvailability class
 */
public class CheckCardAvailability {
    private SessionManager sessionManager;
    private String callFrom = null;

    public void checkCard() {
        sessionManager = SessionManager.get();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getCard();
            }
        }).start();
    }

    public void checkCard(String callFrom) {
        this.callFrom = callFrom;
        sessionManager = SessionManager.get();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getCard();
            }
        }).start();
    }

    /**
     * Fire getCard api from background
     */
    private void getCard() {
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

    /**
     * Handle getcard api response
     */
    private void handleResponse(Response<GlobalResponse<GetCardResponse>> liveData) {
        if (liveData != null) {
            if (liveData.isSuccessful()) {
                GlobalResponse<GetCardResponse> response = liveData.body();
                if (response.isApi_status()) {
                    if (response.getResult() != null) {
                        sessionManager.setOpenTime(response.getResult().getStoreDetails().getOpen());
                        sessionManager.setCloseTime(response.getResult().getStoreDetails().getClosed());
                        sessionManager.setOffset(response.getResult().getStoreDetails().getUTCOffset());
                        sessionManager.setPricingPlainId(response.getResult().getStoreDetails().getPricingPlanID());
                        if (callFrom != null) {
                            sessionManager.setServerTime(response.getResult().getStoreDetails().getCurrentTime());
                            //Utils.getInvertedTimeWithNewCorrectionFactor();
                        }
                        if (!response.getResult().isDefault()) {
                            if (response.getResult().getPricecard() != null && response.getResult().getPricecard().getFileName() != null) {
                                sessionManager.deleteLocation();
                                sessionManager.setPriceCard(response.getResult().getPricecard());
                                sessionManager.setPromotion(response.getResult().getPromotions());
                                sessionManager.setPricing(response.getResult().getPricing());
                                sessionManager.setCardDeleted(false);
                                redirectToMain(response);

                            } else if (response.getResult().getPromotions() != null && !response.getResult().getPromotions().isEmpty()) {
                                sessionManager.setPromotion(response.getResult().getPromotions());

                                if (response.getResult().getPricing() != null && !response.getResult().getPromotions().isEmpty()) {
                                    sessionManager.setPricing(response.getResult().getPricing());
                                }
                                EventBus.getDefault().post(new Promotion());
                            } else if (response.getResult().getPricing() != null && !response.getResult().getPricing().isEmpty()) {
                                sessionManager.setPricing(response.getResult().getPricing());
                                EventBus.getDefault().post(new Pricing());

                            }

                        } else {
                            if (response.getResult().getPromotions() != null && !response.getResult().getPromotions().isEmpty()) {
                                sessionManager.setPromotion(response.getResult().getPromotions());
                                EventBus.getDefault().post(new Promotion());

                            }
                            if (response.getResult().getPricing() != null && !response.getResult().getPricing().isEmpty()) {
                                sessionManager.setPricing(response.getResult().getPricing());
                                EventBus.getDefault().post(new Pricing());

                            }

                        }

                    } else {

                    }
                } else {

                }
            }

        }
    }

    /**
     * Redirect to main activity
     */
    private void redirectToMain(GlobalResponse<GetCardResponse> response) {
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.Q) {
            String UrlPath = response.getResult().getPricecard().getFileName();
            if (response.getResult().getPricecard().getFileName() != null) {
                String configFilePath =Constraint.FOLDER_NAME + Constraint.SLASH;
                File directory = new File(AppController.getInstance().getExternalFilesDir(""), configFilePath);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                String path = Utils.getPath();
                if (path != null) {
                    //if (!path.equals(UrlPath)) {
                        Utils.deleteCardFolder();
                        try {
                            Utils.writeFile(configFilePath, UrlPath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        sessionManager.deleteLocation();
                        EventBus.getDefault().post(new PriceCard());
                  //  }
                } else {
                    try {
                        Utils.writeFile(configFilePath, UrlPath);
                        sessionManager.deleteLocation();
                        EventBus.getDefault().post(new PriceCard());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {

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
                        EventBus.getDefault().post(new PriceCard());
                    }
                } else {
                    try {
                        Utils.writeFile(configFilePath, UrlPath);
                        sessionManager.deleteLocation();
                        EventBus.getDefault().post(new PriceCard());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Create Card request
     */
    private HashMap<String, String> getCardRequest() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constraint.SCREEN_ID, sessionManager.getScreenId() + "");
        hashMap.put(Constraint.TOKEN, sessionManager.getDeviceToken());
        if (sessionManager.getPriceCard() != null)
            hashMap.put(Constraint.pricecardid, sessionManager.getPriceCard().getIdpriceCard());
        return hashMap;
    }

}
