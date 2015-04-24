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
        final TextView listView = (TextView) findViewById(R.id.textView);
        //TODO: might be good to display a list of apps that are already being watched, but I
        //Think that would mean we have to add persistence, and that's probably not needed, so low priority

        //TODO: also we get a list of all installed apps with the following code, idk how hard to display
// http://stackoverflow.com/questions/2695746/how-to-get-a-list-of-installed-android-applications-and-pick-one-to-run
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        //this is a list of packages
       // final List pkgAppsList = this.getPackageManager().queryIntentActivities( mainIntent, 0);
        //find out how many apps there are
        //int numPkg = pkgAppsList.size();
        // make a string for the textBox
        // Flags: See below
        int flags = PackageManager.GET_META_DATA |
                PackageManager.GET_SHARED_LIBRARY_FILES |
                PackageManager.GET_UNINSTALLED_PACKAGES;

        PackageManager pm = getPackageManager();
        List<ApplicationInfo> applications = pm.getInstalledApplications(flags);
        String listPkg = "";

        for (ApplicationInfo appInfo : applications) {
            listPkg = listPkg + appInfo.processName + "\r\n";
        }



//        for (int i = 0; i < numPkg; i++){
////            ResolveInfo info = (ResolveInfo) pkgAppsList.get(i);
////            String temp = info.resolvePackageName;
//            //listPkg = listPkg + pkgAppsList.get(i).toString() + "\r\n";
//        }
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
                    //the current app is a watched app!
                    //TODO: notify user that a watched app is opened, priority: high
                    //TODO: alertUser might do it, untested atm
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

                        //mCamera.setPreviewDisplay(holder);
                        SurfaceTexture texture = new SurfaceTexture(1);
                        mCamera.setPreviewTexture(texture);
                        mCamera.startPreview();
                        //TODO: why are there so many startFaceDetections?
                        //ANSWER: startFaceDetection has a thing in it to check if it's already running,
                        // and it will show up in the log if it tries to open if it already is open.
                        mCamera.startFaceDetection();

                    }catch(Exception e){
                        startFaceDetection();
                    }

                    mCamera.startFaceDetection();
                    Log.i("last line:", "mCamera.startPreview()");
                    startFaceDetection();
                    Log.i("last line:", "startFaceDetection();");

                    /*TODO: at this point, the preview  should be running and the detection should be on
                     but we need to do the "face listening", I think that is happening automatically
                    after startFaceDetection was called.*/


                    // This is supposed to put the activity in the background (by going to home)
                    // an activity in the background will close if resources are limited.
                    Intent i = new Intent(Intent.ACTION_MAIN);
                    i.addCategory(Intent.CATEGORY_HOME);
                    startActivity(i);

                    //I commented this out because i don't know what it's there for
                    //i'm using the method for alerting the user when they open a watched app
                    //alertUser();

                } else {
                        try{
                            mCamera.stopPreview();
                            mCamera.release();
                        }catch (Exception e){
                            //TODO
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

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.generic_icon)
                        .setContentTitle("App Watcher")
                        .setContentText(watchedApp + " is a flagged app! Shoulder surfing initiated.");
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

        //http://www.doepiccoding.com/blog/?p=318
        @Override
        public void onFaceDetection(Camera.Face[] faces, Camera mCamera) {

            if (faces.length <= 1) {

                //TODO: maybe check the one face's parameters to see if they are what is expected?
                //TODO: not sure how this would be done (just positioning maybe). low priority
                Log.d("FaceDetection", "face detected: " + faces.length +
                        " Face 1 Location X: " + faces[0].rect.centerX() +
                        "Y: " + faces[0].rect.centerY());
                Log.d("FaceDetection", "0 or 1 faces, we are ok");

            }else if(faces.length > 1) {
            //TODO: this is the part where we do something if there are too many faces
                Log.d("FaceDetection", "face detected: " + faces.length +
                        " Face 1 Location X: " + faces[0].rect.centerX() +
                        "Y: " + faces[0].rect.centerY());
                Log.d("FaceDetection", "too many faces! need to alert user now");
                //TODO: confused about how to make this right
                //tooManyFaces(mCamera);


            }else{
                Log.d("no","faces");
            }
        }
    }
    public void tooManyFaces(){
        //TODO: pop up the camera from the background: high priority
        //this line is  supposed to bring this activity to the foreground
        //and make sure there is only one of this activity
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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
            Log.d("face detection", "max faces = 0, so it can't detect faces");
        }
    }

    //http://stackoverflow.com/questions/2232238/how-to-bring-an-activity-to-foreground-top-of-stack
    private static Intent getIntent(Context context, Class<?> cls){
        Intent intent = new Intent(context, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        return intent;
    }


}