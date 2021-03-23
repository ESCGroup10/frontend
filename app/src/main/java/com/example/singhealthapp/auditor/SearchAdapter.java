package com.example.singhealthapp.auditor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singhealthapp.R;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchHolder>{
    List<SearchMain> list;
    FragmentActivity parent;
    List<Tenant> tenants;

    public SearchAdapter(List<SearchMain> list, List<Tenant> tenants, FragmentActivity parent) {
        this.list = list;
        this.tenants = tenants;
        this.parent = parent;
    }

    @NonNull
    @Override
    public SearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_tenants_items, null);

        return new SearchHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHolder holder, int position) {
        holder.tenantCompany.setText(list.get(position).getCompany());
        holder.tenantInstitution.setText(list.get(position).getInstitution());
        holder.tenantType.setText(list.get(position).getType());
       /* holder.view.setOnClickListener(v -> parent.getSupportFragmentManager().beginTransaction()
                .replace(parent.getSupportFragmentManager().findFragmentByTag("getTenant").getId()
                        , new TenantFragment(tenants.get(position)), "viewTenant").commit()); */
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
