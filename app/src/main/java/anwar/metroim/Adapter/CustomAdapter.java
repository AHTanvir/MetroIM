package anwar.metroim.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import anwar.metroim.R;

/**
 * Created by anwar on 12/16/2016.
 */

public class CustomAdapter extends BaseAdapter implements Filterable {
    Context context;
    private ValueFilter valueFilter;
    List<RowItem> rowItems;
    List<RowItem> Contacts=new ArrayList<>();
    private LayoutInflater inflater;
    public CustomAdapter(Context context,List<RowItem>rowItems){
        this.context = context;
        this.rowItems = rowItems;
        Contacts=rowItems;
      //  Toast.makeText(context,"constractor ",Toast.LENGTH_SHORT).show();
       // this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      //  this.getFilter();
    }

    public CustomAdapter(Context context) {this.context = context;}

    public int getCount(){
        return rowItems.size();
    }
    public Object getItem(int position){
        return  rowItems.get(position);
    }
    public long getItemId(int position){
        return rowItems.indexOf(getItem(position));
    }

    private class ViewHolder{
        ImageView profile_picture;
        TextView contact_name;
        TextView contact_type;
        TextView status;
        String contact_number;
    }
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder=null;
        LayoutInflater mInflater=(LayoutInflater)context.
                getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        /*
        if use this condition if(convertView==null) app get crash during scorling
        if(convertView==null)
        {
            convertView=mInflater.inflate(R.layout.list_item_contact,null);
            holder.contact_name=(TextView)convertView.findViewById(R.id.contact_name);
            holder.profile_picture=(ImageView)convertView.findViewById(R.id.profile_picture);
            holder.status=(TextView)convertView.findViewById(R.id.status);
            holder.contact_type=(TextView)convertView.findViewById(R.id.contact_type);
        }
        else{
            holder=(ViewHolder)convertView.getTag();
        }
        */
       if(convertView==null)
       {
           holder=new ViewHolder();
           convertView=mInflater.inflate(R.layout.list_item_contact,null);
           holder.contact_name=(TextView)convertView.findViewById(R.id.contact_name);
           holder.profile_picture=(ImageView)convertView.findViewById(R.id.profile_picture);
           holder.status=(TextView)convertView.findViewById(R.id.status);
           holder.contact_type=(TextView)convertView.findViewById(R.id.contact_type);
           convertView.setTag(holder);
       }
       else{
           holder=(ViewHolder)convertView.getTag();
       }
        RowItem row_pos=rowItems.get(position);
            holder.contact_name.setText(row_pos.getContact_name());
            //holder.profile_picture.setImageResource(row_pos.getProfile_picture_id());
            holder.profile_picture.setImageBitmap(row_pos.getPro_image());
            holder.status.setText(row_pos.getStatus());
            holder.contact_type.setText(row_pos.getContact_type());
        return convertView;
    }
    public void updateAdapter() {

        //and call notifyDataSetChanged
        notifyDataSetChanged();
    }
    @Override
    public Filter getFilter() {
        if(valueFilter==null) {
            valueFilter=new ValueFilter();
        }
        return valueFilter;
    }
    private class ValueFilter extends Filter {
        //Invoked in a worker thread to filter the data according to the constraint.
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<RowItem> filterList = new ArrayList<RowItem>();
                for (int i = 0; i < Contacts.size(); i++) {
                    if ((Contacts.get(i).getContact_name().toUpperCase())
                            .startsWith(constraint.toString().toUpperCase())) {
                        RowItem contacts = new RowItem(Contacts.get(i).getContact_name(), Contacts.get(i).getPro_image(),
                                Contacts.get(i).getStatus(), Contacts.get(i).getContact_type(), Contacts.get(i).getContact_number());
                        filterList.add(contacts);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = Contacts.size();
                results.values = Contacts;
            }
            return results;
        }
        //Invoked in the UI thread to publish the filtering results in the user interface.
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
           rowItems=(ArrayList<RowItem>) results.values;
            notifyDataSetChanged();

        }
    }
    public void updateAdapter(List<RowItem> updateList ) {
        //and call notifyDataSetChanged
        rowItems=updateList;
        notifyDataSetChanged();
    }
}
