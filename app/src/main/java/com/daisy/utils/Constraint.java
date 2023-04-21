package com.daisy.utils;

import android.Manifest;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class Constraint {
    public static final String FILE_NAME = "index.html";
    public static final String FOLDER_NAME = ".Daisy";
    public static final String configFile = "configration.txt";
    public static final String SLASH = "/";
    public static final String HARDWARE = "Hardware";
    public static final String UNISOC = "unisoc";
    public static final String TIME_FORMAT = "yyyy.MM.dd.HH.mm.ss";
    public static final String DOWNLOAD_AT = "Downloaded at: ";
    public static final int RESPONSE_CODE = 10112;
    public static final int RESPONSE_CODE_MAIN = 102;
    public static final int ZERO = 0;
    public static final String APPNAME = "Daisy";
    public static final String SUCCESS = "success";
    public static final String INDEX = "index";
    public static final String SUMSUNG_BROWSER_NAME = "sbrowser";
    public static final String MESSENGING = "messaging";
    public static final String WEAK_UP_TAG = "wake_up_tag";
    public static final int THIRTY_INT = 30;
    public static final int SIXTY_INT = 30;
    public static final int ONE_TWENTY = 120;

    //public static final int THIRTY_INT = 1;

    public static final int THIRTY_SIX_HUNDRED = 3600;
    public static final int FIVE_HUNDRED = 500;
    public static final int FOUR = 4;
    // need to change
    public static final int FIVE_INE_REAL = 5;
    public static final int FIVE_INE = 0;
    public static final String PACKAGE_INSTALLER = "com.google.android.packageinstaller";
    public static final String LOCK_SCREEN = "LockScreen";
    public static final String MAIN_ACTIVITY = "com.daisy.activity.mainActivity.MainActivity";
    public static final String LOG = "log";
    public static final String ID_PRICE_CARD = "idpriceCard";
    public static final String ID_PROMOTION = "idpromotion";
    public static final int THIRTY_THOUSAND = 30000;
    public static final int TWENTY_THOUSAND = 20000;

    public static final int SIXTY_THOUSAND = 60000;
    public static final String IDDEVICE = "iddevice";
    public static final String OS_ID = "osID";
    public static final String OS_VER = "osVer";
    public static final String MAV_ID = "mav_id";
    public static final String PRICING_NOT_DEFINE = "handlePriceDynamically";
    public static final int ADMIN = 123;
    public static final int FIFTEEN = 15;
    public static final String AR = "ar";
    public static final String SHOW_PROMOTION = "Promotion show";
    public static final Object PRICECARD = "priceCard";
    public static final String USER_SEEN_PRMOTION = "Face detected";
    public static final String CLICK_ON_PRICE_CARD = "Click on price card";
    public static final String CLICK_ON_PROMOTION = "Click on promotion";
    public static final String ENGLISH = "ENGLISH";
    public static final String ARABIC = "ARABIC";
    public static final String DA = "da";
    public static final String DANISH = "DANISH";
    public static final String DE = "de";
    public static final String DUTCH = "DUTCH";
    public static final String SPANISH = "SPANISH";
    public static final String FI = "fi";
    public static final String FINNISH = "FINNISH";
    public static final String FRENCH = "FRENCH";
    public static final String HI = "hi";
    public static final String HINDI = "HINDI";
    public static final String IT = "it";
    public static final String ITALIAN = "ITALIAN";
    public static final String KO = "ko";
    public static final String KOREAN = "KOREAN";
    public static final String NL = "nl";
    public static final String NO = "no";
    public static final String NORWEGIAN = "NORWEGIAN";
    public static final String PL = "pl";
    public static final String POLISH = "POLISH";
    public static final String PORTUGUESE = "PORTUGUESE";
    public static final String RU = "ru";
    public static final String RUSSIAN = "RUSSIAN";
    public static final String SV = "sv ";
    public static final String SWEDISH = "SWEDISH";
    public static final String TR = "tr";
    public static final String TURKISH = "TURKISH";
    public static final String UK = "uk";
    public static final String UKRAINIAN = "UKRAINIAN";
    public static final String PRICECARD_LOG = "pricecardLogs";
    public static final String EMPTY = "empty";
    public static final String PFV1 = "pfv1";
    public static final String PFV2 = "pfv2";
    public static final String PFV3 = "pfv3";
    public static final String PFV4 = "pfv4";
    public static final String PFV5 = "pfv5";
    public static final String PFV6 = "pfv6";
    public static final String PFV7 = "pfv7";
    public static final String PFV8 = "pfv8";
    public static final String PFV9 = "pfv9";
    public static final String PFV10 = "pfv10";
    public static final String PFV11 = "pfv11";
    public static final String PFV12 = "pfv12";
    public static final String PFV13 = "pfv13";
    public static final String PFV14 = "pfv14";
    public static final String PFV15 = "pfv15";
    public static final String PFV16 = "pfv16";
    public static final String SOMETHING_WENT_WRONG = "something went wrong";
    public static final String FALSE_STR = "false";
    public static final int FILE_SIZE = 8192;
    public static final int MINUS_ONE = -1;
    public static final String SEND_BOX_SERVER_NAME = "SandBox Server";
    public static final String SEND_BOX_SERVER_URL = "http://sandbox.mobilepricecard.com/";
    public static final String VZ_SERVER_NAME = "VZ Server";
    public static final String VZ_SERVER_URL = "http://vz.mobilepricecards.com/";
    public static final String TM_SERVER_NAME = "TM Server";
    public static final String TM_SERVER_URL = "http://tm.mobilepricecards.com/";
    public static final String DEMO_SERVER_NAME = "Demo Server";
    public static final String DEMO_SERVER_URL = "http://demo.mobilepricecards.com/";
    public static final String OAK_DEV_SERVER_NAME = "Oak Dev Server";
    public static final String OAK_DEV_SERVER_URL = "http://oak-dev.mobilepricecard.com/";
    public static final String OAK_TEST_SERVER_NAME = "Oak Test";
    public static final String OAK_TEST_SERVER_URL = "http://oak-test.mobilepricecard.com/";
    public static final String OAK_SERVER_NAME = "Oak  Server";
    public static final String OAK_SERVER_URL = "http://oak.mobilepricecard.com/";
    public static final String VZPROD_SERVER_URL = "http://vzprod.mobilepricecards.com/";
    public static final String VZPROD_SERVER_NAME = "VZPROD";
    public static final String USE_SERVER_URL = "http://use.mobilepricecards.com/";
    public static final String USE_SERVER_NAME = "USE Server";
    public static final int ADMIN_REQUEST_CODE = 123;
    public static final int TWENTY_ONE = 21;
    public static final String MACOS = "_MACOSX";
    public static final String DEFAULT_HOURS_MINUTES = "00:00:00";
    public static final String YOU_TUBE_PATH = "com.google.android.youtube";
    public static final String EXIT_UPPER = "EXIT";
    public static final int MAX_SYNC = 500;
    public static final String BACKGROUND_SERVICE = "Daisy Background Service";
    public static final String APP_IS_RUNNING_IN_BACKGROUND = "App is running in background";
    public static final String SERVICE_RUNNING_IN_BACKGROUND = "Service is running background";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String USER_SEEN_PRICECARD__ = "Face detected";
    public static final String NEWAPK = "newApk";
    public static final String PICK_DOWN = "PickDown";
    public static final String CLICK_PERFORM = "clickPerfrom";
    public static final String USER_INTERACTION = "Interaction";
    public static final String COLON = ":";
    public static final String IDLE = "Idle";
    public static final String HTTP = "http";
    public static final String HTTPS = "https";
    public static final String LOGOUT = "logout";
    public static final int STATIC_IP = 8899;
    public static final int SERVER_PORT = 8899;
    public static final String SEARCHED_DEVICE = "searchDevice";
    public static final String HOME_SCREEN = "homeScreen";
    public static final String SCREEN_ADD = "Screen add";
    public static final String LOGIN_SUCCESSFULL = "Login success";
    public static final String POSITION_UPDATE = "Position updated";
    public static final String OPEN = "OPEN";
    public static final CharSequence PRICE_CARD = "PriceCard";
    public static final long TEN_SECOND = 10000;
    public static final long FIVE_SECOND = 5000;

    public static final String DEVICENAME = "device_name";
    public static final String DEVICE_ID = "deviceId";
    public static final String DEVICEID = "device_id";
    public static final String MAC_ADDRESS = "mac_address";
    public static final String KEY = "Key";
    public static final String customerID = "customerID";
    public static final String ID_BASE_URL = "Id_base_url";
    public static final String DEVICE_TOKEN = "deviceToken";
    public static final String DEVICE_TYPE = "deviceType";
    public static final String VALIDATE_PROMOTION = "validate_promotions";
    public static final String GET_CARDS = "get_cards";
    public static final String PUSH_TYPE = "pushType";
    public static final String PROMOTION_UPDATE = "PROMOTION_UPDATE";
    public static final String SAMSUNG = "samsung";
    public static final String MOTO_RETAIL_APP = "com.motorola.demo.flutter";
    public static final String MOTO_RETAIL_MAIN_ACTIVITY = "com.motorola.demo.flutter.MainActivity";
    public static final String GO = "GO";
    public static final String APP_TYPE = "app_type";
    public static final String OPTIONAL = "Optional";
    public static final String MAIN = "Main";
    public static final String SECURITY = "SECURITY";
    public static final String IS_DISPLAY_OVER_THE_APP = "IsDisplayOverTheApp";
    public static final String DEFAULT_BRIGHTNESS_LEVEL = "20%";
    public static final String MAX_BRIGHTNESS_LEVEL = "90%";
    public static final String PERCENTAGE = "%";
    public static final String MAX = "max";
    public static final int MAX_BRIGHTNESS_INTEGER = 9;
    public static final int DEFAULT_BRIGHTNESS_INTEGER = 2;
    public static final String IS_BRIGHTNESS_DEFAULT = "is_brightness_default";
    public static final String CUSTOM_DEFAULT_BRIGHTNESS = "custom_default_brightness";
    public static final String CUSTOM_HIGH_BRIGHTNESS = "custom_high_brightness";
    public static final String DEFAULT_TIMING = "defaultTiming";
    public static final String XFINITY_BASE_URL = "https://xfinity.mobilepricecards.com";


    public static String[] messages = {"com.google.android.apps.messaging", "com.oneplus.mms", "com.jb.gosms", "com.concentriclivers.mms.com.android.mms", "fr.slvn.mms", "com.android.mms", "com.sonyericsson.conversations"};
    public static final String CARD = "Card";

    public static final String[] STORAGE_PERMISSION = new String[]{Manifest.permission.ACTIVITY_RECOGNITION, Manifest.permission.BLUETOOTH, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_CALL_LOG, Manifest.permission.READ_SMS, Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CAMERA};
    public static final String[] STORAGE_PERMISSION_WITHOUT_SENSOR = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.BLUETOOTH, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_CALL_LOG, Manifest.permission.READ_SMS, Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CAMERA};

    public static final String ANDROID = "Android";
    public static final String FILE = "file://";
    public static final String WAIT = "Wait...";
    public static final String TYPE = "application/zip";
    public static final String DOT_ZIP = ".zip";
    public static final String CALLFROM = "callFrom";
    public static final String SETTINGS = "settings";
    public static final String TOUCH = "Touches";

    public static final int TWENTY = 20;
    public static final int THOUSAND = 1000;
    public static final String APPLICATION_LOGS = "applicationLogs";
    public static final String DATA_STORE = " Data store";
    public static final String CARD_LOGS = "cardLogs";
    public static final String LOGS = "logs";
    public static final String TEXT = ".txt";
    public static final String LOAD = "Load";
    public static final String LOAD_LOGS_FILE = "Log page";
    public static final String LOAD_APPLICATION_LOGS = "application log";
    public static final String LOAD_CARD_LOG = "card log";
    public static final String EXIT = "exit";
    public static final String LOAD_CONFIGURATION = "configuration page";
    public static final String LOAD_MAIN_PAGE = "main page";
    public static final String TYPEE = "type";
    public static final String ADTHERATE = "@";
    public static final int TWO = 2;
    public static final int ONE = 1;
    public static final String APPLICATION_START = "Application Start";
    public static final String APPLICATION_DESCRIPTION = "Started the application";
    public static final String CHANGE_BASE_URL = "Change baseURL";
    public static final String CHANGE_BASE_URL_DESCRIPTION = "Changed baseURL";
    public static final String SETTINGS_DESCRIPTION = "Navigated to settings";
    public static final String TOUCHES_DESCRIPTION = "user touch on screen";

    public static final String REVIEW_APPLICATION_LOG = "Review Application Log";
    public static final String REVIEW_APPLICATION_LOG_DESCRIPTION = "Reviewed the application log";
    public static final String REVIEW_CARD_LOG = "Review Card log";
    public static final String REVIEW_CARD_LOG_DESCRIPTION = "Reviewed the card log";
    public static final String REVIEW_PROMO_LOG = "Review promo log";
    public static final String REVIEW_PROMO_LOG_DESCRIPTION = "Reviewed the promo log";
    public static final String EXIT_APPLICATION_LOG = "Exit application log";
    public static final String EXIT_APPLICATION_LOG_DESCRIPTION = "Exited from application log";
    public static final String EXIT_CARD_LOG = "Exit card log";
    public static final String EXIT_CARD_LOG_DESCRIPTION = "Exited from card log";
    public static final String EXIT_PROMO_LOG = "Exit promo log";
    public static final String EXIT_PROMO_LOG_DESCRIPTION = "Exited from promo log";
    public static final String CLEAR_APPLICATION_LOG = "Clear application log";
    public static final String CLEAR_APPLICATION_LOG_DESCRIPTION = "Cleared application log";
    public static final String CLEAR_CARD_LOG = "Clear card log";
    public static final String CLEAR_CARD_LOG_DESCRIPTION = "Cleared card log";
    public static final String CLEAR_RPOMO_LOG = "Clear promo log";
    public static final String CLEAR_PROMO_LOG_DESCRIPTION = "Cleared promo log";

    public static final String CHANGE_LOG_LEVEL = "Change log level";
    public static final String CHANGE_LOG_LEVEL_DESCRIPTION = "Changed log level";


    public static final String WEB_PAGE_LOAD = "Start loading web page";
    public static final String WEBPAGE_LOAD_DESCRIPTION = "Web page loading";

    public static final String WEB_PAGE_LOAD_FINISH = "Finish loading web page";
    public static final String WEBPAGE_LOAD_FINISH_DESCRIPTION = "Web page loading finish";

    public static final String WEB_PAGE_CHANGE = "page change";
    public static final String WEB_PAGE_CHANGE_DESCRIPTION = " page changing";
    public static final boolean FALSE = false;
    public static final boolean TRUE = true;
    public static final float BRIGHTNESS_LEVEL = 80;
    public static final String BRIGHTNESS_LEVEL_STR = "BrightnessLevel";

    public static final int CODE_WRITE_SETTINGS_PERMISSION = 17;
    public static final long THIRTY = 30;
    public static final int POP_UP_RESPONSE = 121;
    public static final int RETURN = 11;
    public static final int BATTRY_OPTIMIZATION_CODE = 232;
    public static final long FIVE = 5;
    public static final long TEN_MINUTES = 10000 * 60;
    public static final long TEN_HOURS = 10000 * 60;

    public static final long TWO_HOUR = 20000 * 60;
    public static final long ONE_DAY = 10000 * 60;

    //public static final long TWO_HOUR=120000*60;

    public static final String SETTING_PATH = "com.android.settings";
    public static final String PLAY_STORE_PATH = "com.android.vending";
    public static final String PASSWORD = "12345";
    public static final String PACKAGE = "package";
    public static final CharSequence DAISY = ".Daisy";
    public static final String MEDIA_PERMISSION = "mediaPermission";
    public static final String DISPLAY_OVER_THE_APP = "displayOverTheApp";
    public static final String GRAND_USAGE_ACCESS = "grandUsageAccess";
    public static final String MODIFY_SYSTEM_SET = "modifySystemSettings";
    public static final String BATTRY_OPTI = "batteryOptimization";
    public static final int WIFI_COME_BACK = 105;
    public static final String UNINSTALL = "uninstall";
    public static final String YES = "yes";
    public static final String PIXEL = "pixel";
    public static final String CROME = "com.android.chrome";
    public static final String MMS = "mms";
    public static final int THREE = 3;
    public static final String IDSTORE = "idstore";
    public static final String ISLE = "isle";
    public static final String SHELF = "shelf";
    public static final String POSITION = "position";
    public static final String ID_PRODUCT_STATIC = "idproductStatic";
    public static final String ID = "id";
    public static final String SCREEN_ID = "screen_id";
    public static final String TOKEN = "token";
    public static final String REDME = "Redmi";
    public static final int MI_EXTRA_PERMISSION_CODE = 134;

    public static final String SYSTEM_LUNCHER = "System launcher";
    public static final String DAISYY = "Daisy";
    public static final String CARRIER_ID = "carrierId";
    public static final String DEVICE_NAME = "deviceName";
    public static final String BUILD_VERSION = "buildVersion";
    public static final String STORE_CODE = "store_code";
    public static final String PASSWORD_ID = "password";
    public static final String PROMOTION = "promotion";
    public static final String EXTENTION = ".html";
    public static final String PROMOTION_ID = "promotionIds";
    public static final String PRICING = "pricing";
    public static final String DEFAULT = "Default";
    public static final String DAISYAPK = "Daisy.apk";
    public static final int ONE_THOUSAND_TWENTY_FOUR = 1024;
    public static final long HUNDERD = 100;
    public static final String EXIT_CAPITAL = "EXIT";
    public static final String EN = "en";
    public static final String FR = "fr";
    public static final String ES = "es";
    public static final String PT = "pt";
    public static final String DAISY_PACKAGE = "package:com.daisy";
    public static final String PROVIDER = ".provider";
    public static final String ANDROID_PACKAGE_ARCHIVE = "application/vnd.android.package-archive";
    public static final int TWENTY_FOUR = 24;
    public static final String GIVEN_BROWSER = "Mozilla/5.0 (Linux; U; Android 2.0; en-us; Droid Build/ESD20) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17";
    public static final String HTML = ".html";
    public static final String YYY_MM_DD = "yyyy-MM-dd HH:mm:ss";
    public static final String HH_MM_SS = "HH:mm:ss";
    public static final String ID_PRODUCT_FLUID = "idproductFluid";
    public static final String DATE_EFFECTIVE = "dateEffective";
    public static final String TIME_EFFECTIVE = "timeEffective";
    //    public static final String MSRP = "msrp";
