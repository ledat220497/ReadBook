package com.example.readbook.Activity.TabLayoutMain;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.navigation.NavController;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.readbook.Activity.Admin.BookUserFragment;
import com.example.readbook.Activity.Admin.modeladmin.ModelCategories;
import com.example.readbook.databinding.FragmentHomeFmBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class home_fm extends Fragment {
    FragmentHomeFmBinding bindinghome;

    public ViewPagerAdapter viewPagerAdapter;
    //to in showtabs
    private ArrayList<ModelCategories>  categoriesList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bindinghome = FragmentHomeFmBinding.inflate(inflater,container, false);
        setUpViewPager(bindinghome.viewPager);
        bindinghome.tabslayout.setupWithViewPager(bindinghome.viewPager);
        return bindinghome.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



    }
    private void setUpViewPager(ViewPager viewPager){
        viewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,requireActivity());
        categoriesList = new ArrayList<>();
        //load categories from firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear beforadding
                categoriesList.clear();
                //Load Categories
                //Add model data categories
                ModelCategories modeAll = new ModelCategories("01","Tất Cả","",1,"");
                ModelCategories modeMostView = new ModelCategories("02","Đã Đọc","",1,"");
                ModelCategories modeMostDownload = new ModelCategories("03","Đã Tải","",1,"");
                //add models to list
                categoriesList.add(modeAll);
                categoriesList.add(modeMostView);
                categoriesList.add(modeMostDownload);
                //add data to view Pager adapter
                viewPagerAdapter.addFragmentList(BookUserFragment.newInstance(
                        ""+modeAll.getId(),
                        ""+modeAll.getCategory(),
                        ""+modeAll.getUid()),
                        modeAll.getCategory());
                viewPagerAdapter.addFragmentList(BookUserFragment.newInstance(
                                ""+modeMostView.getId(),
                                ""+modeMostView.getCategory(),
                                ""+modeMostView.getUid()),
                        modeMostView.getCategory());
                viewPagerAdapter.addFragmentList(BookUserFragment.newInstance(
                                ""+modeMostDownload.getId(),
                                ""+modeMostDownload.getCategory(),
                                ""+modeMostDownload.getUid()),
                        modeMostDownload.getCategory());

                //refesh list
                viewPagerAdapter.notifyDataSetChanged();
                //now load from firebase
                for(DataSnapshot ds:snapshot.getChildren()){
                    //get data
                    ModelCategories model = ds.getValue(ModelCategories.class);
                    //add data to list
                    categoriesList.add(model);
                    //add data to viewpager
                    viewPagerAdapter.addFragmentList(BookUserFragment.newInstance(
                            ""+model.getId(),
                            ""+model.getCategory(),
                            ""+model.getUid()),model.getCategory());
                    // refesh list
                    viewPagerAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //set Adapter View Pager
        viewPager.setAdapter(viewPagerAdapter);

    }
    public class ViewPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<BookUserFragment> fragmentlist = new ArrayList<>();
    private ArrayList<String> fragmentTitleList = new ArrayList<>();
    private Context context;
        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior,Context context) {
            super(fm, behavior);
            this.context = context;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentlist.get(position);
        }

        @Override
        public int getCount() {
            return fragmentlist.size();
        }
        private void addFragmentList(BookUserFragment fragment,String title){
            //add fragment list pragram
            fragmentlist.add(fragment);
            fragmentTitleList.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }
}