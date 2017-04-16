package anwar.metroim.Backup;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import anwar.metroim.LocalHandeler.DatabaseHandler;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * Created by anwar on 4/4/2017.
 */

public class dbBackup {
    private static final String DATABASE_NAME ="MetroimData.db";
    private static final String L_TAG = "_X_";
    private final String MYROOT = "MetroIm";
    private final String MIME_TEXT = "text/plain";
    private final String MIME_FLDR = "application/vnd.google-apps.folder";
    private final String TITL = "DbTable";
    private final String T="titl";
    private final String GDID = "gdid";
    private final String MIME = "mime";
    private Context context;
    private SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Calendar cal = Calendar.getInstance();
    private String email;
    private Backup_email backup_email;
    private static final String TITL_FMT = "yyMMdd-HHmmss";
    private boolean ConnectionStatus=true;
    DatabaseHandler databaseHandle;
    SharedPreferences spref;
   private GDAAConnection gdaaConnection=new GDAAConnection();

    public dbBackup(Context context, String email) {
        gdaaConnection.ConnectToDrive(context,email);
        this.context = context;
        this.email = email;
        backup_email=new Backup_email(context);
        spref = PreferenceManager.getDefaultSharedPreferences(context);
        databaseHandle=new DatabaseHandler(context);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println(" Thread errror--------------->"+e);
        }
    }

    public void crateBackup(){
              deleteFolder();
        if(uploadTable(exportDbTable("Result_list")) && uploadTable(exportDbTable("Message_list")))
        {
            cal.add(Calendar.DAY_OF_MONTH,1);
            backup_email.addschedule(cal.getTime());
            System.out.println("db----Backup Successful"+cal.getTime());
        }else System.out.println("db----Backup Failed");
    }

    public boolean RestoreBackup(){
        //ArrayList<ContentValues> gfMyRoot = GDAA.search("appfolder", UT.MYROOT, null);  // app folder test

               ArrayList<ContentValues> gfMyRoot = gdaaConnection.search("root", MYROOT, null);
               if (gfMyRoot != null && gfMyRoot.size() == 1 ){
                   iterate(gfMyRoot.get(0),"name");
               }
               else return false;
               if(importDbTable("Result_list") && importDbTable("Message_list"))
               {
                   System.out.println("db----restore Successful");
                   return true;
               }
        return false;
    }
    private void deleteFolder(){
        ArrayList<ContentValues> gfMyRoot = gdaaConnection.search("root", MYROOT, null);
        if (gfMyRoot != null && gfMyRoot.size() == 1 ){
            ContentValues cv = gfMyRoot.get(0);
            iterate(cv);
            String gdid = cv.getAsString(GDID);
            gdaaConnection.trash(gdid);
        }
    }
    private void iterate(ContentValues gfParent) {
        ArrayList<ContentValues> cvs = gdaaConnection.search(gfParent.getAsString(GDID), null, null);
        if (cvs != null) for (ContentValues cv : cvs) {
            String gdid = cv.getAsString(GDID);
            if (gdaaConnection.isFolder(cv))
                iterate(cv);
        }
    }
    private boolean iterate(ContentValues gfParent,String name) {
        ArrayList<ContentValues> cvs =gdaaConnection.search(gfParent.getAsString(GDID), null, null);
        if (cvs != null) for (ContentValues cv : cvs) {
            String gdid = cv.getAsString(GDID);
            String titl = cv.getAsString(T);
            if (gdaaConnection.isFolder(cv)) {
                iterate(cv,"name");
            } else {
                byte[] buf =gdaaConnection.read(gdid);
                if (buf == null)
                    return false;
                String str = buf == null ? "" : new String(buf);
                  if(!gdaaConnection.str2File(str, cv.getAsString(T)))
                      return false;
            }
        }
        return true;
    }
    private boolean uploadTable(String path){
        String rsid = findOrCreateFolder("root", MYROOT);
        if (rsid != null) {
            rsid = findOrCreateFolder(rsid,TITL);
            if (rsid != null) {
                File fl =new File(path);
                String id = null;
                if (fl != null) {
                    id = gdaaConnection.createFile(rsid, TITL, MIME_TEXT, fl);
                   fl.delete();
                    if(id ==null)
                    {
                        System.out.println("db------ file fail to uploeded");
                        return false;
                    }
                }
            }
        }
        return true;
    }
    private String findOrCreateFolder(String prnt, String titl){
        ArrayList<ContentValues> cvs =gdaaConnection.search(prnt, titl, "application/vnd.google-apps.folder");
        String id, txt;
        if (cvs.size() > 0) {
            txt = "found ";
            id =  cvs.get(0).getAsString(GDID);
        } else {
            id = gdaaConnection.createFolder(prnt, titl);
        }
        return id;
    }
    private String exportDbTable(String table){
        String path=Environment.getExternalStorageDirectory()+ "/MetroIm/"+table+".csv";
     /*   File exportDir = new File(path);
        if (!exportDir.exists()) { exportDir.mkdirs(); }*/
        File file = new File(path);
        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            SQLiteDatabase db =databaseHandle.getReadableDatabase();
            Cursor curCSV = db.rawQuery("select * from "+table+"",null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while(curCSV.moveToNext()) {
                String arrStr[]=null;
                String[] mySecondStringArray = new String[curCSV.getColumnNames().length];
                for(int i=0;i<curCSV.getColumnNames().length;i++)
                {
                    mySecondStringArray[i] =curCSV.getString(i);
                }
                csvWrite.writeNext(mySecondStringArray);
            }
            csvWrite.close();
            curCSV.close();
        } catch (IOException e) {
        }
        return path;
    }
    private boolean importDbTable(String table)  {
        Date currentDate = GregorianCalendar.getInstance().getTime();
        String ss=currentDate.toString();
        boolean s=true;
        File csvfile=new File(Environment.getExternalStorageDirectory()+"/MetroIm/"+table+".csv/");
        try {
            CSVReader csvReader=new CSVReader(new FileReader(csvfile));
            String [] Line;
            String [] colNamw=null;
            SQLiteDatabase db =databaseHandle.getWritableDatabase();
            while ((Line = csvReader.readNext()) != null) {
                if(s){
                    colNamw=Line;
                    s=false;
                }
                else {
                    if(table.equals("Result_list"))
                    {
                        ContentValues values=new ContentValues();
                        values.put(colNamw[0],Line[0]);
                        values.put(colNamw[1],Line[1]);
                        values.put(colNamw[2],Line[2]);
                        values.put(colNamw[3],Line[3]);
                        db.insert(table,null,values);
                    }
                    else{
                        ContentValues values=new ContentValues();
                        values.put(colNamw[0],Line[0]);
                        values.put(colNamw[1],Line[1]);
                        values.put(colNamw[2],Line[2]);
                        values.put(colNamw[3],Line[3]);
                        values.put(colNamw[4],Line[4]);
                        values.put(colNamw[5],Line[5]);
                        values.put(colNamw[6],Line[6]);
                        values.put(colNamw[7],Line[7]);
                        db.insert(table,null,values);
                        /*databaseHandle.addMessage(Line[1],Line[3],Line[4],Line[5],Line[2],Line[6]);
                       */
                        System.out.println("db "+Line[1]+""+Line[2]);
                    }
                }
                }
        }catch (FileNotFoundException e){
            System.out.println("db------importDbTable exception e"+e);
            return false;
        }
        catch (IOException ee){
            System.out.println("db------importDbTable exception ee"+ee);
            return false;
        }
        csvfile.delete();
        return true;
    }
}
