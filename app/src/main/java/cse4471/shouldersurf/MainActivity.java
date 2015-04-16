package cse4471.shouldersurf;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //look  at the logs to see result.
        //getCurrentApp();
        final ArrayList <String>  watchedApps = new ArrayList<>();
        final Button button = (Button) findViewById(R.id.enter_button);
        final EditText editText = (EditText) findViewById(R.id.app_name_field);

        Context context = this;
        Intent serviceIntent = new Intent(context,CurrentAppReporter.class);
        //serviceIntent.setAction("cse4471.shouldersurf.CurrentAppReporter");

        ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

// This schedule a runnable task every 2 minutes
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                String x = getCurrentAppAndHandle(watchedApps);
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        String textBoxContents = editText.getText().toString();
                        if(!watchedApps.contains(textBoxContents)){
                            watchedApps.add(editText.getText().toString());
                        }

                    }
                });
                //Log.d(x,"current app is:");

            }
        }, 0, 10, TimeUnit.SECONDS);
        /*

            We might not even need this
         */
        //startService(serviceIntent);


    }

    //this will be called by the background service to get the current app.
    // current this logic is within the service for testing purposes.
    public String getCurrentAppAndHandle(ArrayList watchedApps){
        Context context = this;
        String currentApp = null;
        ActivityManager activityManager = (ActivityManager) context.getSystemService( Context.ACTIVITY_SERVICE );
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        String test= "?";
        Log.i(test,"trying to create new instance of faceDetectionController");
       //final FaceDetectionController fdController = new FaceDetectionController();
         test= "true";
        Log.i(test,"was able to create new instance of faceDetectionController");
        for(ActivityManager.RunningAppProcessInfo ap : appProcesses){
            if(ap.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
                currentApp = ap.processName;
                Log.i("Foreground App", ap.processName);

                if(watchedApps.contains(ap.processName)){
                    //start the controller.
                    Log.i(ap.processName,"detected as foreground, the camera should open");
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
                   //fdController.safeCameraOpen(cameraId);
                   //fdController.createCameraSession();
                   //fdController.startFaceDetection();
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
}
