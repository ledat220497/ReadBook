package com.example.readbook.Activity.Admin;

import static android.app.Activity.RESULT_OK;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.readbook.databinding.ActivityAddPdfBinding;
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

import java.util.ArrayList;
import java.util.HashMap;

public class AddPdf extends Fragment {
    ActivityAddPdfBinding addPdfBinding;
    //firebase Auth
    private FirebaseAuth firebaseAuth;

    //arraylist to hold pdf categories
    ArrayList<String> categoriesTitleArrayList,categoryIdArrayList;
    //progress dialig
    private ProgressDialog progressDialog;
    //tag for debugging
    private static final String TAG ="add_pdf_tag";

    //uri of picked pdf
    private Uri pdfUri = null;
    private static final  int PDF_PICK_CODE = 1000;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        addPdfBinding = ActivityAddPdfBinding.inflate(inflater,container,false);

        return addPdfBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addPdfBinding.imgBackBtn.setOnClickListener(v->{
            requireActivity().onBackPressed();

        });
        loadPdfCategory();
        //setup progress dialog
        progressDialog = new ProgressDialog(requireActivity());
        progressDialog.setTitle("Vui lòng chờ..");
        progressDialog.setCanceledOnTouchOutside(false);


        //ahandle click attchpdf
        addPdfBinding.attachBtn.setOnClickListener(v->{
            pdfPickIntent();
        });
        //handle click ,,pick category
        addPdfBinding.categoryTv.setOnClickListener(v ->{
            categoryPickDialog();
        });
        //handle click upload pdf
        addPdfBinding.submitbookBtn.setOnClickListener(v->{
            validateData();
        });


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init firebase Auth
        firebaseAuth= FirebaseAuth.getInstance();



    }
    private String title="",desription = "";

    private void validateData() {
        //Step:validate Data
        Log.d(TAG, "validateData: validating data...");
        //Step getData
        title = addPdfBinding.nameBookEt.getText().toString().trim();
        desription = addPdfBinding.desEt.getText().toString().trim();
        //validate Data
        if(TextUtils.isEmpty(title)){
            Toast.makeText(requireActivity(), "Nhập tên..", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(desription)){
            Toast.makeText(requireActivity(), "Nhập nội dung..", Toast.LENGTH_SHORT).show();

        }else if(TextUtils.isEmpty(selectedCategoryTitle)){
            Toast.makeText(requireActivity(), "Chọn thể loại..", Toast.LENGTH_SHORT).show();
        }
        else if(pdfUri==null){
            Toast.makeText(requireActivity(), "Chọn Sách..", Toast.LENGTH_SHORT).show();
        }else{
            //all dataa is valid can upload now
            uploadPdfStorage();
        }


    }

    private void uploadPdfStorage() {
        //Step2:Validate data
        Log.d(TAG, "uploadPdfStorage: ");


        //show progressdialog
        progressDialog.setMessage("Đang tải...");
        progressDialog.show();
        //timestamp
        long timetamp = System.currentTimeMillis();
        //path off pdfin firebase storage
        String filepathName = "Books/" +timetamp;
        //Storage refendence
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filepathName);
        storageReference.putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSuccess: PDF uploaded to storage...");
                        Log.d(TAG, "onSuccess: geting pdf url");
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        //get pdf url
                        while (!uriTask.isSuccessful());
                        String uploadPdfUrl = "" +uriTask.getResult();
                        //upload to firebase db
                        uploadToInfoDb(uploadPdfUrl,timetamp);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure: PDF upload failed..."+e.getMessage());
                        Toast.makeText(requireActivity(), "Tải sách lên thất bại", Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void uploadToInfoDb(String uploadPdfUrl, long timetamp) {
        //Step3:Upload pdf info to firebase db

        Log.d(TAG, "uploadPdfStorage:Uploading pdf info to firebase db... ");
        progressDialog.setMessage("Uploading pdf info...");
        String uid = firebaseAuth.getUid();
        //Setup data to upload also add view count ,download count wwhile adding pdf/book

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("uid",""+uid);
        hashMap.put("id",""+timetamp);
        hashMap.put("title",""+title);
        hashMap.put("description",""+desription);
        hashMap.put("categoryId",""+selectedCategoryId);
        hashMap.put("url",""+uploadPdfUrl);
        hashMap.put("viewCount",0);
        hashMap.put("downloadCount",0);
        //db reference DB>Books
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child("" +timetamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onSuccess:Successfully uploaded...");
                        Toast.makeText(requireActivity(), "Successfully uploaded", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure: Failed to upload to db due to"+e.getMessage());
                        Toast.makeText(requireActivity(), "Failed to upload to db due to", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void loadPdfCategory() {
        Log.d(TAG, "loadPdfCategory: Loading");
        categoriesTitleArrayList = new ArrayList<>();
        categoryIdArrayList = new ArrayList<>();
        //db refenrence to load categories > Categories
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoriesTitleArrayList.clear();//clear before adding data
                categoryIdArrayList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    //get id and title category
                    String categoryId =""+ds.child("id").getValue();
                    String categoryTitle =""+ds.child("category").getValue();
                    //add to respective arraylist
                    categoriesTitleArrayList.add(categoryTitle);
                    categoryIdArrayList.add(categoryId);



                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
//Selected categoryid and categorytitle
    private  String selectedCategoryId,selectedCategoryTitle;
    private void categoryPickDialog() {
//        //first we nedd to get category from firebase
//        Log.d(TAG, "categoryPickDialog: showlog category pick dialog");
        //get String array of categories from arraylist
        String[] categoriesArray = new String[categoriesTitleArrayList.size()];
        for(int i= 0;i<categoriesTitleArrayList.size();i++){
            categoriesArray[i] = categoriesTitleArrayList.get(i);


        }
        //Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Pick Category")
                .setItems(categoriesArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        //handle item click
                        //get clicked item from list
                        selectedCategoryTitle  = categoriesTitleArrayList.get(which);
                        selectedCategoryId = categoryIdArrayList.get(which);

                        //set to category textView
                        addPdfBinding.categoryTv.setText(selectedCategoryTitle);
//                        Log.d("category", "onClick: "+category);

                    }
                })
                .show();

    }

    private void pdfPickIntent() {
        Log.d(TAG, "pdfPickIntent: starting pdf pick intent");
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select pdf"),PDF_PICK_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode ==RESULT_OK){
            if(requestCode==PDF_PICK_CODE){
                Log.d(TAG, "onActivityResult: ");
                pdfUri = data.getData();
                Log.d("pdf", "onActivityResult:Uri " +pdfUri);
            }

        }else{
            Log.d(TAG, "onActivityResult: Canceled picking pdf");
            Toast.makeText(requireActivity(), "Cancled king pdf", Toast.LENGTH_SHORT).show();
        }
    }
}