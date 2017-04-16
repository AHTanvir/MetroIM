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
 * Created by anwar on 3/10/2017.
 */

public class ResultRecyclerAdapter extends  RecyclerView.Adapter<ResultRecyclerAdapter.RecyclerViewHolder> {
    private List<RowItem> rowItems =new ArrayList<>();

    public ResultRecyclerAdapter(List<RowItem> rowItems) {
        this.rowItems = rowItems;
    }

    @Override
    public ResultRecyclerAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.result_list,parent,false);
        ResultRecyclerAdapter.RecyclerViewHolder recyclerViewHolder=new ResultRecyclerAdapter.RecyclerViewHolder(view);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(ResultRecyclerAdapter.RecyclerViewHolder holder, int position) {
        RowItem pos= rowItems.get(position);
        holder.subject.setText(pos.getSubject());
        holder.credit.setText(pos.getCredit());
        holder.gpa.setText(pos.getGpa());
    }

    @Override
    public int getItemCount() {
        return rowItems.size();
    }
    public static class RecyclerViewHolder extends RecyclerView.ViewHolder{
        TextView subject,credit,gpa;
        public RecyclerViewHolder(View itemView) {
            super(itemView);
            subject=(TextView)itemView.findViewById(R.id.subject);
            credit=(TextView)itemView.findViewById(R.id.credit);
            gpa=(TextView)itemView.findViewById(R.id.gpa);
        }
    }
}
