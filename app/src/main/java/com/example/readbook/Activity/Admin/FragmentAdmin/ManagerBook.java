package com.example.readbook.Activity.Admin.FragmentAdmin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.readbook.Activity.Admin.AddCategory;
import com.example.readbook.Activity.Admin.AddPdf;
import com.example.readbook.Activity.Admin.AdminDashBoard;
import com.example.readbook.Activity.Admin.adapterAdmin.CategoryAdapter;
import com.example.readbook.Activity.Admin.modeladmin.ModelCategories;
import com.example.readbook.Activity.LoginAndRegister.Login;
import com.example.readbook.R;
import com.example.readbook.databinding.ManagerAdminFragmentBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManagerBook extends Fragment {

     ManagerAdminFragmentBinding managerbookbinding;
    //firebase autth
    private FirebaseAuth firebaseAuth;
    NavController mNavController;
    //Array list categories
    private ArrayList<ModelCategories> categoriesArrayList;
    //adapter
    private CategoryAdapter categoryAdapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init Firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        managerbookbinding= ManagerAdminFragmentBinding.inflate(inflater,container,false);
        return managerbookbinding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNavController =
                Navigation.findNavController(requireActivity(), R.id.idFragmentContainer);
        managerbookbinding.addCategoryBtn.setOnClickListener(v->{
            mNavController.navigate(R.id.action_managerBook_to_addCategory);
            //  start
            AddCategory addCategory = new AddCategory();
            //  end
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.containerCate,addCategory);
        });
        checkUser();
        loadCategories();
        managerbookbinding.addBookBtn.setOnClickListener(v->{
            mNavController.navigate(R.id.action_managerBook_to_addPdf);
            //  start
            AddPdf addPdf = new AddPdf();
            //  end
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.containerAddpđf,addPdf);
        });
        managerbookbinding.seachEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //called as and wwhien user type each letter
                try {
                    categoryAdapter.getFilter().filter(charSequence);
                }catch (Exception e){

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    private void loadCategories() {

        //init arraylisst
        categoriesArrayList = new ArrayList<>();

        //getAll categories form firebase  >Categories
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear arraylist before adđing data info it
                categoriesArrayList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    //get Data
                    ModelCategories model = ds.getValue(ModelCategories.class);
                    //add arraylist
                    categoriesArrayList.add(model);

                }
                //setAdapter Recycle view
                categoryAdapter = new CategoryAdapter(getActivity(),categoriesArrayList);
                managerbookbinding.rcyView.setAdapter(categoryAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser == null){
            startActivity(new Intent(requireActivity(), Login.class));
        }else{
            String Email = firebaseUser.getEmail();
//            dashBoardBinding.showEmail.setText(Email);
        }
    }
}
