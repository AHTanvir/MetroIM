package anwar.metroim.Manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URLEncoder;

import anwar.metroim.LocalHandeler.DatabaseHandler;
import anwar.metroim.MainActivity;
import anwar.metroim.PhoneContactSynchronization.IphoneContacts;
import anwar.metroim.PhoneContactSynchronization.PhoneContacts;
import anwar.metroim.R;
import anwar.metroim.service.MetroImservice;
import anwar.metroim.service.Iappmanager;

public class UpdaterManagerActivity extends AppCompatActivity {
    private ImageView splash_img;
    private Animation animation;
    protected static final int NOT_CONNECTED_TO_SERVICE = 0;
    protected static final int FILL_BOTH_USERNAME_AND_PASSWORD = 1;
    public static final String NO_NEW_UPDATE="9";
    public static final String AUTHENTICATION_FAILED = "0";
    protected static final int MAKE_SURE_USERNAME_AND_PASSWORD_CORRECT = 2 ;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS =100;
    protected static final int NOT_CONNECTED_TO_NETWORK = 3;
    private DatabaseHandler databaseHandler;
    private Button btn_login,btn_register;
    private TextView login_phone,login_password;
    private Iappmanager man_ger=new MetroImservice();
    public static int count=0;
    public static String ss;
    private static final String TAG = "Contacts";
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    SessionManager session;
    IphoneContacts iphoneContacts=new PhoneContacts();
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            man_ger=((MetroImservice.IMBinder)service).getService();
            update();


        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            man_ger = null;
            Toast.makeText(UpdaterManagerActivity.this, "loooog",
                    Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updater_manager);
        databaseHandler=new DatabaseHandler(this);
        session=new SessionManager(getApplicationContext());
    }
    private void update(){
        new Thread(new Runnable() {
            String result,result1;
            Handler handeler=new Handler();
            @Override
            public void run() {
                try {
                    result1=man_ger.UpdateContacts();
                    System.out.println("Result1 "+result1);
                    result = man_ger.updateInfo( "&getinfo=" + URLEncoder.encode("get","UTF-8")+ "&");
                    System.out.println("Result "+result);
                }catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("R exception "+result1);
                }
                if(result !=null )
                {
                    handeler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONArray array=new JSONArray(result);
                                session.createinfoSession(array.getString(0),array.getString(1),array.getString(2));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Intent intent=new Intent(UpdaterManagerActivity.this, MainActivity.class);
                            finish();
                            startActivity(intent);
                        }
                    });
                }
                else {
                    handeler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Log.d(TAG,result);
                            Toast.makeText(UpdaterManagerActivity.this,result,Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();

    }
    @Override
    protected void onPause()
    {
        unbindService(mConnection);
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        bindService(new Intent(UpdaterManagerActivity.this, MetroImservice.class), mConnection , Context.BIND_AUTO_CREATE);

        super.onResume();
    }
}
