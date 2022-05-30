package com.daisy.activity.welcomeScreen;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.daisy.apiService.ApiConstant;
import com.daisy.apiService.ApiResponseStatusCode;
import com.daisy.apiService.ApiService;
import com.daisy.apiService.AppRetrofit;
import com.daisy.app.AppController;
import com.daisy.common.session.SessionManager;
import com.daisy.pojo.response.GlobalResponse;
import com.daisy.pojo.response.KeyToUrlResponse;
import com.daisy.utils.Constraint;
import com.daisy.utils.LiveDataCallAdapterFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WelcomeRepo {
    private MutableLiveData<GlobalResponse<KeyToUrlResponse>> responseLiveData = new MutableLiveData<>();

    public LiveData<GlobalResponse<KeyToUrlResponse>> fireKeyToUrlApi(HashMap<String, String> input) {
        Call<GlobalResponse<KeyToUrlResponse>> globalResponseCall = getCustomServiceObject(input.get(Constraint.ID_BASE_URL)).getKeyToValue(input, input.get(Constraint.TOKEN));
        globalResponseCall.enqueue(new Callback<GlobalResponse<KeyToUrlResponse>>() {
            @Override
            public void onResponse(Call<GlobalResponse<KeyToUrlResponse>> call, Response<GlobalResponse<KeyToUrlResponse>> response) {
                if (response.isSuccessful()) {
                    responseLiveData.setValue(response.body());
                } else {
                    responseLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<GlobalResponse<KeyToUrlResponse>> call, Throwable t) {
                responseLiveData.setValue(null);

            }
        });
        return responseLiveData;

    }

    private ApiService getCustomServiceObject(String baseUrl) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(Constraint.THIRTY, TimeUnit.SECONDS)
                .writeTimeout(Constraint.THIRTY, TimeUnit.SECONDS)
                .readTimeout(Constraint.THIRTY, TimeUnit.SECONDS);

        httpClient.addInterceptor(logging).addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request.Builder requestBuilder = chain.request().newBuilder().addHeader(ApiConstant.KEY_CONTENT_TYPE, ApiConstant.CONTENT_TYPE);
                Request request = requestBuilder
                        .build();
                okhttp3.Response response = chain.proceed(request);

                if (response.code() == ApiResponseStatusCode.ERROR) {
                    AppController.getInstance().getActivity().handleLogout();
                    AppController.getInstance().getActivity().removeAdminRightPermission();


                }
                return response;
            }
        });
        ApiService apiService = new Retrofit.Builder()
                .baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .client(httpClient.build()).build().create(ApiService.class);

        return apiService;

    }
}
