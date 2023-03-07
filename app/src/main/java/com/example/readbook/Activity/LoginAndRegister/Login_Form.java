package com.example.readbook.Activity.LoginAndRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import android.util.Patterns;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.readbook.Activity.Admin.AdminDashBoard;
import com.example.readbook.Activity.MainActivity;
import com.example.readbook.R;
import com.example.readbook.databinding.ActivityLoginFormBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login_Form extends AppCompatActivity {
    ActivityLoginFormBinding loginfrombin;
    ImageView btnToLg;
    TextView loginToRegisterl;
    TextInputEditText email,password;
    boolean passvisible;
    //firebase auth
    private FirebaseAuth firebaseauth;
    private ProgressDialog progressDialog;
    TextView btnForgot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginfrombin = ActivityLoginFormBinding.inflate(getLayoutInflater());
        View view = loginfrombin.getRoot();
        setContentView(view);

        //init firebase auth
        firebaseauth = FirebaseAuth.getInstance();
        //setup progressdiglog
        progressDialog= new ProgressDialog(this);
        progressDialog.setTitle("Vui lòng chờ...");
        progressDialog.setCanceledOnTouchOutside(false);
        btnToLg = view.findViewById(R.id.btnToLogin);
        loginToRegisterl = view.findViewById(R.id.btnToRegister);
        email =view.findViewById(R.id.txt_email);
        password= view.findViewById(R.id.txt_pass);
        btnForgot = loginfrombin.btnForgot;
        btnForgot.setOnClickListener(v->{
        });
        btnToLg.setOnClickListener(v ->{
            startActivity(new Intent(Login_Form.this,Login.class));
            onBackPressed();
        });
        loginToRegisterl.setOnClickListener(v->{
            startActivity(new Intent(Login_Form.this,Register_Form.class));
            onBackPressed();
        });

        loginfrombin.btnToMain.setOnClickListener(v ->{
            validateData();
        });
    }
    private String Email = "" ,Password = "";
    private void validateData() {
        Email = email.getText().toString().trim();
        Password=password.getText().toString().trim();
        if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
            Toast.makeText(this, "Chưa nhập Email..", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(Password)){
            Toast.makeText(this, "Nhập Mật Khẩu...", Toast.LENGTH_SHORT).show();
        }else{
            loginUser();
        }
    }

    private void loginUser() {
        progressDialog.setMessage("Đang Đăng Nhập...");
        progressDialog.show();
        //login user
        firebaseauth.signInWithEmailAndPassword(Email,Password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //login success check user or admin
                        checkUser();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure( Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(Login_Form.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void checkUser() {
        progressDialog.setMessage("Đang Kiểm Tra Tài Khoản...");
        //Check user or admin in realtimebase
        FirebaseUser firebaseUser = firebaseauth.getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
        ref.child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange( DataSnapshot snapshot) {
                        progressDialog.dismiss();
                        //get user type
                        String  userType =""+snapshot.child("userType").getValue();
                        //check user type
                        if(userType.equals("user")){
                            //this is simple user ,open user dashboard
                            startActivity(new Intent(Login_Form.this, MainActivity.class));
                            Toast.makeText(Login_Form.this, "Đăng Nhập Thành Công", Toast.LENGTH_SHORT).show();
//                            finish();

                        }else if(userType.equals("admin")){
                            //this is simple user ,open admin dashboard
                            startActivity(new Intent(Login_Form.this, AdminDashBoard.class));
                            Toast.makeText(Login_Form.this, "Đăng Nhập Thành Công", Toast.LENGTH_SHORT).show();
//                            finish();

                        }
                    }

                    @Override
                    public void onCancelled( DatabaseError error) {

                    }
                });
    }
}