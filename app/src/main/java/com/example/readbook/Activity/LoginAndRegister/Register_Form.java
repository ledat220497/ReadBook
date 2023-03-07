package com.example.readbook.Activity.LoginAndRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import android.util.Patterns;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import com.example.readbook.R;
import com.example.readbook.databinding.ActivityRegisterFormBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Register_Form extends AppCompatActivity {
    ActivityRegisterFormBinding resbin;
    ImageView resToLg;
    TextView resToSign;
    TextInputEditText emailReg,passReg,passRegRe;
    AwesomeValidation anAwesomeValidation;
    Button btnRegister;
    boolean passVisible;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resbin = ActivityRegisterFormBinding.inflate(getLayoutInflater());
        View view = resbin.getRoot();
        setContentView(view);
        //init firebase auth
        firebaseAuth=FirebaseAuth.getInstance();
        //setup progressdiglog
        progressDialog= new ProgressDialog(this);
        progressDialog.setTitle("Vui lòng chờ...");
        progressDialog.setCanceledOnTouchOutside(false);
        resToLg = view.findViewById(R.id.resbtnToLogin);
        resToSign = view.findViewById(R.id.btnToSignIn);
        emailReg = view.findViewById(R.id.txt_Email_Res);
        passReg=view.findViewById(R.id.txt_Pass_Res);
        passRegRe=view.findViewById(R.id.txt_Pass_Res_Re);
        btnRegister = view.findViewById(R.id.resbtnToMain);
        anAwesomeValidation= new AwesomeValidation(ValidationStyle.BASIC);
        anAwesomeValidation.addValidation(this,R.id.txt_Pass_Res,".{6,}",R.string.wrongpass);
        anAwesomeValidation.addValidation(this,R.id.txt_Pass_Res_Re,R.id.txt_Pass_Res,R.string.passdisnotconrect);
        resToLg.setOnClickListener(v->{
            startActivity(new Intent(Register_Form.this,Login.class));
            onBackPressed();
        });
        resToSign.setOnClickListener(v->{
            startActivity(new Intent(Register_Form.this,Login_Form.class));
            onBackPressed();
        });

        btnRegister.setOnClickListener(v ->{

            validateData();
        });
    }
    private String name ="",email = "" ,password= "";

    private void validateData() {
        name = resbin.txtNameRes.getText().toString().trim();
        email=resbin.txtEmailRes.getText().toString().trim();
        password=resbin.txtPassRes.getText().toString().trim();
        String rePassword=resbin.txtPassResRe.getText().toString().trim();
        //Validate data
        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "Nhâp tên ...", Toast.LENGTH_SHORT).show();
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Nhập Email..", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Nhập Mật Khẩu...", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(rePassword)){
            Toast.makeText(this, "Nhập Lại Mật Khẩu...", Toast.LENGTH_SHORT).show();
        }else if(!password.equals(rePassword)){
            Toast.makeText(this, "Mật Khẩu Không Trùng Khớp...", Toast.LENGTH_SHORT).show();

        }else{
            CreateAccount();
        }

    }

    private void CreateAccount() {
        progressDialog.setMessage("Đang Tạo Tài Khoản...");
        progressDialog.show();
        //create user in firebase auth
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //account create success
                        updateUserInfo();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //account create failed
                        progressDialog.dismiss();
                        Toast.makeText(Register_Form.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUserInfo() {
        progressDialog.setMessage("Đang Lưu Tài Khoản...");
        long timetamp = System.currentTimeMillis();
        //get current user uid,since user is regiser
        String uid =firebaseAuth.getUid();
        //setup data add in db
        HashMap<String,Object> hashMap= new HashMap<>();
        hashMap.put("uid",uid);
        hashMap.put("email",email);
        hashMap.put("name",name);
        hashMap.put("profileimage","");
        hashMap.put("userType","user");
        hashMap.put("timetamp",timetamp);
        //set data to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
        ref.child(uid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //data added to database
                        progressDialog.dismiss();
                        Toast.makeText(Register_Form.this, "Tạo Tài Khoản Thành Công...", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(Register_Form.this, UserDashBoard.class));
//                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        //data failed adding to database
                        progressDialog.dismiss();
                        Toast.makeText(Register_Form.this, "Tạo Tài Khoản Thất Bại"+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }
}