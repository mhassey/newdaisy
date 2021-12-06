package com.daisy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.daisy.R;
import com.daisy.databinding.LogRecycleViewItemDesignBinding;
import com.daisy.pojo.Logs;

import java.util.List;

/**
 * Its an adaptor that connect with logs list and what ever data it has show to user
 **/
public class LogsAdapter extends RecyclerView.Adapter<LogsAdapter.ViewHolder> {

    private List<Logs> list;
    private Context context;

    public LogsAdapter(List<Logs> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        LogRecycleViewItemDesignBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.log_recycle_view_item_design, parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.logMessage.setText(list.get(position).getEventDateTime()+" "+list.get(position).getEventName()+"\n"+list.get(position).getEventDescription()+" "+list.get(position).getEventUrl());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LogRecycleViewItemDesignBinding binding;

        public ViewHolder(@NonNull LogRecycleViewItemDesignBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
