package anwar.metroim.Adapter;

import android.content.Context;
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

public class ChatFragmentAdapter extends RecyclerView.Adapter<ChatFragmentAdapter.MyViewHolder> implements Filterable{
    private List<ChatListModel> list=new ArrayList<>();
    private OnItemClickListeners listeners;
    private Context context;
    private ChatFragmentAdapter.ValueFilter valueFilter;
    private List<ChatListModel> filterList=new ArrayList<>();

    public ChatFragmentAdapter(List<ChatListModel> list, OnItemClickListeners listeners) {
        this.list = list;
        this.listeners = listeners;
        filterList=list;
    }

    public ChatFragmentAdapter(OnItemClickListeners listeners) {
        this.listeners = listeners;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context=parent.getContext();
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chatlist,parent,false);
        ChatFragmentAdapter.MyViewHolder holder=new ChatFragmentAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        ChatListModel row=list.get(position);
        holder.image.setImageBitmap(row.getImage());
        holder.name.setText(row.getName());
        holder.satus.setText(row.getMsg());
        if(row.isNewmsg())
            holder.newmsg.setVisibility(View.VISIBLE);
        else holder.date.setText(row.getDate());
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
                //listeners.onClick(v.getRootView().findViewById(R.id.recycler_view),position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public ChatListModel getItem(int position){
        return list.get(position);
    }
    public void updateAdapter(List<ChatListModel> rowItems){
        list=rowItems;
        filterList=rowItems;
        notifyDataSetChanged();
    }
    @Override
    public Filter getFilter() {
        if(valueFilter==null) {
            valueFilter=new ChatFragmentAdapter.ValueFilter();
        }
        return valueFilter;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name;
        TextView satus;
        TextView date;
        TextView newmsg;
        public MyViewHolder(View itemView) {
            super(itemView);
            image=(ImageView)itemView.findViewById(R.id.profile_picture);
            name = (TextView) itemView.findViewById(R.id.contact_name);
            satus= (TextView) itemView.findViewById(R.id.status);
            date = (TextView) itemView.findViewById(R.id.msg_date);
            newmsg = (TextView) itemView.findViewById(R.id.new_msg);
        }
    }
    private class ValueFilter extends Filter {
        //Invoked in a worker thread to filter the data according to the constraint.
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<ChatListModel> filterList2 = new ArrayList<ChatListModel>();
                for (int i = 0; i < filterList.size(); i++) {
                    if ((filterList.get(i).getName().toUpperCase())
                            .startsWith(constraint.toString().toUpperCase())) {
                        ChatListModel contacts = new ChatListModel(filterList.get(i).getName(), filterList.get(i).getImage(),
                                filterList.get(i).isNewmsg(), filterList.get(i).getMsg(), filterList.get(i).getDate(), filterList.get(i).getNumber());
                        filterList2.add(contacts);
                    }
                }
                results.count = filterList2.size();
                results.values = filterList2;
            } else {
                results.count = filterList.size();
                results.values = filterList;
            }
            return results;
        }
        //Invoked in the UI thread to publish the filtering results in the user interface.
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list=(ArrayList<ChatListModel>) results.values;
            notifyDataSetChanged();

        }
    }
}
