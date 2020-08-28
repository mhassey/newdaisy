package com.daisy.activity.onBoarding.slider.getCard.vo;

import com.daisy.pojo.response.PriceCardMain;
import com.daisy.pojo.response.Pricing;
import com.daisy.pojo.response.Promotion;

import java.util.List;

public class GetCardResponse {
    private List<Promotion> promotions;
    private PriceCardMain pricecard;
    private List<Pricing> pricing;
    private String defaultPriceCard;
    private boolean isDefault;

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
