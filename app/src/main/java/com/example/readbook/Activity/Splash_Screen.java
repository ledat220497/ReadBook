package com.example.readbook.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.example.readbook.Activity.Admin.AdminDashBoard;
import com.example.readbook.Activity.Admin.UserDashBoard;
import com.example.readbook.Activity.LoginAndRegister.Login;
import com.example.readbook.Activity.LoginAndRegister.Login_Form;
import com.example.readbook.R;
import com.example.readbook.databinding.ActivitySplashScreenBinding;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Splash_Screen extends AppCompatActivity {
    ActivitySplashScreenBinding binsplash;
    LottieAnimationView lottieAnimation;
    MaterialButton btngetstarted;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binsplash = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        View view = binsplash.getRoot();
        setContentView(view);
        lottieAnimation = (LottieAnimationView) view.findViewById(R.id.animation_splash);
        btngetstarted =(MaterialButton) view.findViewById(R.id.btngetstartlogin);

        startCheckAnimation();

//        new Handler().postDelayed(new Runnable()
//        {
//            @Override
//            public void run() {
//                startActivity(new Intent(Splash_Screen.this,Login.class));
//            }
//        },2000);
        btngetstarted.setOnClickListener(v ->{
            startActivity(new Intent(Splash_Screen.this, Login.class));
        });

    }
    private void startCheckAnimation(){
        ValueAnimator animation = ValueAnimator.ofFloat(0f,1f).setDuration(3000);
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                lottieAnimation.setProgress((Float) animation.getAnimatedValue());
            }
        });
        if(lottieAnimation.getProgress() == 0f){
            animation.start();
        }
        else{
            lottieAnimation.setProgress(0f);
        }
    }

}