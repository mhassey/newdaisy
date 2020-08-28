package com.daisy.activity.onBoarding.slider.deviceDetection.vo;

import com.daisy.pojo.response.PriceCard;
import com.daisy.pojo.response.Promotions;

import java.util.List;

public class DeviceDetectResponse {
    private String  error;
    private PriceCard pricecard;
    private List<Promotions> promotions;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public PriceCard getPricecard() {
        return pricecard;
    }

    public void setPricecard(PriceCard pricecard) {
        this.pricecard = pricecard;
    }

    public List<Promotions> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<Promotions> promotions) {
        this.promotions = promotions;
    }
}
