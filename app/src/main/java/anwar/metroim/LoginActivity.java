package anwar.metroim;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

import anwar.metroim.Manager.SessionManager;
import anwar.metroim.Manager.UpdaterManagerActivity;
import anwar.metroim.PhoneContactSynchronization.IphoneContacts;
import anwar.metroim.PhoneContactSynchronization.PhoneContacts;
import anwar.metroim.service.MetroImservice;
import anwar.metroim.service.Iappmanager;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,View.OnTouchListener {
    public static final String NO_NEW_UPDATE="9";
    public static final String AUTHENTICATION_FAILED = "0";
    public static final int REQUEST_CODE = 121;
    private Button btn_login,btn_register,reset_passBtn,reset_cancelBtn;
    private EditText login_phone,login_password,resetphone;
    private TextView tv_forgetpassword;
    private Context context;
    private boolean visibility=false;
    private Iappmanager man_ger=new MetroImservice();
    private SessionManager session;
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
        //EditText
        login_phone=(EditText)findViewById(R.id.login_phone);
        login_password=(EditText)findViewById(R.id.login_password);
        resetphone=(EditText)findViewById(R.id.resetphone);
        login_phone.setOnTouchListener(this);
        resetphone.setOnTouchListener(this);
        //TextView
        tv_forgetpassword=(TextView)findViewById(R.id.tv_forgetpassword);
        tv_forgetpassword.setOnClickListener(this);
        //Button
        btn_login=(Button)findViewById(R.id.btn_login);
        btn_register=(Button)findViewById(R.id.btn_register);
        reset_passBtn= (Button) findViewById(R.id.reset_passBtn);
        reset_cancelBtn= (Button) findViewById(R.id.reset_cancelBtn);
        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        reset_passBtn.setOnClickListener(this);
        reset_cancelBtn.setOnClickListener(this);

        // For android 6 or above version i just set  targetSdkVersion 22 so android os manage all dangerous permisson
      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            Intent intent=new Intent(LoginActivity.this, MultiplePermissions.class);
            startActivityForResult(intent, REQUEST_CODE);
        }*/
    }
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_register:
               Intent ii = new Intent(LoginActivity.this,RegisterActivity.class);
                finish();
                startActivity(ii);
                break;
            case R.id.tv_forgetpassword:
                hide();
                break;
            case R.id.reset_cancelBtn:
                hide();
                break;
            case R.id.reset_passBtn:
                if(resetphone.getText().length()>10)
                {
                    reset_passBtn.setClickable(false);
                    reset_cancelBtn.setClickable(false);
                    new Thread(new Runnable() {
                        Handler handeler=new Handler();
                        String result = null;
                        @Override
                        public void run() {
                            try {
                                result=man_ger.resetPassword(resetphone.getText(). toString());
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            handeler.post(new Runnable() {
                                @Override
                                public void run() {
                                    reset_passBtn.setClickable(true);
                                    reset_cancelBtn.setClickable(true);
                                    if(result.equals("1"))
                                    {
                                        hide();
                                        Toast.makeText(LoginActivity.this,"NEW PASSWORD SEND VIA SMS",Toast.LENGTH_LONG).show();
                                    }
                                    else Toast.makeText(LoginActivity.this,"MAKE SURE PHONE NUMBER CORRECT",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).start();
                }
                break;
        }

    }

    private void hide() {
        if(!visibility){
            login_phone.setVisibility(View.GONE);
            login_password.setVisibility(View.GONE);
            btn_login.setVisibility(View.GONE);
            btn_register.setVisibility(View.GONE);
            tv_forgetpassword.setVisibility(View.GONE);
            resetphone.setVisibility(View.VISIBLE);
            reset_passBtn.setVisibility(View.VISIBLE);
            reset_cancelBtn.setVisibility(View.VISIBLE);
            visibility=true;
        }
        else {
            login_phone.setVisibility(View.VISIBLE);
            login_password.setVisibility(View.VISIBLE);
            btn_login.setVisibility(View.VISIBLE);
            btn_register.setVisibility(View.VISIBLE);
            tv_forgetpassword.setVisibility(View.GONE);
            resetphone.setVisibility(View.GONE);
            reset_passBtn.setVisibility(View.GONE);
            reset_cancelBtn.setVisibility(View.GONE);
            visibility=false;
        }
    }

    private void login() {
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
                                    //  Toast.makeText(context,result,Toast.LENGTH_SHORT).show();
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
                                    tv_forgetpassword.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    }
                };
                loginThread.start();
            }else Toast.makeText(LoginActivity.this,"Not Conected to Inertnet",Toast.LENGTH_SHORT).show();
        }else Toast.makeText(LoginActivity.this,"Phone or Passwprd can not be blank",Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.login_phone:
                login_phone.setText(iphoneContacts.getPrefixCountyCode(context));
                break;
            case R.id.resetphone:
                resetphone.setText(iphoneContacts.getPrefixCountyCode(context));
                break;
        }
        return false;
    }
}
