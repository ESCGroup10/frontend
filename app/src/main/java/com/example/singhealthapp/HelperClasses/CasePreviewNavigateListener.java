package com.example.singhealthapp.HelperClasses;

import com.example.singhealthapp.Models.Case;
import com.example.singhealthapp.Models.Report;

public interface CasePreviewNavigateListener {

    void navigateFromRecyclerView(Report report, Case thisCase);

}
