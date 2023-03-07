package com.example.readbook.Activity.Admin.adapterAdmin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.readbook.Activity.Admin.AddCategory;
import com.example.readbook.Activity.Admin.ListPdfAdmin;
import com.example.readbook.Activity.Admin.filtersAdmin.FilterCategory;
import com.example.readbook.Activity.Admin.modeladmin.ModelCategories;
import com.example.readbook.databinding.RowCategoriesBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> implements Filterable {

    private Context context;
    public ArrayList<ModelCategories> categoriesArrayList,filterList;
    public CategoryAdapter(Context context, ArrayList<ModelCategories> categoriesArrayList) {
        this.context = context;
        this.categoriesArrayList = categoriesArrayList;
        this.filterList=categoriesArrayList;
        notifyDataSetChanged();
    }

    //intance iff out filter
    private FilterCategory filter;
    //View binding
    private RowCategoriesBinding binding;


    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //bind row_categories.xml
        binding=RowCategoriesBinding.inflate(LayoutInflater.from(context),parent,false);

        return new CategoryViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        //get data
        ModelCategories model = categoriesArrayList.get(position);
        String id = model.getId();
        String category = model.getCategory();
        String uid = model.getUid();
        String uri = model.getUri();
        long timetamp = model.getTimetamp();
        //Set data
        holder.categoryTv.setText(category);
        Glide.with(context).load(uri).into(holder.imageView);



        //handel click btn
        holder.deleteBtn.setOnClickListener(v->{

            //confilm delete dialog
            String[] options = {"Xoá"};
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Bạn chọn")
//                    .setMessage("Are yout sure want to delete this category")
                    .setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if(item ==0){
                                Toast.makeText(context, "Đang Xoá...", Toast.LENGTH_SHORT).show();
                                deleteCategory(model,holder);
                                Toast.makeText(context, "Xoá Thành Công", Toast.LENGTH_SHORT).show();

                            }else if(item==1){
                                Toast.makeText(context, "Cập nhật", Toast.LENGTH_SHORT).show();

                            }else if(item ==2){
                                dialog.dismiss();
                                Toast.makeText(context, "OnClick Delete", Toast.LENGTH_SHORT).show();


                            }
                        }
                    })
//                    .setPositiveButton("Confilm", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            Toast.makeText(context, "Deleting...", Toast.LENGTH_SHORT).show();
//                            deleteCategory(model,holder);
//
//                        }
//                    })

//                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            dialogInterface.dismiss();
//                        }
//                    })
                    .show();
            Toast.makeText(context, ""+category, Toast.LENGTH_SHORT).show();
            //will do after showing
        });
        //handle click item goto PdfAdmin,also pass pdf category categoryId
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inten = new Intent(context, ListPdfAdmin.class);
                inten.putExtra("categoryId",id);
                inten.putExtra("categoryTitle",category);
                context.startActivity(inten);


            }
        });


    }

    private void UpdateCategory(ModelCategories model, CategoryViewHolder holder) {


    }

    private void deleteCategory(ModelCategories model, CategoryViewHolder holder) {

        //get id of category to delete
        String id = model.getId();
        //Firebase Db >CATEGORY >categoryid
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(id)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //delete successfully
                        Toast.makeText(context, "Xoá Thành Công", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    //delete to failed
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public int getItemCount() {
        return categoriesArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new FilterCategory(filterList,this);
        }
        return filter;
    }
    //View holder class to holder ui view for row_categories.xml

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        //UI view of row_categories.xml
        TextView categoryTv;
        ImageView imageView;
        ImageButton deleteBtn;


        public CategoryViewHolder(@NonNull View itemView) {

            super(itemView);
            categoryTv = binding.categoryTv;
            deleteBtn=binding.deleteBtn;
            imageView = binding.imgCategoryView;
        }
    }
}
