package com.allvens.allworkouts.log_manager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.allvens.allworkouts.R;
import com.allvens.allworkouts.data_manager.database.WorkoutHistory_Info;

import java.util.ArrayList;

public class SetListRecyclerView_Adapter extends RecyclerView.Adapter<SetListRecyclerView_Adapter.ViewHolder>{

    private ArrayList<WorkoutHistory_Info> history_info;
    private Context context;

    public SetListRecyclerView_Adapter(Context context, ArrayList<WorkoutHistory_Info> history_info){
        this.context = context;
        this.history_info = history_info;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_listset_item, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvMax.setText("Max: " + history_info.get(position).getMax_value());
        holder.tvTotalSet.setText("Total Set: " + get_TotalSetAmount(position));

        holder.tvSet1.setText(Integer.toString(history_info.get(position).getFirst_value()));
        holder.tvSet2.setText(Integer.toString(history_info.get(position).getSecond_value()));
        holder.tvSet3.setText(Integer.toString(history_info.get(position).getThird_value()));
        holder.tvSet4.setText(Integer.toString(history_info.get(position).getForth_value()));
        holder.tvSet5.setText(Integer.toString(history_info.get(position).getFifth_value()));
    }

    private int get_TotalSetAmount(int pos){
        return history_info.get(pos).get_TotalReps();
    }

    @Override
    public int getItemCount() {
        return history_info.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvMax;
        TextView tvTotalSet;

        TextView tvSet1;
        TextView tvSet2;
        TextView tvSet3;
        TextView tvSet4;
        TextView tvSet5;

        public ViewHolder(View itemView) {
            super(itemView);
            tvMax = itemView.findViewById(R.id.tv_log_listset_Max);
            tvTotalSet = itemView.findViewById(R.id.tv_log_listset_TotalSet);

            tvSet1 = itemView.findViewById(R.id.tv_log_listset_Value1);
            tvSet2 = itemView.findViewById(R.id.tv_log_listset_Value2);
            tvSet3 = itemView.findViewById(R.id.tv_log_listset_Value3);
            tvSet4 = itemView.findViewById(R.id.tv_log_listset_Value4);
            tvSet5 = itemView.findViewById(R.id.tv_log_listset_Value5);

        }
    }
}
