package cse4471.shouldersurf;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Brian on 4/10/2015.
 * This is the background service.
 */

public class CurrentAppReporter extends IntentService {
    private static final String serviceName = CurrentAppReporter.class.getSimpleName();
    private Timer timer;
    //this timer is not working for somer reason
    private TimerTask updateTask = new TimerTask(){
        @Override
        public void run(){
            Log.i(serviceName,"Doin work");
        }
    };

    public CurrentAppReporter(){
        super("CurrentAppReporter");
    }
    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        String dataString = workIntent.getDataString();

        // Do work here, based on the contents of dataString

    }
    //used for others services to connect to this one but since we don't need that return null.
    @Override
    public IBinder onBind(Intent intent){return null;}
//
    @Override
    public void onCreate(){
        super.onCreate();
        Log.i(serviceName,"Service Created");
        timer = new Timer("AppCheckerTimer");
        Context context = this;
        String currentApp = null;
        ActivityManager activityManager = (ActivityManager) context.getSystemService( Context.ACTIVITY_SERVICE );
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for(ActivityManager.RunningAppProcessInfo ap : appProcesses){
            if(ap.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
                currentApp = ap.processName;
                if (currentApp.equals("cse4471.shouldersurf")){

                }
                Log.i("Foreground App", ap.processName);
            }
        }
        //schedule time to repeat every 60 seconds
        timer.schedule(updateTask,1000L,60*1000L);

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.i(serviceName,"Service Destroyed");
        timer.cancel();
        timer = null;
    }


}

