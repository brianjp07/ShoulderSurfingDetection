package cse4471.shouldersurf;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

        Context context = this;
        Intent serviceIntent = new Intent(context,CurrentAppReporter.class);
        //serviceIntent.setAction("cse4471.shouldersurf.CurrentAppReporter");

        ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

// This schedule a runnable task every 2 minutes
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                String x = getCurrentApp();
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
    public String getCurrentApp(){
        Context context = this;
        String currentApp = null;
        ActivityManager activityManager = (ActivityManager) context.getSystemService( Context.ACTIVITY_SERVICE );
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
       // final FaceDetectionController fdController = new FaceDetectionController();
        for(ActivityManager.RunningAppProcessInfo ap : appProcesses){
            if(ap.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
                currentApp = ap.processName;
               // Log.i("Foreground App", ap.processName);
                if(ap.processName.equals("cse4471.shouldersurf")){
                    //start the controller.
                    Log.i(ap.processName,"detected as foreground, the camera should open");
                    //fdController.safeCameraOpen(1);
                   // fdController.createCameraSession();
                   // fdController.startFaceDetection();
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
