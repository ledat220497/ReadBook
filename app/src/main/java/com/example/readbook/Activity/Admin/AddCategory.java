package com.example.readbook.Activity.Admin;

import static android.app.Activity.RESULT_OK;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.readbook.R;
import com.example.readbook.databinding.ActivityAddCategoryBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;

public class AddCategory extends Fragment {
    ActivityAddCategoryBinding addCategoryBinding;
    private static final int PICK_IMAGE_REQUEST = 1;
    //firebase auth
    private FirebaseAuth firebaseAuth;
    private Uri mImageUri = null;
    //progress diglog
    private ProgressDialog progressDialog;
    private static final String TAG ="SINGER_IV";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init firebase
        firebaseAuth=FirebaseAuth.getInstance();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        addCategoryBinding = ActivityAddCategoryBinding.inflate(inflater, container,false);
        return addCategoryBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressDialog = new ProgressDialog(requireActivity());
        progressDialog.setTitle("Vui lòng chờ..");
        progressDialog.setCanceledOnTouchOutside(false);
        //back to Admindashboard
        addCategoryBinding.backToAdmin.setOnClickListener(v->{
            requireActivity().onBackPressed();

        });
        //handle Click seclect image
        addCategoryBinding.btnSelect.setOnClickListener(v->{
            selectImageCategory();
        });
        //handle click ,begin upload categorry
        addCategoryBinding.submitBtn.setOnClickListener(v->{
            validateData();
        });

    }



    private void selectImageCategory() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST && resultCode ==RESULT_OK && data != null && data.getData() != null){
            mImageUri =data.getData();
//            Picasso.LoadedFrom.load(mImageUri).into(addCategoryBinding.mImageUrl);
            Glide.with(this).load(mImageUri).into(addCategoryBinding.mImageUrl);
//            addCategoryBinding.mImageUrl.setImageURI(mImageUri);
        }
    }

    private String category = "", uri = "";

    private void validateData() {
        //befor adding validate data

        //getData
        category=addCategoryBinding.categoryEt.getText().toString().trim();
        if(TextUtils.isEmpty(category)){
            Toast.makeText(requireActivity(), "Yêu cầu bạn chọn thể loại...", Toast.LENGTH_SHORT).show();

        }if(mImageUri==null){
            Toast.makeText(requireActivity(), "Bạn phải chọn ảnh", Toast.LENGTH_SHORT).show();
        }
        else{
//            addCategoryFirebase();
            uploadImageFirebase();
        }
    }

    private void uploadImageFirebase() {
        //Step2:Validate data
        Log.d(TAG, "uploadImageStorage: ");


        //show progressdialog
        progressDialog.setMessage("Đang Tải...");
        progressDialog.show();
        //timestamp
        long timetamp = System.currentTimeMillis();
        //path off pdfin firebase storage
        String filepathName = "Categories/" +timetamp;
        //Storage refendence
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filepathName);
        storageReference.putFile(mImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSuccess: PDF uploaded to storage...");
                        Log.d(TAG, "onSuccess: geting pdf url");
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        //get pdf url
                        while (!uriTask.isSuccessful());
                        String uploadImagegUrl = "" +uriTask.getResult();
                        //upload to firebase db
                        uploadToInfoDb(uploadImagegUrl,timetamp);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure: Image upload failed..."+e.getMessage());
                        Toast.makeText(requireActivity(), "Tải ảnh thất bại", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void uploadToInfoDb(String uploadImagegUrl, long timetamp) {
        //Step3:Upload pdf info to firebase db

        Log.d(TAG, "uploadPdfStorage:Uploading Image info to firebase db... ");
        progressDialog.setMessage("Đang tải ảnh lên...");
        String uid = firebaseAuth.getUid();
        //Setup data to upload also add view count ,download count wwhile adding pdf/book


        HashMap<String,Object> hasmap = new HashMap<>();
        hasmap.put("id" ,""+timetamp);
        hasmap.put("category",""+category);
        hasmap.put("timetamp",timetamp);
        hasmap.put("uid",""+firebaseAuth.getUid());
        hasmap.put("uri",uploadImagegUrl);
        //db reference DB>Books
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.child("" +timetamp)
                .setValue(hasmap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onSuccess:Successfully uploaded...");
                        Toast.makeText(requireActivity(), "Tải ảnh thành công", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure: Failed to upload to db due to"+e.getMessage());
                        Toast.makeText(requireActivity(), "Tải lên thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //    private String getFileExtension(Uri uri){
//        ContentResolver contentResolver = getContentResolver();
//        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
//        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
//    }
//    private void addCategoryFirebase() {
//        progressDialog.setMessage("Adding category...");
//        progressDialog.show();
//
//        //getTimeTamp
//        long timetamp = System.currentTimeMillis();
//        String uri = mImageUri.getPath();
//        //Setup info to add in firebase
//        HashMap<String,Object> hasmap = new HashMap<>();
//        hasmap.put("id" ,""+timetamp);
//        hasmap.put("category",""+category);
//        hasmap.put("timetamp",timetamp);
//        hasmap.put("uid",""+firebaseAuth.getUid());
//        hasmap.put("uri",uri);
//
//        //add firebase db ,,, databaseStorage image
//        StorageReference storage = FirebaseStorage.getInstance().getReference();
//
//        // Defining the child of storageReference
//        if(mImageUri != null) {
//            StorageReference ref
//                    = storage
//                    .child(
//                            "Categories/"
//                                    + UUID.randomUUID().toString());
//
//            // adding listeners on upload
//            // or failure of image
//            ref.putFile(mImageUri)
//                    .addOnSuccessListener(
//                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
//
//                                @Override
//                                public void onSuccess(
//                                        UploadTask.TaskSnapshot taskSnapshot) {
//
//                                    // Image uploaded successfully
//                                    // Dismiss dialog
//                                    progressDialog.dismiss();
//                                    Toast
//                                            .makeText(requireActivity(),
//                                                    "Image Uploaded!!",
//                                                    Toast.LENGTH_SHORT)
//                                            .show();
//                                }
//                            })
//
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//
//                            // Error, Image not uploaded
//                            progressDialog.dismiss();
//                            Toast
//                                    .makeText(requireActivity(),
//                                            "Failed " + e.getMessage(),
//                                            Toast.LENGTH_SHORT)
//                                    .show();
//                        }
//                    })
//                    .addOnProgressListener(
//                            new OnProgressListener<UploadTask.TaskSnapshot>() {
//
//                                // Progress Listener for loading
//                                // percentage on the dialog box
//                                @Override
//                                public void onProgress(
//                                        UploadTask.TaskSnapshot taskSnapshot) {
//                                    double progress
//                                            = (100.0
//                                            * taskSnapshot.getBytesTransferred()
//                                            / taskSnapshot.getTotalByteCount());
//                                    progressDialog.setMessage(
//                                            "Uploaded "
//                                                    + (int) progress + "%");
//                                }
//                            });
//        }else{
//
//        }
//
//
//
//
//        //add firebase db...database root categories,categoryid,categoryinfo
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
//
//        ref.child(""+timetamp)
//                .setValue(hasmap)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        //category add success
//                        progressDialog.dismiss();
//                        Toast.makeText(requireActivity(), "Category added successfully...", Toast.LENGTH_SHORT).show();
//
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        //add category failed
//                        progressDialog.dismiss();
//                        Toast.makeText(requireActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//
//
//
//    }
}