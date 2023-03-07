package com.example.readbook.Activity.TabLayoutMain;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.readbook.Activity.Admin.modeladmin.ModelPdfFile;
import com.example.readbook.Adapter.AdapterFavorite;
import com.example.readbook.Adapter.AdapterUserViewCount;
import com.example.readbook.R;
import com.example.readbook.databinding.FragmentLikeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Like extends Fragment {
    FragmentLikeBinding bindinglike;
    FirebaseAuth mFirebaseAuth;
    private List<ModelPdfFile> pdfFileArrayList;
    AdapterUserViewCount adapterUserView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bindinglike = FragmentLikeBinding.inflate(inflater,container,false);
        return bindinglike.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapterUserView = new AdapterUserViewCount(requireActivity(),pdfFileArrayList);
        //set adapter rcv
        bindinglike.rcvViewRead.setAdapter(adapterUserView);
//            loadBookViewRead();

    }



    private void loadBookViewRead() {
        //init
        pdfFileArrayList = new ArrayList<>();
        //load favorite book from firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
        ref.child(mFirebaseAuth.getUid()).child("ViewBooks")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //CLEAR list before startding  adding data
                        pdfFileArrayList.clear();
                        for(DataSnapshot ds :snapshot.getChildren()){
                            String bookId =""+ ds.child("bookId").getValue();
                            ModelPdfFile model = new ModelPdfFile();
                            model.setId(bookId);
                            Log.d("listbook", "onDataChange: "+bookId);

                            //ADD
                            pdfFileArrayList.add(model);
                            Log.d(">>", "onDataChange: "+pdfFileArrayList.size());

                        }
                        adapterUserView.setData(pdfFileArrayList);
                        Log.d("listuser", "onDataChange: "+pdfFileArrayList.get(1).getTitle());
                        //setup adapter

                        Log.d(">>", "onDataChange: "+adapterUserView);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}