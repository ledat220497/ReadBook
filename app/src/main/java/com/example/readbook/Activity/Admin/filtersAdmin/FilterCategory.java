package com.example.readbook.Activity.Admin.filtersAdmin;

import android.widget.Filter;

import com.example.readbook.Activity.Admin.adapterAdmin.CategoryAdapter;
import com.example.readbook.Activity.Admin.modeladmin.ModelCategories;

import java.util.ArrayList;

public class FilterCategory extends Filter {
    //array liist which we want to seach
    ArrayList<ModelCategories> filterlist;
    //Adapter in which filter  need to be implemented
    CategoryAdapter categoryAdapter;

    public FilterCategory(ArrayList<ModelCategories> filterlist, CategoryAdapter categoryAdapter) {
        this.filterlist = filterlist;
        this.categoryAdapter = categoryAdapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        if(charSequence !=null && charSequence.length()>0){
            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ModelCategories> filtermodel =  new ArrayList<>();
            for(int i=0 ; i<filterlist.size();i++){
                //validate
                if(filterlist.get(i).getCategory().toUpperCase().contains(charSequence)){
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
        categoryAdapter.categoriesArrayList = (ArrayList<ModelCategories>) filterResults.values;
        categoryAdapter.notifyDataSetChanged();
    }
}
