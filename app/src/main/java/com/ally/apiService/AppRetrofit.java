package com.ally.apiService;

import com.ally.BuildConfig;
import com.ally.common.session.SessionManager;
import com.ally.utils.Constraint;
import com.ally.utils.LiveDataCallAdapterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This class give all configuration for http request
 **/
public class AppRetrofit {
    private static AppRetrofit instance;
    private final ApiService apiService;
    String token = "";
    private static SessionManager sessionManager;
    private AppRetrofit() {
        sessionManager=SessionManager.get();
       String baseUrl= sessionManager.getBaseUrl();
       if (baseUrl!=null && !baseUrl.equals(""))
       {
           apiService = provideService(baseUrl);

       }
       else {
           apiService = provideService(BuildConfig.BASE_URL);
       }
       }

    public AppRetrofit(String BaseUrl) {
        apiService = provideService(BaseUrl);
    }


    private static void initInstance() {
        if (sessionManager==null)
            sessionManager=SessionManager.get();
      String baseUrl=  sessionManager.getBaseUrl();
        if (baseUrl!=null && !baseUrl.equals(""))
        {
            instance = new AppRetrofit();
        }
        else {
            if (instance == null) {
                // Create the instance
                instance = new AppRetrofit();
            }
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
        httpClient.connectTimeout(Constraint.THIRTY, TimeUnit.SECONDS)
                .writeTimeout(Constraint.THIRTY, TimeUnit.SECONDS)
                .readTimeout(Constraint.THIRTY, TimeUnit.SECONDS);
       if (SessionManager.get().getDeviceToken() != null) {
            token = SessionManager.get().getDeviceToken();
        }
        httpClient.addInterceptor(logging).addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request.Builder requestBuilder = chain.request().newBuilder().addHeader(ApiConstant.KEY_CONTENT_TYPE, ApiConstant.CONTENT_TYPE);
                Request request = requestBuilder
                        .build();
                 Response response = chain.proceed(request);

                if (response.isSuccessful() && response.code() == ApiResponseStatusCode.ERROR) {

                }
                return response;
            }
        });
        return new Retrofit.Builder()
                .baseUrl(BaseUrl).addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .client(httpClient.build()).build().create(ApiService.class);
    }


}
