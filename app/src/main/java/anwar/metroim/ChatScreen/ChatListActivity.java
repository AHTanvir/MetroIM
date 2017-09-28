package anwar.metroim.ChatScreen;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import anwar.metroim.CustomImage.CompressImage;
import anwar.metroim.CustomImage.getCustomImage;
import anwar.metroim.Adapter.ChatAdapter;
import anwar.metroim.Model.ChatModel;

import anwar.metroim.Adapter.arrayList;
import anwar.metroim.LocalHandeler.DatabaseHandler;
import anwar.metroim.MainActivity;
import anwar.metroim.Manager.SessionManager;
import anwar.metroim.MessageInfo;
import anwar.metroim.PhoneContactSynchronization.StoreContacts;
import anwar.metroim.R;
import anwar.metroim.service.MetroImservice;
import anwar.metroim.service.Iappmanager;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;




/**
 * Created by anwar on 1/19/2017.
 */

public class ChatListActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener{
    Context context;
    private ListView mListView;
    private ProgressBar circularprogressBar,horizontal_progressBar,progres_bar;
    private static final int REQUEST_CODE_PICTURE=1;
    private static final int REQUEST_CODE_PDF=2;
    private static final int REQUEST_CODE_ADD_CONTACT=3;
    private EditText Chat_edit_text;
    private ChatAdapter mAdapter;
    private LinearLayout mRevealView;
    private boolean hidden = true;
    private ImageButton gallery_btn, photo_btn;
    private ImageView toolbar_imageView;
    private TextView CustomAcBar_name,CustomAcBar_lastSeen;
    private Button Chat_send_button,toolbar_backButton;
    private Iappmanager man_ger;
    public  static String currentDateHolder="date";
    private String active_friend_phone;
    private String active_friend_name;
    private String active_friend_img;
    String path="";
    private boolean isHidden=true;
    private String result=null,content,imid;
    private int typ;
    private FloatingActionButton add_contactBtn;
    DatabaseHandler databaseHandler;
    private List<ChatModel> Chat_list = new ArrayList<ChatModel>();
    private Calendar calander = Calendar.getInstance();
    private DateFormat dbStoreFormate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private getCustomImage customImage=new getCustomImage();
    SessionManager session;
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            man_ger= ((MetroImservice.IMBinder)service).getService();
          new Thread(new Runnable() {
              @Override
              public void run() {
                  try {
                      man_ger.getLastSeen();
                  } catch (UnsupportedEncodingException e) {
                      e.printStackTrace();
                  }
              }
          }).start();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            // man_ger= null;
            // Toast.makeText(RegisterActivity.this, "disconn", Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        context=this;
        session=new SessionManager(this);
        databaseHandler=new DatabaseHandler(this);
        Bundle extras = this.getIntent().getExtras();
        active_friend_name=extras.getString(MessageInfo.NAME);
        active_friend_img=extras.getString(MessageInfo.IMAGE);
        active_friend_phone=extras.getString(MessageInfo.ACTIVEFRIENDPHONE);
        MessageInfo.ACTIVEFRIEND_PHONE=active_friend_phone;
        chatScreenView();
        revelView();
        NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
    private void chatScreenView(){
        Chat_send_button=(Button)findViewById(R.id.Chat_send_button);
        Chat_edit_text=(EditText)findViewById(R.id.Chat_edit_text);
        add_contactBtn=(FloatingActionButton)findViewById(R.id.add_contactBtn);
        Chat_send_button.setOnClickListener(this);
        add_contactBtn.setOnClickListener(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        toolbar_imageView=(ImageView)findViewById(R.id.toolbar_imageView);
        toolbar_backButton=(Button)findViewById(R.id.toolbar_backbutton);
        CustomAcBar_name=(TextView)getSupportActionBar().getCustomView().findViewById(R.id.custon_acbar_textview);
        CustomAcBar_lastSeen=(TextView)getSupportActionBar().getCustomView().findViewById(R.id.custom_acbar_lastseen);
        int totalConversion=databaseHandler.getContactsConversinCount(active_friend_phone);
        Bitmap bitmap= customImage.getRoundedShape(active_friend_img,100,100);
        toolbar_imageView.setImageBitmap(bitmap);
        CustomAcBar_name.setText(active_friend_name);
        if(active_friend_name.matches("\\+\\d{13}")) {
            add_contactBtn.setVisibility(View.VISIBLE);
        }
        if(totalConversion>0){
           Chat_list=databaseHandler.getMessage(active_friend_phone);
        }
        mListView = (ListView) findViewById(R.id.lv_chat);
       // mListView.setStackFromBottom(true);
        mAdapter= new ChatAdapter(Chat_list);
        mListView.setAdapter(mAdapter);
        databaseHandler.updateMessage(active_friend_phone);
        mListView.setOnItemClickListener(this);
        toolbar_imageView.setOnClickListener(this);
        toolbar_backButton.setOnClickListener(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat_screen_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.attachment) {
            if(isHidden)
            {
                mRevealView.setVisibility(View.VISIBLE);
                isHidden=false;
            }
            else {
                mRevealView.setVisibility(View.GONE);
                isHidden=true;
            }

        /*    int cx = (mRevealView.getLeft() + mRevealView.getRight());
            int cy = mRevealView.getTop();
            int radius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());
            //Below Android LOLIPOP Version
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                SupportAnimator animator =io.codetail.animation.ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.setDuration(700);
                SupportAnimator animator_reverse = animator.reverse();
                if (hidden) {
                    mRevealView.setVisibility(View.VISIBLE);
                    animator.start();
                    hidden = false;
                } else {
                    if (animator_reverse != null) {
                        animator_reverse.addListener(new SupportAnimator.AnimatorListener() {
                            @Override
                            public void onAnimationStart() {}
                            @Override
                            public void onAnimationEnd() {
                                mRevealView.setVisibility(View.INVISIBLE);
                                hidden = true;
                            }
                            @Override
                            public void onAnimationCancel() {}
                            @Override
                            public void onAnimationRepeat() {}
                        });
                        animator_reverse.start();
                    }
                }
            }
            // Android LOLIPOP And ABOVE Version
            else {
                if (hidden) {
                    Animator anim = android.view.ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
                    mRevealView.setVisibility(View.VISIBLE);
                    anim.start();
                    hidden = false;
                } else {
                    Animator anim = android.view.ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, radius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mRevealView.setVisibility(View.INVISIBLE);
                            hidden = true;
                        }
                    });
                    anim.start();
                }
            }
            return true;*/
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        arrayList.getmInstance().setCurrentDateHolder("noDate");
        arrayList.getmInstance().setChatlist(databaseHandler.getViewForChatFrag());
        MessageInfo.ACTIVEFRIEND_PHONE=null;
        MessageInfo.frndStatus=null;
        MessageInfo.frndIP=null;
        Intent intent=new Intent(ChatListActivity.this,MainActivity.class);
        finish();
    }
    protected void onPause()
    {
        super.onPause();
        arrayList.getmInstance().setCurrentDateHolder("noDate");
        unregisterReceiver(messageReceiver);
        unbindService(mConnection);
        MessageInfo.ACTIVEFRIEND_PHONE=null;
        MessageInfo.frndStatus=null;
        MessageInfo.frndIP=null;
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        bindService(new Intent(ChatListActivity.this, MetroImservice.class), mConnection , Context.BIND_AUTO_CREATE);
        IntentFilter i = new IntentFilter();
        i.addAction(MetroImservice.TAKE_MESSAGE);
        i.addAction("LAST_SEEN");
        i.addAction("DOWNLOAD_STATUS");
        i.addAction("UPLOAD_STATUS");
        registerReceiver(messageReceiver, i);
        MessageInfo.ACTIVEFRIEND_PHONE=active_friend_phone;
        //currentDateHolder="date";
    }
    @Override
    public void onActivityResult(int requestCode, final int resultCode, Intent data){
        super.onActivityResult(requestCode,requestCode,data);
        if(data != null)
        {
            if (requestCode==REQUEST_CODE_PICTURE && resultCode== Activity.RESULT_OK)
            {
                Uri file=data.getData();
                path=new CompressImage().getCompressImage(file,context);
                String id=addItem(path,1,"image",1);
                databaseHandler.updateMessage(id,1);
                man_ger.FileUpload(path,active_friend_phone,"image",0,id);
            }
            else if (requestCode==REQUEST_CODE_PDF&& resultCode== Activity.RESULT_OK)
            {
                String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
                String id=addItem(filePath,0,"pdf",1);
                databaseHandler.updateMessage(id,1);
                man_ger.FileUpload(filePath,active_friend_phone,"pdf",0,id);
            }
            else if(requestCode==REQUEST_CODE_ADD_CONTACT)
            {
                add_contactBtn.setVisibility(View.GONE);
                new StoreContacts(this).startSynce(active_friend_phone);
                chatScreenView();
                arrayList.getmInstance().setContactlist(databaseHandler.getContact(0));
            }
        }
    }
    public class  MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Bundle extra = intent.getExtras();
            String action=intent.getAction().toString();
            if(action.equals(MetroImservice.TAKE_MESSAGE))
            {
                String userPhone=extra.getString(MessageInfo.FROM);
                String name=extra.getString(MessageInfo.SENDERNAME);
                String date=extra.getString(MessageInfo.SENDATE);
                String type=extra.getString(MessageInfo.MESSAGE_TYPE);
                String msg=extra.getString(MessageInfo.MESSAGE_LIST);
                System.out.println("new messsgs from "+userPhone +" user "+active_friend_phone);
                if(active_friend_phone.equals(userPhone))
                {
                    String datearr[];
                    datearr=databaseHandler.praseDate(date);
                    if(type.equals("image"))
                        typ=3;
                    else typ =2;

                    content=msg;
                    ChatModel item = new ChatModel(typ,content,datearr[1],datearr[0]);
                    Chat_list.add(item);
                    mAdapter.updateAdapter(Chat_list);
                    databaseHandler.updateMessage(active_friend_phone);
                    mListView.setSelection(Chat_list.size()-1);
                }
                else
                    Toast.makeText(context,"New message from "+name,Toast.LENGTH_SHORT).show();
            }
            else if(action.equals("LAST_SEEN"))
            {
                if(extra.getString("LASTSEEN").equals("online")) {
                    CustomAcBar_lastSeen.setText("Online");
                    MessageInfo.frndStatus="Online";
                }
                else {
                    String cdate=arrayList.getmInstance().getCurrentDateHolder();
                    arrayList.getmInstance().setCurrentDateHolder("nodate");
                    String date[]= databaseHandler.praseDate(extra.getString("LASTSEEN"));
                    if(date[0]==" " || date[0].equals("Today"))
                        CustomAcBar_lastSeen.setText("Seen "+date[1]);
                    else  CustomAcBar_lastSeen.setText("Seen "+date[0]);
                    arrayList.getmInstance().setCurrentDateHolder(cdate);
                    MessageInfo.frndStatus="Offline";
                }
                MessageInfo.frndIP=extra.getString("IP");
                MessageInfo.frndPort=extra.getString("PORT");
            }
            else if(action.equals("DOWNLOAD_STATUS"))
            {
                boolean status=extra.getBoolean(MessageInfo.STATUS);
                int position=extra.getInt(MessageInfo.POSITION);
                String path=extra.getString(MessageInfo.PATH);
                updaeteChatList(path,position,0);
                if(status)
                    Toast.makeText(context,"Download Competed",Toast.LENGTH_SHORT).show();
                else Toast.makeText(context,"Download Failed",Toast.LENGTH_SHORT).show();
            }
            else if(action.equals("UPLOAD_STATUS"))
            {
                String status=extra.getString(MessageInfo.STATUS);
                if(status.equals("1")){
                    mAdapter.updateAdapter(databaseHandler.getMessage(active_friend_phone));
                    Toast.makeText(context,"Message Send",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(context,"Message can't be send",Toast.LENGTH_SHORT).show();
                    mAdapter.updateAdapter(databaseHandler.getMessage(active_friend_phone));
                }
            }
            arrayList.getmInstance().setChatlist(databaseHandler.getViewForChatFrag());
        }
    };
    private MessageReceiver messageReceiver =new MessageReceiver();
    private void hideRevealView() {
        if (mRevealView.getVisibility() == View.VISIBLE) {
            mRevealView.setVisibility(View.GONE);
            hidden = true;
        }
    }
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.add_contactBtn:
                Intent addContactIntent = new Intent();
                addContactIntent.setAction(Intent.ACTION_INSERT);
                addContactIntent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                addContactIntent.putExtra(ContactsContract.Intents.Insert.PHONE,active_friend_phone);
                startActivityForResult(addContactIntent,REQUEST_CODE_ADD_CONTACT);
                break;
            case R.id.Chat_send_button:
                if(man_ger.isNetworkConnected())
                {
                    if (Chat_edit_text.getText().length() > 0) {
                        String rowid=addItem(Chat_edit_text.getText().toString(),0,"text",0);
                        sendMeg(Chat_edit_text.getText().toString(),"text", rowid);
                        typ=0;
                        Chat_edit_text.setText("");
                        //Toast.makeText(context, man_ger.test(),Toast.LENGTH_SHORT).show();
                    }
                }
                else Toast.makeText(ChatListActivity.this,"Not Conected to Inertnet",Toast.LENGTH_SHORT).show();
                break;
            case R.id.gallery_img_btn:
                //hideRevealView();
                isHidden=true;
                mRevealView.setVisibility(View.GONE);
                if(man_ger.isNetworkConnected())
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                        Intent Pick_image_intent=new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        Pick_image_intent.addCategory(Intent.CATEGORY_OPENABLE);
                        Pick_image_intent.setType("image/*");
                        String pickTitel="Select Photo";
                        Intent chooseIntent=Intent.createChooser(Pick_image_intent,pickTitel);
                        startActivityForResult(chooseIntent,REQUEST_CODE_PICTURE);
                    }
                    else {
                        Intent Pick_image_intent=new Intent();
                        Pick_image_intent.setType("image/*");
                        Pick_image_intent.setAction(Intent.ACTION_GET_CONTENT);
                        String pickTitel="Select Photo";
                        Intent chooseIntent=Intent.createChooser(Pick_image_intent,pickTitel);
                        startActivityForResult(chooseIntent,REQUEST_CODE_PICTURE);
                    }
                }else Toast.makeText(ChatListActivity.this,"Not Conected to Inertnet",Toast.LENGTH_SHORT).show();
                break;
            case R.id.photo_img_btn:
                isHidden=true;
                mRevealView.setVisibility(View.GONE);
                if (man_ger.isNetworkConnected())
                {
                    new MaterialFilePicker()
                            .withActivity(ChatListActivity.this)
                            .withRequestCode(REQUEST_CODE_PDF)
                            .withFilter(Pattern.compile(".*\\.pdf$"))
                            .withFilterDirectories(false)
                            .withHiddenFiles(true)
                            .start();
                }else Toast.makeText(ChatListActivity.this,"Not Conected to Inertnet",Toast.LENGTH_SHORT).show();
                break;
            case R.id.toolbar_backbutton:
                arrayList.getmInstance().setChatlist(databaseHandler.getViewForChatFrag());
                finish();
                break;
            case R.id.toolbar_imageView:
                finish();
                break;
        }
    }
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
       final ChatModel item=(ChatModel)parent.getItemAtPosition(position);
       final String content=item.getContent();
        final int vis=item.getVisibility();
        String rowId=item.getId();
        if(item.getType()==(3) ||item.getType()==(1)) {
                if (content.startsWith("http://") && vis==0) {
                    if(man_ger.isNetworkConnected()) {
                        updaeteChatList(content, position, 1);
                        databaseHandler.updateMessage(rowId, 1);
                        man_ger.fileDownload(content, active_friend_phone, item.getId(), position);
                    } else Toast.makeText(ChatListActivity.this,"Not Conected to Inertnet",Toast.LENGTH_SHORT).show();
                }
                else if (content.endsWith(".jpg") && vis !=1){
                    File file = new File(content);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file),  "image/jpg");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
        }
        else if(content.startsWith("http://") && vis!=1) {
            if(man_ger.isNetworkConnected())
            {
                progres_bar=(ProgressBar)view.findViewById(R.id.simpleProgressBar);
                updaeteChatList(content,position,1);
                databaseHandler.updateMessage(rowId,1);
                man_ger.fileDownload(content,active_friend_phone,item.getId(),position);
            } else Toast.makeText(ChatListActivity.this,"Not Conected to Inertnet",Toast.LENGTH_SHORT).show();
        }
        else if(content.endsWith(".pdf") && vis !=1) {
               try {
                   File file = new File(content);
                   Intent intent = new Intent(Intent.ACTION_VIEW);
                   intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                   intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                   startActivity(intent);
               }catch (ActivityNotFoundException e){Toast.makeText(context,"No app to perform",Toast.LENGTH_LONG).show();}
        }
    }
    public void sendMeg(final String msg, final String mType,final String RowId){
        mListView.setSelection(Chat_list.size()-1);
        result=null;
        new Thread(){
            Handler handler = new Handler();
            public void run() {
                try {
                    result = man_ger.sendMessage(active_friend_phone, mType, msg,RowId);
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(result !=null &&result.equals("1"))
                            {
                                Toast.makeText(context,"Msg sent",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(context,"Msg Can't be send",Toast.LENGTH_SHORT).show();
                                mAdapter.updateAdapter(databaseHandler.getMessage(active_friend_phone));
                            }
                        }
                    });
                }
        }.start();
    }
    private String addItem(String msg,int ItemTyp,String Mtype,int visibility) {
        String d=dbStoreFormate.format(calander.getTime());
        databaseHandler.addMessage(active_friend_phone,msg,d,"to",Mtype,"1");
        String rowid=databaseHandler.lastRowId(active_friend_phone,d);
        String []timedate=databaseHandler.praseDate(d);
        ChatModel item = new ChatModel(ItemTyp,msg,timedate[1],timedate[0],rowid,visibility);
        Chat_list.add(item);
        mAdapter.updateAdapter(Chat_list);
        return rowid;
    }
    private void updaeteChatList(String content,int position, int visibility){
        //if visibility 1 Progress bar will be visiable otherwise gone
        //Contenet can be file path or msg
        ChatModel item=new ChatModel(Chat_list.get(position).getType(),content,
                Chat_list.get(position).getTime(),Chat_list.get(position).getDate(),Chat_list.get(position).getId(),visibility);
        Chat_list.set(position,item);
        mAdapter.updateAdapter(Chat_list);

    }
    private void revelView() {
        mRevealView = (LinearLayout) findViewById(R.id.reveal_items);
        mRevealView.setVisibility(View.GONE);
        gallery_btn = (ImageButton) findViewById(R.id.gallery_img_btn);
        photo_btn = (ImageButton) findViewById(R.id.photo_img_btn);
        gallery_btn.setOnClickListener(this);
        photo_btn.setOnClickListener(this);
    }
}
/*class DownloadTask extends AsyncTask<String,Integer,String> {
    private int contentLength,counter=0;
    private int calculatedprogress=0;
    private int position;
    private String id;
    private String phone;
    private Context context;
    @Override
    protected void onPreExecute() {
        //this.context=ChatListActivity.this;
        //databaseHandler=new DatabaseHandler(context);
        this.phone=active_friend_phone;
        //horizontal_progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... params) {
        this.position=Integer.parseInt(params[1]);
        this.id=params[2];
        File sdCard = Environment.getExternalStorageDirectory();
        //  final String path=durl.substring(durl.lastIndexOf("/")+1);
        String path=Uri.parse(params[0]).getLastPathSegment().toString();
        if(path.contains(".pdf"))
        {
            StoragePath=sdCard.getAbsolutePath().toString()+"/MetroIM/Received/Document/";
            File file = new File(Environment.getExternalStorageDirectory().getPath(), "/MetroIM/Received/Document/");
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        else if(path.contains(".jpg"))
        {
            StoragePath=sdCard.getAbsolutePath().toString()+"/MetroIM/Received/images/";
            File file = new File(Environment.getExternalStorageDirectory().getPath(), "/MetroIM/Received/images/");
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        try {

            StoragePath=StoragePath+path;
            URL url = new URL(params[0]);
            FileOutputStream f = new FileOutputStream(new File(StoragePath));
            long startTime = System.currentTimeMillis();
            URLConnection ucon = url.openConnection();
            contentLength=ucon.getContentLength();
            InputStream in = ucon.getInputStream();
            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = in.read(buffer)) > 0) {
                f.write(buffer, 0, len1);
                counter=counter+len1;
                publishProgress(counter);
            }
            f.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return StoragePath;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        calculatedprogress=(int)(((double) values[0]/contentLength)*100);
        //  progres_bar.setProgress(calculatedprogress);

    }

    @Override
    protected void onPostExecute(String path) {
        databaseHandler.updateMessage(id,path, 0);
        if(MessageInfo.ACTIVEFRIEND_PHONE!=null)
        {
            if(MessageInfo.ACTIVEFRIEND_PHONE.equals(phone))
            {
                System.out.println(phone+"="+MessageInfo.ACTIVEFRIEND_PHONE);
                mAdapter= new ChatAdapter(Chat_list);
                mListView.setAdapter(mAdapter);
                startActivity(getIntent());
                finish();
            }
        }
    }
}*/
/*    private String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }*/
/* take photo
 Intent Pick_image_intent=new Intent();
                Pick_image_intent.setType("image/*");
                Pick_image_intent.setAction(Intent.ACTION_GET_CONTENT);
                Intent takePhoto=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String pickTitel="Take or select Photo";
                Intent chooseIntent=Intent.createChooser(Pick_image_intent,pickTitel);
                chooseIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,new Intent[]{takePhoto});
                startActivityForResult(chooseIntent,REQUEST_CODE_PICTURE);
 */