//    public static final String OUR_PRICE = "ourprice";
//    public static final String SALE_PRICE = "saleprice";
//    public static final String PLAN_A_PRICE = "planAprice";
//    public static final String PLAN_B_PRICE = "planBprice";
//    public static final String PLAN_C_PRICE = "planCprice";
//    public static final String PLAN_D_PRICE = "planDprice";
//    public static final String DOWN_PRICE = "downprice";
//    public static final String MONTHLY_PRICE = "monthlyprice";
//    public static final String CONFIG_ONE = "config1";
//    public static final String CONFIG_TWO = "config2";
//    public static final String CONFIG_THREE = "config3";
//    public static final String CONFIG_FOUR = "config4";
    public static final String ONE_STRING = "1";
    public static final int NINE_THOUSANT_NINE_HUNDRED = 9900;
    public static final int SIXTY = 60;
    public static final int TEN = 10;
    public static final CharSequence MOBILE_PRICE_CARD_NOT_DEFINE = "MobilePriceCard is not defined";
    public static final long FOUR_THOUSAND = 4000;
    public static final String DATE_CREATE = "dateCreated";
    public static final String DATE_EXPIRES = "dateExpires";
    public static final String PORTRAIT = "Portrait";
    public static final String VERTICAL = "vertical";
    public static final String HORIZONTAL = "horizontal";
    public static final String MANUFACTURE_ID = "mfgId";
    public static final String TRUE_STR = "true";
    public static final int GPS_ENABLE = 106;
    public static final String GPS = "GPS";
    public static final String UPDATE_INVERSION = "update_inversion";

    public static String FILE_NAME_AFTER_DOWNLOAD = "filename";

    public static boolean IS_OVER_APP_SETTING = false;
    public static int CREENTBRIGHNESS;
    public static String current_running_process = "";

    public static String pricecardid = "pricecardid";

    public static String Extra_pass_screen = "com.lge.retailmode";
    public static String adFrameUrl = "adFrameUrl";
    public static String currentFrameName = "currentFrameName";
    public static String click = "click";
    public static String Impression = "Impression";
    public static String price = "price";
    public static String Ip_SEARCHED = "IpSearched";

    public static String adCard = "adCard";
    public static String hyperesources = "hyperesources";


    public static String MobilePriceCard = "MobilePriceCard";
    public static long Two_Minute = 60000;
}
