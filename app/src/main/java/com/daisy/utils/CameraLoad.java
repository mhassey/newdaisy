package com.daisy.utils;

public class CameraLoad {
    public boolean isLoaded=false;
    private static CameraLoad cameraLoad=new CameraLoad();
    private CameraLoad()
    {

    }

    public  static CameraLoad getInstance()
    {
        return cameraLoad;
    }

}
