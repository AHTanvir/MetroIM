package anwar.metroim.BloodDonor;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import anwar.metroim.Adapter.RowItem;
import anwar.metroim.Adapter.DonorRecyclerAdapter;
import anwar.metroim.MainActivity;
import anwar.metroim.PhoneContactSynchronization.IphoneContacts;
import anwar.metroim.PhoneContactSynchronization.PhoneContacts;
import anwar.metroim.R;
import anwar.metroim.service.MetroImservice;
import anwar.metroim.service.imanager;

public class donorActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,View.OnClickListener {
    private Spinner spinner,dspinner;
    private AlertDialog dialog;
    private ListView listView;
    private TextView  donorName,donorNumber;
    private String bloodGroup[] = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recycleradapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayAdapter<String> arrayAdapter;
    private spinnerAdapter spinneradapter;
    private LayoutInflater inflater;
    private View alertLayout;
    private DateFormat dataBaseStoreFormate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Calendar calander = Calendar.getInstance();
    private String result;
    private Button backBtn;
    private imanager man_ger = new MetroImservice();
    private  IphoneContacts iphoneContacts=new PhoneContacts();
    private List<RowItem> rowitem = new ArrayList<>();
    Context context;
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            man_ger = ((MetroImservice.IMBinder) service).getService();

        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            man_ger = null;
            Toast.makeText(donorActivity.this, "loooog",
                    Toast.LENGTH_SHORT).show();
        }
    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.donor_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        context=this;
        spinner = (Spinner) findViewById(R.id.toolbar_spinner);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        for (int i = 0; i < 10; i++) {
            RowItem ro = new RowItem("Name", "Contact Number");
            rowitem.add(ro);
        }
        recycleradapter = new DonorRecyclerAdapter(rowitem);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recycleradapter);
        inflater= this.getLayoutInflater();
        addItemOnSpinner(spinner);
        backBtn=(Button)findViewById(R.id.donorActivity_back);
        backBtn.setOnClickListener(this);

    }

    private void addItemOnSpinner(Spinner spinnerr) {
        // spinner.setPrompt("Select Blood Group");
        spinneradapter = new spinnerAdapter(this, R.layout.custom_spinner_item);
        spinneradapter.addAll(bloodGroup);
        spinneradapter.add("Select Blood Group");
        spinneradapter.setDropDownViewResource(R.layout.custom_spinner_item);
        spinnerr.setAdapter(spinneradapter);
        spinnerr.setSelection(spinneradapter.getCount());
        spinnerr.setOnItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(donorActivity.this, MainActivity.class);
        finish();
        startActivity(i);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mConnection);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(donorActivity.this, MetroImservice.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.donor_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_register) {
            ShowRegisterDialog();
            return true;
        }
        if (id == R.id.update_date) {
            ShowDatePickerDialog(donorActivity.this);
            return true;
        }
        if (id == R.id.donor_delete) {
            try {
               String params = "&action=" + URLEncoder.encode("deleteDonor", "UTF-8") +
                        "&";
                sendDonorRequest(params,"deleteDonor");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void ShowDatePickerDialog(Context context) {
        this.context=context;
        DatePickerDialog datePicker=new DatePickerDialog(context,R.style.AppTheme,
                datePickerListener,calander.get(Calendar.YEAR),
                calander.get(Calendar.MONTH),
                calander.get(Calendar.DAY_OF_MONTH));
        datePicker.setCancelable(false);
        datePicker.setTitle("Select donation date");
        datePicker.show();
    }
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        String params=null;
        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            calander.set(selectedYear,selectedMonth,selectedDay);
            try {
                params = "&action=" + URLEncoder.encode("updateDonationDate", "UTF-8") +
                        "&date=" + URLEncoder.encode(dataBaseStoreFormate.format(calander.getTime()).toString(), "UTF-8") +
                         "&";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            sendDonorRequest(params,"updateDonationDate");


        }
    };
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String params=null;
        switch (parent.getId())
        {
            case R.id.dialog_spinner:
                if (!dspinner.getSelectedItem().equals("Select Blood Group"))
                {
                    if (donorName.length()>3 && donorNumber.length()>10)
                    {
                        try {
                            params = "&action=" + URLEncoder.encode("addDonor", "UTF-8") +
                                    "&name=" + URLEncoder.encode(donorName.getText().toString(), "UTF-8") +
                                    "&number=" + URLEncoder.encode(donorNumber.getText().toString(), "UTF-8") +
                                    "&group=" + URLEncoder.encode(dspinner.getSelectedItem().toString(), "UTF-8") + "&";
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        sendDonorRequest(params,"addDonor");
                    }
                    else {
                        Toast.makeText(this,"Name or Number is invalid ", Toast.LENGTH_LONG).show();
                        addItemOnSpinner(dspinner);
                    }
                }
                break;
            case R.id.toolbar_spinner:
                if (!spinner.getSelectedItem().equals("Select Blood Group"))
                {
                    try {
                        params = "&action=" + URLEncoder.encode("donorlist", "UTF-8") +
                                "&group=" + URLEncoder.encode(spinner.getSelectedItem().toString(), "UTF-8") + "&";
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    sendDonorRequest(params,"donorlist");

                }
                break;
        }
        if (spinner.getSelectedItem() == "Select Blood Group") {

            //Do nothing.
        } else {

        }
        //Toast.makeText(this, spinner.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void donorListDecoder(String result) {
        rowitem.clear();
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("donorList");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                //parts[0]=from,parts[1]=sentDate,parts[2]=MessageType,parts[3]=Message
                RowItem ro = new RowItem(jsonObj.getString("name"), jsonObj.getString("number"));
                rowitem.add(ro);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        recycleradapter = new DonorRecyclerAdapter(rowitem);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recycleradapter);
    }

    private void ShowRegisterDialog() {
        LayoutInflater inflater= this.getLayoutInflater();
        alertLayout = inflater.inflate(R.layout.donor_reg_dialog, null);
         donorName = (EditText) alertLayout.findViewById(R.id.reg_donor_name);
        donorNumber= (EditText) alertLayout.findViewById(R.id.reg_donor_phone);
        dspinner=(Spinner)alertLayout.findViewById(R.id.dialog_spinner);
        donorNumber.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                donorNumber.setText(iphoneContacts.getPrefixCountyCode(donorActivity.this));
                return false;
            }
        });
       addItemOnSpinner(dspinner);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Register");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog = alert.create();
        dialog.show();
        dspinner.setOnItemSelectedListener(this);
        /*
        dspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (dspinner.getSelectedItem()== "Select Blood Group"){
                  //  dialog.dismiss();
                }
                else{
                    dialog.dismiss();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        */
        // final Button button=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        //button.setEnabled(false);

    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
