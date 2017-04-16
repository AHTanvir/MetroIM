package anwar.metroim.PhoneContactSynchronization;

import android.content.Context;

/**
 * Created by anwar on 12/17/2016.
 */

public interface IphoneContacts {
    public String getAllContactNumber(Context context) throws Exception;
    String getPrefixCountyCode(Context context);
    public String CheckNumber(Context context, String number);

}
