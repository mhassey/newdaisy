package com.daisy.ObjectDetection;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.daisy.ObjectDetection.cam.FaceDetectionCamera;

import java.io.IOException;

@SuppressLint("ViewConstructor") // View can only be inflated programatically
public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private final FaceDetectionCamera camera;
    private final FaceDetectionCamera.Listener listener;

    public CameraSurfaceView(Context context, FaceDetectionCamera camera, FaceDetectionCamera.Listener listener) {
        super(context);


        this.camera = camera;
        this.listener = listener;
        // Listen for when the surface is ready to be drawn on
        this.getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // When the surface is ready to be drawn on
        // tell our camera to use this to show a preview

            camera.initialise(listener, holder);
        }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e("kali","surface change");
        if (surfaceDoesNotExist()) {
            Log.e("kali","surface not available");

            return;
        }

        // When the surface changes we need to re-attach it to our camera
            camera.initialise(listener, holder);

    }

    private boolean surfaceDoesNotExist() {
        return getHolder().getSurface() == null;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
        // (done in FrontCameraRetriever for us)
    }

    public FaceDetectionCamera getCamera() {
        return camera;
    }
}
