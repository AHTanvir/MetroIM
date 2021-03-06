package anwar.metroim.service;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import anwar.metroim.Backup.DbBackup;
import anwar.metroim.ChatScreen.ChatListActivity;
import anwar.metroim.CustomImage.getCustomImage;
import anwar.metroim.Adapter.arrayList;
import anwar.metroim.LocalHandeler.DatabaseHandler;
import anwar.metroim.LoginActivity;
import anwar.metroim.Manager.SessionManager;
import anwar.metroim.MessageInfo;
import anwar.metroim.PhoneContactSynchronization.PhoneContacts;
import anwar.metroim.PhoneContactSynchronization.StoreContacts;
import anwar.metroim.R;
import anwar.metroim.socketOperation.*;


/**
 * Created by anwar on 12/5/2016.
 */

public class MetroImservice extends Service implements Iappmanager {
    public static final String TAKE_MESSAGE = "Take_Message";
    public static final String LIST_UPDATED = "List_Upadate";
    public ConnectivityManager conManager = null;
    private final int UPDATE_TIME_PERIOD = 15000;
    private Iappmanager Iappmanager;
    private getCustomImage customImages=new getCustomImage();
    private Calendar calander = Calendar.getInstance();
    private DateFormat dbfmtDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SessionManager session;
    private SocketInterface socketOperator = new SocketConnection(this);
    private DatabaseHandler databaseHandler = new DatabaseHandler(this);
    private final IBinder mBinder = new IMBinder();
    private String userphone;
    private String password;
    String rawmList = new String();
    private boolean authenticatedUser = false;
    private SharedPreferences spre;
    private Timer timer=null;
    public class IMBinder extends Binder {
        public Iappmanager getService() {
            return MetroImservice.this;
        }

    }
    public void onCreate() {
        System.out.println("----Oncreate");
        conManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        session=new SessionManager(getApplicationContext());
        getApplicationContext().getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, mObserver);
        timer = new Timer();
        setUserId();
       Thread thread = new Thread() {
            @Override
            public void run() {
                Random random = new Random();
                int tryCount = 0;
                while (socketOperator.startListening(1000 + random.nextInt(2000),MetroImservice.this) == 0) {
                    tryCount++;
                    if (tryCount > 10) {
                        // if it can't listen a port after trying 10 times, give up...
                        break;
                    }
                }
            }
        };
            thread.start();

    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(timer ==null) {
            timer=new Timer();
            socketOperator=new SocketConnection(this);
            databaseHandler = new DatabaseHandler(this);
        }
         spre = PreferenceManager.getDefaultSharedPreferences(this);
        if(setUserId()){
            arrayList.getmInstance().setServiceIsRunning(true);
            SceduleTimer();
            System.out.println("m-------->service running");
        }else exit();
        return START_STICKY;
    }
    private boolean setUserId(){
        if(session.isLoggedIn()){
            HashMap<String, String> user = session.getUserDetails();
            this.userphone= user.get(SessionManager.KEY_PHONE);
            this.password = user.get(SessionManager.KEY_PASSWORD);
            return true;
        }
        return false;
    }

    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public boolean isNetworkConnected() {
        NetworkInfo netInfo = conManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    public void onDestroy() {
        super.onDestroy();
    }

    public void exit() {
        System.out.println("m-------->services Stop self");
        timer.cancel();
        socketOperator.exit();
        arrayList.getmInstance().setServiceIsRunning(false);
        MetroImservice.this.stopSelf();
    }

    public String getMessage() throws UnsupportedEncodingException {
        String result =socketOperator.sendHttpRequest(getAuthenticateUserParams(this.userphone,this.password));
        if(result !=LoginActivity.NO_NEW_UPDATE) {
            this.decodeMessage(result);
        }
        return result;
    }

    public String sendMessage(String to,String mType,String message,String RowId) throws UnsupportedEncodingException {
        String date=dbfmtDate.format(calander.getTime());
        String msg=message;
        String result="0";
        if(message!=null && message !="") {
            String params = "phone=" + URLEncoder.encode(this.userphone, "UTF-8") +
                    "&password=" + URLEncoder.encode(this.password, "UTF-8") +
                    "&action=" + URLEncoder.encode("sendMessage", "UTF-8") +
                    "&tto=" + URLEncoder.encode(to, "UTF-8") +
                    "&mType=" + URLEncoder.encode(mType, "UTF-8") +
                    "&message=" + URLEncoder.encode(message, "UTF-8") +
                    "&";
            if(MessageInfo.frndStatus!=null && MessageInfo.frndStatus=="Online")
                result=socketOperator.sendDirectMsg(this.userphone,date,mType,message);
            if (result=="0")
                result = socketOperator.sendHttpRequest(params);
        }
            if(result.equals("1")){
                databaseHandler.updateMessage(RowId,3);
            }
            else  databaseHandler.updateMessage(RowId,4);
        return result;
    }

    public String getUserUpdateInfo() throws UnsupportedEncodingException {
        String params = "&phone=" + URLEncoder.encode(this.userphone,"UTF-8")+
                "&password=" + URLEncoder.encode(this.password,"UTF-8")+
                "&action=" +  URLEncoder.encode("updateUsersInfo","UTF-8") +
                "&contact_list=" + URLEncoder.encode(getUserForUpdate(),"UTF-8") +
                "&";
        String result = socketOperator.sendHttpRequest(params);
        //Server send result in json array formet so need to decode and store store local database
        if(result !="0") {
            this.decodeUserUpdateInfo(result);
        }
        return "1";
    }

    public void getLastSeen() throws UnsupportedEncodingException{
        String params="&phone="+URLEncoder.encode(this.userphone,"UTF-8")+
                "&password="+URLEncoder.encode(this.password,"UTF-8")+
                "&port=" + URLEncoder.encode(Integer.toString(socketOperator.getListeningPort()), "UTF-8") +
                "&action="+URLEncoder.encode("getLastSeen","UTF-8")+
                "&friendphone="+URLEncoder.encode(MessageInfo.ACTIVEFRIEND_PHONE,"UTF-8")+
                "&";
        String res=socketOperator.sendHttpRequest(params);
        if(res !="0") {
            try {
                JSONObject ja=new JSONObject(res);
                Intent lastseen = new Intent("LAST_SEEN");
                lastseen.putExtra("LASTSEEN",ja.getString("a"));
                lastseen.putExtra("IP",ja.getString("b"));
                lastseen.putExtra("PORT",ja.getString("c"));
                sendBroadcast(lastseen);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public String signUpUser(String nameText, String emailText, String passwordText,
                             String deptText, String batchText, String idText, String phoneText) throws UnsupportedEncodingException{
        String params = "&phone=" +URLEncoder.encode(phoneText,"UTF-8") +
                "&password=" + URLEncoder.encode(passwordText,"UTF-8") +
                "&action=" +URLEncoder.encode("signUpUser","UTF-8") +
                "&name=" + URLEncoder.encode(nameText,"UTF-8") +
                "&email=" + URLEncoder.encode(emailText,"UTF-8")+
                "&dept=" + URLEncoder.encode(deptText,"UTF-8") +
                "&batch=" + URLEncoder.encode(batchText,"UTF-8") +
                "&Id=" + URLEncoder.encode(idText,"UTF-8") +
                "&";
        String result = socketOperator.sendHttpRequest(params);
        return result;
    }

    @Override
    public String donorOperation(String param)throws UnsupportedEncodingException {
        String params = "&phone=" + URLEncoder.encode(this.userphone, "UTF-8") +
                "&password=" +URLEncoder.encode(this.password, "UTF-8")+
                param;
        return socketOperator.sendHttpRequest(params);
    }
    public String authenticateUser(String usernameText, String passwordText) throws UnsupportedEncodingException {
        this.userphone = usernameText;
        this.password = passwordText;
        this.authenticatedUser = false;
        String result =this.getMessage();
        rawmList = result;
        if (result != null && !result.equals(LoginActivity.AUTHENTICATION_FAILED)) {
            this.authenticatedUser = true;
        }
        return result;
    }
    //
    private  void SceduleTimer() {
        try {
            timer.schedule(new TimerTask() {
                public void run() {
                    if (isNetworkConnected()) {
                        try {
                            final String email = getBackupEmail();
                            if (email != null) {
                                if (new Date().after(dbfmtDate.parse(spre.getString("date", "")))) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            new DbBackup(MetroImservice.this, email);
                                        }
                                    }).start();
                                }
                            }
                            String result2 = MetroImservice.this.getMessage();
                            if (MessageInfo.ACTIVEFRIEND_PHONE != null) {
                                MetroImservice.this.getLastSeen();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("m e " + e);
                        }
                    } else exit();

                }
            }, UPDATE_TIME_PERIOD, UPDATE_TIME_PERIOD);
        } catch (IllegalArgumentException ex) {
            System.out.println("meroimservice time schedular IllegalArgumentException "+ex);
        }catch(IllegalStateException e){System.out.println("meroimservice time schedular IllegalStateException "+e);}
    }
    private String getAuthenticateUserParams(String usernameText, String passwordText) throws UnsupportedEncodingException {
        String params = "&phone=" + URLEncoder.encode(usernameText, "UTF-8") +
                "&password=" + URLEncoder.encode(passwordText, "UTF-8") +
                "&action=" + URLEncoder.encode("authenticateUser", "UTF-8") +
                "&port=" + URLEncoder.encode(Integer.toString(socketOperator.getListeningPort()), "UTF-8") +
                "&";

        return params;
    }
    // this method update contact to find MetroIM users
    public String UpdateContacts() throws Exception {
        String params = null;
        String result=null;
        String contactlist=new PhoneContacts().getAllContactNumber(this);
        if(contactlist !=null) {
            params = "&phone=" + URLEncoder.encode(this.userphone, "UTF-8") +
                    "&password=" + URLEncoder.encode(this.password, "UTF-8") +
                    "&action=" +URLEncoder.encode("contactUpdate", "UTF-8") +
                    "&json=" +URLEncoder.encode(contactlist, "UTF-8") +
                    "&";
            result= socketOperator.sendHttpRequest(params);
            if(result !=LoginActivity.NO_NEW_UPDATE) {
                new StoreContacts(this).JosnDecoding(result);
                result="updated";
            }
        }
        else result="No Contacts for update";
        return result;
    }
    // this method will be invoke whenever user want to update profile photo or status or after loging to fetch information
    //para=getInfo or photo or status
    //this method return value acoording para String
    //
    public String updateInfo(String para)throws UnsupportedEncodingException {
        System.out.println("userphone "+this.userphone );
        String params = "&phone=" + URLEncoder.encode(this.userphone, "UTF-8") +
                "&password=" +URLEncoder.encode(this.password, "UTF-8")+
                "&action=" +URLEncoder.encode("updateInfo", "UTF-8")  +
                para;
        String result = socketOperator.sendHttpRequest(params);
        if(para.contains("&updatePassword=") && result.equals("1"))
            setUserId();
        return result;
    }

    public void decodeUserUpdateInfo(String jsonresult) {
        JSONObject jsonObject= null;
        try {
            jsonObject = new JSONObject(jsonresult);
            JSONArray jsonArray=jsonObject.getJSONArray("contactinfo");
            for(int i=0; i<jsonArray.length(); i++) {
                JSONObject jsonObj= jsonArray.getJSONObject(i);
                databaseHandler.updateContact(jsonObj.getString("number"),jsonObj.
                        getString("contactstatus"),jsonObj.getString("photo").replace(" ","+"),jsonObj.getString("infoupdatestatus"));

            }
            if(jsonArray.length()>0)
            {
                Intent i = new Intent(LIST_UPDATED);
                sendBroadcast(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public String getBackupEmail(){
        SharedPreferences spref = PreferenceManager.getDefaultSharedPreferences(this);
        if(spref.contains("email"))
            return spref.getString("email","");
        else return null;
    }
    public void MessageReceived(String from,String sentdt,String mType,String message){
        DatabaseHandler db=new DatabaseHandler(this);
        String []name=db.getContactName(from);
        db.addMessage(from,message,sentdt,"from",mType,"0");
        Intent i=new Intent(TAKE_MESSAGE);
        i.putExtra(MessageInfo.FROM,from);
        i.putExtra(MessageInfo.SENDERNAME,name[0]);
        i.putExtra(MessageInfo.SENDATE,sentdt);
        i.putExtra(MessageInfo.MESSAGE_TYPE,mType);
        i.putExtra(MessageInfo.MESSAGE_LIST,message);
        sendBroadcast(i);
        if(MessageInfo.ACTIVEFRIEND_PHONE==null) {
            ShowNotification(name[0],name[1],from,message);
        }
        db.close();
    }
    public void decodeMessage(String jsonresult) {
        try{
            JSONObject jsonObject=new JSONObject(jsonresult);
            JSONArray jsonArray=jsonObject.getJSONArray("message");
            for(int i=0; i<jsonArray.length(); i++) {
                JSONObject jsonObj =jsonArray.getJSONObject(i);
                //parts[0]=from,parts[1]=sentDate,parts[2]=MessageType,parts[3]=Message
                this.MessageReceived(jsonObj.getString("sfrom"),jsonObj.getString("sentDt"),
                        jsonObj.getString("messagesType"),jsonObj.getString("messageText"));
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }
    public void ShowNotification(String name,String img,String number,String msg){
       // NotificationManager notificationManager=null;
        NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Intent intent=new Intent(this, ChatListActivity.class);
        intent.putExtra(MessageInfo.NAME,name);
        intent.putExtra(MessageInfo.IMAGE,img);
        intent.putExtra(MessageInfo.ACTIVEFRIENDPHONE,number);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,(int)System.currentTimeMillis(),intent,0);
        Notification notification=new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_noti)
                .setContentTitle(name)
                .setContentText(msg)
                .setLargeIcon(customImages.getRoundedShape(img,100,100))
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(pendingIntent)
                .build();
        notificationManager.notify((int)System.currentTimeMillis(),notification);

    }

    public String getUserForUpdate(){
       // infoUpdateList=databaseHandler.getContact(1);
        List<String> infolist=new ArrayList<>();
        infolist=databaseHandler.getContact(1);
        JSONArray jsonArray=new JSONArray(infolist);
        return jsonArray.toString();
        //this method return only 10 contacts for update..........................................
    /*    ArrayList<String> stringArray = new ArrayList<String>();
        stringArray.clear();
        if(infoUpdateCounter==0)
        {
           infoUpdateList=databaseHandler.getContact(1);
            infoUpdateCounter=0;
        }
        int c=0;
        for(int i=infoUpdateCounter;i<infoUpdateList.size();i++)
        {
            stringArray.add(infoUpdateList.get(i).getContact_number()+";"+
            infoUpdateList.get(i).getContact_name());
            System.out.println(infoUpdateList.get(i).getContact_number());
            if(c==10){
                infoUpdateCounter=i;
                break;
            }
            if(i==infoUpdateList.size()-1){
                infoUpdateCounter=0;
            }
            c++;
        }
            JSONArray jsonArray=new JSONArray(stringArray);
        return jsonArray.toString();*/

    }
    private ContentObserver mObserver = new ContentObserver(new Handler()) {

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

        }
        public void onChange(boolean selfChange, Uri uri) {
            try {
                MetroImservice.this.UpdateContacts();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    public void FileUpload(final String path,final String phone,final String type,final int position,final String RowId){
        new Thread(new Runnable() {
            String url=null;
            String res=null;
            @Override
            public void run() {
                url=socketOperator.sendHttpFileUploadRequest(path);
                try {
                    res=MetroImservice.this.sendMessage(phone,type,url,RowId);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if(MessageInfo.ACTIVEFRIEND_PHONE!=null) {
                    if (phone.equals(MessageInfo.ACTIVEFRIEND_PHONE)) {
                        Intent i = new Intent("UPLOAD_STATUS");
                        i.putExtra(MessageInfo.STATUS, res);
                        i.putExtra(MessageInfo.POSITION, position);
                        sendBroadcast(i);
                    }
                }
            }
        }).start();
    }
    public void fileDownload(final String url,final String phone,final String rowId,final int position){
        new Thread(new Runnable() {
            boolean status=false;
            String path=null;
            @Override
            public void run() {
               path=socketOperator.sendHttpFileDownloadRequest(url);
                if(path !=null)
                {
                   databaseHandler.updateMessage(rowId,path,0);
                    status=true;
                }
                else {
                    path=url;
                    databaseHandler.updateMessage(rowId,path,0);
                }

               if(MessageInfo.ACTIVEFRIEND_PHONE!=null) {
                    if (phone.equals(MessageInfo.ACTIVEFRIEND_PHONE)) {
                        Intent i = new Intent("DOWNLOAD_STATUS");
                        i.putExtra(MessageInfo.STATUS, status);
                        i.putExtra(MessageInfo.POSITION, position);
                        i.putExtra(MessageInfo.PATH, path);
                        sendBroadcast(i);
                    }
                }
            }
        }).start();
    }
    public String send_Sms(String phone){
        String id=generateRandomId();
        String msg="Your MetroIM Verification code is "+id;
        String res=socketOperator.sendSms(phone,msg);
        System.out.println("Mservice sms"+res+" id="+id);
        if(res.contains("messageId"))
            return id;
        else return "0";
    }
    public String sendNoticeToStudent(String dept,String id,String msg,String type,String name){
        String params="0";
        try {
            if(type!="text"){
                msg=socketOperator.sendHttpFileUploadRequest(msg);
                if(msg==null && msg=="")
                    return "0";
            }
            params = "&phone=" + URLEncoder.encode(this.userphone, "UTF-8") +
                     "&password=" + URLEncoder.encode(this.password, "UTF-8") +
                     "&action=" +URLEncoder.encode("sendNotice", "UTF-8") +
                    "&dept=" +URLEncoder.encode(dept, "UTF-8") +
                    "&name=" +URLEncoder.encode(name, "UTF-8") +
                     "&id=" +URLEncoder.encode(id, "UTF-8") +
                    "&type=" +URLEncoder.encode(type, "UTF-8") +
                    "&msg=" +URLEncoder.encode(msg, "UTF-8") +
                     "&";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return socketOperator.sendHttpRequest(params);
    }
    @Override
    public String resetPassword(String Phone) throws UnsupportedEncodingException {
        password=this.generateRandomId();
        String params = "&phone=" + URLEncoder.encode(Phone, "UTF-8") +
                "&password=" +URLEncoder.encode(password, "UTF-8")+
                "&action=" +URLEncoder.encode("resetPassword", "UTF-8")  +
                "&";
        String result=null;
        if (socketOperator.sendHttpRequest(params).equals("1"))
        {
            String msg="Your MetroIM new passwoid is: "+password;
            result=socketOperator.sendSms(Phone,msg);
            if(result.contains("messageId"))
                return "1";
        }
            return "0";
    }

    public String generateRandomId(){
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        Random rnd = new Random();
        //StringBuilder sb = new StringBuilder((100000 + rnd.nextInt(900000)) );
        /*for (int i = 0; i < 5; i++)
            sb.append(chars[rnd.nextInt(chars.length)]);*/

        return String.valueOf((100000 + rnd.nextInt(900000)));
    }
    public int generatePortNO(String number){
        String port=number.substring(4,9);
        System.out.println(number+" is portNO: "+port);
        return Integer.parseInt(port);
    }
    public String test(){
        new Thread(new Runnable() {
            int i=0;
            @Override
            public void run() {
                while (true)
                {
                  /*  try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/

                }
            }
        }).start();
        return "return";
    }
public void t(){
    System.out.println("-caled from socket class");
}
    public void urlDecode(String msg){
        try {
            String d[]=msg.split("&");
            new MetroImservice().MessageReceived(URLDecoder.decode(d[0],"UTF-8"),URLDecoder.decode(d[1],
                    "UTF-8"),URLDecoder.decode(d[2],"UTF-8"),URLDecoder.decode(d[3],"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}