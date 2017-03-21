package tddd82.healthcare;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class VideoCall{

    private Camera cameraInstance;

    private SurfaceTexture displayTexture;
    private SurfaceHolder displayHolder;


    public VideoCall(){
        cameraInstance = openFrontCamera();
        displayTexture = new SurfaceTexture(10);

        try {
            cameraInstance.setPreviewTexture(displayTexture);
        } catch (IOException e) {
            e.printStackTrace();
        }

        cameraInstance.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                System.out.println("BP");
            }
        });

        cameraInstance.startPreview();

    }

    private Camera openFrontCamera(){
        int count = Camera.getNumberOfCameras();
        Camera camera = null;
        Camera.CameraInfo info = new Camera.CameraInfo();
        try {
            for (int i = 0; i < count; i++) {
                Camera.getCameraInfo(i, info);
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    camera = Camera.open(i);
                }
            }
        } catch (Exception ex){
            ex.printStackTrace();
            return null;
        }

        return camera;
    }

};