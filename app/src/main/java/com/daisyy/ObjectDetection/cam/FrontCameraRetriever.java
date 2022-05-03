package com.daisyy.ObjectDetection.cam;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

/**
 * I manage loading and destroying the camera reference for you
 */
public class FrontCameraRetriever implements Application.ActivityLifecycleCallbacks, LoadFrontCameraAsyncTask.Listener {

    private static FrontCameraRetriever frontCameraRetriever;
    private final Listener listener;

    private FaceDetectionCamera camera;
    private Activity activity;
    public static void retrieveFor(Context activity) {

        Log.e("working","here");
        Listener listener = (Listener) activity;
        retrieve(activity, listener);
    }

    private static void retrieve(Context context, Listener listener) {
        Application application = (Application) context.getApplicationContext();
         frontCameraRetriever = new FrontCameraRetriever(listener);
        Log.e("register","camera");
        application.registerActivityLifecycleCallbacks(frontCameraRetriever);

    }

    FrontCameraRetriever(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        // not used
        Log.e("working","created");

    }

    @Override
    public void onActivityStarted(Activity activity) {
        // not used
        Log.e("working","started");
    }

    public static FrontCameraRetriever getInstance()
    {
        return frontCameraRetriever;
    }
    public void load()
    {
        new LoadFrontCameraAsyncTask(FrontCameraRetriever.this).load();

    }

    @Override
    public void onActivityResumed(Activity activity) {
        this.activity = activity;

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//              //  if (activity instanceof MainActivity)
//                Log.e("working","onActivityResumed-------");
//                    new LoadFrontCameraAsyncTask(FrontCameraRetriever.this).load();
//
//            }
//        }).start();
    }

    @Override
    public void onLoaded(FaceDetectionCamera camera) {
        this.camera = camera;
        Log.e("working","load-------");

        listener.onLoaded(camera);

    }

    @Override
    public void onFailedToLoadFaceDetectionCamera() {
        //if (activity instanceof MainActivity)
            listener.onFailedToLoadFaceDetectionCamera();
    }

    @Override
    public void onActivityPaused(Activity activity) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (activity instanceof MainActivity) {
//                    if (camera != null) {
//                        camera.recycle();
//
//
//                    }
//                }
//
//            }
//        }).start();
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
            activity.getApplication().unregisterActivityLifecycleCallbacks(this);
    }

    public interface Listener extends LoadFrontCameraAsyncTask.Listener {

    }
}
