package cse4471.shouldersurf;

import android.app.Activity;
import android.hardware.Camera;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;


/**
 * Created by Brian on 4/3/2015.
 */
public class CameraActivity extends Activity{
    private Camera fCamera;

    public void createCameraSession(){
        fCamera = Camera.open();
        fCamera.setFaceDetectionListener(FDListener);
        fCamera.startFaceDetection();
    }



    private Camera.FaceDetectionListener FDListener  = new Camera.FaceDetectionListener() {

        @Override
        public void onFaceDetection(Camera.Face[] faces, Camera camera) {
            if (faces.length > 1){
                Log.d("FaceDetection", " more than 1 face detected: " + faces.length);
                /*todo: warn the user */

            }
        }
    };
}


