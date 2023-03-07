package com.example.readbook.Activity.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.readbook.Activity.LoginAndRegister.Login;
import com.example.readbook.Activity.MainActivity;
import com.example.readbook.R;
import com.example.readbook.databinding.ActivityUserDashBoardBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserDashBoard extends AppCompatActivity {
    ActivityUserDashBoardBinding userbind;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userbind = ActivityUserDashBoardBinding.inflate(getLayoutInflater());
        View view = userbind.getRoot();
        setContentView(view);

        //init firebase auth
        firebaseAuth =FirebaseAuth.getInstance();
        checkUser();
        userbind.btnlogutuser.setOnClickListener(v ->{
            firebaseAuth.signOut();
            checkUser();
        });
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser == null){
            startActivity(new Intent(this, Login.class));
            finish();
        }else{
            String email = firebaseUser.getEmail();
        }
    }
}