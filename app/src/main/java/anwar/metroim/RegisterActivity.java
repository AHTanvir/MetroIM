package anwar.metroim;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import anwar.metroim.BloodDonor.donorActivity;
import anwar.metroim.PhoneContactSynchronization.IphoneContacts;
import anwar.metroim.PhoneContactSynchronization.PhoneContacts;
import anwar.metroim.service.*;



public class RegisterActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemSelectedListener,TextWatcher,View.OnFocusChangeListener {
    Context context;
    public static String va="register";
    private View alertLayout;
    private AlertDialog dialog;
    private  Spinner spinner_type;
    private String vCode;
    private ArrayAdapter adapter;
    private AutoCompleteTextView tf_batch;
    private AutoCompleteTextView tf_dept;
    private Button btn_singup,resend,ok;
    private EditText tf_name,tf_email,tf_password,tf_id,tf_phone,veri_code;
    private imanager man_ger;
    private Handler handler = new Handler();
    private String deptList[]=new String[]{"CSE","EEE","BBA","LLB","EC0"};
    private IphoneContacts iphoneContacts=new PhoneContacts();
    private ServiceConnection mConnection = new ServiceConnection() {


        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            man_ger= ((MetroImservice.IMBinder)service).getService();


        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
           // man_ger= null;
          //  Toast.makeText(RegisterActivity.this, "disconn", Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        context = this;
        btn_singup = (Button) findViewById(R.id.btn_singup);
        tf_name = (EditText) findViewById(R.id.tf_name);
        tf_email = (EditText) findViewById(R.id.tf_email);
        tf_password = (EditText) findViewById(R.id.tf_password);
        tf_id = (EditText) findViewById(R.id.tf_id);
        tf_phone = (EditText) findViewById(R.id.tf_phone);
        tf_dept = (AutoCompleteTextView) findViewById(R.id.tf_dept);
        tf_batch = (AutoCompleteTextView) findViewById(R.id.tf_batch);
        spinner_type = (Spinner) findViewById(R.id.spinner_type);
        btn_singup.setOnClickListener(this);
        tf_dept.addTextChangedListener(this);
        addItemOnSpinner();
        spinner_type.setOnItemSelectedListener(this);
        tf_phone.setOnFocusChangeListener(this);
        adapter=new ArrayAdapter<>(this,R.layout.list_item, deptList);
        tf_dept.setOnFocusChangeListener(this);
    }


    public void addItemOnSpinner(){
        List<String>list=new ArrayList<>();
        list.add("Student");
        list.add("Teacher");
        ArrayAdapter adapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_type.setAdapter(adapter);
    }
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(spinner_type.getSelectedItem().equals("Teacher")) {
           // tf_dept.setVisibility(View.GONE);
            tf_batch.setVisibility(View.GONE);
            tf_batch.setText("Teacher");
        }
        else {
           // tf_dept.setVisibility(view.VISIBLE);
            tf_batch.setVisibility(View.VISIBLE);
            tf_batch.setText("");
        }
    }
    public void onNothingSelected(AdapterView<?> arg0){
        Toast.makeText(RegisterActivity.this, "NothingSelected", Toast.LENGTH_SHORT).show();

    }

    public void onClick(View view){
       switch (view.getId())
       {
           case R.id.btn_singup:
               if(tf_phone.length()>=0 && tf_id.length()>=0 && tf_password.length()>=0 ) {
                    ShowDialog();
                }
               else {
                   Toast.makeText(getApplicationContext(), "Phone or Id ", Toast.LENGTH_SHORT).show();
               }
               break;
           case R.id.verificatonbtn:
               if(veri_code.getText().toString().equals(vCode))
               {
                   dialog.dismiss();
                   if(tf_batch.getText().toString().equals("Teacher")) {
                       tf_batch.setText(tf_dept.getText().toString());
                       tf_dept.setText("Teacher");
                   }
                   Thread thread=new Thread(){
                       String result=new String();
                       @Override
                       public void run(){
                           try {
                               result=man_ger.signUpUser(tf_name.getText().toString(),tf_email.getText().toString(),
                                       tf_password.getText().toString(),tf_dept.getText().toString(), tf_batch.getText().
                                               toString(),tf_id.getText().toString(),tf_phone.getText().
                                               toString());
                           } catch (UnsupportedEncodingException e) {
                               e.printStackTrace();
                           }
                               handler.post(new Runnable(){
                                   public void run() {
                                       if(result .equals("1")) {
                                           Toast.makeText(RegisterActivity.this,"SUCCESFULL", Toast.LENGTH_LONG).show();
                                           Intent i=new Intent(RegisterActivity.this,LoginActivity.class);
                                           finish();
                                           startActivity(i);
                                       }
                                       else if (result .equals("5")) {
                                           Toast.makeText(RegisterActivity.this,"Phone number already used", Toast.LENGTH_LONG).show();
                                       }
                                       else  Toast.makeText(RegisterActivity.this,"FAILED", Toast.LENGTH_LONG).show();
                                   }

                               });
                       }
                   };
                   thread.start();
               }else Toast.makeText(RegisterActivity.this, "Wrong code", Toast.LENGTH_LONG).show();
               break;
           case R.id.resendbtn:
               new Thread(new Runnable() {
                   final Handler h=new Handler();
                   @Override
                   public void run() {
                       vCode =man_ger.send_Sms(tf_phone.getText().toString());
                       h.post(new Runnable() {
                           @Override
                           public void run() {
                               if(vCode.equals("0")) {
                                   Toast.makeText(getApplicationContext(), "Make sure  Phone number is correct and try Again", Toast.LENGTH_SHORT).show();
                               }
                           }
                       });
                   }
               }).start();
               break;

       }

    }
    @Override
    protected void onResume() {
       bindService(new Intent(RegisterActivity.this, MetroImservice.class), mConnection , Context.BIND_AUTO_CREATE);

        super.onResume();
    }

    @Override
    protected void onPause()
    {
       unbindService(mConnection);
        super.onPause();
    }
    private void ShowDialog(){
        LayoutInflater inflater= this.getLayoutInflater();
        alertLayout = inflater.inflate(R.layout.sms_veri, null);
       veri_code = (EditText) alertLayout.findViewById(R.id.veri_code);
        ok= (Button) alertLayout.findViewById(R.id.verificatonbtn);
        resend= (Button) alertLayout.findViewById(R.id.resendbtn);
        ok.setOnClickListener(this);
        resend.setOnClickListener(this);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Verification");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog = alert.create();
        dialog.show();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
 //      adapter=new ArrayAdapter<>(this,R.layout.list_item, deptList);
        tf_dept.setAdapter(adapter);
        tf_dept.showDropDown();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()){
            case R.id.tf_dept:
                tf_dept.setAdapter(adapter);
                tf_dept.showDropDown();
                break;
            case R.id.tf_phone:
                tf_phone.setText(iphoneContacts.getPrefixCountyCode(context));
                break;
        }
    }
}
