package anwar.metroim.service;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import anwar.metroim.CustomListView.arrayList;
import anwar.metroim.MainActivity;
import anwar.metroim.Manager.SessionManager;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by anwar on 2/17/2017.
 */

public class BootBroadcast extends BroadcastReceiver {
    public ConnectivityManager conManager = null;
    private SessionManager session;
    public void onReceive(Context context,Intent intent){
        session = new SessionManager(context);
        String action=intent.getAction();
        if(session.isLoggedIn()) {
            switch (intent.getAction()) {
                case "android.intent.action.BOOT_COMPLETED":
                    if (!isMyServiceRunning(context) && isNetworkConnected(context)) {
                        context.startService(new Intent(context, MetroImservice.class));
                    }
                    break;
                case "android.net.conn.CONNECTIVITY_CHANGE":
                    //if the isNetworkConnected =false  Background Service will be stop
                    if (isNetworkConnected(context)) {
                        context.startService(new Intent(context, MetroImservice.class));
                    }
                    break;
                case "android.intent.action.DATE_CHANGED":
                    System.out.println("-------->Boot action" + action);
                  //  new Backup_email(context).addShedule("date");
                    break;
            }
        }
       // isMyServiceRunning(context);
    }
    public boolean isNetworkConnected(Context context) {
        conManager = (ConnectivityManager)context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
   public boolean isMyServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MetroImservice.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public void ServiceStart(Context context){
        if(isNetworkConnected(context) && !isMyServiceRunning(context))
            context.startService(new Intent(context, MetroImservice.class));
    }
}
