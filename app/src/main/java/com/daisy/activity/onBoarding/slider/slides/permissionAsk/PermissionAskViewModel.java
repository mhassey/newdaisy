package com.daisy.activity.onBoarding.slider.slides.permissionAsk;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class PermissionAskViewModel extends AndroidViewModel {
    private boolean grandMediaPermission = false;
    private boolean grandDisplayOverTheApp = false;
    private boolean grandModifySystemSettings = false;
    private boolean grandUsageAccess = false;
    private boolean grandBatteyOptimization = false;
    private boolean grandExtraAccess = false;
    private boolean grandAdminPermission = false;
    private boolean grandGpsEnable = false;
    private boolean isAutoStart = false;

    public boolean isGrandGpsEnable() {
        return grandGpsEnable;
    }

    public void setGrandGpsEnable(boolean grandGpsEnable) {
        this.grandGpsEnable = grandGpsEnable;
    }

    public boolean isGrandAdminPermission() {
        return grandAdminPermission;
    }

    public boolean isAutoStart() {
        return isAutoStart;
    }

    public void setAutoStart(boolean autoStart) {
        isAutoStart = autoStart;
    }

    public void setGrandAdminPermission(boolean grandAdminPermission) {
        this.grandAdminPermission = grandAdminPermission;
    }

    public PermissionAskViewModel(@NonNull Application application) {
        super(application);
    }

    public boolean isGrandMediaPermission() {
        return grandMediaPermission;
    }

    public void setGrandMediaPermission(boolean grandMediaPermission) {
        this.grandMediaPermission = grandMediaPermission;
    }

    public boolean isGrandDisplayOverTheApp() {
        return grandDisplayOverTheApp;
    }

    public void setGrandDisplayOverTheApp(boolean grandDisplayOverTheApp) {
        this.grandDisplayOverTheApp = grandDisplayOverTheApp;
    }

    public boolean isGrandModifySystemSettings() {
        return grandModifySystemSettings;
    }

    public void setGrandModifySystemSettings(boolean grandModifySystemSettings) {
        this.grandModifySystemSettings = grandModifySystemSettings;
    }

    public boolean isGrandUsageAccess() {
        return grandUsageAccess;
    }

    public void setGrandUsageAccess(boolean grandUsageAccess) {
        this.grandUsageAccess = grandUsageAccess;
    }

    public boolean isGrandBatteyOptimization() {
        return grandBatteyOptimization;
    }

    public void setGrandBatteyOptimization(boolean grandBatteyOptimization) {
        this.grandBatteyOptimization = grandBatteyOptimization;
    }

    public boolean isGrandExtraAccess() {
        return grandExtraAccess;
    }

    public void setGrandExtraAccess(boolean grandExtraAccess) {
        this.grandExtraAccess = grandExtraAccess;
    }
}
