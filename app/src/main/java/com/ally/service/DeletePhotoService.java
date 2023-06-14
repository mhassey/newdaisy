package com.ally.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.ally.common.session.SessionManager;
import com.ally.utils.Utils;

/**
 * DeletePhotoService is an service that help to delete content in background
 */
public class DeletePhotoService extends Service {
    private SessionManager sessionManager;

    public DeletePhotoService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    // TODO Delete all photo and data from storage
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
