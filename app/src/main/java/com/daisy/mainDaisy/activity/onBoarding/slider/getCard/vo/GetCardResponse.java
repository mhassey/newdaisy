package com.daisy.mainDaisy.activity.onBoarding.slider.getCard.vo;

import com.daisy.mainDaisy.pojo.response.PriceCardMain;
import com.daisy.mainDaisy.pojo.response.Pricing;
import com.daisy.mainDaisy.pojo.response.Promotion;
import com.daisy.mainDaisy.pojo.response.StoreDetails;

import java.util.List;

public class GetCardResponse {
    private List<Promotion> promotions;
    private PriceCardMain pricecard;
    private List<Pricing> pricing;
    private String defaultPriceCard;
    private boolean isDefault;
    private boolean token_status;
    private StoreDetails storeDetails;

    public boolean isToken_status() {
        return token_status;
    }

    public void setToken_status(boolean token_status) {
        this.token_status = token_status;
    }

    public StoreDetails getStoreDetails() {
        return storeDetails;
    }

    public void setStoreDetails(StoreDetails storeDetails) {
        this.storeDetails = storeDetails;
    }


    public boolean isDefault() {
        return isDefault;
    }

    public String getDefaultPriceCard() {
        return defaultPriceCard;
    }

    public void setDefaultPriceCard(String defaultPriceCard) {
        this.defaultPriceCard = defaultPriceCard;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public List<Promotion> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<Promotion> promotions) {
        this.promotions = promotions;
    }

    public PriceCardMain getPricecard() {
        return pricecard;
    }

    public void setPricecard(PriceCardMain pricecard) {
        this.pricecard = pricecard;
    }

    public List<Pricing> getPricing() {
        return pricing;
    }

    public void setPricing(List<Pricing> pricing) {
        this.pricing = pricing;
    }


}
