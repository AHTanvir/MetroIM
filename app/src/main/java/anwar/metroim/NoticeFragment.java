package anwar.metroim;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.IntegerRes;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.regex.Pattern;
import anwar.metroim.Adapter.NoticeAdapter;
import anwar.metroim.Adapter.arrayList;
import anwar.metroim.BloodDonor.spinnerAdapter;
import anwar.metroim.ChatScreen.ChatListActivity;
import anwar.metroim.CustomImage.CompressImage;
import anwar.metroim.PhoneContactSynchronization.StoreContacts;
import anwar.metroim.Utill.DocumentViewerActivity;

import static anwar.metroim.Constant.FLOAT_PATTERN;
import static anwar.metroim.Constant.ID_PATTERN;
import static anwar.metroim.Constant.REQUEST_CODE_PDF;
import static anwar.metroim.Constant.REQUEST_CODE_PICTURE;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NoticeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoticeFragment extends Fragment implements AdapterView.OnItemSelectedListener,View.OnClickListener ,View.OnFocusChangeListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String notice_type[] = {"Text", "Image", "Pdf"};
    private String dept_list[] = {"CSE", "BBA","EEE","IT"};
    private Spinner type;
    private String msg=null;
    private Spinner dept;
    private EditText fr_id,se_id,batch,text_msg;
    private Button noticeSend;
    private String toDept,toBatch,mtype;
    private String path=null;
    private NoticeAdapter typeAdapter;
    private spinnerAdapter deptAdapter;
    private TextInputLayout inputLayout;
    //private ProgressDialog progressDialog;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private boolean isOk;
    private ProgressDialog progressDialog;

    public NoticeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NoticeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NoticeFragment newInstance(String param1, String param2) {
        NoticeFragment fragment = new NoticeFragment();
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
        View view= inflater.inflate(R.layout.fragment_notice, container, false);
        noticeSend=(Button)view.findViewById(R.id.notice_send);
        inputLayout=(TextInputLayout) view.findViewById(R.id.msg_Input_layout);
        fr_id=(EditText) view.findViewById(R.id.f_to_id);
        se_id=(EditText) view.findViewById(R.id.s_to_id);
        text_msg=(EditText)view.findViewById(R.id.text_notice);
        batch=(EditText) view.findViewById(R.id.batch);
        type=(Spinner)view.findViewById(R.id.notice_type);
        dept=(Spinner)view.findViewById(R.id.dept_list);
        se_id.setOnFocusChangeListener(this);
        addItemOnTypeSpinner();
        addItemOnDeptSpinner();
        noticeSend.setOnClickListener(this);
        progressDialog= new ProgressDialog(getActivity(),R.style.MyAlertDialogThemeDatePicker);
        progressDialog.setIndeterminate(false);
        progressDialog.setMessage("Sending...");
        return view;
    }
    private void addItemOnTypeSpinner() {
        typeAdapter = new NoticeAdapter(getActivity(), R.layout.custom_spinner_item);
        typeAdapter.addAll(notice_type);
        typeAdapter.add("Select Notice Type");
        typeAdapter.setDropDownViewResource(R.layout.custom_spinner_item);
        type.setAdapter(typeAdapter);
        type.setSelection(typeAdapter.getCount());
        type.setOnItemSelectedListener(this);
    }
    private void addItemOnDeptSpinner() {
        deptAdapter = new spinnerAdapter(getActivity(), R.layout.custom_spinner_item);
        deptAdapter.addAll(dept_list);
        deptAdapter.add("Select Department");
        deptAdapter.setDropDownViewResource(R.layout.custom_spinner_item);
        dept.setAdapter(deptAdapter);
        dept.setSelection(deptAdapter.getCount());
        dept.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId())
        {
            case R.id.notice_type:
                if (!type.getSelectedItem().equals("Select Notice Type")) {
                    inputLayout.setVisibility(View.GONE);
                    if(type.getSelectedItem().equals("Pdf")){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mtype="pdf";
                                new MaterialFilePicker()
                                        .withActivity(getActivity())
                                        .withRequestCode(REQUEST_CODE_PDF)
                                        .withFilter(Pattern.compile(".*\\.pdf$"))
                                        .withFilterDirectories(false)
                                        .withHiddenFiles(true)
                                        .start();
                            }
                        },100);
                    }else if(type.getSelectedItem().equals("Image")){
                        mtype="Image";
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                            Intent Pick_image_intent=new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            Pick_image_intent.addCategory(Intent.CATEGORY_OPENABLE);
                            Pick_image_intent.setType("image/*");
                            String pickTitel="Select Photo";
                            Intent chooseIntent=Intent.createChooser(Pick_image_intent,pickTitel);
                            startActivityForResult(chooseIntent,REQUEST_CODE_PICTURE);
                        }
                        else{
                            Intent Pick_image_intent=new Intent();
                            Pick_image_intent.setType("image/*");
                            Pick_image_intent.setAction(Intent.ACTION_GET_CONTENT);
                            String pickTitel="Select Photo";
                            Intent chooseIntent=Intent.createChooser(Pick_image_intent,pickTitel);
                            startActivityForResult(chooseIntent,REQUEST_CODE_PICTURE);
                        }
                    }else {
                        inputLayout.setVisibility(View.VISIBLE);
                        mtype="text";
                    }
                }
                break;
            case R.id.dept_list:
                if (!dept.getSelectedItem().equals("Select Department")) {
                    toDept=dept.getSelectedItem().toString();
                }   else {
                    //Toast.makeText(getActivity(),"Please select again", Toast.LENGTH_LONG).show();
                    //addItemOnDeptSpinner();
                }
                if(path==null)
                    type.setSelection(typeAdapter.getCount());
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.notice_send:
                if(isValid()){
                    if(path!=null)
                        msg=path;
                    else if(text_msg.getText()!=null)
                        msg=text_msg.getText().toString();
                    else text_msg.setError("Message length 0");
                    noticeSend.setEnabled(false);
                    final Handler han=new Handler();
                        progressDialog.show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String id=jsonIdEncoding(fr_id.getText().toString(),se_id.getText().toString());
                                System.out.println("json array "+id);
                                final String res=((MainActivity)getActivity()).sendNotice(toDept.toLowerCase(),id,msg,mtype);
                                han.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(res.equals("1")) {
                                            Toast.makeText(getActivity(), "Send", Toast.LENGTH_SHORT).show();
                                            ((MainActivity)getActivity()).onBackPressed();
                                        }
                                        else Toast.makeText(getActivity(),"Faild "+res,Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        System.out.println("reslut "+res);
                                        noticeSend.setEnabled(true);
                                    }
                                });
                            }
                        }).start();
                }else fr_id.setError("Invalid Id",getResources().getDrawable(R.color.colorAccent));
                break;
        }
    }
    public void onActivityResult(int requestCode, final int resultCode, Intent data){
        super.onActivityResult(requestCode,requestCode,data);
        if(data != null) {
            if (requestCode==REQUEST_CODE_PICTURE && resultCode== Activity.RESULT_OK) {
                Uri file=data.getData();
                path=new CompressImage().getCompressImage(file,getActivity());
                System.out.println("file path "+path);
            } else if (requestCode==REQUEST_CODE_PDF&& resultCode== Activity.RESULT_OK) {
                path=data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
                System.out.println("file path "+path);
            }
            }
        }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).getSupportActionBar().hide();
    }
    public String jsonIdEncoding(String f,String l){
        JSONArray jsonArray=new JSONArray();
        String id1=f.split("-")[2];
        String id2=l.split("-")[2];
        int j=0;
        String id_formate=(l.substring(0,l.lastIndexOf("-"))).replace("-","");
        System.out.println("formated id "+id_formate);
        for (int i=Integer.parseInt(id1);i<=Integer.parseInt(id2);i++){
            String id=id_formate+i;
            System.out.println("formated id "+id);
            try {
                jsonArray.put(j,id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            j++;
        }
        return jsonArray.toString();
    }
    private boolean isValid(){
        isOk=true;
        if(!fr_id.getText().toString().matches(ID_PATTERN)) {
            isOk=false;
            fr_id.setError("Invalid Id");
        }
        if(!se_id.getText().toString().matches(ID_PATTERN)){
            isOk=false;
            se_id.setError("Invalid Id");
        }else {
            int id1= Integer.parseInt(fr_id.getText().toString().split("-")[2]);
            int id2=Integer.parseInt(se_id.getText().toString().split("-")[2]);
            if(id1>id2){
                isOk=false;
                Toast.makeText(getActivity(),"Invalid id, Secend id must be greater then frist id",Toast.LENGTH_SHORT).show();
            }
        }
        if(path==null && text_msg.length()<1){
            isOk=false;
            if(text_msg.isShown())
                text_msg.setError("Message can't be empty");
            else {
                type.setSelection(typeAdapter.getCount());
                Toast.makeText(getActivity(),"Message empty ! please select notice type",Toast.LENGTH_SHORT).show();
            }
        }
        if(toDept==null){
            isOk=false;
            Toast.makeText(getActivity(),"Please select Department",Toast.LENGTH_SHORT).show();
        }
        return isOk;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()){
            case R.id.s_to_id:
                if(hasFocus && fr_id.getText().toString().matches(ID_PATTERN)) {
                    String id[] = (fr_id.getText().toString()).split("-");
                    se_id.setText(id[0] + "-" + id[1]+"-");
                }else if(hasFocus)
                    fr_id.setError("Invalid id");
                break;
        }
    }
}
