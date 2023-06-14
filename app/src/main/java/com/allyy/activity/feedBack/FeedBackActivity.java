package com.allyy.activity.feedBack;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.allyy.R;
import com.allyy.activity.base.BaseActivity;
import com.allyy.common.session.SessionManager;
import com.allyy.databinding.ActivityFeedBackBinding;
import com.allyy.pojo.response.FeedBackResponse;
import com.allyy.pojo.response.GlobalResponse;
import com.allyy.pojo.response.OsType;
import com.allyy.utils.Constraint;
import com.allyy.utils.Utils;
import com.allyy.utils.ValidationHelper;

import java.util.HashMap;
import java.util.List;

/**
 * Purpose -  FeedBackActivity is an activity that helps users to give feedback
 * Responsibility - Its help user to send feedback
 **/
public class FeedBackActivity extends BaseActivity implements View.OnClickListener {

    private ActivityFeedBackBinding mBinding;
    private FeedBackModelView feedBackModelView;
    private FeedBackValidationHelper feedBackValidationHelper;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding= DataBindingUtil.setContentView(this,R.layout.activity_feed_back);
        initView();
        setNoTitleBar(this);
        hideSystemUI();
        initClick();
    }



    /**
     * Initial data setup
     */
    private void initView() {
        context=this;
        feedBackValidationHelper=new FeedBackValidationHelper(context,mBinding);
        feedBackModelView=new ViewModelProvider(this).get(FeedBackModelView.class);
        ArrayAdapter<String> orientationAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, feedBackModelView.getFeedbackTitle());
        orientationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.title.setAdapter(orientationAdapter);

    }

    /**
     * Button clicks initializing
     */
    private void initClick() {
        mBinding.submit.setOnClickListener(this::onClick);
        mBinding.cancel.setOnClickListener(this::onClick);
    }


    /**
     * Handle full screen mode
     */
    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

    }

    /**
     * Change system ui to full screen when any change perform in activity
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }

    }

    /**
     * Handle Clicks listener
     */
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.submit:
            {
                if (feedBackValidationHelper.isValid())
                handleFeedBackRequest();
                break;
            }
            case R.id.cancel:
            {
                finish();
                break;
            }
        }
    }

    /**
     * fire feedback api and get response
     */

    private void handleFeedBackRequest() {
        if (Utils.getNetworkState(context))
        {
            showHideProgressDialog(true);
            feedBackModelView.setFeedBackRequest(getFeedBackLRequest());
            LiveData<GlobalResponse<FeedBackResponse>> liveData=feedBackModelView.getLiveData();
            if (!liveData.hasActiveObservers())
            {
                liveData.observe(this, new Observer<GlobalResponse<FeedBackResponse>>() {
                    @Override
                    public void onChanged(GlobalResponse<FeedBackResponse> feedBackResponseGlobalResponse) {
                     handleFeedBackResponse(feedBackResponseGlobalResponse);
                    }
                });
            }
        }
        else
        {
            ValidationHelper.showToast(context,getString(R.string.no_internet_available));
        }
    }



    /**
     * handleFeedBackResponse is help us to get the response from server
     */
    private void handleFeedBackResponse(GlobalResponse<FeedBackResponse> feedBackResponseGlobalResponse) {
        showHideProgressDialog(false);
        if (feedBackResponseGlobalResponse!=null)
        {
            ValidationHelper.showToast(context,feedBackResponseGlobalResponse.getMessage());
            if (feedBackResponseGlobalResponse.isApi_status())
                finish();

        }
        else
        {
            ValidationHelper.showToast(context,getString(R.string.no_internet_available));
        }
    }

    /**
     * Create feedback request
     */
    private HashMap<String, String> getFeedBackLRequest() {
    HashMap<String,String> hashMap=new HashMap<>();
    hashMap.put(Constraint.TITLE,(String) mBinding.title.getSelectedItem());
    hashMap.put(Constraint.DESCRIPTION,mBinding.description.getText().toString());
    hashMap.put(Constraint.TOKEN,SessionManager.get().getDeviceToken());
    List<OsType> osTypes= SessionManager.get().getOsType();
    for (OsType osType:osTypes)
    {
        if (osType.getOsName().equals(Constraint.ANDROID))
        {
            hashMap.put(Constraint.OS_ID,osType.getOsID()+"");
        }
    }
    return hashMap;
    }
}
