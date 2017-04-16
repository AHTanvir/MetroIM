package anwar.metroim;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import anwar.metroim.CustomImage.getCustomImage;

import static anwar.metroim.R.id.Relative_layoutfor_fragments;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link contactViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link contactViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class contactViewFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private getCustomImage customImage;
    private Button backBtn;
    private ImageView con_image;
    private TextView name,number,email,status,type,dept;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int mParam3;

    private OnFragmentInteractionListener mListener;

    public contactViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment contactViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static contactViewFragment newInstance(String param1, String param2,int tab) {
        contactViewFragment fragment = new contactViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putInt(ARG_PARAM3,tab);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3=getArguments().getInt(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view= inflater.inflate(R.layout.fragment_contact_view, container, false);
        collapsingToolbarLayout=(CollapsingToolbarLayout)view.findViewById(R.id.collapsingtool);
        collapsingToolbarLayout.setTitle(mParam2);
        customImage=new getCustomImage();
        con_image=(ImageView)view.findViewById(R.id.contact_view_img);
        backBtn=(Button)view.findViewById(R.id.contact_view_backBtn);
        name=(TextView)view.findViewById(R.id.contact_view_name);
        number=(TextView)view.findViewById(R.id.contact_view_number);
        email=(TextView)view.findViewById(R.id.contact_view_email);
        status=(TextView)view.findViewById(R.id.contact_view_status);
        type=(TextView)view.findViewById(R.id.contact_view_type);
        dept=(TextView)view.findViewById(R.id.contact_view_dept);
        backBtn.setOnClickListener(this);
        jsonDecoder(mParam1);
        return view;
    }

    private void jsonDecoder(String mParam1) {
        try{
            JSONObject jsonObject=new JSONObject(mParam1);
            JSONArray jsonArray=jsonObject.getJSONArray("info");
            for(int i=0; i<jsonArray.length(); i++) {
                JSONObject jsonObj =jsonArray.getJSONObject(i);
                name.setText(jsonObj.getString("name"));
                number.setText(jsonObj.getString("phone"));
                email.setText(jsonObj.getString("email"));
                status.setText(jsonObj.getString("contactstatus"));
                 type.setText(jsonObj.getString("type"));
                Bitmap bit=customImage.base64Decode(jsonObj.getString("photo"));
                if(bit==null)
                {
                    con_image.setImageResource(R.drawable.my);
                }else  con_image.setImageBitmap(bit);
                dept.setText(jsonObj.getString("dept"));
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
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
        ((MainActivity)getActivity()).onBackPressed();
        /*
        ChatListFragment chatListFragment = new ChatListFragment();
        FragmentManager fragmentManager =getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(Relative_layoutfor_fragments, chatListFragment, chatListFragment.getTag()).commit();*/
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
}
