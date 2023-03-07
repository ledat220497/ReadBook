package com.example.readbook.Activity.TabLayoutMain;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.readbook.Activity.Admin.AddCategory;
import com.example.readbook.Activity.Admin.MyApplication;
import com.example.readbook.Activity.Admin.modeladmin.ModelPdfFile;
import com.example.readbook.Adapter.AdapterFavorite;
import com.example.readbook.R;
import com.example.readbook.databinding.FragmentProfileFmBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class profile_fm extends Fragment {
    FragmentProfileFmBinding bindingprofile;
    FirebaseAuth mFirebaseAuth;
    NavController mNavController;
    private ArrayList<ModelPdfFile> pdfFileArrayList;

    //adapter to set in recycle View
    private AdapterFavorite adapterFavorite;
    private static final String TAG = "PROFILE_TAG";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bindingprofile = FragmentProfileFmBinding.inflate(inflater,container, false);
        return bindingprofile.getRoot();
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNavController =
                Navigation.findNavController(requireActivity(), R.id.fragmentContainerUser);
        loadUserInfo();
        loadFavoriteBook();
        //handle click started profile edit page
        bindingprofile.btnEditProfile.setOnClickListener(v->{
            mNavController.navigate(R.id.action_profile_fm_to_profileEdit);
            //  start
            ProfileEdit profileEdit = new ProfileEdit();
            //  end
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.containerProfileEdit,profileEdit);

        });
    }
    private void loadFavoriteBook() {
        //init
        pdfFileArrayList = new ArrayList<>();
        //load favorite book from firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
        ref.child(mFirebaseAuth.getUid()).child("Favorite")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //CLEAR list before startding  adding data
                        pdfFileArrayList.clear();
                        for(DataSnapshot ds :snapshot.getChildren()){
                            String bookId =""+ ds.child("bookId").getValue();
                            ModelPdfFile model = new ModelPdfFile();
                            model.setId(bookId);

                            //ADD
                            pdfFileArrayList.add(model);

                        }


                        //setup adapter
                        adapterFavorite = new AdapterFavorite(requireActivity(),pdfFileArrayList);
                        //set adapter rcv
                        bindingprofile.bookRcv.setAdapter(adapterFavorite);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }
    private void loadUserInfo() {
        Log.d(TAG, "loadUserInfo:Đang tải dữ liệu "+mFirebaseAuth.getUid());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
        ref.child(mFirebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get all info user here from snapshot
                        String fullName = ""+snapshot.child("name").getValue();
                        String email  = "" + snapshot.child("email").getValue();
                        String profileImage = "" + snapshot.child("profileimage").getValue();
                        String timestamp = "" + snapshot.child("timetamp").getValue();
                        String uid = "" + snapshot.child("uid").getValue();
                        String userType = ""+ snapshot.child("userType").getValue();

                        //formatdate to dd//mm//yyyy
                        String formatedate = MyApplication.formatTimetamp(Long.parseLong(timestamp));
                        //setdatato ui
                        bindingprofile.fullnameTv.setText(fullName);
                        bindingprofile.emailTv.setText(email);
                        bindingprofile.memberDataTv.setText(formatedate);
                        bindingprofile.accountTypeTv.setText(userType);
                        //set image ,using glide
                        Glide.with(requireActivity())
                                .load(profileImage)
                                .placeholder(R.drawable.ic_person_64)
                                .into(bindingprofile.proImg);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}