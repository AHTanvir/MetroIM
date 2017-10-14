package anwar.metroim;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.util.PatternsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
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
import java.util.regex.Pattern;

import anwar.metroim.PhoneContactSynchronization.IphoneContacts;
import anwar.metroim.PhoneContactSynchronization.PhoneContacts;
import anwar.metroim.service.*;

import static anwar.metroim.Constant.ID_PATTERN;
import static anwar.metroim.Constant.PHONE_PATTERN;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemSelectedListener,TextWatcher,View.OnFocusChangeListener {
    Context context;
    public static String va="register";
    private View alertLayout;
    private AlertDialog dialog;
    private  Spinner spinner_type;
    private String vCode="1216";
    private String batch=null;
    private ArrayAdapter adapter;
    private String dept=null;
    private AutoCompleteTextView tf_batch;
    private AutoCompleteTextView tf_dept;
    private Button btn_singup,resend,ok;
    private EditText tf_name,tf_email,tf_password,tf_id,tf_phone,veri_code;
    private Iappmanager man_ger;
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
        tf_name.setOnFocusChangeListener(this);
        tf_email.setOnFocusChangeListener(this);
        tf_batch.setOnFocusChangeListener(this);
        tf_password.setOnFocusChangeListener(this);
        tf_phone.setOnFocusChangeListener(this);
        tf_id.setOnFocusChangeListener(this);
        adapter=new ArrayAdapter<>(this,R.layout.custom_spinner_item, deptList);
        tf_dept.setOnFocusChangeListener(this);
    }


    public void addItemOnSpinner(){
        List<String>list=new ArrayList<>();
        list.add("Student");
        list.add("Teacher");
        ArrayAdapter adapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,list);
        adapter.setDropDownViewResource(R.layout.custom_spinner_item);
        spinner_type.setAdapter(adapter);
    }
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(spinner_type.getSelectedItem().equals("Teacher")) {
           // tf_dept.setVisibility(View.GONE);
            tf_batch.setVisibility(View.GONE);
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
       switch (view.getId()) {
           case R.id.btn_singup:
               if(isValid()) {
                    ShowDialog();
                } else {
                   Toast.makeText(getApplicationContext(), "Phone or Id Invalid", Toast.LENGTH_SHORT).show();
               }
               break;
           case R.id.verificatonbtn:
               if(veri_code.getText().toString().equals(vCode)){
                   dialog.dismiss();
                   if(spinner_type.getSelectedItem().equals("Teacher")) {
                       batch=tf_dept.getText().toString();
                       dept="Teacher";
                   }else {
                       batch=tf_batch.getText().toString();
                       dept=tf_dept.getText().toString();;
                   }
                   final ProgressDialog progressDialog= new ProgressDialog(this,R.style.MyAlertDialogThemeDatePicker);
                   progressDialog.setIndeterminate(false);
                   progressDialog.setMessage("Processing...");
                   progressDialog.show();
                   Thread thread=new Thread(){
                       String result=new String();
                       @Override
                       public void run(){
                           try {
                               String id=(tf_id.getText().toString()).replace("-","");
                               result=man_ger.signUpUser(tf_name.getText().toString(),tf_email.getText().toString(),
                                       tf_password.getText().toString(),dept.toLowerCase(),batch,id,tf_phone.getText().
                                               toString());
                           } catch (UnsupportedEncodingException e) {
                               e.printStackTrace();
                           }
                               handler.post(new Runnable(){
                                   public void run() {
                                       progressDialog.dismiss();
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
                                       System.out.println("reg res "+result);
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
        veri_code.setText("1216");
        ok= (Button) alertLayout.findViewById(R.id.verificatonbtn);
        resend= (Button) alertLayout.findViewById(R.id.resendbtn);
        ok.setOnClickListener(this);
        resend.setOnClickListener(this);
        AlertDialog.Builder alert = new AlertDialog.Builder(this,R.style.MyAlertDialogTheme);
        alert.setView(alertLayout);
        alert.setCancelable(true);
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
                if(hasFocus) {
                    tf_dept.setAdapter(adapter);
                    tf_dept.showDropDown();
                }
                else if(tf_dept.getText().length()<2)
                    tf_dept.setError("Select Department");
                break;
            case R.id.tf_phone:
                if (hasFocus)
                    tf_phone.setText(iphoneContacts.getPrefixCountyCode(context));
                else if (!hasFocus){
                    if(!tf_phone.getText().toString().matches(PHONE_PATTERN))
                        tf_phone.setError("Invalid Phone Number");

                }
                break;
            case R.id.tf_name:
                if(!hasFocus && tf_name.length()<5)
                    tf_name.setError("Too short");
                break;
            case R.id.tf_email:
                if(!hasFocus && !tf_email.getText().toString().matches(PatternsCompat.EMAIL_ADDRESS.pattern()))
                    tf_email.setError("Invalid email");
                break;
            case R.id.tf_password:
                if(!hasFocus && tf_password.length()<5)
                    tf_password.setError("Passsword length must be grather then 4");
                break;
            case R.id.tf_id:
                if(!hasFocus && !tf_id.getText().toString().matches(ID_PATTERN))
                    tf_id.setError("Invalid id! e,g:123-123-0");
                break;
            case R.id.tf_batch:
                if(!hasFocus && !tf_batch.getText().toString().matches("\\d{1,3}"))
                    tf_batch.setError("Invalid! Only digits");
                break;
        }
    }
    public boolean isValid(){
        boolean isOk=true;
        if(!tf_phone.getText().toString().matches(PHONE_PATTERN)) {
            isOk=false;
            tf_phone.setError("Invalid Phone Number");
        }
        if(tf_name.length()<5){
            isOk=false;
            tf_name.setError("Too short");
        }
        if(!tf_email.getText().toString().matches(PatternsCompat.EMAIL_ADDRESS.pattern())) {
            isOk=false;
            tf_email.setError("Invalid email");
        }
        if(tf_password.length()<4) {
            isOk=false;
            tf_password.setError("Passsword length must be grather then 4");
        }
        if(!tf_id.getText().toString().matches(ID_PATTERN)){
            isOk=false;
            tf_id.setError("Invalid id! e,g:123-123-1");
        }
        if(tf_batch.isShown() &&!tf_batch.getText().toString().matches("\\d{1,3}")) {
            isOk=false;
            tf_batch.setError("Invalid! Only digits");
        }
        if (tf_dept.getText().length()<2){
            isOk=false;
            tf_dept.setError("Select Department");
        }
        return isOk;
    }
}
