package com.example.readbook.Activity.Admin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.readbook.Activity.Admin.modeladmin.ModelPdfFile;
import com.example.readbook.Adapter.AdaperPdfUser;
import com.example.readbook.databinding.BookUserFragmentBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BookUserFragment extends Fragment {

    BookUserFragmentBinding bookUserbinding;
    private String categoryId;
    private String category;
    private String uid;
    private ArrayList<ModelPdfFile> pdfArrayList;
    private AdaperPdfUser adaperPdfUser;


    private static final String TAG = "BOOK_USER_PDF";
    public BookUserFragment(){

    }

    public static BookUserFragment newInstance(String categoryId, String category,String uid) {
        
        Bundle args = new Bundle();
        
        BookUserFragment fragment = new BookUserFragment();
        args.putString("categoryId",categoryId);
        args.putString("category",category);
        args.putString("uid",uid);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            categoryId = getArguments().getString("categoryId");
            category = getArguments().getString("category");
            uid = getArguments().getString("uid");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bookUserbinding = BookUserFragmentBinding.inflate(LayoutInflater.from(getContext()),container,false);

        Log.d(TAG, "onCreateView: "+category);
        if(category.equals("Tất Cả")){
            //load All book
            loadAllBook();
        }else if(category.equals("Đã Đọc")){
            //load most view Book
            loadViewDownloadBooks("viewCount");
        }else if(category.equals("Đã Tải")){
            //load mode download Book
            loadViewDownloadBooks("downloadCount");
        }else{
            //load selected category Bookm
            loadCategoriesBook();
        }

        //search
        bookUserbinding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //called as and when user type laster
                try {
                    adaperPdfUser.getFilter().filter(s);
                }catch (Exception ex){
                    Log.d(TAG, "onTextChanged: "+ ex.getMessage());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return bookUserbinding.getRoot();
    }

    private void loadCategoriesBook() {
        pdfArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.orderByChild("categoryId").equalTo(categoryId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        //Clear before starting add data into it
                        pdfArrayList.clear();
                        for(DataSnapshot ds:snapshot.getChildren()){
                            //getData
                            ModelPdfFile model = ds.getValue(ModelPdfFile.class);
                            //add to list
                            pdfArrayList.add(model);

                        }
                        //setup adapter user
                        adaperPdfUser = new AdaperPdfUser(getContext(),pdfArrayList);
                        //setup adapter to rcv
                        bookUserbinding.rcvBook.setAdapter(adaperPdfUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadViewDownloadBooks(String orderBy) {
        pdfArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.orderByChild(orderBy).limitToLast(10)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Clear before starting add data into it
                pdfArrayList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    //getData
                    ModelPdfFile model = ds.getValue(ModelPdfFile.class);
                    //add to list
                    pdfArrayList.add(model);

                }
                //setup adapter user
                adaperPdfUser = new AdaperPdfUser(getContext(),pdfArrayList);
                //setup adapter to rcv
                bookUserbinding.rcvBook.setAdapter(adaperPdfUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadAllBook() {
        pdfArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Clear before starting add data into it
                pdfArrayList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    //getData
                    ModelPdfFile model = ds.getValue(ModelPdfFile.class);
                    //add to list
                    pdfArrayList.add(model);

                }
                //setup adapter user
                adaperPdfUser = new AdaperPdfUser(getContext(),pdfArrayList);
                //setup adapter to rcv
                bookUserbinding.rcvBook.setAdapter(adaperPdfUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
