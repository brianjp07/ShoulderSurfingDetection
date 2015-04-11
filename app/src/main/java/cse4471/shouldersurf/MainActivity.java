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
        startService(serviceIntent);


    }

    //this will be called by the background service to get the current app.
    public String getCurrentApp(){
        Context context = this;
        String currentApp = null;
        ActivityManager activityManager = (ActivityManager) context.getSystemService( Context.ACTIVITY_SERVICE );
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for(ActivityManager.RunningAppProcessInfo ap : appProcesses){
            if(ap.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
                currentApp = ap.processName;
                Log.i("Foreground App", ap.processName);
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
