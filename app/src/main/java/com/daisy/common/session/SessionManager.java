package com.daisy.common.session;

import android.app.Application;

import com.daisy.activity.onBoarding.slider.slides.signup.vo.SignUpResponse;
import com.daisy.apiService.ApiConstant;
import com.daisy.app.AppController;
import com.daisy.pojo.response.LoginResponse;
import com.daisy.pojo.response.OsType;
import com.daisy.pojo.response.PriceCard;
import com.daisy.pojo.response.PriceCardMain;
import com.daisy.pojo.response.Pricing;
import com.daisy.pojo.response.Promotion;
import com.daisy.pojo.response.ScreenPosition;
import com.daisy.pojo.response.Time;
import com.daisy.utils.Constraint;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class SessionManager {

    private static SessionManager sInstance;
    private PreferenceUtil pref;


    private SessionManager(Application application) {
        pref = new PreferenceUtil(application);
    }

    public static void init(Application application) {
        if (sInstance == null) {
            sInstance = new SessionManager(application);
        }
    }

    public static SessionManager get(Application application) {
        init(application);
        return sInstance;
    }

    public static SessionManager get() {
        init(AppController.getInstance());
        return sInstance;
    }

    public void removeSession() {
        String val = getDeviceToken();
        pref.removeSession();
        setDeviceToken(val);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(PrefConstant.IS_LOGGED_IN);
    }

    public void setLoggedIn(boolean b) {
        pref.setBooleanData(PrefConstant.IS_LOGGED_IN, b);
    }

    public void setEmailVerification(boolean b) {
        pref.setBooleanData(PrefConstant.email_Verification, b);
    }

    public boolean getEmailVerification() {
        return pref.getBoolean(PrefConstant.email_Verification);
    }

    public boolean isPhoneVerified() {
        return pref.getBoolean(PrefConstant.IS_PHONE_VERIFIED);
    }

    public void setPhoneVerified(boolean b) {
        pref.setBooleanData(PrefConstant.IS_PHONE_VERIFIED, b);
    }

    public void setSubData(String subData) {
        pref.setStringData(PrefConstant.SUBDATA, subData);
    }

    public String getSubData() {
        return pref.getStringData(PrefConstant.SUBDATA);
    }

    public void setUserType(int type) {
        pref.setIntData(PrefConstant.USER_TYPE, type);
    }

    public int getUserType() {
        return pref.getIntData(PrefConstant.USER_TYPE);
    }

    public boolean isEmailVerified() {
        return pref.getBoolean(PrefConstant.IS_EMAIL_VERIFIED);
    }

    public void setEmailVerified(boolean b) {
        pref.setBooleanData(PrefConstant.IS_EMAIL_VERIFIED, b);
    }


    public int getNotificationOn() {
        return pref.getIntData(PrefConstant.isNotificationOn);
    }

    public void setNotificationOn(int value) {
        pref.setIntData(PrefConstant.isNotificationOn, value);
    }


    public int getMobileOn() {
        return pref.getIntData(PrefConstant.isMobileVisible);
    }

    public void setMobileOn(int value) {
        pref.setIntData(PrefConstant.isMobileVisible, value);
    }

    /**
     * This method is to send userid in headre even if user is not marked as logged in
     * to overcome verifyMobile api issue.
     *
     * @return
     */
    public int getUserIdForAPiHeader() {
        return pref.getIntData(PrefConstant.USER_ID);
    }

    public void saveUserInfo(String accessToken) {
        pref.setBooleanData(PrefConstant.IS_LOGGED_IN, true);
        pref.setStringData(PrefConstant.ACCESS_TOKEN, accessToken);
    }

    public void clear() {
        pref.clear();
    }

    public String getUserId() {
        return pref.getStringData(PrefConstant.USER_ID);
    }

    public void setUserId(String token) {
        pref.setStringData(PrefConstant.USER_ID, token);
    }

    public String getFCMToken() {
        return pref.getStringData(PrefConstant.FCM_TOKEN);
    }

    public void setFCMToken(String token) {
        pref.setStringData(PrefConstant.FCM_TOKEN, token);
    }


    public String getPassword() {
        return pref.getStringData(PrefConstant.PASSWORD);
    }

    public void setPassword(String token) {
        pref.setStringData(PrefConstant.PASSWORD, token);
    }


    public String getFilterDate() {
        return pref.getStringData(PrefConstant.FILTER_DATE);
    }

    public void setFilterDate(String date) {
        pref.setStringData(PrefConstant.FILTER_DATE, date);
    }

    public String getFilterTime() {
        return pref.getStringData(PrefConstant.FILTER_TIME);
    }

    public void setFilterTime(String time) {
        pref.setStringData(PrefConstant.FILTER_TIME, time);
    }

    public String getSortType() {
        return pref.getStringData(PrefConstant.SORT_TYPE);
    }

    public void setSortType(String type) {
        pref.setStringData(PrefConstant.SORT_TYPE, type);
    }

    public String getServiceCategory() {
        return pref.getStringData(PrefConstant.SERVICE_CATEGORY);
    }

    public void setServiceCategory(String type) {
        pref.setStringData(PrefConstant.SERVICE_CATEGORY, type);
    }

//    public void setSocialUser(boolean isSocialUser) {
//        pref.setBooleanData(AppConstants.IS_SOCIAL_USER, isSocialUser);
//    }
//
//    public boolean getSocialUser() {
//        return pref.getBoolean(AppConstants.IS_SOCIAL_USER);
//    }

    public String getEmail() {
        return pref.getStringData(PrefConstant.EMAIL);
    }

    public void setEmail(String token) {
        pref.setStringData(PrefConstant.EMAIL, token);
    }

    public String getUserName() {
        return pref.getStringData(PrefConstant.USERNAME);
    }

    public void setUserName(String token) {
        pref.setStringData(PrefConstant.USERNAME, token);
    }


    public String getFirstName() {
        return pref.getStringData(PrefConstant.FIRST_NAME);
    }

    public void setFirstName(String token) {
        pref.setStringData(PrefConstant.FIRST_NAME, token);
    }

    public String getLastName() {
        return pref.getStringData(PrefConstant.LAST_NAME);
    }

    public void setLastName(String token) {
        pref.setStringData(PrefConstant.LAST_NAME, token);
    }

    public String getPhoneNo() {
        return pref.getStringData(PrefConstant.PHONE_NUMEBR);
    }

    public void setPhoneNo(String token) {
        pref.setStringData(PrefConstant.PHONE_NUMEBR, token);
    }

    public String getLoginType() {
        return pref.getStringData(PrefConstant.LOGIN_TYPE);
    }

    public void setLoginType(String token) {
        pref.setStringData(PrefConstant.LOGIN_TYPE, token);
    }

    public String getImagePath() {
        return pref.getStringData(PrefConstant.IMAGE_PATH);
    }

    public void setImagePath(String token) {
        pref.setStringData(PrefConstant.IMAGE_PATH, token);
    }

    public String getUserImagePath() {
        return pref.getStringData(PrefConstant.IMAGE_PATH);
    }

    public void setUserImagePath(String token) {
        pref.setStringData(PrefConstant.IMAGE_PATH, token);
    }

    public String getLoggedInUserId() {
        return pref.getStringData(PrefConstant.LOGGED_IN_USER_ID);
    }

    public void setLoggedInUserId(String token) {
        pref.setStringData(PrefConstant.LOGGED_IN_USER_ID, token);
    }

    public String getLatitude() {
        return pref.getStringData(PrefConstant.LATITUDE);
    }

    public void setLatitude(String token) {
        pref.setStringData(PrefConstant.LATITUDE, token);
    }

    public String getLongitude() {
        return pref.getStringData(PrefConstant.LONGITUDE);
    }

    public void setLongitude(String token) {
        pref.setStringData(PrefConstant.LONGITUDE, token);
    }

    public String getWorkAddress() {
        return pref.getStringData(PrefConstant.WORK_ADDRESS);
    }

    public void setWorkAddress(String token) {
        pref.setStringData(PrefConstant.WORK_ADDRESS, token);
    }

    public String getLocation() {
        return pref.getStringData(PrefConstant.LOCATION);
    }

    public void setLocation(String token) {
        pref.setStringData(PrefConstant.LOCATION, token);
    }

    public String getAvailableFromTime() {
        return pref.getStringData(PrefConstant.AVAILABLE_FROM);
    }

    public void setAvailableFromTime(String token) {
        pref.setStringData(PrefConstant.AVAILABLE_FROM, token);
    }

    public String getAvailableToTime() {
        return pref.getStringData(PrefConstant.AVAILABLE_TO);
    }

    public void setAvailableToTime(String token) {
        pref.setStringData(PrefConstant.AVAILABLE_TO, token);
    }

    public String getBookingSwitch() {
        return pref.getStringData(PrefConstant.BOOKING_SWITCH);
    }

    public void setBookingSwitch(String token) {
        pref.setStringData(PrefConstant.BOOKING_SWITCH, token);
    }

    public String getAgentId() {
        return pref.getStringData(PrefConstant.AGENT_ID);
    }

    public void setAgentId(String token) {
        pref.setStringData(PrefConstant.AGENT_ID, token);
    }


    public String getPortfolioDetails() {
        return pref.getStringData(PrefConstant.PORTFOLIO_DETAILS);
    }

    public void setPortfolioDetails(String token) {
        pref.setStringData(PrefConstant.PORTFOLIO_DETAILS, token);
    }

    public String getBankAccountNo() {
        return pref.getStringData(PrefConstant.BANK_ACC_NO);
    }

    public void setBankAccountNo(String token) {
        pref.setStringData(PrefConstant.BANK_ACC_NO, token);
    }

    public String getAccHolderName() {
        return pref.getStringData(PrefConstant.ACCOUNT_HOLDER_NAME);
    }

    public void setAccHolderName(String token) {
        pref.setStringData(PrefConstant.ACCOUNT_HOLDER_NAME, token);
    }

    public String getBranchCode() {
        return pref.getStringData(PrefConstant.BANK_BRANCH_CODE);
    }

    public void setBranchCode(String token) {
        pref.setStringData(PrefConstant.BANK_BRANCH_CODE, token);
    }

    public boolean isLocationOnOff() {
        return pref.getBoolean(PrefConstant.IS_LOCATION);
    }

    public void setLocationOnOff(boolean b) {
        pref.setBooleanData(PrefConstant.IS_LOCATION, b);
    }

    public String getCurrencyCode() {
        return pref.getStringData(PrefConstant.CURRENCY_CODE);
    }

    public void setCurrencyCode(String token) {
        pref.setStringData(PrefConstant.CURRENCY_CODE, token);
    }

    public String getCurrencySymbol() {
        return pref.getStringData(PrefConstant.CURRENCY_SYMBOL);
    }

    public void setCurrencySymbol(String token) {
        pref.setStringData(PrefConstant.CURRENCY_SYMBOL, token);
    }

    public String getRateKm() {
        return pref.getStringData(PrefConstant.RATEKM);
    }

    public void setRateKm(String token) {
        pref.setStringData(PrefConstant.RATEKM, token);
    }

    public String getCountryCode() {
        return pref.getStringData(PrefConstant.COUNTRY_CODE);
    }

    public void setCountryCode(String token) {
        pref.setStringData(PrefConstant.COUNTRY_CODE, token);
    }

    public String getLocalCountryCode() {
        return pref.getStringData(PrefConstant.OLD_COUNTRY_CODE);
    }

    public void setLocalCountryCode(String token) {
        pref.setStringData(PrefConstant.OLD_COUNTRY_CODE, token);
    }


    public String getUnitType() {
        return pref.getStringData(PrefConstant.UNIT_TYPE);
    }

    public void setUnitType(String token) {
        pref.setStringData(PrefConstant.UNIT_TYPE, token);
    }

    public String getReportType() {
        return pref.getStringData(PrefConstant.REPORT_TYPE);
    }

    public void setReportType(String token) {
        pref.setStringData(PrefConstant.REPORT_TYPE, token);
    }

    public String getUserProfile() {
        return pref.getStringData(PrefConstant.USER_PROFILE);
    }

    public void setUserProfile(String token) {
        pref.setStringData(PrefConstant.USER_PROFILE, token);
    }

    public boolean isGPlusLoggin() {
        return pref.getBoolean(PrefConstant.IS_GPLUS_LOGGIN);
    }

    public void setGplusLoggin(boolean b) {
        pref.setBooleanData(PrefConstant.IS_GPLUS_LOGGIN, b);
    }


    public String getRecentSearch() {
        return pref.getStringData(PrefConstant.RECENT_SEARCH);
    }

    public void setRecentSearch(String RecentSearch) {
        pref.setStringData(PrefConstant.RECENT_SEARCH, RecentSearch);
    }

    public String getMessagePattern() {
        return pref.getStringData(PrefConstant.MESSAGE_PATTERN);
    }

    public void setMessagePattern(String MessagePattern) {
        pref.setStringData(PrefConstant.MESSAGE_PATTERN, MessagePattern);
    }


    public String getSigninType() {
        return pref.getStringData(PrefConstant.SINGIN_TYPE);
    }

    public void setSigninType(String token) {
        pref.setStringData(PrefConstant.SINGIN_TYPE, token);
    }

    public boolean isNotificationOnOff() {
        return pref.getBoolean(PrefConstant.IS_NOTIFICATION);
    }

    public void setNotificationOnOff(boolean b) {
        pref.setBooleanData(PrefConstant.IS_NOTIFICATION, b);
    }

    public String getBio() {
        return pref.getStringData(PrefConstant.bio);

    }

    public void setBio(String bio) {
        pref.setStringData(PrefConstant.bio, bio);

    }

    public void setSpecialization(String spec) {
        pref.setStringData(PrefConstant.specialization, spec);

    }

    public String getSpecialization() {
        return pref.getStringData(PrefConstant.specialization);

    }

    public void setDeviceToken(String msg) {
        pref.setStringData(PrefConstant.devicetoken, msg);
    }

    public String getDeviceToken() {
        return pref.getStringData(PrefConstant.devicetoken);

    }

    public void setLang(String lang) {
        pref.setStringData(PrefConstant.lang, lang);
    }

    public String getLang() {
        return pref.getStringData(PrefConstant.lang);

    }

    public void setBlocked(String blocked) {
        pref.setStringData(PrefConstant.blocked, blocked);

    }

    public String getBlocked() {
        return pref.getStringData(PrefConstant.blocked);

    }

    public void setStripeAccountID(String stripeAccountID) {
        pref.setStringData(PrefConstant.stripeAccountID, stripeAccountID);
    }

    public void setStripeCustomerID(String stripeCustomerID) {
        pref.setStringData(PrefConstant.stripeCustomerID, stripeCustomerID);
    }

    public void setPaymentMethod(String paymentMethod) {
        pref.setStringData(PrefConstant.paymentMethod, paymentMethod);

    }

    public void setAddress(String address) {
        pref.setStringData(PrefConstant.address, address);

    }

    public void setCompanyID(String companyID) {
        pref.setStringData(PrefConstant.companyid, companyID);
    }

    public void setCompanyName(String companyName) {
        pref.setStringData(PrefConstant.companyName, companyName);
    }

    public void setWeeklyTotal(String weeklyTotal) {
        pref.setStringData(PrefConstant.weeklyTotal, weeklyTotal);

    }

    public void setLifetimeTotal(String lifetimeTotal) {
        pref.setStringData(PrefConstant.lifetimeTotal, lifetimeTotal);
    }

    public void setIndustry(String industry) {
        pref.setStringData(PrefConstant.industry, industry);
    }

    public void setServiceProviderId(String serviceProviderID) {
        pref.setStringData(PrefConstant.serviceProviderID, serviceProviderID);
    }

    public void setServiceProviderWeeklyTotal(String ServiceProviderWeeklyTotal) {
        pref.setStringData(PrefConstant.ServiceProviderWeeklyTotal, ServiceProviderWeeklyTotal);
    }

    public void setServiceProviderLifetimeTotal(String ServiceProviderLifetimeTotal) {
        pref.setStringData(PrefConstant.ServiceProviderLifetimeTotal, ServiceProviderLifetimeTotal);

    }

    public void setServiceProviderIndustry(String ServiceProviderIndustry) {
        pref.setStringData(PrefConstant.ServiceProviderIndustry, ServiceProviderIndustry);

    }

    public void setServiceProviderPosition(String serviceProviderPosition) {
        pref.setStringData(PrefConstant.serviceProviderPosition, serviceProviderPosition);

    }

    public void setServiceProviderCompany(String serviceProviderCompany) {
        pref.setStringData(PrefConstant.serviceProviderCompany, serviceProviderCompany);

    }

    public void setIndustryData(String industryData) {
        pref.setStringData(PrefConstant.INDUSTRY_DATA, industryData);
    }


    public void setStripeCode(String stripeCode) {
        pref.setStringData(PrefConstant.STRIPE_CODE, stripeCode);

    }

    public String getStripeCode() {
        return pref.getStringData(PrefConstant.STRIPE_CODE);

    }

    public String getSripeAccountId() {
        return pref.getStringData(PrefConstant.stripeAccountID);
    }

    public void removeStripeData() {
        pref.removeStrpe();
    }


    public void setFilePath(String location) {
        pref.setStringData(PrefConstant.LOCATION, location);
    }

    public String getFilePath() {
        return pref.getStringData(PrefConstant.LOCATION);
    }

    public void deleteLocation() {
        pref.removeLocation();
    }

    public void darkMode(boolean isChecked) {
        pref.setBooleanData(PrefConstant.DARK_MODE, isChecked);
    }

    public boolean getDarkTheme() {
        return pref.getBoolean(PrefConstant.DARK_MODE);
    }

    public void popUpPermission(boolean b) {
        pref.setBooleanData(PrefConstant.POP_UP_PERMISSION, b);
    }

    public boolean getPopUpPermission() {
        return pref.getBoolean(PrefConstant.POP_UP_PERMISSION);
    }

    public void setInForground(boolean b) {
        pref.setBooleanData(PrefConstant.IN_FORGROUND, b);
    }

    public boolean getInForground() {
        return pref.getBoolean(PrefConstant.IN_FORGROUND);
    }

    public void useCasePermission(boolean b) {
        pref.setBooleanData(PrefConstant.USE_CASE_PERMISSION, b);

    }

    public boolean getUseCasePermission() {
        return pref.getBoolean(PrefConstant.USE_CASE_PERMISSION);
    }

    public boolean getModifySystemSettings() {
        return pref.getBoolean(PrefConstant.MODIFY_SYSTEM_SETTINGS);
    }

    public void setModifySystemSettings(boolean b) {
        pref.setBooleanData(PrefConstant.MODIFY_SYSTEM_SETTINGS, b);

    }

    public void setData(String data) {
        pref.setStringData(PrefConstant.DATA, data);

    }

    public String getData() {
        return pref.getStringData(PrefConstant.DATA);

    }

    public void setProcessId(int myPid) {
        pref.setIntData(PrefConstant.PROCESS_DATA, myPid);

    }

    public int getProcessId() {
        return pref.getIntData(PrefConstant.PROCESS_DATA);

    }

    public void setPasswordCorrect(boolean b) {
        pref.setBooleanData(PrefConstant.PASSWORD_CORRECT, b);

    }

    public boolean getPasswordCorrect() {
        return pref.getBoolean(PrefConstant.PASSWORD_CORRECT);

    }

    public void setDeletePhoto(boolean b) {
        pref.setBooleanData(PrefConstant.DELETE_PHOTO, b);

    }

    public boolean getDeletePhoto() {
        return pref.getBoolean(PrefConstant.DELETE_PHOTO);
    }

    public void setLock(boolean b) {
        pref.setBooleanData(PrefConstant.LOCK, b);

    }

    public void onBoarding(boolean b) {
        pref.setBooleanData(PrefConstant.ON_BOARDING, b);
    }

    public boolean getOnBoarding() {
        return pref.getBoolean(PrefConstant.ON_BOARDING);
    }

    public boolean getLock() {
        return pref.getBoolean(PrefConstant.LOCK);
    }

    public void setWifiGone(boolean b) {

        pref.setBooleanData(PrefConstant.WIFI_GONE, b);

    }

    public boolean getWifiGone() {
        return pref.getBoolean(PrefConstant.WIFI_GONE);
    }

    public void setUninstall(boolean b) {
        pref.setBooleanData(PrefConstant.UNINSTALL, b);
    }

    public boolean getUninstall() {
        return pref.getBoolean(PrefConstant.UNINSTALL);
    }

    public String getAccessToken() {
        return null;
    }

    public void setLockOnBrowser(boolean b) {
        pref.setBooleanData(PrefConstant.BROWSER_LOCK, b);
    }

    public void setLockOnMessage(boolean b) {
        pref.setBooleanData(PrefConstant.MESSAGE_LOCK, b);
    }

    public boolean getBrowserLock() {
        return pref.getBoolean(PrefConstant.BROWSER_LOCK);
    }

    public boolean getMessageLock() {
        return pref.getBoolean(PrefConstant.MESSAGE_LOCK);
    }

    public void setSignUpData(LoginResponse signUpResponse) {
        Gson gson = new Gson();
        String data = gson.toJson(signUpResponse);
        pref.setStringData(PrefConstant.LOGIN_RESPONSE, data);
    }

    public LoginResponse getLoginResponse() {
        Gson gson = new Gson();
        String response = pref.getStringData(PrefConstant.LOGIN_RESPONSE);
        LoginResponse loginResponse = gson.fromJson(response, LoginResponse.class);
        return loginResponse;
    }

    public void setScreenID(int id) {
        pref.setIntData(PrefConstant.SCREEN_ID, id);
    }


    public int getScreenId() {
        return pref.getIntData(PrefConstant.SCREEN_ID);

    }

    public void setPriceCard(PriceCardMain pricecard) {
        Gson gson = new Gson();
        String data = gson.toJson(pricecard);
        pref.setStringData(PrefConstant.PRICE_CARD, data);
    }


    public void setPromotion(List<Promotion> promotions) {
        Gson gson = new Gson();
        String data = gson.toJson(promotions);
        pref.setStringData(PrefConstant.PROMOTION, data);
    }

    public void setPricing(List<Pricing> pricing) {
        Gson gson = new Gson();
        String data = gson.toJson(pricing);
        pref.setStringData(PrefConstant.PRICING, data);
    }


    public void setScreenPosition(ScreenPosition screenPosition) {
        Gson gson = new Gson();
        String data = gson.toJson(screenPosition);
        pref.setStringData(PrefConstant.SCREEN_POSITION, data);
    }

    public PriceCardMain getPriceCard() {
        Gson gson = new Gson();
        String response = pref.getStringData(PrefConstant.PRICE_CARD);
        PriceCardMain priceCard = gson.fromJson(response, PriceCardMain.class);
        return priceCard;
    }

    public ScreenPosition getPosition() {
        Gson gson = new Gson();
        String response = pref.getStringData(PrefConstant.SCREEN_POSITION);
        ScreenPosition loginResponse = gson.fromJson(response, ScreenPosition.class);
        return loginResponse;
    }

    public List<Promotion> getPromotion() {
        Gson gson = new Gson();
        String response = pref.getStringData(PrefConstant.PROMOTION);
        List<Promotion> promotion = gson.fromJson(response, new TypeToken<List<Promotion>>() {
        }.getType());
        return promotion;
    }

    public String getBaseUrl() {
        return pref.getStringData(PrefConstant.BASE_URL);
    }

    public void setBaseUrl(String baseUrl) {
        pref.setStringData(PrefConstant.BASE_URL, baseUrl);
    }

    public void deleteAllSession() {
        pref.clear();
    }

    public void removeBaseUrl() {
        pref.removeBaseUrl();
    }

    public void setTimerToGetCard(Time time) {
        Gson gson = new Gson();
        String data = gson.toJson(time);
        pref.setStringData(PrefConstant.TIME, data);
    }

    public Time getTimeData() {
        Gson gson = new Gson();
        String response = pref.getStringData(PrefConstant.TIME);
        if (response != null) {
            Time time = gson.fromJson(response, Time.class);
            return time;
        }
        return null;
    }

    public boolean getCardDeleted() {
        return pref.getBoolean(PrefConstant.DELETE_CARD);
    }

    public void setCardDeleted(boolean cardDeleted) {
        pref.setBooleanData(PrefConstant.DELETE_CARD, cardDeleted);
    }

    public void setOSType(List<OsType> osTypes) {
        Gson gson = new Gson();
        String data = gson.toJson(osTypes);
        pref.setStringData(PrefConstant.OS_TYPE, data);

    }

    public boolean getBaseUrlAdded() {
        boolean b = pref.getBoolean(PrefConstant.BASE_URL_ENTER);
        return b;
    }

    public void setBaseUrlChange(boolean b) {
        pref.setBooleanData(PrefConstant.BASE_URL_ENTER, b);
    }

    public List<Pricing> getPricing() {
        Gson gson = new Gson();
        String response = pref.getStringData(PrefConstant.PRICING);
        List<Pricing> promotion = gson.fromJson(response, new TypeToken<List<Pricing>>() {
        }.getType());
        return promotion;
    }

    public void setPromotions(String s) {
        String response = pref.getStringData(PrefConstant.PROMOTIONS);
        JSONArray jsonArray = null;
        try {
            if (response!=null && !response.equals("")) {
                jsonArray = new JSONArray(response);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("" + jsonArray.length(), s);
                jsonArray.put(jsonObject);
            }
            else
            {
                jsonArray=new JSONArray();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("" + jsonArray.length(), s);
                jsonArray.put(jsonObject);

            }
            } catch (JSONException e) {
            e.printStackTrace();
        }
        pref.setStringData(PrefConstant.PROMOTIONS, jsonArray.toString());

    }

    public JSONArray getPromotions() {

        String response = pref.getStringData(PrefConstant.PROMOTIONS);
        try {
            if (response!=null && !response.equals("")) {
                JSONArray elements = new JSONArray(response);
                return elements;
            }
            }
        catch (Exception e)
        {

        }
        return null;
    }

    public void setMainFilePath(String s) {
        pref.setStringData(PrefConstant.MAIN_FILE_PATH,s);
    }

    public String getMainFilePath() {
    return pref.getStringData(PrefConstant.MAIN_FILE_PATH);
    }

    public void deletePromotions() {
    pref.removePromotions();
    }
}