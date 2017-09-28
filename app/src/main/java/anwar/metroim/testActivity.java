package anwar.metroim;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import anwar.metroim.Model.RowItem;
import anwar.metroim.PhoneContactSynchronization.PhoneContacts;


public class testActivity extends AppCompatActivity implements  AdapterView.OnItemClickListener {
    ImageView imageView;
    private String[] contact_name;
    private TypedArray profile_picture;
    private String[]status;
    private String[] contact_type;
    List<RowItem> rowItems;
    ListView contact_list;
    Context context;
    private ArrayList<String> mNames = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        context=this.getApplicationContext();
        /*
       View myView =(View) findViewById(R.id.awesome_card);
        int cx = (myView.getLeft() + myView.getRight()) / 2;
        int cy = (myView.getTop() + myView.getBottom()) / 2;

        // get the final radius for the clipping circle
        int dx = Math.max(cx, myView.getWidth() - cx);
        int dy = Math.max(cy, myView.getHeight() - cy);
        float finalRadius = (float) Math.hypot(dx, dy);
        SupportAnimator animator = io.codetail.animation.ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(1500);
        animator.start();
        //      Retrieve names from phone's contact list and save in mNames
        */
        /*
        rowItems=new ArrayList<RowItem>();
        contact_name=getResources().getStringArray(R.array.contact_name);
        profile_picture=getResources().obtainTypedArray(profile_pics);
        status=getResources().getStringArray(R.array.status);
        contact_type=getResources().getStringArray(R.array.contact_type);
        for(int i=0;i<contact_name.length;i++){
            RowItem itm=new RowItem(contact_name[i],profile_picture.getResourceId(i,-1),status[i],contact_type[i]);
            rowItems.add(itm);
        }
        contact_list=(ListView)findViewById(R.id.testView);
        CustomAdapter adapter = new CustomAdapter(this, rowItems);
        contact_list.setAdapter(adapter);
        profile_picture.recycle();
        contact_list.setOnItemClickListener(this);
        */
       try{
           PhoneContacts ob=new PhoneContacts();
          // ob.getMetroImContacts(context);
       }
       catch (Exception e)
       {

       }
    }
    private void getNames(){
        int hasPhone;
        Cursor c = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                null,null,null,null);
        if((c != null) && c.moveToFirst()){
            while(c.moveToNext()){
                hasPhone = Integer.parseInt(c.getString(
                        c.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if(hasPhone == 1)
                    mNames.add(c.getString(
                            c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)));
            }
            c.close();
        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String member_name = rowItems.get(position).getContact_name();
        Toast.makeText(getApplicationContext(), "" + member_name,
                Toast.LENGTH_SHORT).show();
    }
}
