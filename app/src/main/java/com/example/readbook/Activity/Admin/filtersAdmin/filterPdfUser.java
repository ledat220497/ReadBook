package com.example.readbook.Activity.Admin.filtersAdmin;

import android.widget.Filter;

import com.example.readbook.Activity.Admin.modeladmin.ModelPdfFile;
import com.example.readbook.Adapter.AdaperPdfUser;

import java.util.ArrayList;

public class filterPdfUser extends Filter {
    //array list we want to search
    ArrayList<ModelPdfFile> modelPdfFiles;
    //adapter in which filter need to be implemented
    AdaperPdfUser adaperPdfUser;

    public filterPdfUser(ArrayList<ModelPdfFile> modelPdfFiles, AdaperPdfUser adaperPdfUser) {
        this.modelPdfFiles = modelPdfFiles;
        this.adaperPdfUser = adaperPdfUser;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if(constraint!=null && constraint.length() >0){
            //not null nor empty

            //changer to upercase
             constraint = constraint.toString().toUpperCase();
             ArrayList<ModelPdfFile> filtermodel = new ArrayList<>();
             for(int i=0; i<modelPdfFiles.size();i++){
                 if(modelPdfFiles.get(i).getTitle().toUpperCase().contains(constraint)){
                     //search machet add to list
                     filtermodel.add(modelPdfFiles.get(i));
                 }
             }

            results.count = filtermodel.size();
            results.values = filtermodel;
        }else{
            //empty or null make original list /result
            results.count = modelPdfFiles.size();
            results.values = modelPdfFiles;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        //apply filter change
        adaperPdfUser.pdfFileArrayList = (ArrayList<ModelPdfFile>)results.values;
        //notifi change
        adaperPdfUser.notifyDataSetChanged();
    }
}
