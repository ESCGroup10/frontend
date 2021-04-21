package com.example.singhealthapp.Views.Auditor.TenantsPreview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singhealthapp.Models.Tenant;
import com.example.singhealthapp.R;

import java.util.List;

public class TenantsPreviewAdapter extends RecyclerView.Adapter<TenantsPreviewHolder> {
    List<Tenant> list;
    FragmentActivity parent;
    List<Tenant> tenants;
    NavFromTenantSelection underlyingFragment;

    public interface NavFromTenantSelection {
        public void navigate(int position);
    }

    public TenantsPreviewAdapter(List<Tenant> list, List<Tenant> tenants, FragmentActivity parent, String s, NavFromTenantSelection underlyingFragment) {
        this.list = list;
        this.tenants = tenants;
        this.parent = parent;
        this.underlyingFragment = underlyingFragment;
    }

    @NonNull
    @Override
    public TenantsPreviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_tenant, null);

        return new TenantsPreviewHolder(view, this.parent, tenants);
    }

    @Override
    public void onBindViewHolder(@NonNull TenantsPreviewHolder holder, int position) {
        holder.tenantCompany.setText(list.get(position).getCompany());
        holder.tenantInstitution.setText(list.get(position).getInstitution());
        holder.tenantType.setText(list.get(position).getType());
        holder.cardView.setClickable(true);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                underlyingFragment.navigate(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}