package com.daisy.ObjectDetection.cam;

import android.hardware.Camera;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * Manages the android camera and sets it up for face detection
 * can throw an error if face detection is not supported on this device
 */
public class FaceDetectionCamera implements OneShotFaceDetectionListener.Listener {

    private Camera camera;

    private Listener listener;
    private SurfaceHolder surfaceHolder;

    public FaceDetectionCamera(Camera camera) {
        this.camera = camera;
    }

    /**
     * Use this to detect faces when you have a custom surface to display upon
     *
     * @param listener the {@link com.blundell.tutorial.cam.FaceDetectionCamera.Listener} for when faces are detected
     * @param holder   the {@link android.view.SurfaceHolder} to display upon
     */
    public void initialise(Listener listener, SurfaceHolder holder) {
        this.listener = listener;
        try {
            surfaceHolder=holder;
            camera.stopPreview();

        } catch (Exception swallow) {
            // ignore: tried to stop a non-existent preview
        }
        try {
            if (camera!=null) {
                camera.setPreviewDisplay(holder);
                camera.startPreview();
                camera.setFaceDetectionListener(new OneShotFaceDetectionListener(this));
                camera.startFaceDetection();
            }
            } catch (IOException e) {
                resetCamera(holder);

            this.listener.onFaceDetectionNonRecoverableError();
        }
    }

    private void resetCamera(SurfaceHolder holder) {
        try {
            camera.stopPreview();
            try {
                camera.setPreviewDisplay(null);
            } catch (IOException ex) {
            }
            camera.release();
            camera = null;
            camera = Camera.open(getFrontFacingCameraId());
            try {
                camera.setPreviewDisplay(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            camera.startPreview();
            camera.setFaceDetectionListener(new OneShotFaceDetectionListener(this));
            camera.startFaceDetection();
        }catch (Exception e)
        {

        }
    }

    private int getFrontFacingCameraId() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int i = 0;
        for (; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                break;
            }
        }
        return i;
    }

    @Override
    public void onFaceDetected() {
        listener.onFaceDetected();
    }

    @Override
    public void onFaceTimedOut() {
        listener.onFaceTimedOut();
    }

    public void recycle() {
        if (camera != null) {

            camera.release();
            camera=null;

        }

    }

    public interface Listener {
        void onFaceDetected();

        void onFaceTimedOut();

        void onFaceDetectionNonRecoverableError();

    }
}
