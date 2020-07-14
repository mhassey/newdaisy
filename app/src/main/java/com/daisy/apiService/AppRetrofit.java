package com.daisy.apiService;

import android.util.Log;

import com.daisy.common.session.SessionManager;
import com.daisy.utils.LiveDataCallAdapterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppRetrofit {
    private static AppRetrofit instance;
    private final ApiService apiService;

    private AppRetrofit() {
        apiService = provideService("");
    }

    public AppRetrofit(String BaseUrl) {
        apiService = provideService(BaseUrl);
    }


    private static void initInstance() {
        if (instance == null) {
            // Create the instance
            instance = new AppRetrofit();
        }
    }

    public static AppRetrofit getInstance() {
        // Return the instance
        initInstance();
        return instance;
    }

    public ApiService getApiService() {
        return apiService;
    }

    private ApiService provideService(String BaseUrl) {

        // To show the Api Request & Params
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);
        httpClient.addInterceptor(logging).addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request.Builder requestBuilder = chain.request().newBuilder().addHeader(ApiConstant.KEY_CONTENT_TYPE, ApiConstant.CONTENT_TYPE);
                Request request = requestBuilder
                        /*.addHeader(ApiConstant.USER_ID, String.valueOf(SessionManager.get().getUserIdForAPiHeader()))
                        .addHeader("accessToken", SessionManager.get().getAccessToken())
*/
                        .addHeader(ApiConstant.ACCESS_TOKEN, SessionManager.get().getAccessToken())
                        .addHeader(ApiConstant.KEY_CONTENT_TYPE, ApiConstant.CONTENT_TYPE)
                        .build();
                Response response = chain.proceed(request);
                Log.e("AppRetrofit", SessionManager.get().getAccessToken());

                if (response.isSuccessful() && response.code() == 202) {
//                    if (mAlertDialog == null) {
//                      //  logout("You are already logged in on another device or your session expired");
//                    } else if (!mAlertDialog.isShowing()) {
//                       // logout("You are already logged in on another device or your session expired");
//                    }
                }
                return response;
            }
        });
        return new Retrofit.Builder()
                .baseUrl(BaseUrl).addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .client(httpClient.build()).build().create(ApiService.class);
    }



  /*  private void logout(String message) {
        if (AppController.getInstance().getActivity() != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mAlertDialog = AppController.getInstance().getActivity().showLogoutDialog(message);
                }
            });
        }
    }

*/

}