private void sendDonorRequest(final String params, final String action){
    result=null;
    final ProgressDialog progressDialog=new ProgressDialog(donorActivity.this);
    progressDialog.setMessage("DOWNLOADING......");
   progressDialog.show();
   final Handler handeler=new Handler();
  final Thread t=new Thread(new Runnable() {
       @Override
       public void run() {
           try {
               result= man_ger.donorOperation(params);
           } catch (UnsupportedEncodingException e) {
               e.printStackTrace();
           }
           handeler.post(new Runnable() {
               @Override
               public void run() {
                   progressDialog.dismiss();
                 if(!result.equals("0"))
                 {
                     switch (action)
                     {
                         case  "donorlist":
                             donorListDecoder(result);
                             addItemOnSpinner(spinner);
                             break;
                         case "addDonor":
                             if(result.equals("5"))
                                 Toast.makeText(context,"NUMBER ALREADY USED", Toast.LENGTH_LONG).show();
                             else    Toast.makeText(context,"SUCCESSFUL", Toast.LENGTH_LONG).show();
                             dialog.dismiss();
                             break;
                         case "updateDonationDate":
                             Toast.makeText(context,"SUCCESSFUL", Toast.LENGTH_LONG).show();
                             break;
                         case "deleteDonor":
                             Toast.makeText(context,"SUCCESSFUL"+result, Toast.LENGTH_LONG).show();
                             break;
                     }
                 }else  Toast.makeText(context,"FAILED", Toast.LENGTH_LONG).show();
               }
           });

       }
   });
    t.start();
}

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.donorActivity_back:
                Toast.makeText(this,"back",Toast.LENGTH_SHORT).show();
                this.onBackPressed();
                break;

        }
    }
}
