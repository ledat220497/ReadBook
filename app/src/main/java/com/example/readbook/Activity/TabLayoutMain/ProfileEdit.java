package com.example.readbook.Activity.TabLayoutMain;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.readbook.Activity.Admin.MyApplication;
import com.example.readbook.R;
import com.example.readbook.databinding.FragmentProfileEditBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.util.HashMap;


public class ProfileEdit extends Fragment {

    FragmentProfileEditBinding bindingeditProfile;
    FirebaseAuth mFirebaseAuth;
    private Uri imageUri = null;
    private String name = "";
    private ProgressDialog progressDialog;


    private static final String TAG ="PROFILE_EDIT";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bindingeditProfile = FragmentProfileEditBinding.inflate(inflater,container,false);
        // Inflate the layout for this fragment
        return bindingeditProfile.getRoot();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadUserInfo();

        //setup Progress Dialog
        progressDialog = new ProgressDialog(requireActivity());
        progressDialog.setTitle("Xin vui lòng chờ");
        progressDialog.setCanceledOnTouchOutside(false);

        //handle click back
        bindingeditProfile.btnBack.setOnClickListener(v->{
            requireActivity().onBackPressed();
        });
    //handle click imageView
        bindingeditProfile.profileIv.setOnClickListener(v->{
            showImageAttachMenu();
        });

        //handle click update profile
        bindingeditProfile.btnUpdate.setOnClickListener(v ->{
            validateData();
        });
    }

    private void validateData() {
        ///get data
        name = bindingeditProfile.nameEt.getText().toString().trim();
        //validateData
        if(TextUtils.isEmpty(name)){
            Toast.makeText(requireActivity(), "Bạn cần nhập tên", Toast.LENGTH_SHORT).show();
        }else{
            if(imageUri ==null){
                //  need to update without image
                updateProfile("");
            }else{
                //  need to update without image
                uploadImage();
            }
        }
    }

    private void uploadImage() {
        Log.d(TAG, "uploadImage: Đang tải ảnh lên ...");
        progressDialog.setMessage("Đang tải ảnh...");
        progressDialog.show();
        //image path and name , use uid  to replace
        String profilePathName = "ProfileImages/"+mFirebaseAuth.getUid();


        //StorageReference
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(profilePathName);
        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        String uploadImageUrl = "" +uriTask.getResult();
                        updateProfile(uploadImageUrl);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(requireActivity(), "Không thành công "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateProfile(String imageUrl) {
        progressDialog.setMessage("Cập nhật thông tin người dùng...");
        progressDialog.show();


        //setup data to update firebase
        HashMap<String,Object> has = new HashMap<>();
        has.put("name",name);
        if(imageUri != null){
            has.put("profileimage",""+imageUrl);
        }

        //Update data to firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
        ref.child(mFirebaseAuth.getUid())
                .updateChildren(has)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Cập nhật thông tin...");
                        progressDialog.dismiss();
                        Toast.makeText(requireActivity(), "Cập nhật thông tin..", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Thất bại"+e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(requireActivity(), "không thành công ", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showImageAttachMenu() {
        PopupMenu popup = new PopupMenu(requireActivity(),bindingeditProfile.profileIv);
        popup.getMenu().add(Menu.NONE,0,0,"Camera");
        popup.getMenu().add(Menu.NONE,1,1,"Gallery");
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int which  = item.getItemId();
                if(which ==0){
                    //Camera
                    pickImageCamera();
                }else if(which ==1){
                    //Gallery
                    pickImageGallery();
                }
                return false;
            }
        });

    }

    private void pickImageGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryResultLauncher.launch(intent);
    }

    private void pickImageCamera() {
        //intent to pick image from camera
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"Chọn Mới");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"");
        imageUri = requireActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        cameraResultLauncher.launch(intent);


    }
    private ActivityResultLauncher<Intent> cameraResultLauncher= registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG, "onActivityResult: "+imageUri);
                    //used to handle result of camera
                    //get uri of image
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data  = result.getData(); //no need here as in camera case we alrealdy  have image in imageuri varible
                        bindingeditProfile.profileIv.setImageURI(imageUri);

                    }else{
                        Toast.makeText(requireActivity(), "Thử lại", Toast.LENGTH_SHORT).show();
                    }

                }
            }
    );
    private ActivityResultLauncher<Intent> galleryResultLauncher= registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //get uri of image
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Log.d(TAG, "onActivityResult: "+imageUri);
                        Intent data  = result.getData(); //no need here as in camera case we alrealdy  have image in imageuri varible
                        imageUri = data.getData();
                        Log.d(TAG, "onActivityResult: "+imageUri    );
                        bindingeditProfile.profileIv.setImageURI(imageUri);

                    }else{
                        Toast.makeText(requireActivity(), "Thử lại", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );
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

                        //setdatato ui
                        bindingeditProfile.nameEt.setText(name);

                        //set image ,using glide
                        Glide.with(requireActivity())
                                .load(profileImage)
                                .placeholder(R.drawable.ic_person_64)
                                .into(bindingeditProfile.profileIv);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}