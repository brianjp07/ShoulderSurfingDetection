package cse4471.shouldersurf;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;


import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;


/**
 * Created by Brian on 4/3/2015.
 */

public class FaceDetectionController extends Activity{
    public boolean cameraIsOpen = false;

    private Camera mCamera;
    public boolean openCamera(int id) {


        try {

            mCamera = Camera.open(id);
            cameraIsOpen = (mCamera != null);
        } catch (Exception e) {
            Log.e(getString(R.string.app_name), "failed to open Camera");
            e.printStackTrace();
        }

        return cameraIsOpen;
    }

    public void createCameraSession(){
        mCamera = Camera.open();
        mCamera.setFaceDetectionListener(FDListener);
        mCamera.startFaceDetection();
    }


    private Camera.FaceDetectionListener FDListener;{
        FDListener = new Camera.FaceDetectionListener() {

            @Override
            public void onFaceDetection(Camera.Face[] faces, Camera camera) {
                if (faces.length > 0) {
                    Log.d("FaceDetection", " more than 1 face detected: " + faces.length);
                    //just a alias to main activity to do the notification.
                MainActivity main = new MainActivity();
                    main.alertUser();
                }
            }
        };
    }
    public boolean startFaceDetection(){
        // Try starting Face Detection
        Camera.Parameters params = mCamera.getParameters();

        if (params.getMaxNumDetectedFaces() > 0){
            // camera supports face detection, so can start it:
            mCamera.startFaceDetection();
        }else{
            Log.d("error","phone does not support face detection");
        }
        return true;
    }
   public void closeCamera(){
       mCamera.release();
   }

}



