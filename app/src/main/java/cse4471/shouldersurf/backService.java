package cse4471.shouldersurf;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.ArrayList;

/**
 * Created by millstev on 4/23/15.
 */
public class backService extends Service {

    public void onCreate(){
        super.onCreate();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID){

        ArrayList<String> watchedApps = intent.getStringArrayListExtra("KEY1");
        MainActivity.getCurrentAppAndHandle(watchedApps);
        return 0;
    }

}