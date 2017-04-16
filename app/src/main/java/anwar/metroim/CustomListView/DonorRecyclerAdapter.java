package anwar.metroim.CustomListView;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import anwar.metroim.R;

/**
 * Created by anwar on 3/4/2017.
 */

public class DonorRecyclerAdapter extends RecyclerView.Adapter<DonorRecyclerAdapter.RecyclerViewHolder> {
    private List<RowItem> rowItems =new ArrayList<>();

    public DonorRecyclerAdapter(List<RowItem> rowItems) {
        this.rowItems = rowItems;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.donar_list,parent,false);
        RecyclerViewHolder recyclerViewHolder=new RecyclerViewHolder(view);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        RowItem pos= rowItems.get(position);
        holder.donor_name.setText(pos.getContact_name());
        holder.donor_number.setText(pos.getContact_number());
    }

    @Override
    public int getItemCount() {
        return rowItems.size();
    }
    public static class RecyclerViewHolder extends RecyclerView.ViewHolder{
        TextView donor_name,donor_number;
        public RecyclerViewHolder(View itemView) {
            super(itemView);
            donor_name=(TextView)itemView.findViewById(R.id.donor_name);
            donor_number=(TextView)itemView.findViewById(R.id.donor_number);
        }
    }
}
