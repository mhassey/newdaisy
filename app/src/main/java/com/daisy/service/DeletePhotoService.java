package com.daisy.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.daisy.common.session.SessionManager;
import com.daisy.utils.Utils;

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
        throw null;
    }

    // TODO Delete all photo and data from storage
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            sessionManager = SessionManager.get();
            if (sessionManager.getDeletePhoto()) {
                Utils.deleteGalaryPhoto();
            }
            //    Utils.deleteCallList(getApplicationContext());
            stopSelf();
        } catch (Exception e) {

        }
        return super.onStartCommand(intent, flags, startId);

    }
}
