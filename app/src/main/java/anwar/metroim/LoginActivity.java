package anwar.metroim;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.helpers.LocatorImpl;

import java.io.UnsupportedEncodingException;
import java.security.Permission;

import anwar.metroim.Manager.MultiplePermissions;
import anwar.metroim.Manager.SessionManager;
import anwar.metroim.Manager.UpdaterManagerActivity;
import anwar.metroim.PhoneContactSynchronization.IphoneContacts;
import anwar.metroim.PhoneContactSynchronization.PhoneContacts;
import anwar.metroim.service.BootBroadcast;
import anwar.metroim.service.MetroImservice;
import anwar.metroim.service.imanager;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    protected static final int NOT_CONNECTED_TO_SERVICE = 0;
    protected static final int FILL_BOTH_USERNAME_AND_PASSWORD = 1;
    public static final String NO_NEW_UPDATE="9";
    public static final String AUTHENTICATION_FAILED = "0";
    protected static final int MAKE_SURE_USERNAME_AND_PASSWORD_CORRECT = 2 ;
    private static final int REQUEST_CODE_READ_SMS=100;
    protected static final int NOT_CONNECTED_TO_NETWORK = 3;
    public static final int REQUEST_CODE = 121;

    private Button btn_login,btn_register;
    private TextView login_phone,login_password;
    Context context;
    private imanager man_ger=new MetroImservice();
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
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            man_ger= null;
            Toast.makeText(LoginActivity.this, "Service Disconnected ",
                    Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session=new SessionManager(getApplicationContext());
        setContentView(R.layout.activity_login);
        context=this.getApplicationContext();
        login_phone=(TextView)findViewById(R.id.login_phone);
        login_password=(TextView)findViewById(R.id.login_password);
        btn_login=(Button)findViewById(R.id.btn_login);
        btn_register=(Button)findViewById(R.id.btn_register);
        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        login_phone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                login_phone.setText(iphoneContacts.getPrefixCountyCode(context));
                return false;
            }
        });
      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            Intent intent=new Intent(LoginActivity.this, MultiplePermissions.class);
            startActivityForResult(intent, REQUEST_CODE);
        }*/
    }
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                if(login_phone.getText().length()>0 && login_password.length()>0)
                {
                    if(man_ger.isNetworkConnected())
                    {
                        Thread loginThread = new Thread() {
                            Handler loginHandeler = new Handler();
                            String result = new String();
                            String result1;

                            public void run() {
                                try {
                                    result = man_ger.authenticateUser(login_phone.getText().toString(),login_password.getText().toString());

                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                if (!result.equals(AUTHENTICATION_FAILED) ) {
                                    loginHandeler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(context,result,Toast.LENGTH_SHORT).show();
                                            session.createLoginSession(login_phone.getText().toString(),login_password.getText().toString());
                                            Intent i=new Intent(LoginActivity.this,UpdaterManagerActivity.class);
                                            startService(new Intent(LoginActivity.this,MetroImservice.class));
                                            finish();
                                            startActivity(i);
                                        }
                                    });
                                }
                                else {
                                    loginHandeler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(context,"Make sure Phone and Password correct",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        };
                        loginThread.start();
                    }else Toast.makeText(LoginActivity.this,"Not Conected to Inertnet",Toast.LENGTH_SHORT).show();
                }else Toast.makeText(LoginActivity.this,"Phone or Passwprd can not be blank",Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_register:
               Intent ii = new Intent(LoginActivity.this,RegisterActivity.class);
                finish();
                startActivity(ii);
        }

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
        bindService(new Intent(LoginActivity.this, MetroImservice.class), mConnection , Context.BIND_AUTO_CREATE);

        super.onResume();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode== Activity.RESULT_OK)
        {
            if (requestCode==REQUEST_CODE)
            {
                String result=data.getStringExtra("Status");
                if(result=="Denied")
                {
                    finish();
                }
            }
        }
    }
}
