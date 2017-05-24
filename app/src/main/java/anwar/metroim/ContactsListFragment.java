package anwar.metroim;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import anwar.metroim.ChatScreen.ChatListActivity;
import anwar.metroim.CustomImage.getCustomImage;
import anwar.metroim.CustomListView.*;
import anwar.metroim.LocalHandeler.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;

import static anwar.metroim.MessageInfo.ACTIVEFRIENDPHONE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactsListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContactsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactsListFragment extends Fragment implements AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener,AbsListView.OnScrollListener{
    public CustomAdapter adapter;
    public static String[]status;
    private ListPopupWindow popupWindow;
    MainActivity mainActivity;
    private List<RowItem> contactRowList;
    private ListView contact_list;
    public static int mLastFirstVisibleItem;
    float x1, x2, y1, y2;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    DatabaseHandler databaseHandler;
    public ContactsListFragment() {
        // Required empty public constructor
    }



    // TODO: Rename and change types and number of parameters
    public static ContactsListFragment newInstance(String param1, String param2) {
        ContactsListFragment fragment = new ContactsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
       mainActivity=((MainActivity)getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_contacts_list, container, false);
        //context=getContext();
        databaseHandler=new DatabaseHandler(getActivity());
        int tolat_contact= databaseHandler.getContactsCount();
        contactRowList=arrayList.getmInstance().getContactlist();
        contact_list =(ListView) view.findViewById(R.id.contact_list_view);
        adapter= new CustomAdapter(getActivity(),contactRowList);
        contact_list.setAdapter(adapter);
        ((MainActivity)getActivity()).viewFriendInfo("CheckForUpadteContactsInfo","","",getActivity());
        contact_list.setOnItemLongClickListener(this);
        contact_list.setTextFilterEnabled(true);
        contact_list.setOnItemClickListener(this);
        contact_list.setOnScrollListener(this);
        return view;
    }

    public void checkForUpdate(final Context con) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                arrayList.getmInstance().setContactlist(databaseHandler.getContact(0));
                adapter.updateAdapter(arrayList.getmInstance().getContactlist());
            }
        }).start();
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
        /*
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        */
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (view.getId() == contact_list.getId()) {
            final int currentFirstVisibleItem = contact_list.getFirstVisiblePosition();

            if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                // getSherlockActivity().getSupportActionBar().hide();
                ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                // getSherlockActivity().getSupportActionBar().show();
                ((AppCompatActivity) getActivity()).getSupportActionBar().show();
            }

            mLastFirstVisibleItem = currentFirstVisibleItem;
        }
    }
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.main, menu);
        //View v = (View) menu.findItem(R.id.action_search).getActionView();
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                adapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
               adapter.getFilter().filter(s);
                return false;
            }
        });
        /*
        inputSearch = (EditText)v.findViewById(R.id.inputSearch);

        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                // clientAdapter.getFilter().filter(s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
                // ListData.this.clientAdapter.getFilter().filter(s);
            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub

              //  adapter.getFilter().filter(s.toString());

            }

        });
*/
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
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        databaseHandler=new DatabaseHandler(getActivity());
        RowItem itm=(RowItem)parent.getItemAtPosition(position);
        String member_phone=itm.getContact_number();
        Intent i=new Intent(getActivity(), ChatListActivity.class);
        i.putExtra(MessageInfo.NAME,itm.getContact_name());
        i.putExtra(MessageInfo.IMAGE,new getCustomImage().base64Encode(itm.getPro_image()));
        i.putExtra(ACTIVEFRIENDPHONE,member_phone);
        startActivity(i);

    }
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final RowItem itm=(RowItem)parent.getItemAtPosition(position);
        final String []arr={"View Profile","Delete Contact"};
        popupWindow = new ListPopupWindow(getActivity());
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getActivity(),R.layout.list_item,arr);
        popupWindow.setAnchorView(view.findViewById(R.id.contact_name));
        popupWindow.setAdapter(arrayAdapter);
        popupWindow.setWidth(250);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.white));
        // note: don't use pixels, use a dimen resource// the callback for when a list item is selected
        popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                popupWindow.dismiss();
                if(arr[position]=="View Profile")
                {
                    ((MainActivity)getActivity()).viewFriendInfo("&getFriendInfo=",itm.getContact_number(),itm.getContact_name(),getActivity());
                    Toast.makeText(getActivity(),"View",Toast.LENGTH_SHORT).show();
                }else {
                   // Toast.makeText(getActivity(),"",Toast.LENGTH_SHORT).show();
                    databaseHandler.deleteContact(itm.getContact_number());
                    arrayList.getmInstance().setContactlist(databaseHandler.getContact(0));
                    adapter.updateAdapter(arrayList.getmInstance().getContactlist());
                    //updateListitm();
                    Toast.makeText(getActivity(),"Delete",Toast.LENGTH_SHORT).show();
                }
            }
        });
        popupWindow.show();
        return true;
    }
  /*  @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        ((MainActivity)getActivity()).updateFriendInfo("&getFriendInfo=",contactRowList.get(position).getContact_number(),contactRowList.get(position).getContact_name(),getActivity());
        return false;
    }*/

    /*
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        String direction="xx";
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                y1 = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                y2 = event.getY();
                direction = null;
                float xDiff = x2 - x1;
                float yDiff = y2 - y1;
                if (Math.abs(xDiff) > Math.abs(yDiff)) {
                    if (x1 < x2) {
                        direction ="Right";
                    } else {
                        direction ="left";
                    }
                } else {
                    if (y1 > y2) {
                        direction ="top";
                    } else {
                        direction = "bottom";
                    }
                }
        }
        Toast.makeText(getActivity(), direction, Toast.LENGTH_SHORT).show();
        return true;
    }
    */
}
