package cse4471.shouldersurf;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by millstev on 4/23/15.
 */
public class backService extends Service {

    public void onCreate(){
        Log.i("onCreate", " has been called");
        super.onCreate();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID){
        Log.i("onStartCommand", " has been called");
        ArrayList<String> watchedApps = intent.getStringArrayListExtra("KEY1");
        MainActivity main = new MainActivity();
        main.getCurrentAppAndHandle(watchedApps);
        return 0;
    }

}
