package com.daisy.activity.onBoarding.slider.slides.securityAsk;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.daisy.R;
import com.daisy.databinding.ActivityOnBaordingBinding;
import com.daisy.databinding.FragmentSecurityAskBinding;

/**
 * Purpose -  SecurityAsk is an fragment that ask for security checks
 * Responsibility - Its ask for some security check example delete photo or not
 **/
public class SecurityAsk extends Fragment implements View.OnClickListener {

    public FragmentSecurityAskBinding securityAskBinding;
    private Context context;
    final int sdk = android.os.Build.VERSION.SDK_INT;
    private static ActivityOnBaordingBinding ActivityOnBaordingBinding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        securityAskBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_security_ask, container, false);

        return securityAskBinding.getRoot();
    }

    /**
     * Purpose - getInstance method set binding data and return object of SecurityAsk
     *
     * @param onBaordingBinding
     * @return
     */
    public static SecurityAsk getInstance(ActivityOnBaordingBinding onBaordingBinding) {
        ActivityOnBaordingBinding = onBaordingBinding;
        return new SecurityAsk();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initClick();
    }

    /**
     * Purpose - initClick method initiate the click listener
     */
    private void initClick() {
//    securityAskBinding.cancel.setOnClickListener(this);
        securityAskBinding.next.setOnClickListener(this);
        securityAskBinding.lockToBrowser.setOnClickListener(this);
        securityAskBinding.lockToMessage.setOnClickListener(this);
        securityAskBinding.deletePhoto.setOnClickListener(this);
        securityAskBinding.lock.setOnClickListener(this);

    }

    private void initView() {
        context = requireContext();

    }

    @Override
    public void onResume() {
        super.onResume();
        designWork();

    }


    /**
     * Purpose - designWork method set background of next button ui
     */
    private void designWork() {


        ActivityOnBaordingBinding.tabDotsLayout.getTabAt(0).setIcon(getResources().getDrawable(R.drawable.default_dot));
        ActivityOnBaordingBinding.tabDotsLayout.getTabAt(1).setIcon(getResources().getDrawable(R.drawable.default_dot));
        ActivityOnBaordingBinding.tabDotsLayout.getTabAt(2).setIcon(getResources().getDrawable(R.drawable.selected_blue));
        ActivityOnBaordingBinding.tabDotsLayout.getTabAt(3).setIcon(getResources().getDrawable(R.drawable.default_dot));
//        ActivityOnBaordingBinding.tabDotsLayout.getTabAt(4).setIcon(getResources().getDrawable(R.drawable.default_dot));

    }

    /**
     * Purpose - onClick method is handle all click listener
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel: {
                getActivity().onBackPressed();
                break;
            }
            case R.id.lock: {
                enableDisableView(securityAskBinding.lockAccessToPlayStoreLayout,securityAskBinding.lockAccessToPlayStoreTxt, securityAskBinding.yesPlayTxt, securityAskBinding.yesPlayThumb, securityAskBinding.noPlay, securityAskBinding.noPlayThumb);

                break;
            }
            case R.id.lockToBrowser: {
                enableDisableView(securityAskBinding.lockAccessToBrowserLayout,securityAskBinding.lockAccessToBrowserTxt, securityAskBinding.yesBrowser, securityAskBinding.yesBrowserThumb, securityAskBinding.noBrowser, securityAskBinding.noBrowserThumb);

                break;
            }
            case R.id.lockToMessage: {
                enableDisableView(securityAskBinding.lockAccessToMessageLayout,securityAskBinding.lockAccessToMsgTxt, securityAskBinding.yesLock, securityAskBinding.yesLockThumb, securityAskBinding.noLock, securityAskBinding.noLockThumb);

                break;
            }
            case R.id.deletePhoto: {
                enableDisableView(securityAskBinding.deleteDeviceContentLayout,securityAskBinding.deleteDeviceContentTxt, securityAskBinding.yesDeviceContent, securityAskBinding.yesDeviceContentImage, securityAskBinding.noDeviceContent, securityAskBinding.noDeviceContentThumb);

                break;
            }
            case R.id.next: {
                ActivityOnBaordingBinding.nextSlide.performClick();
                break;
            }
        }
    }

    void enableDisableView(LinearLayout upperLayout, TextView mainText,TextView yesTestView, ImageView yesTestImage, TextView noTestView, ImageView moTestImage) {
        if (yesTestView.getVisibility() == View.VISIBLE) {
            upperLayout.setBackground(requireContext().getDrawable(R.drawable.edit_txt_rouned_with_dark_blue_border));
            mainText.setTextColor(requireContext().getResources().getColor(R.color.dark_grey_blue));


            yesTestView.setVisibility(View.GONE);
            yesTestImage.setVisibility(View.GONE);
            noTestView.setVisibility(View.VISIBLE);
            moTestImage.setVisibility(View.VISIBLE);


        } else {
            upperLayout.setBackground(requireContext().getDrawable(R.drawable.edit_text_rounded_with_purple_border));
            mainText.setTextColor(requireContext().getResources().getColor(R.color.white));

            yesTestView.setVisibility(View.VISIBLE);
            yesTestImage.setVisibility(View.VISIBLE);
            noTestView.setVisibility(View.GONE);
            moTestImage.setVisibility(View.GONE);
        }
    }
}
