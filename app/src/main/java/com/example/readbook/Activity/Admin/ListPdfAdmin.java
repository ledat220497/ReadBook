package com.example.readbook.Activity.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Filterable;

import com.example.readbook.Activity.Admin.adapterAdmin.AdapterPdfAdmin;
import com.example.readbook.Activity.Admin.filtersAdmin.FilterPdfAdmin;
import com.example.readbook.Activity.Admin.modeladmin.ModelPdfFile;
import com.example.readbook.R;
import com.example.readbook.databinding.ActivityListPdfAdminBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListPdfAdmin extends AppCompatActivity {
    ActivityListPdfAdminBinding listbindingl;

    //array list to hold list of data of type Modelpdf
    private ArrayList<ModelPdfFile> modelPdfFiles;
    //adapter
    private AdapterPdfAdmin  adapterPdfAdmin;

    private String categoryId,categoryTitle;
    private static final String TAG = "PDF_LIST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listbindingl = ActivityListPdfAdminBinding.inflate(getLayoutInflater());
        View view = listbindingl.getRoot();
        setContentView(view);

        //get data from intent
        Intent intent = getIntent();
        categoryId =intent.getStringExtra("categoryId");
        categoryTitle = intent.getStringExtra("categoryTitle");

        //set pdf category
        listbindingl.subTitleTv.setText(categoryTitle);

        loadListPdf();

        //seach book
        listbindingl.textSearchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //seach as and when user type each letter
                try {
                    adapterPdfAdmin.getFilter().filter(charSequence);
                }catch (Exception e){
                    Log.d(TAG, "onTextChanged: "+e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //handle click go to provius activity
        listbindingl.backBtn.setOnClickListener(v->{
            onBackPressed();
        });

    }

    private void loadListPdf() {
        //init list before adding data
        modelPdfFiles =new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.orderByChild("categoryId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        modelPdfFiles.clear();
                        for(DataSnapshot ds :snapshot.getChildren()){
                            //get data
                            ModelPdfFile model= ds.getValue(ModelPdfFile.class);
                            //add to list
                            modelPdfFiles.add(model);
                            Log.d(TAG, "onDataChange: "+model.getId()+""+model.getTitle());
                        }
                        //setupdater
                        adapterPdfAdmin = new AdapterPdfAdmin(ListPdfAdmin.this,modelPdfFiles);
                        listbindingl.bookRv.setAdapter(adapterPdfAdmin);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}