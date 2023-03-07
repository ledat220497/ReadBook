package com.example.readbook.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.readbook.Activity.LoginAndRegister.Login;
import com.example.readbook.Activity.LoginAndRegister.Login_Form;
import com.example.readbook.Activity.TabLayoutMain.Like;
import com.example.readbook.Activity.TabLayoutMain.Share;
import com.example.readbook.Activity.TabLayoutMain.book_fm;
import com.example.readbook.Activity.TabLayoutMain.home_fm;
import com.example.readbook.Activity.TabLayoutMain.profile_fm;
import com.example.readbook.Activity.TabLayoutMain.setting_fm;
import com.example.readbook.Activity.TabLayoutMain.store_fm;
import com.example.readbook.R;
import com.example.readbook.databinding.ActivityMainBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding bindingmain ;
//    private static  final int FRAGMENT_HOME =1;
//    private static  final int FRAGMENT_BOOK =2;
//    private static  final int FRAGMENT_STORE =3;
//    private static  final int FRAGMENT_PROFILE =4;
//    private static  final int FRAGMENT_SETTING =5;
//    private static  final int FRAGMENT_SHARE =6;
//    private static  final int FRAGMENT_LIKE =7;
//    private int currentFragment = FRAGMENT_HOME;
    NavController mNavController;
    private FirebaseAuth firebaseAuth;
    private  MaterialToolbar mtoolbar;
    FirebaseUser mUser;
    TextView emailUser;
    TextView mEmail;
    String emailName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingmain = ActivityMainBinding.inflate(getLayoutInflater());
        View view = bindingmain.getRoot();
        setContentView(view);
        //navigation Fragment
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragmentContainerUser);
        NavGraph navGraph =
                navHostFragment.getNavController()
                        .getNavInflater().inflate(R.navigation.nav_user);
        navHostFragment.getNavController().setGraph(navGraph);

        mNavController = navHostFragment.getNavController();

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();

        mUser = firebaseAuth.getCurrentUser();
        emailName = mUser.getEmail();
        checkUser();
        mtoolbar =bindingmain.menuTopBar;
        mtoolbar.setTitle("TRANG CHỦ");
        mtoolbar.hideOverflowMenu();
        setSupportActionBar(mtoolbar);
        final  DrawerLayout drawer = bindingmain.drawerLayout;
        NavigationView naviView = bindingmain.navigationView;
        View hView =naviView.getHeaderView(0);
        mEmail = hView.findViewById(R.id.email_User);

        mEmail.setText(emailName);


        ActionBarDrawerToggle tongle = new ActionBarDrawerToggle(this,drawer,mtoolbar,R.string.open,R.string.close);
        drawer.addDrawerListener(tongle);
        tongle.syncState();


        bindingmain.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        mNavController.navigate(R.id.home_fm);
                        bindingmain.menuTopBar.setTitle("TRANG CHỦ");
                        break;
                    case R.id.book:
                        mNavController.navigate(R.id.book_fm);
                        bindingmain.menuTopBar.setTitle("SÁCH CỦA TÔI");
                        break;
                    case R.id.storebook:
                        mNavController.navigate(R.id.store_fm);
                        bindingmain.menuTopBar.setTitle("SÁCH");
                        break;
                    case R.id.profile:
                        mNavController.navigate(R.id.profile_fm);
                        bindingmain.menuTopBar.setTitle("THÔNG TIN CÁ NHÂN");
                        break;
                    case R.id.logout:
                        firebaseAuth.signOut();
                        startActivity(new Intent(MainActivity.this, Login_Form.class));
                        finish();

                    case R.id.setting:
                        mNavController.navigate(R.id.setting_fm);
                        bindingmain.menuTopBar.setTitle("CÀI ĐẶT");
                        break;
                    case R.id.share:
                        mNavController.navigate(R.id.share);
                        bindingmain.menuTopBar.setTitle("ĐÃ CHIA SẺ");
                        break;
                    case R.id.like:
                        mNavController.navigate(R.id.like);
                        bindingmain.menuTopBar.setTitle("ĐÃ ĐỌC");
                        break;
                }
                bindingmain.drawerLayout.closeDrawer(bindingmain.navigationView);
                return true;
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }

    }


    private void checkUser() {
        String User;
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser == null){
            startActivity(new Intent(this, Login.class));
            finish();
        }else{
            String email = firebaseUser.getEmail();

        }
    }
}