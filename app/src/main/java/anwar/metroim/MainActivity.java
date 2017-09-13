package anwar.metroim;


import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.soundcloud.android.crop.Crop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;

import anwar.metroim.Backup.Backup_email;
import anwar.metroim.Backup.DbBackup;
import anwar.metroim.BloodDonor.donorActivity;
import anwar.metroim.CustomImage.getCustomImage;
import anwar.metroim.Adapter.arrayList;
import anwar.metroim.LocalHandeler.DatabaseHandler;
import anwar.metroim.Manager.SessionManager;
import anwar.metroim.service.BootBroadcast;
import anwar.metroim.service.imanager;
import anwar.metroim.service.MetroImservice;

import static anwar.metroim.ChatScreen.ChatListActivity.currentDateHolder;
import static anwar.metroim.R.id.Relative_layoutfor_fragments;
import static anwar.metroim.service.MetroImservice.LIST_UPDATED;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, TabLayout.OnTabSelectedListener {
    private TabLayout tabLayout;
    private ImageView profile_image;
    private imanager man_ger=new MetroImservice();
    public static Bitmap images = null;
    private String image_str;
    private String jobject;
    private TextView Profile_name, Profile_phone;
    private String view_Frag ="not",currentFragment="not";
    private int selectedtab=3;
    private DatabaseHandler databaseHandler;
    private SessionManager session;
    private Handler handeler = new Handler();
    private String result = null;
    private NavigationView navigationView;
    private  View navview;
    private String st,nam,ph;
    private AlertDialog.Builder updateStatusDialog;
    private Calendar ca = Calendar.getInstance();
    private getCustomImage getCustomImage;
    private DbBackup dbBackup;
   private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            man_ger = ((MetroImservice.IMBinder) service).getService();
            System.out.println("Service is bound");
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
        session = new SessionManager(this);
        if(!session.isLoggedIn()) {
            Intent i=new Intent(this,LoginActivity.class);
            finish();
            startActivity(i);
            return;
        }
        new BootBroadcast().ServiceStart(MainActivity.this);
        setContentView(R.layout.activity_main);
        databaseHandler = new DatabaseHandler(this);
        //This Thread help to faster startup and  UiThread required to update ui
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateList();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        NavView();

                    }
                });
            }
        }).start();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        tabLayout = (TabLayout) findViewById(R.id.tab_view);
        tabLayout.addOnTabSelectedListener(this);

        if(selectedtab !=0)
            onTabSelected(tabLayout.getTabAt(0));
    }

    public void NavView() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navview = navigationView.getHeaderView(0);
        Profile_name = (TextView) navview.findViewById(R.id.nav_header_name);
        Profile_phone = (TextView) navview.findViewById(R.id.nav_header_phone);
        profile_image = (ImageView) navview.findViewById(R.id.profile_imageView);
        getCustomImage = new getCustomImage();
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        st=user.get(SessionManager.KEY_STATUS);
        ph=user.get(SessionManager.KEY_PHOTO);
        //System.out.println("Main ph"+ph);
        nam=user.get(SessionManager.KEY_NAME);
        Profile_name.setText(nam);
        Profile_phone.setText(user.get(SessionManager.KEY_PHONE));
        if(ph !=null)
           images=getCustomImage.getRoundedShape(ph,100,100);
        if (images == null)
            profile_image.setImageResource(R.drawable.my);
        else
            profile_image.setImageBitmap(images);
        profile_image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(man_ger.isNetworkConnected())
                    Crop.pickImage(MainActivity.this);
                else Toast.makeText(MainActivity.this,"Not Conected to Inertnet",Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(LIST_UPDATED)){
                updateList();
            }

        }

    }
    private MessageReceiver messageReceiver = new MessageReceiver();

    public void onTabSelected(TabLayout.Tab tab) {
        //This will be called 2nd when you select a tab or swipe using viewpager
        final int position = tab.getPosition();
        selectedtab=position;
        Log.i("card", "Tablayout pos: " + position);
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                if (position == 0 ) {
                    view_Frag = "Chat_list";
                    ChatListFragment chatListFragment = new ChatListFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(Relative_layoutfor_fragments, chatListFragment, chatListFragment.getTag()).commit();
                }
                if (position == 1) {
                    view_Frag ="Contact_list";
                    ContactsListFragment contactsListFragment = new ContactsListFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(Relative_layoutfor_fragments, contactsListFragment, contactsListFragment.getTag()).commit();
                }
            }
        });
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        //This will be called 1st when you select a tab or swipe using viewpager
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        //This will be called only when you select the already selected tab(Ex: selecting 3rd tab again and again)
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            if(!view_Frag.equals("Chat_list") || !view_Frag.equals("Contact_list")){
                tabLayout.setVisibility(View.VISIBLE);
                getSupportActionBar().show();
            }
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_update_status) {
            if(man_ger.isNetworkConnected())
                showUpdateStatusDialog();
            else Toast.makeText(MainActivity.this,"Not Conected to Inertnet",Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.msg_Backup) {
                   dbBackup=new DbBackup(MainActivity.this, man_ger.getBackupEmail(), new GoogleApiClient.ConnectionCallbacks() {
                       @Override
                       public void onConnected(@Nullable Bundle bundle) {
                                 new Thread(new Runnable() {
                                     @Override
                                     public void run() {
                                         dbBackup.crateBackup();
                                     }
                                 }).start();
                       }
                       @Override
                       public void onConnectionSuspended(int i) {
                           System.out.println("db-- suspe");
                       }
                   });
        } else if (id == R.id.nav_donation_result) {
            view_Frag = "nn";
            tabLayout.setVisibility(View.GONE);
           // getSupportActionBar().hide();
            ResultFragment resultFrag = new ResultFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(Relative_layoutfor_fragments, resultFrag, resultFrag.getTag()).addToBackStack(null).commit();
        } else if (id == R.id.nav_blood_donor) {
            Intent intent = new Intent(this, donorActivity.class);
            finish();
            startActivity(intent);

        }
        else if(id==R.id.nav_setting){
            view_Frag = "nn";
            tabLayout.setVisibility(View.GONE);
            getSupportActionBar().hide();
            SettingFragment settingfrag = new SettingFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(Relative_layoutfor_fragments, settingfrag, settingfrag.getTag())
                    .addToBackStack(null).commit();
        }
        else if (id == R.id.nav_logout) {
            session.logoutUser();
            man_ger.exit();
            Intent intent = new Intent(this, MainActivity.class);
            finish();
            //startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(messageReceiver);
        unbindService(mConnection);
        currentFragment=view_Frag;
        view_Frag="pasue";
    }
    @Override

    protected void onResume() {
        super.onResume();
        view_Frag=currentFragment;
        bindService(new Intent(MainActivity.this, MetroImservice.class), mConnection, Context.BIND_AUTO_CREATE);
        IntentFilter i = new IntentFilter();
        i.addAction(LIST_UPDATED);
        registerReceiver(messageReceiver, i);
        currentDateHolder = "date";
        getSupportActionBar().show();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
       // Toast.makeText(MainActivity.this,result.toString()+ result.getStringExtra(AccountManager.KEY_ACCOUNT_NAME), Toast.LENGTH_LONG).show();
        if(result !=null)
        {
            if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
                beginCrop(result.getData());
            } else if (requestCode == Crop.REQUEST_CROP) {
                handleCrop(resultCode, result);
            } else if (requestCode == 5 && resultCode == RESULT_OK) {
                Backup_email backup=new Backup_email(MainActivity.this);
                ca.add(Calendar.DAY_OF_MONTH,-1);
                backup.addEmail(result.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
                backup.addschedule(ca.getTime());
                Toast.makeText(MainActivity.this,result.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)+"added as your backup account", Toast.LENGTH_LONG).show();
                //onNavigationItemSelected(itm);
            }
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Crop.getOutput(result));
                StoreAndViewImages(getCustomImage.getCropImage(bitmap));
            } catch (IOException e) {
               e.printStackTrace();
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //This method Store profile image and fetch from server When user update image
    private void StoreAndViewImages(final Bitmap bitmap) throws IOException {
        result=null;
        image_str=getCustomImage.base64Encode(bitmap);
        try {
            JSONObject aj = new JSONObject();
            aj.put("tt", image_str);
            jobject = aj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
       new Thread() {
            public void run() {
                try {
                    result = man_ger.updateInfo( "&photo=" + URLEncoder.encode(jobject,"UTF-8")+ "&");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (result !=null)
                {
                    handeler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (result.equals("1")) {
                                session.createinfoSession(nam,st,image_str);
                                if (bitmap != null) {
                                    profile_image.setImageBitmap(bitmap);
                                }
                                Toast.makeText(MainActivity.this,"SUCCESSFUL",Toast.LENGTH_SHORT).show();
                            }else Toast.makeText(MainActivity.this,"FAILED",Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        }.start();
    }

    private void showUpdateStatusDialog() {
        String[] status = {"Availble", "Busy", "Other"};
        updateStatusDialog = new AlertDialog.Builder(MainActivity.this);
        updateStatusDialog.setTitle("Update Status");
        final AutoCompleteTextView auto_text = new AutoCompleteTextView(MainActivity.this);
         ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, status);
        auto_text.setInputType(InputType.TYPE_CLASS_TEXT);
        auto_text.setText(st);
        auto_text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                auto_text.setText("");
                return false;
            }
        });
        auto_text.setThreshold(1);
        final Button update_btn = new Button(this);
        updateStatusDialog.setView(auto_text);
        auto_text.setAdapter(adapter);
        updateStatusDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    @Override

                    public void run() {
                        st=auto_text.getText().toString();
                        try {
                            result = man_ger.updateInfo( "&status=" + URLEncoder.encode(st,"UTF-8")+ "&");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        if (result !=null)
                        {
                            handeler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (result.equals("1")) {
                                        session.createinfoSession(nam,st,ph);
                                        Toast.makeText(MainActivity.this,"SUCCESSFUL",Toast.LENGTH_SHORT).show();
                                    }else Toast.makeText(MainActivity.this,"FAILED",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();

            }
        });
        updateStatusDialog.show();
    }
    //when you click view profile in fragment this method will be invoked and contactViewFragment will be visiable
    public  void viewFriendInfo(final String req, final String value, final String name,Context con){
      //  showUpdateStatusDialog();
        if(man_ger.isNetworkConnected()) {
            final ProgressDialog progressDialog = new ProgressDialog(con);
            progressDialog.setMessage("plz Wait......");
            if (req.equals("&getFriendInfo="))
                progressDialog.show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (req.equals("&getFriendInfo="))
                            result = man_ger.updateInfo(req + URLEncoder.encode(value, "UTF-8") + "&");
                        else if (req.equals("CheckForUpadteContactsInfo"))
                            result = man_ger.getUserUpdateInfo();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    handeler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (req.equals("&getFriendInfo=")) {
                                getSupportActionBar().hide();
                                tabLayout.setVisibility(View.GONE);
                                contactViewFragment contactViewFragment = new contactViewFragment().newInstance(result, name, selectedtab);
                                FragmentManager fragmentManager = getSupportFragmentManager();
                                fragmentManager.beginTransaction().replace(Relative_layoutfor_fragments, contactViewFragment, contactViewFragment.getTag()).addToBackStack(null).commit();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }).start();
        }else Toast.makeText(con,"Not Conected to Inertnet",Toast.LENGTH_SHORT).show();
    }
    public void updateList(){
       if(!view_Frag.equals("Chat_list"))
           arrayList.getmInstance().setChatlist(databaseHandler.getViewForChatFrag());
        if(!view_Frag.equals("Contact_list"))
            arrayList.getmInstance().setContactlist(databaseHandler.getContact(0));
    }


}