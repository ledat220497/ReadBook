package com.example.readbook.Activity.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.example.readbook.Activity.Admin.adapterAdmin.CategoryAdapter;
import com.example.readbook.Activity.LoginAndRegister.Login;
import com.example.readbook.Activity.Admin.modeladmin.ModelCategories;
import com.example.readbook.Activity.LoginAndRegister.Login_Form;
import com.example.readbook.Activity.MainActivity;
import com.example.readbook.R;
import com.example.readbook.databinding.ActivityAdminDashBoardBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminDashBoard extends AppCompatActivity {
    ActivityAdminDashBoardBinding dashBoardBinding;

    NavController mNavController;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    NavigationView btnnavView;
    DrawerLayout drawerLayout;
    ImageButton btnOpenNav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dashBoardBinding =ActivityAdminDashBoardBinding.inflate(getLayoutInflater());
        View view = dashBoardBinding.getRoot();
        //inst firebase
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser= mFirebaseAuth.getCurrentUser();
        setContentView(view);
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.idFragmentContainer);
        NavGraph navGraph =
                navHostFragment.getNavController()
                        .getNavInflater().inflate(R.navigation.nav_admin);
        navHostFragment.getNavController().setGraph(navGraph);

        mNavController = navHostFragment.getNavController();
        btnnavView = dashBoardBinding.navView;
        btnOpenNav = dashBoardBinding.btnNavigation;
        drawerLayout = dashBoardBinding.drawerLayout;
        btnOpenNav.setOnClickListener(v->{
            drawerLayout.openDrawer(GravityCompat.START);
        });

        btnnavView = dashBoardBinding.navView;
        btnnavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.iddashboard:
                        mNavController.navigate(R.id.managerBook);
//                        mRelative.addView(mImageButton,params);
                        dashBoardBinding.titleDash.setText("QUẢN LÝ SÁCH");

                        break;
                    case R.id.idbook:
                        mNavController.navigate(R.id.managerUser);
                        dashBoardBinding.titleDash.setText("QUẢN LÝ NGƯỜI DÙNG");
                        break;
                    case R.id.idlogout:
                        mFirebaseAuth.signOut();
                        startActivity(new Intent(AdminDashBoard.this, Login_Form.class));
                        finish();
                    case R.id.idsetting:
                        mNavController.navigate(R.id.settingAdmin);
                        dashBoardBinding.titleDash.setText("CÀI ĐẶT");
                        break;



                }
                drawerLayout.closeDrawer(btnnavView);
                return true;
            }
        });


    }


}