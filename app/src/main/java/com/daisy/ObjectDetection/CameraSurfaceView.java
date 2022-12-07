package com.daisy.ObjectDetection;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.daisy.ObjectDetection.cam.FaceDetectionCamera;
import com.daisy.ObjectDetection.cam.OneShotFaceDetectionListener;

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
        try {
            camera.initialise(listener, holder);
        }
        catch (Exception e) {
            Log.e("Errorrr", "COmes");
        }
        }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (surfaceDoesNotExist()) {

            return;
        }
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
