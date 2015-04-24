package cse4471.shouldersurf;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MainActivity extends ActionBarActivity {
    private Camera mCamera;
    final ArrayList<String> watchedApps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //look  at the logs to see result.

        final Button button = (Button) findViewById(R.id.enter_button);
        final EditText editText = (EditText) findViewById(R.id.app_name_field);
        final TextView listView = (TextView) findViewById(R.id.textView);

        //create a list of all the installed apps
        int flags = PackageManager.GET_META_DATA |
                PackageManager.GET_SHARED_LIBRARY_FILES |
                PackageManager.GET_UNINSTALLED_PACKAGES;

        PackageManager pm = getPackageManager();
        List<ApplicationInfo> applications = pm.getInstalledApplications(flags);
        String listPkg = "";

        for (ApplicationInfo appInfo : applications) {
            listPkg = listPkg + appInfo.processName + "\r\n";
        }

        //display in Text
        listView.setText(listPkg);


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
                        Log.i("added process ",textBoxContents+" to watched apps");

                    }
                });
                //Log.d(x,"current app is:");

            }
        }, 0, 10, TimeUnit.SECONDS);




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
                    //the current app is a watched app!
                    alertUser(ap.processName);
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

                        mCamera.setPreviewDisplay(holder);
                        SurfaceTexture texture = new SurfaceTexture(1);
                        mCamera.setPreviewTexture(texture);
                        mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                            @Override
                            public void onPreviewFrame(byte[] data, Camera camera) {
                                startFaceDetection();
                            }
                        });
                        mCamera.startPreview();
                        Log.i("hola", "hola");
                            Thread.sleep(1000);

                    }catch(Exception e){

                    }

                    /*at this point, the preview  should be running and the detection should be on
                     but we need to do the "face listening", I think that is happening automatically
                    after startFaceDetection was called.*/

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

    public void alertUser(String watchedApp) {

        //this is to show a notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.generic_icon)
                        .setContentTitle("App Watcher")
                        .setContentText(watchedApp + " is a flagged app! Shoulder surfing initiated.");
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // this is to show a toast
        mNotificationManager.notify(1, mBuilder.build());
        Context context = getApplicationContext();

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

            c = Camera.open(cameraId); // attempt to get a Camera instance
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

            if (faces.length <= 1) {

                Log.d("FaceDetection", "face detected: " + faces.length +
                        " Face 1 Location X: " + faces[0].rect.centerX() +
                        "Y: " + faces[0].rect.centerY());
                Log.d("FaceDetection", "0 or 1 faces, we are ok");

            }else if(faces.length > 1) {
            //this is the part where we do something if there are too many faces
                Log.d("FaceDetection", "face detected: " + faces.length +
                        " Face 1 Location X: " + faces[0].rect.centerX() +
                        "Y: " + faces[0].rect.centerY());
                Log.d("FaceDetection", "too many faces! need to alert user now");

            }else{
                Log.d("no","faces");
            }
        }
    }

    protected void onPause(){
        Log.i("test","pause ping");
        super.onPause();
        // use this to start and trigger a service
        Context context = getApplicationContext();
        Intent i = new Intent(context, backService.class);
        // potentially add data to the intent
        i.putExtra("KEY1", watchedApps);
        context.startService(i);
    }

    protected void onResume(){
        Log.i("test","resume ping");
        super.onResume();
        // use this to start and trigger a service
        Context context = getApplicationContext();
        Intent i = new Intent(context, backService.class);
        // potentially add data to the intent
        i.putExtra("KEY1", watchedApps);
        context.stopService(i);
    }

}