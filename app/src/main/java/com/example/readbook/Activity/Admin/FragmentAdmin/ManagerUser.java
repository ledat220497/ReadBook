package com.example.readbook.Activity.Admin.FragmentAdmin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readbook.Activity.Admin.adapterAdmin.AdapterUserAdmin;
import com.example.readbook.Activity.Admin.modeladmin.ModelUser;
import com.example.readbook.databinding.ManagerUserFragmentBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManagerUser extends Fragment {
    ManagerUserFragmentBinding useradminbinding;

    private List<ModelUser> arrlistuser;
    AdapterUserAdmin adapteruser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        useradminbinding = ManagerUserFragmentBinding.inflate(inflater, container,false);
        return useradminbinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapteruser = new AdapterUserAdmin(getContext(), new ArrayList<>());
        useradminbinding.rcvUser.setAdapter(adapteruser);
        listAllUser();
    }

    private void listAllUser() {
        //setup data for genre
        useradminbinding.rcvUser.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        arrlistuser = new ArrayList<>();




        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear arraylist before adđing data info it
                arrlistuser.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    //get Data
                    ModelUser model = ds.getValue(ModelUser.class);
                    //add arraylist
                    Log.d(">>>", "User: "+model.getName());

                    arrlistuser.add(model);

                }
                //setAdapter Recycle view
                adapteruser.setData(arrlistuser);
                Log.d("listuser", "onDataChange: "+adapteruser);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
