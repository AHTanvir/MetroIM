package anwar.metroim;

import android.content.Context;
import android.content.Intent;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import anwar.metroim.ChatScreen.ChatListActivity;
import anwar.metroim.CustomImage.getCustomImage;
import anwar.metroim.CustomListView.CustomAdapter;
import anwar.metroim.CustomListView.RowItem;
import anwar.metroim.CustomListView.arrayList;
import anwar.metroim.LocalHandeler.DatabaseHandler;
import anwar.metroim.PhoneContactSynchronization.IphoneContacts;
import anwar.metroim.PhoneContactSynchronization.PhoneContacts;

import static anwar.metroim.ContactsListFragment.mLastFirstVisibleItem;
import static anwar.metroim.MessageInfo.ACTIVEFRIENDPHONE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatListFragment extends Fragment implements OnItemClickListener,AdapterView.OnItemLongClickListener ,AbsListView.OnScrollListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    IphoneContacts iphoneContacts=new PhoneContacts();
    private arrayList arraylist=new arrayList();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private CustomAdapter adapter;
    private String mParam2;
    private ListView chat_list;
    DatabaseHandler databaseHandler;
    private ListPopupWindow popupWindow;
    private OnFragmentInteractionListener mListener;
    private MainActivity main;
    public ChatListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatListFragment newInstance(String param1, String param2) {
        ChatListFragment fragment = new ChatListFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view= inflater.inflate(R.layout.fragment_chat_list, container, false);
        Context context = inflater.getContext();
        main=((MainActivity)getActivity());
        databaseHandler=new DatabaseHandler(getActivity());
        chat_list=(ListView)view.findViewById(R.id.chat_list);
        updateListitm();
        chat_list.setOnItemClickListener(this);
        chat_list.setOnItemLongClickListener(this);
        chat_list.setOnScrollListener(this);
        /*
        ListView chat_list=(ListView)view.findViewById(R.id.chat_list);
        ArrayAdapter<String> allItemsAdapter = new ArrayAdapter<String>(getActivity(
        ).getBaseContext(), android.R.layout.simple_list_item_1, jssonList);
        chat_list.setAdapter(allItemsAdapter);
        */
        return view;
    }

    private void updateListitm() {
       // databaseHandler.getViewForChatFrag();
        adapter = new CustomAdapter(getActivity(),arraylist.getmInstance().getChatlist());
        chat_list.setAdapter(adapter);
        // profile_picture.recycle();
        //adapter.updateAdapter();
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

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
       final RowItem itm=(RowItem)parent.getItemAtPosition(position);
        final String []arr={"View Profile","Delete Chat"};
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
                   Toast.makeText(getActivity(),"Delete",Toast.LENGTH_SHORT).show();
                   databaseHandler.deleteMessage(itm.getContact_number());
                   arrayList.getmInstance().setChatlist(databaseHandler.getViewForChatFrag());
                   updateListitm();
               }

            }
        });
        popupWindow.show();
        return true;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        // TODO Add your menu entries here
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
                // Toast.makeText(MainActivity.this,s,Toast.LENGTH_SHORT).show();
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
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (view.getId() == chat_list.getId()) {
            final int currentFirstVisibleItem = chat_list.getFirstVisiblePosition();

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

    @Override
    public void onResume() {
        super.onResume();
        adapter.updateAdapter(arrayList.getmInstance().getChatlist());

    }
}
