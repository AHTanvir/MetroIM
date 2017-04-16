package anwar.metroim.PhoneContactSynchronization;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import anwar.metroim.CustomImage.getCustomImage;
import anwar.metroim.LocalHandeler.DatabaseHandler;
import anwar.metroim.LocalHandeler.IDatabaseHandler;
import anwar.metroim.R;

import static android.content.ContentValues.TAG;

/**
 * Created by anwar on 1/4/2017.
 */

public class StoreContacts {
    Context context;
    private IphoneContacts iphoneContacts = new PhoneContacts();
    getCustomImage CusImg= new getCustomImage();
    public static List<String> jssonList = new ArrayList<>();
    String n="0222";

    public StoreContacts(Context context) {
        this.context = context;
    }

    public void JosnDecoding( String jsonresult) {
        if (jsonresult.length() > 9) {
            try {
                JSONArray jarray = new JSONArray(jsonresult);
                for (int i = 0; i <jarray.length(); i++) {
                    String data = jarray.getString(i);
                        Log.e(TAG, "data on jsondecoding => " + data);
                  startSynce(data);
                }
            } catch (JSONException e) {
                Toast.makeText(context, "jsssssssssss" + e, Toast.LENGTH_LONG).show();
                Log.e(TAG, "onPostExecute > Try > JSONException => " + e);
                e.printStackTrace();
            }
        }
    }

    public void startSynce( String number) {
        int i=0;
        String nam = null;
        String num = null;
        String number_type=null;
        DatabaseHandler databaseHandler = new DatabaseHandler(this.context);
        Cursor ph =this.context.getContentResolver().query(ContactsContract.CommonDataKinds.
                Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.NUMBER + "=?", new String[]{number}, null);
        while (ph.moveToNext() ) {
            nam = ph.getString(ph.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            num = ph.getString(ph.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            int phoneType = ph.getInt(ph.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
            switch (phoneType) {
                case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                    number_type = "MOBILE";
                    break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                    number_type = "HOME";
                    break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                    number_type = "WORK";
                    break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                    number_type = "OTHER";
                    break;
                default:
                    break;
            }
        }
        ph.close();
        databaseHandler.addContact(nam, iphoneContacts.CheckNumber(context, num),
                "Whats Up",CusImg.base64Encode(CusImg.getProImg(context)), number_type, "3");
    }
}