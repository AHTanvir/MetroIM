package anwar.metroim.Backup;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by anwar on 4/4/2017.
 */

public class Backup_email {
    private Context context;
   private SharedPreferences pfs;
    private String Email="email";
    private String BACKUPDATE="date";
    private SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public Backup_email(Context context) {
        this.context = context.getApplicationContext();
        pfs= PreferenceManager.getDefaultSharedPreferences(context);
    }
    public void addEmail(String email){
        pfs.edit().putString(Email,email).apply();
        System.out.println("Backup_email new Backup Email added"+email);
    }
    public void addschedule(Date date){
        pfs.edit().putString(BACKUPDATE,sdf.format(date)).apply();
        System.out.println("Backup_email new Backup schedule added"+date);
    }
    public void RemoveEmail(){
        pfs.edit().clear().apply();
    }
}
