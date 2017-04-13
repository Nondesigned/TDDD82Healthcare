package tddd82.healthcare;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class    VideoCall{

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
    private float currentFps = 10;
    private float framesRecorded = 0;

    private long lastQualityChange = 0;

    private final Camera.PreviewCallback onFrame = new Camera.PreviewCallback(){

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {

            if (alive){
                Camera.Size previewSize = cameraParameters.getPreviewSize();
                YuvImage yuv = new YuvImage(data, ImageFormat.NV21, previewSize.width, previewSize.height, null);

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                boolean ret = yuv.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), imageQuality, os);

                if (ret) {

                    if (fpsTimestamp + 500 < System.currentTimeMillis()){
                        fpsTimestamp = System.currentTimeMillis();
                        currentFps = 2*framesRecorded;
                        framesRecorded = 0;
                    }
                    framesRecorded++;

                    byte[] compressed = os.toByteArray();
                    DataPacket p = new DataPacket(compressed.length);
                    p.setPayload(compressed);
                    p.setBufferSize(compressed.length);
                    p.setFlag(DataPacket.FLAG_IS_VIDEO, true);
                    p.setFrameRate(playbackBuffer.getReceiveRate());

                    recorderBuffer.push(p);
                } else {
                    System.out.println("Failed to compress");
                }
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

    public int getImageQuality(){
        return imageQuality;
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

    public void terminate(){
        this.alive = false;
        if (cameraInstance != null){
            cameraInstance.stopPreview();
            cameraInstance.setPreviewCallback(null);
            cameraInstance.release();
            cameraInstance = null;
            displayTexture.release();
        }
    }

    public CallError initialize(){
        cameraInstance = openFrontCamera();

        if (cameraInstance == null){
            return CallError.CAMERA_ERROR;
        }

        cameraParameters = cameraInstance.getParameters();
        cameraParameters.setPreviewFormat(ImageFormat.NV21);

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

    private void increaseQuality(){
        if (imageQuality < 30 && lastQualityChange + 250 < System.currentTimeMillis()){
            lastQualityChange = System.currentTimeMillis();
            imageQuality++;
        }
    }

    private void decreaseQuality(){
        if (imageQuality > 1 && lastQualityChange + 250 < System.currentTimeMillis()){
            lastQualityChange = System.currentTimeMillis();
            imageQuality--;
        }
    }

    private void playbackWorker(){

        while(alive){
            if (!playbackBuffer.empty()){
                DataPacket p = playbackBuffer.poll();

                if (Math.abs(playbackBuffer.getReceiveRate() - p.getFrameRate()) > currentFps/5) {
                    decreaseQuality();
                }
                else {
                    increaseQuality();
                }

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

                sleep(1);
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

    private void sleep(int ms){
        try{
            Thread.sleep(ms);
        } catch (Exception ex){

        }
    }

};