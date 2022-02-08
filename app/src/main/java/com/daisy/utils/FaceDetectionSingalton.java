package com.daisy.utils;

public class FaceDetectionSingalton {
    private static FaceDetectionSingalton faceDetectionSingalton = new FaceDetectionSingalton();
    private boolean isDetected=false;
    public static FaceDetectionSingalton getFaceDetectionSingalton() {
        return faceDetectionSingalton;
    }

    public boolean isDetected() {
        return isDetected;
    }

    public void setDetected(boolean detected) {
        isDetected = detected;
    }
}
