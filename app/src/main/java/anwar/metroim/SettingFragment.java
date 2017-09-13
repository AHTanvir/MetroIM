package anwar.metroim;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import anwar.metroim.Backup.DbBackup;
import anwar.metroim.Adapter.arrayList;
import anwar.metroim.LocalHandeler.DatabaseHandler;
import anwar.metroim.Manager.SessionManager;
import anwar.metroim.service.MetroImservice;
import anwar.metroim.service.imanager;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private imanager man_ger;
    private static final int PICKEMAIL=5;
    private   String result=null,backupEmail;
    private Button backBtn,changNameBtn,changPassBtn,deleteAccu,add_backup_email,restoreBackup;
    private EditText newName,oldPassword,newPassword;
    private TextView current_bakup_email,connected_email;
    private SessionManager session;
    private HashMap<String, String> user;
    private HashMap<String, String> userlogin;
    private OnFragmentInteractionListener mListener;
    private  SharedPreferences spref;
    private boolean isServiceBound=false;
    private DbBackup dbBackup;
    private ServiceConnection mConnection = new ServiceConnection() {


        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            man_ger = ((MetroImservice.IMBinder) service).getService();
            backupEmail=man_ger.getBackupEmail();
            Toast.makeText(getActivity(),backupEmail, Toast.LENGTH_SHORT).show();
            isServiceBound=true;

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

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view= inflater.inflate(R.layout.fragment_setting, container, false);
        man_ger=new MetroImservice();
        session=new SessionManager(getActivity());
         user = session.getUserDetails();
        //button
        backBtn=(Button)view.findViewById(R.id.settingFrag_back);
        changNameBtn=(Button)view.findViewById(R.id.change_nameBtn);
        changPassBtn=(Button)view.findViewById(R.id.change_passBtn);
        deleteAccu=(Button)view.findViewById(R.id.delete_accu);
        add_backup_email= (Button) view.findViewById(R.id.add_backup_email);
        restoreBackup= (Button) view.findViewById(R.id.RestoreBackup);
        backBtn.setOnClickListener(this);
        changNameBtn.setOnClickListener(this);
        changPassBtn.setOnClickListener(this);
        deleteAccu.setOnClickListener(this);
        add_backup_email.setOnClickListener(this);
        restoreBackup.setOnClickListener(this);
        //EditText
        newName=(EditText)view.findViewById(R.id.new_name);
        oldPassword=(EditText)view.findViewById(R.id.old_password);
        newPassword=(EditText)view.findViewById(R.id.new_password) ;
        //TextView
        current_bakup_email= (TextView) view.findViewById(R.id.current_bakup_email);
        connected_email= (TextView) view.findViewById(R.id.connected_email);
        SharedPreferences spref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if(spref.contains("email"))
        {
            backupEmail=spref.getString("email","");
            add_backup_email.setText("CHANGE EMAIL");
            connected_email.setText(backupEmail);
            current_bakup_email.setVisibility(View.VISIBLE);
            connected_email.setVisibility(View.VISIBLE);
            restoreBackup.setVisibility(View.VISIBLE);
        }
        /*
        man_ger=new MetroImservice();
        final Thread imageSaveThread = new Thread() {
            Handler imageSaveHandeler = new Handler();
            String result=new String();
            public void run() {
                man_ger.isNetworkConnected();
                //result = man_ger.UpdateContacts("fff");
                imageSaveHandeler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),"jfgjjh", Toast.LENGTH_LONG).show();
                        //tt(result);
                    }
                });

            }
        };
        imageSaveThread.start();
        */
        return view;
    }
    public void onPause() {
        super.onPause();
       getActivity().unbindService(mConnection);
    }

    @Override
  public void onResume() {
        super.onResume();
        getActivity().bindService(new Intent(getActivity(), MetroImservice.class), mConnection, Context.BIND_AUTO_CREATE);
        ((MainActivity)getActivity()).getSupportActionBar().hide();

    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        HashMap<String, String> user = session.getUserDetails();
        switch (v.getId())
        {
            case R.id.change_nameBtn:
                if(changNameBtn.getText().equals("Change Name"))
                {
                    newName.setVisibility(View.VISIBLE);
                    changNameBtn.setText("Change");
                    oldPassword.setVisibility(View.GONE);
                    newPassword.setVisibility(View.GONE);
                    changPassBtn.setText("Change Password");
                }else {
                    if(newName.length()>3)
                    {
                        sendReq("&updateName=",newName.getText().toString());
                    }
                }
                break;
            case R.id.change_passBtn:
                if(changPassBtn.getText().equals("Change Password"))
                {
                    oldPassword.setVisibility(View.VISIBLE);
                    newPassword.setVisibility(View.VISIBLE);
                    changPassBtn.setText("Change");
                    newName.setVisibility(View.GONE);
                    changNameBtn.setText("Change Name");
                }
                else if(oldPassword.getText().toString().equals(user.get(SessionManager.KEY_PASSWORD)))
                {
                    if(newPassword.length()>4){
                        sendReq("&updatePassword=",newPassword.getText().toString());
                    }
                    else Toast.makeText(getActivity(),"Enter long password",Toast.LENGTH_LONG).show();
                }
                else Toast.makeText(getActivity(),"Wrong password",Toast.LENGTH_LONG).show();
                break;
            case R.id.delete_accu:
                sendReq("&deleteaccount=","remove");
                break;
            case R.id.add_backup_email:
                if(man_ger.isNetworkConnected())
                {
                    getActivity().startActivityForResult(AccountPicker.newChooseAccountIntent(null,
                            null, new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, true, null, null, null, null),
                            PICKEMAIL);
                }else Toast.makeText(getActivity(),"Not Conected to Inertnet",Toast.LENGTH_SHORT).show();
                break;
            case R.id.RestoreBackup:
                if(man_ger.isNetworkConnected())
                {
                    final Handler handler=new Handler();
                    dbBackup=new DbBackup(getActivity(), man_ger.getBackupEmail(), new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            new Thread(new Runnable() {
                                boolean  result;
                                @Override
                                public void run() {
                                  result=dbBackup.RestoreBackup();
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(result){
                                                Toast.makeText(getActivity(),"Restore Successful",Toast.LENGTH_SHORT).show();
                                                arrayList.getmInstance().setChatlist(new DatabaseHandler(getActivity()).getViewForChatFrag());
                                            } else Toast.makeText(getActivity(),"Restore Failed",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).start();
                        }
                        @Override
                        public void onConnectionSuspended(int i) {

                        }
                    });
                } else Toast.makeText(getActivity(),"Not Conected to Inertnet",Toast.LENGTH_SHORT).show();
                break;
            case R.id.settingFrag_back:
                ((AppCompatActivity) getActivity()).onBackPressed();
                break;
        }

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void sendReq(final String act, final String value){
        result=null;
        if(man_ger.isNetworkConnected())
        {
            new Thread(new Runnable() {
                Handler handler=new Handler();
                public void run() {
                    try {
                        result=man_ger.updateInfo(act + URLEncoder.encode(value,"UTF-8")+ "&");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if(result.equals("1"))
                    {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(),"SUCCESSFUL",Toast.LENGTH_LONG).show();
                                if(act.equals("&updatePassword="))
                                {
                                    session.createLoginSession(user.get(SessionManager.KEY_PHONE),value);
                                    oldPassword.setVisibility(View.GONE);
                                    newPassword.setVisibility(View.GONE);
                                }
                                else if(act.equals("&deleteaccount=")) {
                                    session.logoutUser();
                                    man_ger.exit();
                                }
                                else if(act.equals("&updateName=")) {
                                    session.createinfoSession(value,user.get(SessionManager.KEY_STATUS),user.get(SessionManager.KEY_PHOTO));
                                    newName.setVisibility(View.GONE);
                                    ((MainActivity)getActivity()). NavView();
                                }
                            }
                        });

                    }else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(),"FAILED"+result,Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).start();
        }
        else Toast.makeText(getActivity(),"Not Conected to Inertnet",Toast.LENGTH_SHORT).show();
    }

}
