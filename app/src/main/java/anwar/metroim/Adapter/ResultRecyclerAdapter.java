package anwar.metroim.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import anwar.metroim.Model.RowItem;
import anwar.metroim.R;

/**
 * Created by anwar on 3/10/2017.
 */

public class ResultRecyclerAdapter extends  RecyclerView.Adapter<ResultRecyclerAdapter.RecyclerViewHolder> implements Filterable{
    private List<RowItem> rowItems =new ArrayList<>();
    private List<RowItem> filterResult=new ArrayList<>();
    private ValueFilter valueFilter;

    public ResultRecyclerAdapter() {
    }

    public ResultRecyclerAdapter(List<RowItem> rowItems) {
        this.rowItems = rowItems;
        filterResult=rowItems;
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
    @Override
    public Filter getFilter() {
        if(valueFilter==null) {
            valueFilter=new ResultRecyclerAdapter.ValueFilter();
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
                for (int i = 0; i < filterResult.size(); i++) {
                    if ((filterResult.get(i).getSubject().toUpperCase())
                            .startsWith(constraint.toString().toUpperCase())) {
                        RowItem contacts = new RowItem(filterResult.get(i).getSubject(), filterResult.get(i).getCredit(),
                                filterResult.get(i).getGpa());
                        filterList.add(contacts);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = filterResult.size();
                results.values = filterResult;
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
}
