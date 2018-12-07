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

public class ListRecyclerView_Adapter extends RecyclerView.Adapter<ListRecyclerView_Adapter.ViewHolder>{

    private ArrayList<WorkoutHistory_Info> history_infos = new ArrayList<>();
    private Context context;

    public ListRecyclerView_Adapter(Context context, ArrayList<WorkoutHistory_Info> history_infos){
        this.context = context;
        this.history_infos = history_infos;
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
        holder.tvMax.setText("Max: " + history_infos.get(position).getMax_value());
        holder.tvTotalSet.setText("Total Set: " + get_TotalSetAmount(position));

        holder.tvSet1.setText("Set 1: " + history_infos.get(position).getFirst_value());
        holder.tvSet2.setText("Set 2: " + history_infos.get(position).getSecond_value());
        holder.tvSet3.setText("Set 3: " + history_infos.get(position).getThird_value());
        holder.tvSet4.setText("Set 4: " + history_infos.get(position).getForth_value());
        holder.tvSet5.setText("Set 5: " + history_infos.get(position).getFifth_value());
    }

    private int get_TotalSetAmount(int pos){
        return history_infos.get(pos).getFirst_value() + history_infos.get(pos).getSecond_value() +
                history_infos.get(pos).getThird_value() + history_infos.get(pos).getForth_value() +
                history_infos.get(pos).getFifth_value();
    }

    @Override
    public int getItemCount() {
        return history_infos.size();
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
