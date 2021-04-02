package com.example.singhealthapp.HelperClasses;


import com.example.singhealthapp.Views.Auditor.Checklists.ChecklistAdapter;

public interface HandlePhotoInterface {

    //should change to void
    boolean takePhoto(ChecklistAdapter checklistAdapter, int adapterPosition, String question);

}
