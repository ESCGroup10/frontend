package com.example.singhealthapp.Views.Auditor.CasesPreview;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singhealthapp.R;

public class CasePreviewHolder extends RecyclerView.ViewHolder {
    public TextView id, is_resolved, type;
    public CardView cardView;
    public CasePreviewHolder(@NonNull View itemView) {
        super(itemView);
        id = itemView.findViewById(R.id.casePreviewName);
        type = itemView.findViewById(R.id.casePreviewType);
        is_resolved = itemView.findViewById(R.id.casePreviewStatus);
        cardView = itemView.findViewById(R.id.casePreviewCard);
    }
}