/*
public String DownloadFromFile(final String durl,final int position) {
    final ProgressDialog progressDialog=new ProgressDialog(this);
    progressDialog.setMessage("DOWNLOADING......");
    // progressDialog.show();
    final Handler handler=new Handler();
    new Thread(new Runnable(){
        @Override
        public void run() {
            File sdCard = Environment.getExternalStorageDirectory();
            final String path=durl.substring(durl.lastIndexOf("/")+1);
            if(path.contains(".pdf"))
            {
                StoragePath=sdCard.getAbsolutePath().toString()+"/MetroIM/Received/Document/";
                File file = new File(Environment.getExternalStorageDirectory().getPath(), "/MetroIM/Received/Document/");
                if (!file.exists()) {
                    file.mkdirs();
                }
            }
            else if(path.contains(".jpg"))
            {
                StoragePath=sdCard.getAbsolutePath().toString()+"/MetroIM/Received/images/";
                File file = new File(Environment.getExternalStorageDirectory().getPath(), "/MetroIM/Received/images/");
                if (!file.exists()) {
                    file.mkdirs();
                }
            }
            try {

                StoragePath=StoragePath+path;
                URL url = new URL(durl);
                FileOutputStream f = new FileOutputStream(new File(StoragePath));
                long startTime = System.currentTimeMillis();
                URLConnection ucon = url.openConnection();
                InputStream in = ucon.getInputStream();
                byte[] buffer = new byte[1024];
                int len1 = 0;
                while ((len1 = in.read(buffer)) > 0) {
                    f.write(buffer, 0, len1);
                }
                f.close();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            if (StoragePath != null){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(path.endsWith(".jpg"))
                        {
                            Toast.makeText(context,path,Toast.LENGTH_LONG).show();
                      */
