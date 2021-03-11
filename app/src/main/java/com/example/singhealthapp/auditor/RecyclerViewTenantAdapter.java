package com.example.singhealthapp.auditor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singhealthapp.R;
import com.example.singhealthapp.Tenant;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewTenantAdapter extends RecyclerView.Adapter<RecyclerViewTenantAdapter.MyViewHolder> implements Filterable {

    private List<Tenant> tenantInfoList;
    private List<Tenant> tenantInfoListFiltered;
    private Context context;

    public void setTenantInfoList(Context context,final List<Tenant> tenantInfoList){
        this.context = context;
        if(this.tenantInfoList == null){
            this.tenantInfoList = tenantInfoList;
            this.tenantInfoListFiltered = tenantInfoList;
            notifyItemChanged(0, tenantInfoListFiltered.size());
        } else {
            final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return RecyclerViewTenantAdapter.this.tenantInfoList.size();
                }

                @Override
                public int getNewListSize() {
                    return tenantInfoList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return RecyclerViewTenantAdapter.this.tenantInfoList.get(oldItemPosition).getName() == tenantInfoList.get(newItemPosition).getName();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

                    Tenant newTenant = RecyclerViewTenantAdapter.this.tenantInfoList.get(oldItemPosition);

                    Tenant oldTenant = tenantInfoList.get(newItemPosition);

                    return newTenant.getName() == oldTenant.getName() ;
                }
            });
            this.tenantInfoList = tenantInfoList;
            this.tenantInfoListFiltered = tenantInfoList;
            result.dispatchUpdatesTo(this);
        }
    }

    @Override
    public RecyclerViewTenantAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_tenant_block,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewTenantAdapter.MyViewHolder holder, int position) {
        holder.title.setText(tenantInfoListFiltered.get(position).getName());
    }

    @Override
    public int getItemCount() {

        if(tenantInfoList != null){
            return tenantInfoListFiltered.size();
        } else {
            return 0;
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    tenantInfoListFiltered = tenantInfoList;
                } else {
                    List<Tenant> filteredList = new ArrayList<>();
                    for (Tenant tenant : tenantInfoList) {
                        if (tenant.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(tenant);
                        }
                    }
                    tenantInfoListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = tenantInfoListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                tenantInfoListFiltered = (ArrayList<Tenant>) filterResults.values;

                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView image;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            image = (ImageView)view.findViewById(R.id.image);
        }
    }
}