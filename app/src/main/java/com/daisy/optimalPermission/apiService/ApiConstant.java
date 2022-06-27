package com.daisy.optimalPermission.apiService;

/**
 * This class contains all api names content type
 **/
public class ApiConstant {

    public static final String URL_PACK = "api";
    public static final String SIGN_UP = URL_PACK + "/login";
    public static final String CONTENT_TYPE = "form-data";
    public static final String KEY_CONTENT_TYPE = "Content-Type";
    public static final String ACCESS_TOKEN = "token";
    public static final String KEY_TO_URL = URL_PACK + "/api_gateway";


    public static final String RESOURCE_ERROR_TYPE = "type must be a resource";
    public static final String RESOURCE_ERROR_PARAMETER = "resource must be parameterized";
    public static final String DETECT_DEVICE = "detect_device";
    public static final String GENERAL = URL_PACK + "/general";
    public static final String CREATE_SCREEN = URL_PACK + "/create_screen";
    public static final String GET_CARD = URL_PACK + "/get_cards";
    public static final String SEND_LOGS = URL_PACK + "/create_log";
    public static final String UPDATE_POSITION = URL_PACK + "/update_screen_position";
    public static final String DELETE_CARD = URL_PACK + "/delete_cards";
    public static final String PROMOTION_CHECK = "validate_promotions";
    public static final String API = "api";
    public static final String ADD_FEEDBACK = URL_PACK + "/report_bug";
    public static final String CREATE_SCREEN_OS = URL_PACK + "/create_screen_os";
    public static final String UPDATE_PRODUCT = URL_PACK + "/update_screen_product";
}
