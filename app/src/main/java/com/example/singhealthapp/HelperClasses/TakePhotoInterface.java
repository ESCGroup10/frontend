package com.example.singhealthapp.HelperClasses;


import com.example.singhealthapp.Views.Auditor.Checklists.ChecklistAdapter;

public interface TakePhotoInterface {

    boolean takePhoto(ChecklistAdapter checklistAdapter, int adapterPosition, String question);

}
