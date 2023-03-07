package com.example.readbook.Activity.TabLayoutMain;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.readbook.R;
import com.example.readbook.databinding.FragmentBookFmBinding;
import com.google.firebase.auth.FirebaseAuth;


public class book_fm extends Fragment {


    FragmentBookFmBinding bindingbook;
    FirebaseAuth mFirebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bindingbook = FragmentBookFmBinding.inflate(inflater,container, false);
        return bindingbook.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuth  = FirebaseAuth.getInstance();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}