/*   ChatModel item=new ChatModel();
                         item.type=Chat_list.get(position).type;
                         item.imgpath=StoragePath;
                         item.time=Chat_list.get(position).time;
                         item.date=Chat_list.get(position).date;
                         item.imgId=Chat_list.get(position).imgId;
                         item.visibility=0;*//*

                            ChatModel item=new ChatModel(Chat_list.get(position).getType(),StoragePath,
                                    Chat_list.get(position).getTime(),Chat_list.get(position).getDate(),Chat_list.get(position).getId(),0);
                            Chat_list.set(position,item);
                            mAdapter = new ChatAdapter(Chat_list);
                            mListView.setAdapter(mAdapter);
                            circularprogressBar.setVisibility(View.GONE);
                            databaseHandler.updateMessage(Chat_list.get(position).getId(), StoragePath, active_friend_phone);

                        }
                        else if(path.endsWith(".pdf")){
                      */
/*   ChatModel item=new ChatModel();
                         item.type=Chat_list.get(position).type;
                         item.content=StoragePath;
                         item.time=Chat_list.get(position).time;
                         item.date=Chat_list.get(position).date;
                         item.imgId=Chat_list.get(position).imgId;
                         item.visibility=0;*//*

                            ChatModel item=new ChatModel(Chat_list.get(position).getType(),StoragePath,
                                    Chat_list.get(position).getTime(),Chat_list.get(position).getDate(),Chat_list.get(position).getId(),0);
                            Chat_list.set(position,item);
                            // mAdapter = new ChatAdapter(Chat_list);
                            mListView.setAdapter(mAdapter);
                            horizontal_progressBar.setProgress(View.GONE);
                            databaseHandler.updateMessage(Chat_list.get(position).getId(), StoragePath, active_friend_phone);
                        }
                    }
                });
            }
            else{
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context,"Failed",Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }).start();
    return StoragePath;
}*/
