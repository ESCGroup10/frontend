package com.example.singhealthapp.auditor.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.singhealthapp.R;
import com.example.singhealthapp.Tenant;

import java.util.List;



public class RecyclerViewTenantAdapter extends RecyclerView.Adapter<RecyclerViewTenantAdapter.MyViewHolder>{

    Context context;
    List<Tenant> tenantInfoList;

    public RecyclerViewTenantAdapter(Context context, List<Tenant> tenantInfoList) {
    }

    public void setTenantInfoList(List<Tenant> tenantInfoList) {
        this.tenantInfoList = tenantInfoList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerViewTenantAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_viewtenant,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewTenantAdapter.MyViewHolder holder, int position) {
        holder.tenantName.setText(tenantInfoList.get(position).getName().toString());

    }

    @Override
    public int getItemCount() {
        if(tenantInfoList != null){
            return tenantInfoList.size();
        }
        return 0;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tenantName;

        public MyViewHolder(View itemView) {
            super(itemView);
            tenantName = (TextView)itemView.findViewById(R.id.name);
        }
    }
}
