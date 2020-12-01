package com.daisy.ObjectDetection.cam;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.daisy.activity.mainActivity.MainActivity;

/**
 * I manage loading and destroying the camera reference for you
 */
public class FrontCameraRetriever implements Application.ActivityLifecycleCallbacks, LoadFrontCameraAsyncTask.Listener {

    private final Listener listener;

    private FaceDetectionCamera camera;
    private Activity activity;

    public static void retrieveFor(Context activity) {
        if (!(activity instanceof Listener)) {
            throw new IllegalStateException("Your activity needs to implement FrontCameraRetriever.Listener");
        }
        Listener listener = (Listener) activity;
        retrieve(activity, listener);
    }

    private static void retrieve(Context context, Listener listener) {
        Application application = (Application) context.getApplicationContext();
        FrontCameraRetriever frontCameraRetriever = new FrontCameraRetriever(listener);
        application.registerActivityLifecycleCallbacks(frontCameraRetriever);
    }

    FrontCameraRetriever(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        // not used
    }

    @Override
    public void onActivityStarted(Activity activity) {
        // not used
    }

    @Override
    public void onActivityResumed(Activity activity) {
        this.activity = activity;

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (activity instanceof MainActivity)
                    new LoadFrontCameraAsyncTask(FrontCameraRetriever.this).load();

            }
        }).start();
    }

    @Override
    public void onLoaded(FaceDetectionCamera camera) {
        this.camera = camera;
        listener.onLoaded(camera);

    }

    @Override
    public void onFailedToLoadFaceDetectionCamera() {
        if (activity instanceof MainActivity)
            listener.onFailedToLoadFaceDetectionCamera();
    }

    @Override
    public void onActivityPaused(Activity activity) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (activity instanceof MainActivity) {
                    if (camera != null) {
                        camera.recycle();


                    }
                }

            }
        }).start();
    }

    @Override
    public void onActivityStopped(Activity activity) {
        // not used
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        // not used
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (activity instanceof MainActivity)
            activity.getApplication().unregisterActivityLifecycleCallbacks(this);
    }

    public interface Listener extends LoadFrontCameraAsyncTask.Listener {

    }
}
