package anwar.metroim;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import anwar.metroim.Adapter.RecyclerItemClickListener;
import anwar.metroim.Adapter.ResultRecyclerAdapter;
import anwar.metroim.Adapter.RowItem;
import anwar.metroim.LocalHandeler.DatabaseHandler;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultFragment extends Fragment implements View.OnClickListener{
    private FloatingActionButton fabmenu,fabcgpa,fabadd,fabback;
    private EditText addSub,addGpa,addCredit;
    private Button InsertResult,cancel;
    private View alertLayout;
    private AlertDialog dialog;
    private boolean isFABOpen=false;
    private ListPopupWindow popupWindow;
    private List<RowItem> rowitem = new ArrayList<>();
    private RecyclerView resultRecycleView;
    private ResultRecyclerAdapter recycleradapter;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseHandler databaseHandler;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ResultFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResultFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResultFragment newInstance(String param1, String param2) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
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
      View view= inflater.inflate(R.layout.fragment_result, container, false);
        databaseHandler=new DatabaseHandler(getActivity());
        resultRecycleView = (RecyclerView)view.findViewById(R.id.resultrecyclerView);
      //  view.setOnKeyListener(this);
        recyclerView();
        fabButton(view);
        resultRecycleView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), resultRecycleView, new RecyclerItemClickListener
                .OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //handle click events here
            }

            @Override
            public void onItemLongClick(View v, int position) {
                menuPopupWindow(v,position);
            }
        }));
        //resultRecycleView.setOnLongClickListener(this);
        return view;
    }

    private void recyclerView() {
        rowitem=databaseHandler.getResult(rowitem);
        String letter="ABCDEFGHIJKLMNOPQRST";
        for (int i = 0; i < 100; i++) {
            RowItem ro = new RowItem("subject","Credit" ,"gpa");
            rowitem.add(ro);
        }
        recycleradapter = new ResultRecyclerAdapter(rowitem);
        resultRecycleView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        resultRecycleView.setLayoutManager(layoutManager);
        resultRecycleView.setAdapter(recycleradapter);
        resultRecycleView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                int dy=(int)resultRecycleView.getY();
                if (dy > 0) {
                    fabmenu.hide();
                    fabcgpa.hide();
                    fabadd.hide();
                    fabback.show();
                    ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
                }
                else if (dy <= 0) {
                    fabmenu.show();
                    fabcgpa.show();
                    fabadd.show();
                    fabback.hide();
                    ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                }
            }
        });

    }
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
                recycleradapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
               recycleradapter.getFilter().filter(s);
                // Toast.makeText(MainActivity.this,s,Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    private void menuPopupWindow(View v, final int pos) {
        final String []arr={"Update","Delete"};
        popupWindow = new ListPopupWindow(getActivity());
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getActivity(),R.layout.list_item,arr);
        popupWindow.setAnchorView(v.findViewById(R.id.credit));
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.white));
        popupWindow.setAdapter(arrayAdapter);
        popupWindow.setWidth(150);
        popupWindow.setHeight(150);
        // note: don't use pixels, use a dimen resource// the callback for when a list item is selected
        popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                popupWindow.dismiss();
               String subject=rowitem.get(pos).getSubject();
                if(arr[position].equals("Delete"))
                {
                    databaseHandler.deleteResult(subject);
                    recyclerView();
                }
                else {
                    ShowAddResultDialog("Update Result","Update");
                    addSub.setText(subject);
                    addCredit.setText(rowitem.get(pos).getCredit());
                    addGpa.setText(rowitem.get(pos).getGpa());
                }

            }
        });
        popupWindow.show();
    }

    private void fabButton(View view) {
        fabmenu = (FloatingActionButton) view.findViewById(R.id.fabmenu);
        fabcgpa = (FloatingActionButton) view.findViewById(R.id.fabcgpa);
        fabadd = (FloatingActionButton) view.findViewById(R.id.fabadd);
        fabback= (FloatingActionButton) view.findViewById(R.id.fab_back);
        fabmenu.setOnClickListener(this);
        fabcgpa.setOnClickListener(this);
        fabadd.setOnClickListener(this);
        fabback.setOnClickListener(this);
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
      switch(v.getId()){
          case R.id.fabmenu:
              if(!isFABOpen){
                  showFABMenu();
              }else{
                  closeFABMenu();
              }
                break;
          case R.id.fabcgpa:
              break;
          case R.id.fabadd:
              ShowAddResultDialog("Insert Result","Insert");
              break;
          case R.id.add_result_OkButton:
              if(InsertResult.getText().equals("Insert"))
              {
                  if(addSub.length()>2 &&addGpa.length()>0 &&addGpa.length()>0)
                  {
                     dialog.dismiss();
                      databaseHandler.addResult(addSub.getText().toString(),addCredit.getText().toString(),addGpa.getText().toString());
                  }
                  else Toast.makeText(getActivity(),"Fill all field",Toast.LENGTH_SHORT).show();
              }
              else{
                  if(addSub.length()>2 &&addGpa.length()>0 &&addGpa.length()>0)
                  {
                      databaseHandler.updateResult(addSub.getText().toString(),addCredit.getText().toString(),addGpa.getText().toString());
                      dialog.dismiss();
                  }
                  else Toast.makeText(getActivity(),"Fill all field",Toast.LENGTH_SHORT).show();
              }
              recyclerView();
              break;
          case R.id.add_result_calcelButton:
              dialog.dismiss();
              break;
          case R.id.fab_back:
              ((MainActivity)getActivity()).onBackPressed();
              break;
        }

    }

    private void ShowAddResultDialog(String titel,String buttonAction) {
        LayoutInflater inflater= getActivity().getLayoutInflater();
        alertLayout = inflater.inflate(R.layout.result_add_dialog, null);
        addSub= (EditText) alertLayout.findViewById(R.id.add_resultSubject);
       // addSub.setText(subject);
        addCredit=(EditText) alertLayout.findViewById(R.id.add_resultCredit);
        addGpa=(EditText) alertLayout.findViewById(R.id.add_resultGpa);
        InsertResult=(Button) alertLayout.findViewById(R.id.add_result_OkButton);
        cancel=(Button)alertLayout.findViewById(R.id.add_result_calcelButton);
        InsertResult.setText(buttonAction);
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
       //alert.setTitle(titel);
        // this is set the view from XML inside AlertDialog
        TextView ct = new TextView(getActivity());
        ct.setText(titel);
        ct.setBackgroundColor(Color.DKGRAY);
        ct.setPadding(10, 10, 10, 10);
        ct.setGravity(Gravity.CENTER);
        ct.setTextColor(Color.WHITE);
        ct.setTextSize(20);
        alert.setCustomTitle(ct);
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(true);
        InsertResult.setOnClickListener(this);
        cancel.setOnClickListener(this);
        dialog=alert.create();
        dialog.show();
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
    private void showFABMenu(){
        isFABOpen=true;
        fabcgpa.animate().translationY(-getResources().getDimension(R.dimen.standard_130));
        fabadd.animate().translationY(-getResources().getDimension(R.dimen.standard_65));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        fabcgpa.animate().translationY(0);
        fabadd.animate().translationY(0);
    }
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
       /* getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(this);*/
    }
    /*
      resultRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0) {
                    fabmenu.hide();
                    fabcgpa.hide();
                    fabadd.hide();
                    fabback.show();
                    ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
                }
                else if (dy < 0) {
                    fabmenu.show();
                    fabcgpa.show();
                    fabadd.show();
                    fabback.hide();
                    ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                }
            }
        });
    ////// implement View.OnKeyListener
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
          //  back();
            return true;
        }
        return false;
    }
     private void back() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        TabLayout tabLayout=(TabLayout)getActivity().findViewById(R.id.tab_view);
        tabLayout.setVisibility(View.VISIBLE);
        ChatListFragment chatListFragment = new ChatListFragment();
        FragmentManager fragmentManager =getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(Relative_layoutfor_fragments, chatListFragment, chatListFragment.getTag()).commit();
    }*/
}

