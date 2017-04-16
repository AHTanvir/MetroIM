package anwar.metroim.BloodDonor;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * Created by anwar on 3/4/2017.
 */

public class spinnerAdapter extends ArrayAdapter<String> {
    public spinnerAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        // TODO Auto-generated constructor stub

    }

    @Override
    public int getCount() {

        // TODO Auto-generated method stub
        int count = super.getCount();

        return count>0 ? count-1 : count ;


    }
}

