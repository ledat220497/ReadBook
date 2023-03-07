package com.example.readbook.Activity.TabLayoutMain;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.readbook.R;
import com.example.readbook.databinding.FragmentStoreFmBinding;
import com.google.firebase.auth.FirebaseAuth;


public class store_fm extends Fragment {
    FragmentStoreFmBinding bindingstore;

    FirebaseAuth mFirebaseAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bindingstore = FragmentStoreFmBinding.inflate(inflater,container,false);
        return bindingstore.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}