package com.example.singhealthapp.Views.Auditor.SearchTenant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singhealthapp.Models.Tenant;
import com.example.singhealthapp.R;
import com.example.singhealthapp.Views.Auditor.Tenants.TenantsFragment;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchHolder>{
    List<SearchMain> list;
    FragmentActivity parent;
    List<Tenant> tenants;

    public SearchAdapter(List<SearchMain> list, List<Tenant> tenants, FragmentActivity parent, String s) {
        this.list = list;
        this.tenants = tenants;
        this.parent = parent;
    }

    @NonNull
    @Override
    public SearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_tenants_items, null);

        return new SearchHolder(view, this.parent, tenants);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHolder holder, int position) {
        holder.tenantCompany.setText(list.get(position).getCompany());
        holder.tenantInstitution.setText(list.get(position).getInstitution());
        holder.tenantType.setText(list.get(position).getType());
        holder.cardView.setClickable(false);
        holder.view.setOnClickListener(v -> parent.getSupportFragmentManager().beginTransaction()
                .replace(parent.getSupportFragmentManager().findFragmentByTag("getTenant").getId()
                        , new TenantsFragment(tenants.get(position)), "viewTenant").commit()); //tenants.get(position)
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
