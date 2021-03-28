package com.example.singhealthapp.Views.Auditor.CasePreview;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singhealthapp.R;
import com.example.singhealthapp.Models.Case;

import java.util.List;

public class CasePreviewAdapter extends RecyclerView.Adapter<CasePreviewHolder>{
    List<Case> cases;

    public CasePreviewAdapter(List<Case> cases) {
        this.cases = cases;
    }

    @NonNull
    @Override
    public CasePreviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.case_card_layout, null);
        return new CasePreviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CasePreviewHolder holder, int position) {
        if (cases.get(position).isIs_resolved()) {
            holder.is_resolved.setText("Resolved");
            holder.cardView.setCardBackgroundColor(Color.rgb(159, 221, 88));
        }
        else {
            holder.is_resolved.setText("Unresolved");
            holder.cardView.setCardBackgroundColor(Color.rgb(239, 117, 119));
        }
        holder.type.setText(cases.get(position).getNon_compliance_type());
        holder.id.setText("Case " + cases.get(position).getId());

    }

    @Override
    public int getItemCount() {
        return cases.size();
    }
}
