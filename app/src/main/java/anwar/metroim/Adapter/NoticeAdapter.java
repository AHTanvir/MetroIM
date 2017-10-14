package anwar.metroim.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * Created by anwar on 10/5/2017.
 */

public class NoticeAdapter extends ArrayAdapter<String> {
    public NoticeAdapter(Context context, int textViewResourceId) {
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
