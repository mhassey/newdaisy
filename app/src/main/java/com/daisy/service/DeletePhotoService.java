package com.daisy.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.daisy.common.session.SessionManager;
import com.daisy.utils.Utils;

public class DeletePhotoService extends Service {
    private SessionManager sessionManager;

    public DeletePhotoService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sessionManager = SessionManager.get();
        if (sessionManager.getDeletePhoto()) {
           Utils.deleteGalaryPhoto();
        }
        Utils.deleteCallList(getApplicationContext());
        stopSelf();
        return super.onStartCommand(intent, flags, startId);

    }
}
