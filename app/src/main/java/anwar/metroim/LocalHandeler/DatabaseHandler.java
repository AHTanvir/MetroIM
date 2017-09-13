package anwar.metroim.LocalHandeler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.format.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import anwar.metroim.CustomImage.getCustomImage;
import anwar.metroim.Adapter.ChatModel;
import anwar.metroim.Adapter.RowItem;
import anwar.metroim.Adapter.arrayList;
import anwar.metroim.R;

/**
 * Created by anwar on 1/22/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper implements IDatabaseHandler {
    Calendar calander = Calendar.getInstance();
    DateFormat dataBaseStoreFormate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    DateFormat simpleDateFormat = new java.text.SimpleDateFormat("dd-MMM-yy");
    DateFormat simpleTimeFormat=new java.text.SimpleDateFormat("hh:mm a");
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME ="MetroimData.db";
    private static final String ContactList="Contact_list";
    private static final String MessageList="Message_list";
    private static final String Result="Result_list";
    private static final String Id="Id";
    private int type,visibility;
    private String content,conId;
    private static final String Contact_Name="contact_name";
    private static final String Contact_number="contact_number";
    private static final String Status="status";
    private static final String Profile_image="profile_picture";
    private static final String Contact_Type="contact_type";
    private static final String Messagetype="Messagetype";
    private static final String Messagetext="Messagetext";
    private static final String Messageimage="Messageimage";
    private static final String Date="Date";
    private static final String Subject="subject";
    private static final String Credit="credit";
    private static final String GPA="gpa";
    private static final String MNUMBER="mnumber";
    private static final String From_To="ForT";
    private static final String Seen="seen";
    private static final String Mstatus="mstatus";
    private List<RowItem> rowItems;
    private List<ChatModel> chatModels=new ArrayList<>();
    private static final String InfoUpdateStatus="infoupdatestatus";
    Context context;
private getCustomImage CusImg=new getCustomImage();
    public DatabaseHandler(Context context) {
        super(context, Environment.getExternalStorageDirectory() + "/MetroIm/"+ DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String Create_Table_Contact_list="CREATE TABLE "+ContactList+"("+Id+ " INTEGER PRIMARY KEY  AUTOINCREMENT," +
                " contact_name VARCHAR(55) NOT NULL," + "contact_number  VARCHAR(20) NOT NULL UNIQUE," +
                "status TEXT,profile_picture BLOB, contact_type TEXT NOT NULL,infoupdatestatus INT"+")";

        String Create_Table_Messaage="CREATE TABLE "+MessageList+"("+Id+ " INTEGER PRIMARY KEY  AUTOINCREMENT," +
                "mnumber VARCHAR(20) NOT NULL, Messagetype TEXT,Messagetext TEXT," +
                " Date TEXT NOT NULL , ForT text NOT NULL,seen INT, mstatus INT DEFAULT 0"+")";

        String Create_Table_result="CREATE TABLE "+Result+"("+Id+ " INTEGER PRIMARY KEY  AUTOINCREMENT, " +
                ""+Subject+" VARCHAR(55) NOT NULL,"+Credit+" VARCHAR(55) NOT NULL,"+GPA+" VARCHAR(55) NOT NULL"+")";
        db.execSQL(Create_Table_Contact_list);
        db.execSQL(Create_Table_Messaage);
        db.execSQL(Create_Table_result);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ContactList);
        db.execSQL("DROP TABLE IF EXISTS " + MessageList);
        db.execSQL("DROP TABLE IF EXISTS " + Result);
        onCreate(db);
    }
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
    ///////// All opration in contactList table////////////////////////////////////////////////////////////////////////////////////////
    public  void addContact(String name,String number,String status,String image, String type,String infoupdatestatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Contact_Name,name);
        values.put(Contact_number,number);
        values.put(Status,status);
        values.put(Profile_image,image);
        values.put(Contact_Type,type);
        values.put(InfoUpdateStatus,infoupdatestatus);
        db.insert(ContactList,null,values);
        db.close();
    }
    public String[] getContact(String number) {
        String coninfo[]=new String[3];
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery ="SELECT  * FROM " + ContactList+" WHERE contact_number='"+number+"'";
        Cursor cursor      = db.rawQuery(selectQuery, null);
        int i=0;
        if (cursor.moveToFirst()) {
            do {
                coninfo[0]=cursor.getString(1);
                coninfo[1]=cursor.getString(3);
                coninfo[2]=cursor.getString(4);
            } while (cursor.moveToNext());
        }
        return coninfo;
    }
    public List getContact(int CalledFor) {
        //If MetroImservice.getUserForUpdate() called then or if ContaclistFragment called then 0
       ArrayList<String> infolist=new ArrayList<>();
        rowItems=new ArrayList<>();
        rowItems.clear();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + ContactList;
        Cursor cursor      = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst())
        {
            do{
                if(CalledFor==0)
                {
                    String proImage=cursor.getString(cursor.getColumnIndex(Profile_image));
                    RowItem itm=new RowItem(cursor.getString(cursor.getColumnIndex(Contact_Name)),CusImg.getRoundedShape(proImage,100,100),
                            cursor.getString(cursor.getColumnIndex(Status)),cursor.getString(cursor.getColumnIndex(Contact_Type)),cursor.getString(cursor.getColumnIndex(Contact_number)));
                    rowItems.add(itm);
                }
                else if(CalledFor==1)
                {
                    infolist.add(cursor.getString(2)+";"+cursor.getString(6));
                    //row.add(cursor.getString(2)+";"+cursor.getString(6));
                }
                else if(CalledFor==2)
                {
                    RowItem item=new RowItem(cursor.getString(2));
                    rowItems.add(item);
                }

            }while (cursor.moveToNext());
        }
        cursor.close();
        if(CalledFor==1)
            return infolist;
        return rowItems;
    }
    public String[] getContactName(String userNumber){
        String []name=new String[2];
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + ContactList+" WHERE contact_number='"+userNumber+"'";
        Cursor cursor= db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                name[0]=cursor.getString(1);
                name[1]=cursor.getString(4);
            } while (cursor.moveToNext());
        }
        if(name[0]==null){
            name[0]=userNumber;
            name[1]=CusImg.base64Encode(BitmapFactory.decodeResource(context.getResources(),R.drawable.profile_image));
        }
        name[1]=name[1].replaceAll(" ","+");
        return name;
    }
    public void updateContact(String number,String status,String image,String updatestatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Status, status);
        values.put(Profile_image,image);
        values.put(InfoUpdateStatus,updatestatus);
        db.update(ContactList, values, Contact_number + " = ?",
                new String[] { String.valueOf(number) });
        db.close();
    }
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + ContactList;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int total_contact=cursor.getCount();
        cursor.close();
        return total_contact;
    }
    public int checkContactIsExist(String num) {
        String countQuery = "SELECT  * FROM " + ContactList+" WHERE contact_number='"+num+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int total_contact=cursor.getCount();
        cursor.close();
        return total_contact;
    }
    public void deleteContact(String number) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ContactList, Contact_number + " = ?",
                new String[] { String.valueOf(number) });
        db.close();
    }
    ///////// All opration in message table////////////////////////////////////////////////////////////////////////////////////////
    public  void addMessage(String number,String mText, String mDate,String ForT,String mtype,String seen){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MNUMBER,number);
        values.put(Messagetype,mtype);
        values.put(Messagetext,mText);
       // values.put(Messageimage,mImage);
        values.put(Date,mDate);
        values.put(From_To,ForT);
        values.put(Seen,seen);
        db.insert(MessageList,null,values);
        db.close();
    }

    public List getMessage(String userNumber){
        chatModels.clear();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM "+MessageList+" WHERE mnumber='"+userNumber+"'";
        Cursor cursor= db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                conId = cursor.getString(0);
                String mType=cursor.getString(2);
                if(cursor.getString(5).equals("to"))
                {
                    if(mType.equals("image"))
                    {
                        type=1;
                        content=cursor.getString(3);
                    }
                    else{
                        type =0;
                        content =cursor.getString(3);
                    }
                }
                else{
                    if(mType.equals("image"))
                    {
                         type=3;
                        if (cursor.getString(3).startsWith("http")){

                        }
                        content=cursor.getString(3);

                    }
                    else{
                        type =2;
                        if (cursor.getString(3).startsWith("http")){
                        }
                        content =cursor.getString(3);
                    }
                }
                String []dateTime=praseDate(cursor.getString(4));
                ChatModel item = new ChatModel(type,content,dateTime[1],dateTime[0],conId,cursor.getInt(cursor.getColumnIndex(Mstatus)));
               // item.date=dateTime[0];
                //item.time=dateTime[1];
                chatModels.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return chatModels;
    }


    public int getMessageCount() {
        String countQuery = "SELECT  * FROM " + MessageList;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        return cursor.getCount();
    }
    public List getViewForChatFrag(){
        rowItems=new ArrayList<>();
        rowItems.clear();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+MessageList+" m LEFT JOIN "+ContactList+" c ON" +
                " m.mnumber=c.contact_number GROUP BY contact_number ORDER BY Date DESC";
        Cursor cursor= db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                String number=cursor.getString(cursor.getColumnIndex(MNUMBER));
                String date=cursor.getString(cursor.getColumnIndex(Date));
                String STATUS=null,mtype;
                mtype=cursor.getString(cursor.getColumnIndex(Messagetype));
                if(cursor.getString(cursor.getColumnIndex(Seen))=="0")
                {
                    STATUS="New message";
                }
                else if (mtype.equals("text")){
                    STATUS=cursor.getString(cursor.getColumnIndex(Messagetext));
                }
                else if(mtype.equals("image"))
                {
                    STATUS="PHOTO";
                }
                else if(mtype.equals("pdf"))
                {
                    STATUS="File";
                }
                try{
                    Date perseDate=dataBaseStoreFormate.parse(date);
                    date=simpleDateFormat.format(perseDate);
                }
                catch (ParseException e){
                    e.printStackTrace();
                }
                String name=null,proimg=null;
                Bitmap bitmap;
                name=cursor.getString(cursor.getColumnIndex(Contact_Name));
                proimg=cursor.getString(cursor.getColumnIndex(Profile_image));
                if(name==null && proimg==null)
                {
                    name=number;
                    bitmap=CusImg.getProImg(context);
                }
                else bitmap=CusImg.getRoundedShape(proimg,100,100);
                RowItem itm=new RowItem(name,bitmap,STATUS,date,number);
                rowItems.add(itm);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return rowItems;
       /* rowItems2.clear();
        SQLiteDatabase db = this.getReadableDatabase();
       //String selectQuery = "SELECT DISTINCT  contact_number FROM "+table2+" ORDER BY Date DESC";
        String selectQuery = "SELECT * FROM "+MessageList+" GROUP BY contact_number ORDER BY Date DESC";
        Cursor cursor= db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                String number=cursor.getString(1);
                String date=cursor.getString(4);
                String STATUS;
                if(cursor.getString(6)=="0")
                {
                    STATUS="New message";
                }
                else if (cursor.getString(3)!=null){
                    STATUS=cursor.getString(3);
                }
                else {
                    STATUS="PHOTO";
                }
                try{
                    Date perseDate=dataBaseStoreFormate.parse(date);
                    date=simpleDateFormat.format(perseDate);
                }
                catch (ParseException e){
                    e.printStackTrace();
                }
                String []arr=getContactName(number);
                arr[1]=arr[1].replaceAll(" ", "+");
                RowItem itm=new RowItem(arr[0],CustomImage.getRoundedShape(arr[1],100,100),STATUS,date,number);
                rowItems2.add(itm);
            } while (cursor.moveToNext());
        }
        cursor.close();*/
    }

    public int getContactsConversinCount(String number) {
        String countQuery = "SELECT  * FROM  "+ MessageList+" WHERE mnumber='"+number+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int total_contact=cursor.getCount();
        cursor.close();
        return total_contact;
    }



    public void updateMessage(String number){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Seen,1);
        db.update(MessageList, values, MNUMBER + " = ?",
                new String[] { String.valueOf(number) });
        db.close();
    }
    public void updateMessage(String rowId,int visibility){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Mstatus,visibility);
        db.update(MessageList, values, Id + " = ?",
                new String[] { String.valueOf(rowId) });
        db.close();
    }
    public void updateMessage(String id,String path,int visibility){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Messagetext,path);
        values.put(Mstatus,visibility);
        db.update(MessageList, values, Id + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
        //getMessage(number);
    }
    public void deleteMessage(String number){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MessageList, MNUMBER + " = ?",
                new String[] { String.valueOf(number) });
        db.close();
    }
    public String lastRowId(String number,String d){
        String id=null;
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "SELECT Id FROM '"+MessageList+"' WHERE mnumber='"+number+"' AND Date='"+d+"'  ORDER BY Id DESC LIMIT 1 ";
        Cursor cursor      = db.rawQuery(Query, null);
        int i=0;
        if (cursor.moveToFirst()) {
            do {
               id=cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return id;
    }
////////////////ALL Operation in result Table///////////////////////////////////////////////////////////////////
    public  void addResult(String subject,String credit,String gpa) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Subject,subject);
        values.put(Credit,credit);
        values.put(GPA,gpa);
        db.insert(Result,null,values);
        db.close();
    }
    public List getResult(List<RowItem> row) {
        row.clear();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery ="SELECT  * FROM " + Result;
        Cursor cursor      = db.rawQuery(selectQuery, null);
        int i=0;
        if (cursor.moveToFirst()) {
            do {
                RowItem rowItem=new RowItem(cursor.getString(1),cursor.getString(2),cursor.getString(3));
                row.add(rowItem);
            } while (cursor.moveToNext());
        }
        return row;
    }
    public void updateResult(String subject,String credit,String gpa){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Subject,subject);
        values.put(Credit,credit);
        values.put(GPA,gpa);
        db.update(Result, values, Subject + " = ?",
                new String[] { String.valueOf(subject) });
        db.close();
    }
    public void deleteResult(String subject) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Result, Subject+ " = ?",
                new String[] { String.valueOf(subject) });
        db.close();
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  public String[] praseDate(String StoreDate){
        String arr[]=new String[2];
        java.util.Date perseDate=null;

        try{
            perseDate=dataBaseStoreFormate.parse(StoreDate);
            arr[0]=simpleDateFormat.format(perseDate);//date
            arr[1]=simpleTimeFormat.format(perseDate);//time
        }
        catch (ParseException e){
            e.printStackTrace();
        }
        calander.setTime(perseDate);
        if((DateUtils.isToday(calander.getTimeInMillis()) && (arrayList.getmInstance().getCurrentDateHolder() !="Today")))
        {
            arr[0]="Today";
            arrayList.getmInstance().setCurrentDateHolder("Today");
        }
        else if((!arrayList.getmInstance().getCurrentDateHolder().equals(arr[0]) && !(DateUtils.isToday(calander.getTimeInMillis())))) {
            arrayList.getmInstance().setCurrentDateHolder(arr[0]);
        }
        else {
            arr[0]=" ";
        }
            return arr;
    }
}
