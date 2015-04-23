package cse4471.shouldersurf;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MainActivity extends ActionBarActivity {
    private Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //look  at the logs to see result.
        //getCurrentApp();
        final ArrayList<String> watchedApps = new ArrayList<>();
        final Button button = (Button) findViewById(R.id.enter_button);
        final EditText editText = (EditText) findViewById(R.id.app_name_field);

        Context context = this;


        ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

        /*
        This schedules a runnable task every 10 seconds. This means that the task will look
        at the current running app every 10 seconds and determine if it should start the camera.

        This task will keep refreshing even if the current app stays open but the time
        it takes to reinitialize the camera should be less than a second so it raises no security concerns.
        */
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                //calls the method that looks at current app and decides to start the camera or not.

                String x = getCurrentAppAndHandle(watchedApps);

                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        String textBoxContents = editText.getText().toString();
                        if (!watchedApps.contains(textBoxContents)) {
                            watchedApps.add(editText.getText().toString());
                        }

                    }
                });
                //Log.d(x,"current app is:");

            }
        }, 0, 20, TimeUnit.SECONDS);




    }

    //this will be called by the background service to get the current app.
    // current this logic is within the service for testing purposes.
    public String getCurrentAppAndHandle(ArrayList watchedApps) {
        Context context = this;
        String currentApp = null;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        String test = "?";
        Log.i(test, "trying to create new instance of faceDetectionController");

        test = "true";
        Log.i(test, "was able to create new instance of faceDetectionController");
        for (ActivityManager.RunningAppProcessInfo ap : appProcesses) {
            if (ap.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                currentApp = ap.processName;
                Log.i("Foreground App", ap.processName);

                if (watchedApps.contains(ap.processName)) {
                    //start the controller.
                    Log.i(ap.processName, "detected as foreground");
                    try{
                        mCamera = getCameraInstance();
                        SurfaceHolder holder = new SurfaceHolder() {
                            @Override
                            public void addCallback(Callback callback) {

                            }

                            @Override
                            public void removeCallback(Callback callback) {

                            }

                            @Override
                            public boolean isCreating() {
                                return true;
                            }

                            @Override
                            public void setType(int type) {

                            }

                            @Override
                            public void setFixedSize(int width, int height) {

                            }

                            @Override
                            public void setSizeFromLayout() {

                            }

                            @Override
                            public void setFormat(int format) {

                            }

                            @Override
                            public void setKeepScreenOn(boolean screenOn) {

                            }

                            @Override
                            public Canvas lockCanvas() {
                                return null;
                            }

                            @Override
                            public Canvas lockCanvas(Rect dirty) {
                                return null;
                            }

                            @Override
                            public void unlockCanvasAndPost(Canvas canvas) {

                            }

                            @Override
                            public Rect getSurfaceFrame() {
                                return null;
                            }

                            @Override
                            public Surface getSurface() {
                                return null;
                            }
                        };

                        //mCamera.setPreviewDisplay(holder);
                        SurfaceTexture texture = new SurfaceTexture(1);
                        mCamera.setPreviewTexture(texture);
                        mCamera.startPreview();
                        mCamera.startFaceDetection();
                    }catch(Exception e){
                        startFaceDetection();
                    }

                    Log.i("last line:", "mCamera.startPreview()");
                    startFaceDetection();
                    Log.i("last line:", "startFaceDetection();");

                    alertUser();

                } else {
                        try{
                            mCamera.stopPreview();
                            mCamera.release();
                        }catch (Exception e){

                        }
                }
            }
        }
        return currentApp;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void alertUser() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.generic_icon)
                        .setContentTitle("My notification")
                        .setContentText("Hello World! Someone is watching");
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// notificationID allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());

    }


    public static Camera getCameraInstance() {
        Log.d("camera entered", "test");
        int cameraId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                Log.d("it was", "Camera found");
                cameraId = i;
                break;
            }
        }
        Camera c = null;
        try {

            c = Camera.open(0); // attempt to get a Camera instance
            c.setFaceDetectionListener(new FDListener());

        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            Log.d(e.toString(), "error opening camera");
        }
        return c; // returns null if camera is unavailable
    }


    public static class FDListener implements Camera.FaceDetectionListener {


        @Override
        public void onFaceDetection(Camera.Face[] faces, Camera mCamera) {

            if (faces.length > 0) {
                Log.d("FaceDetection", "face detected: " + faces.length +
                        " Face 1 Location X: " + faces[0].rect.centerX() +
                        "Y: " + faces[0].rect.centerY());
            }else{
                Log.d("no","faces");
            }
        }
    }

    public void startFaceDetection() {
        // Try starting Face Detection
        Camera.Parameters params = mCamera.getParameters();
        //mCamera.startFaceDetection();
        // start face detection only *after* preview has started
        int x = params.getMaxNumDetectedFaces();
        Log.i("params.getMaxedFaces()", Integer.toString(x));
        if (params.getMaxNumDetectedFaces() > 0) {

            // camera supports face detection, so can start it:
            Log.d("face dection", "started");
            mCamera.startFaceDetection();
        }else{
            Log.d("face dection", "didn't start");
        }
    }

}