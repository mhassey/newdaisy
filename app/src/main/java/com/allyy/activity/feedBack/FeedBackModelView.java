package com.allyy.activity.feedBack;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.allyy.R;
import com.allyy.pojo.response.FeedBackResponse;
import com.allyy.pojo.response.GlobalResponse;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Purpose -  FeedBackModelView is an view model class that helps to send feedback
 * Responsibility - Its helps to call getFeedBackResponse method of FeedBackRepo class
 **/
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

    /**
     * Purpose - addFeedBackData method add feedback title
     *
     * @param application
     */
    private void addFeedBackData(Application application) {
        feedbackTitle.add(application.getApplicationContext().getString(R.string.Bug));
        feedbackTitle.add(application.getApplicationContext().getString(R.string.SUGGESION));
        feedbackTitle.add(application.getApplicationContext().getString(R.string.OTHER));


    }

    /**
     * Purpose - getFeedbackTitle method return feedback title list
     */
    public ArrayList<String> getFeedbackTitle() {
        return feedbackTitle;
    }

    /**
     * Purpose - setFeedBackRequest method set  request for feedback api
     */
    public void setFeedBackRequest(HashMap<String, String> request) {
        requestMutableLiveData.setValue(request);
    }

    /**
     * Purpose - getLiveData method return feedback response
     */
    public LiveData<GlobalResponse<FeedBackResponse>> getLiveData() {
        return liveData;
    }

}
