package tddd82.healthcare;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.provider.MediaStore;
import android.hardware.Camera.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class VideoCall{

    private Camera cameraInstance;
    private Camera.Parameters cameraParameters;

    private SurfaceTexture displayTexture;

    private VideoBuffer recorderBuffer;
    private VideoBuffer playbackBuffer;

    private ImageView displayView;

    private boolean alive;

    private final Camera.PreviewCallback onFrame = new Camera.PreviewCallback(){

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            Bitmap bm = Bitmap.createBitmap(640, 480, Bitmap.Config.ARGB_8888);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 10, os);

            byte[] compressed = os.toByteArray();
            DataPacket p = new DataPacket(compressed.length);
            p.setPayload(compressed);

            p.setFlag(DataPacket.FLAG_IS_VIDEO, true);

            recorderBuffer.push(p);
        }
    };

    public VideoCall(final VideoBuffer recorderBuffer, VideoBuffer playbackBuffer, ImageView view){

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

    public CallError initialize(){
        cameraInstance = openFrontCamera();

        if (cameraInstance == null){
            return CallError.CAMERA_ERROR;
        }

        cameraParameters = cameraInstance.getParameters();
        cameraParameters.setJpegQuality(10);
        for(Size s : cameraParameters.getSupportedPictureSizes()){
            System.out.println(s.height);
            System.out.println(s.width);
        }

        try {
            cameraInstance.setPreviewTexture(displayTexture);
        } catch (IOException e) {
            e.printStackTrace();
            return CallError.CAMERA_PREVIEW_ERROR;
        }

        cameraParameters.setPictureSize(640, 480);
        cameraInstance.setParameters(cameraParameters);

        cameraInstance.setPreviewCallback(onFrame);

        return CallError.SUCCESS;
    }

    private void playbackWorker(){
        while(alive){
            if (!playbackBuffer.empty()){

                DataPacket p = playbackBuffer.poll();

                Bitmap bm = BitmapFactory.decodeByteArray(p.getBuffer(), 0, p.getBufferSize());
                if (bm != null)
                    displayView.setImageBitmap(bm);

            } else{
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