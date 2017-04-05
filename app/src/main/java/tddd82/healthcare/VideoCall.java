package tddd82.healthcare;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.provider.ContactsContract;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class VideoCall{

    private Camera cameraInstance;
    private Camera.Parameters cameraParameters;

    private SurfaceTexture displayTexture;

    private VideoBuffer recorderBuffer;
    private VideoBuffer playbackBuffer;

    private ImageView displayView;

    private boolean alive;
    private Activity activity;

    private int imageWidth = 1280;
    private int imageHeight = 720;

    private int imageQuality = 10;

    private long fpsTimestamp = 0;
    private int currentFps = 0;
    private int framesRecorded = 0;

    private final Camera.PreviewCallback onFrame = new Camera.PreviewCallback(){

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {

            if (fpsTimestamp + 1000 < System.currentTimeMillis()){
                fpsTimestamp = System.currentTimeMillis();
                currentFps = framesRecorded;
                framesRecorded = 0;
            }
            framesRecorded++;

            YuvImage yuv = new YuvImage(data, cameraParameters.getPreviewFormat(), imageWidth, imageHeight, null);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            boolean ret = yuv.compressToJpeg(new Rect(0, 0, imageWidth, imageHeight), imageQuality, os);
            if (ret) {
                byte[] compressed = os.toByteArray();
                DataPacket p = new DataPacket(compressed.length);
                p.setPayload(compressed);
                p.setBufferSize(compressed.length);
                p.setFlag(DataPacket.FLAG_IS_VIDEO, true);
                p.setSampleRate(imageQuality);

                recorderBuffer.push(p);
            }
        }
    };

    public VideoCall(final VideoBuffer recorderBuffer, VideoBuffer playbackBuffer, ImageView view, Activity activity){

        this.activity = activity;
        displayTexture = new SurfaceTexture(10);
        displayView = view;
        this.recorderBuffer = recorderBuffer;
        this.playbackBuffer = playbackBuffer;

    }

    public void start(){
        cameraInstance.startPreview();

        alive = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                playbackWorker();
            }
        }).start();
    }

    public void setImageSize(int width, int height){
        this.imageWidth = width;
        this.imageHeight = height;
    }

    public void setImageQuality(int quality){
        this.imageQuality = quality;
    }

    public void terminate(){
        this.alive = false;
        if (cameraInstance != null){
            cameraInstance.stopPreview();
            cameraInstance.release();
            displayTexture.release();
        }
    }

    public CallError initialize(){
        cameraInstance = openFrontCamera();

        if (cameraInstance == null){
            return CallError.CAMERA_ERROR;
        }

        cameraParameters = cameraInstance.getParameters();
        cameraParameters.setJpegQuality(imageQuality);
        cameraParameters.setPreviewFormat(ImageFormat.NV21);
        cameraParameters.setRotation(90);

        try {
            cameraInstance.setPreviewTexture(displayTexture);
        } catch (IOException e) {
            e.printStackTrace();
            return CallError.CAMERA_PREVIEW_ERROR;
        }

        cameraParameters.setPictureSize(imageWidth, imageHeight);
        cameraParameters.setPreviewSize(imageWidth, imageHeight);
        cameraInstance.setParameters(cameraParameters);

        cameraInstance.setPreviewCallback(onFrame);

        return CallError.SUCCESS;
    }

    private void playbackWorker(){

        long lastPlayback = System.currentTimeMillis();

        while(alive){
            if (!playbackBuffer.empty()){
                lastPlayback = System.currentTimeMillis();
                if (imageQuality < 30)
                    imageQuality++;

                DataPacket p = playbackBuffer.poll();

                if (p.getBufferSize() < DataPacket.MAX_SIZE){
                    final Bitmap bm = BitmapFactory.decodeByteArray(p.getBuffer(), DataPacket.HEADER_SIZE, p.getBufferSize());
                    if (bm != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                displayView.setImageBitmap(bm);
                            }
                        });
                    }
                }
            } else {
                if (lastPlayback + 250 < System.currentTimeMillis()) {
                    lastPlayback = System.currentTimeMillis();
                    if (imageQuality > 1) {
                        imageQuality--;
                    }
                }
                try{
                    Thread.sleep(1);
                } catch (Exception ex){

                }
            }
        }
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