package com.example.readbook.Activity.LoginAndRegister;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.readbook.databinding.FragmentForgotPasswordBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ForgotPassword extends Fragment {
    FragmentForgotPasswordBinding forgotPasswordBinding;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mUser;
    EditText mEdit;
    Button btnRepass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        forgotPasswordBinding = FragmentForgotPasswordBinding.inflate(inflater,container,false);
        return forgotPasswordBinding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEdit = forgotPasswordBinding.etEmail;
        btnRepass = forgotPasswordBinding.btnRePass;


    }
}