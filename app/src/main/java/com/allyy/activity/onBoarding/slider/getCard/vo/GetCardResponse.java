package com.allyy.activity.onBoarding.slider.getCard.vo;

import com.allyy.pojo.response.PriceCardMain;
import com.allyy.pojo.response.Pricing;
import com.allyy.pojo.response.Promotion;
import com.allyy.pojo.response.StoreDetails;

import java.util.List;

public class GetCardResponse {
    private List<Promotion> promotions;
    private PriceCardMain pricecard;
    private List<Pricing> pricing;
    private String defaultPriceCard;
    private boolean isDefault;
    private StoreDetails storeDetails;
    private boolean isToken_status;

    public boolean isToken_status() {
        return isToken_status;
    }

    public void setToken_status(boolean token_status) {
        isToken_status = token_status;
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
