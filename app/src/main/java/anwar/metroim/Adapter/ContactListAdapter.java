package anwar.metroim.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import anwar.metroim.Interface.OnItemClickListeners;
import anwar.metroim.Model.ChatListModel;
import anwar.metroim.Model.RowItem;
import anwar.metroim.R;

/**
 * Created by anwar on 9/27/2017.
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.MyViewHolder> implements Filterable {
    private ContactListAdapter.ValueFilter valueFilter;
    private List<RowItem> rowItems;
    private OnItemClickListeners listeners;
    private List<RowItem> Contacts=new ArrayList<>();

    public ContactListAdapter(List<RowItem> rowItems, OnItemClickListeners listeners) {
        this.rowItems = rowItems;
        this.listeners = listeners;
        Contacts=rowItems;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_contact,parent,false);
        ContactListAdapter.MyViewHolder holder=new ContactListAdapter.MyViewHolder(view);
        return holder;
    }
    public RowItem getItem(int position){
        return rowItems.get(position);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder,final int position) {
        RowItem row_pos=rowItems.get(position);
        holder.contact_name.setText(row_pos.getContact_name());
        holder.profile_picture.setImageBitmap(row_pos.getPro_image());
        holder.status.setText(row_pos.getStatus());
        holder.contact_type.setText(row_pos.getContact_type());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listeners.onClick(v.getRootView(),position);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listeners.onLongClick(v.getRootView(),position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return rowItems.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView profile_picture;
        TextView contact_name;
        TextView contact_type;
        TextView status;
        String contact_number;
        public MyViewHolder(View itemView) {
            super(itemView);
            contact_name=(TextView)itemView.findViewById(R.id.contact_name);
            profile_picture=(ImageView)itemView.findViewById(R.id.profile_picture);
            status=(TextView)itemView.findViewById(R.id.status);
            contact_type=(TextView)itemView.findViewById(R.id.contact_type);
        }
    }
    @Override
    public Filter getFilter() {
        if(valueFilter==null) {
            valueFilter=new ContactListAdapter.ValueFilter();
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
        Contacts=updateList;
        notifyDataSetChanged();
    }
}
