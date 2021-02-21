package com.daisy.activity.feedBack;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.daisy.R;
import com.daisy.pojo.response.FeedBackResponse;
import com.daisy.pojo.response.GlobalResponse;

import java.util.ArrayList;
import java.util.HashMap;

public class FeedBackModelView extends AndroidViewModel {
    private ArrayList<String> feedbackTitle;
    private LiveData<GlobalResponse<FeedBackResponse>> liveData;
    private FeedBackRepo feedBackRepo = new FeedBackRepo();
    private MutableLiveData<HashMap<String, String>> requestMutableLiveData = new MutableLiveData<>();

    public FeedBackModelView(@NonNull Application application) {
        super(application);
        feedbackTitle = new ArrayList<>();
        addFeedBackData(application);
        liveData = Transformations.switchMap(requestMutableLiveData, new Function<HashMap<String, String>, LiveData<GlobalResponse<FeedBackResponse>>>() {
            @Override
            public LiveData<GlobalResponse<FeedBackResponse>> apply(HashMap<String, String> input) {
                return feedBackRepo.getFeedBackResponse(input);
            }
        });

    }

    private void addFeedBackData(Application application) {
        feedbackTitle.add(application.getApplicationContext().getString(R.string.Bug));
        feedbackTitle.add(application.getApplicationContext().getString(R.string.SUGGESION));
        feedbackTitle.add(application.getApplicationContext().getString(R.string.OTHER));


    }

    public ArrayList<String> getFeedbackTitle() {
        return feedbackTitle;
    }

    public void setFeedBackRequest(HashMap<String, String> request) {
        requestMutableLiveData.setValue(request);
    }

    public LiveData<GlobalResponse<FeedBackResponse>> getLiveData() {
        return liveData;
    }

}
