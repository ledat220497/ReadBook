package com.example.readbook.Activity.Admin.filtersAdmin;

import android.widget.Filter;

import com.example.readbook.Activity.Admin.adapterAdmin.AdapterPdfAdmin;
import com.example.readbook.Activity.Admin.adapterAdmin.CategoryAdapter;
import com.example.readbook.Activity.Admin.modeladmin.ModelCategories;
import com.example.readbook.Activity.Admin.modeladmin.ModelPdfFile;

import java.util.ArrayList;

public class FilterPdfAdmin extends Filter {
    //array liist which we want to seach
    ArrayList<ModelPdfFile> filterlist;
    //Adapter in which filter  need to be implemented
    AdapterPdfAdmin adapterPdfAdmin;

    public FilterPdfAdmin(ArrayList<ModelPdfFile> filterlist, AdapterPdfAdmin adapterPdfAdmin) {
        this.filterlist = filterlist;
        this.adapterPdfAdmin = adapterPdfAdmin;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        if(charSequence !=null && charSequence.length()>0){
            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ModelPdfFile> filtermodel =  new ArrayList<>();
            for(int i=0 ; i<filterlist.size();i++){
                //validate
                if(filterlist.get(i).getTitle().toUpperCase().contains(charSequence)){
                    //add to filtermodels
                    filtermodel.add(filterlist.get(i));
                }


            }
            results.count=filtermodel.size();
            results.values=filtermodel;

        }else {
            results.count=filterlist.size();
            results.values=filterlist;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        //apply filter changes
        adapterPdfAdmin.pdfFileArrayList = (ArrayList<ModelPdfFile>) filterResults.values;
        adapterPdfAdmin.notifyDataSetChanged();
    }
}
