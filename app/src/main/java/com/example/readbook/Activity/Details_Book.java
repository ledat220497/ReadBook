package com.example.readbook.Activity;

import static java.security.AccessController.getContext;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.readbook.Activity.Admin.MyApplication;
import com.example.readbook.Activity.Admin.modeladmin.ModelPdfFile;
import com.example.readbook.Adapter.AdaperPdfUser;
import com.example.readbook.Adapter.AdapterComment;
import com.example.readbook.Adapter.AdapterFavorite;
import com.example.readbook.Adapter.OtherBookAdapter;
import com.example.readbook.Model.ModelComment;
import com.example.readbook.R;
import com.example.readbook.databinding.ActivityDetailsBookBinding;
import com.example.readbook.databinding.DialogCommentBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Details_Book extends AppCompatActivity {
    ActivityDetailsBookBinding detailsbin;

    private static final String TAG_DOWNLOAD = "DOWNLOAD_TAG";

    String bookId, bookTitle,bookUrl,categoryId;
    private boolean isInMyFavorite = false;
    private boolean isInMyRead = false;

    private FirebaseAuth firebaseAuth;
    //Progressbar
    private ProgressDialog progress;
    //Arraylist hold comments
    private ArrayList<ModelComment>  commentList;
    //arraylist pdf
    private ArrayList<ModelPdfFile> ortherpdffile;
    //adapter to set rcv
    private AdapterComment adapterComment;
    //adapter OrtherBook
    OtherBookAdapter adapterOtherBook;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailsbin = ActivityDetailsBookBinding.inflate(getLayoutInflater());
        View view = detailsbin.getRoot();
        setContentView(view);
        //get data from intent
        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");
        categoryId= intent.getStringExtra("categoryId");
        
        //at Start hide download button ,because we need book url we will load later in function detailbook
        //
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            checkIsFavorite();
        }
        detailsbin.btnDownload.setVisibility(View.GONE);
        loadBookDetails();
        //load Orther Book

        loadOrtherBook();

        //load Comment
        loadComment();

        //init progress dialog
        progress = new ProgressDialog(this);
        progress.setTitle("Xin vui lòng chờ");
        progress.setCanceledOnTouchOutside(false);


        //Increment Book viewCount when ever this page start
        MyApplication.incrementBookViewCount(bookId);


        //handle click go back
        detailsbin.btnBack.setOnClickListener(v -> {
            onBackPressed();
        });
        //handle click Open readbook

            detailsbin.btnRead.setOnClickListener(v -> {
                if(!isInMyRead) {
                    Intent inten1 = new Intent(Details_Book.this, PDFViewActivity.class);
                    inten1.putExtra("bookId", bookId);
                    startActivity(inten1);
                    //save view user
                    loadUserViewCount();
                }else{
                    Toast.makeText(this, "Không đọc thành công", Toast.LENGTH_SHORT).show();
                }

            });

        //Handle Click add Comment
        detailsbin.addComment.setOnClickListener(v ->{
            if(firebaseAuth.getCurrentUser() ==null){
                Toast.makeText(this, "Bạn cần đăng nhập để bình luận", Toast.LENGTH_SHORT).show();
            }else {
                addCommentDialog();
            }
        });
        //handle Click to download
        detailsbin.btnDownload.setOnClickListener(v -> {
            Log.d(TAG_DOWNLOAD, "onCreate: Checking permission");
            if(ContextCompat.checkSelfPermission(Details_Book.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                Log.d(TAG_DOWNLOAD, "onCreate:Permission already granded can download book ");
                MyApplication.downLoadBook(Details_Book.this,""+bookUrl,""+bookTitle,""+bookUrl);
              SaveUserDownloadCount();

            }else{
                Log.d(TAG_DOWNLOAD, "onCreate:Permission was not granted request permission... ");
                request.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            }
        });

        //handle click Favorite
        detailsbin.btnLike.setOnClickListener(v ->{
            if(firebaseAuth.getCurrentUser() == null){
                Toast.makeText(this, "Bạn cần đăng nhập để yêu thích", Toast.LENGTH_SHORT).show();
            }else {
                if(isInMyFavorite){
                    //in favorite , remove  from favorite
                    MyApplication.RemoveFavorite(Details_Book.this,bookId);
                }else{
                    //not in favorite,add to favortite
                    MyApplication.addToFactorite(Details_Book.this,bookId);
                }
            }
        });



    }

    private void loadOrtherBook() {
        ortherpdffile = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.orderByChild("categoryId").equalTo(categoryId).limitToLast(4)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        //Clear before starting add data into it
                        ortherpdffile.clear();
                        for(DataSnapshot ds:snapshot.getChildren()){
                            //getData
                            ModelPdfFile model = ds.getValue(ModelPdfFile.class);
                            //add to list
                            ortherpdffile.add(model);

                        }
                        //setup adapter user
                        adapterOtherBook = new OtherBookAdapter(Details_Book.this,ortherpdffile);
                        //setup adapter to rcv
                        detailsbin.rcvOtherBook.setAdapter(adapterOtherBook);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void SaveUserDownloadCount()
    {
        long timetamp = System.currentTimeMillis();
        //set up data add in firebase of curren user for favarite
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("bookId",""+bookId);
        hashMap.put("timetamp",""+timetamp);

        //Save to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
        ref.child(firebaseAuth.getUid()).child("UserDownLoad").child(bookId)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(Details_Book.this, "Luu thành  cong", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Details_Book.this, "Không lưu thành công", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void loadUserViewCount() {
        long timetamp = System.currentTimeMillis();
        //set up data add in firebase of curren user for favarite
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("bookId",""+bookId);
        hashMap.put("timetamp",""+timetamp);

        //Save to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
        ref.child(firebaseAuth.getUid()).child("ViewBooks").child(bookId)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(Details_Book.this, "Luu thành công", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Details_Book.this, "Không lưu thành công", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void loadComment() {
        //init arraylist before adđing data into it
        commentList = new ArrayList<>();

        //db path to load Comment
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId).child("Comments")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //Clear arraylist before  start adđing dâta into it
                        commentList.clear();
                        for(DataSnapshot ds : snapshot.getChildren()){
                            //get Data as model ,spelling of cariables in model must be as same as in firebase
                            ModelComment model = ds.getValue(ModelComment.class);
                            //add to arraylist
                            commentList.add(model);
                        }
                        //setup adapter
                        adapterComment  = new AdapterComment(Details_Book.this,commentList);
                        //set adapter to rcv
                        detailsbin.rcvComment.setAdapter(adapterComment);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private String comment ="";
    private void addCommentDialog() {
        //inflate bindView
        DialogCommentBinding dialog = DialogCommentBinding.inflate(LayoutInflater.from(this));
        //setup dialog Builer

        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.CustomDialog);
        builder.setView(dialog.getRoot());
        //create and show dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        //handle Click back
        dialog.btnBack.setOnClickListener(v->{
            alertDialog.dismiss();
        });
        //handle Click add Comment
        dialog.btnSubmit.setOnClickListener(v->{

            //getData
            comment = dialog.commentEt.getText().toString().trim();
            //Validate data
            if(TextUtils.isEmpty(comment)){
                Toast.makeText(this, "Nhập bình luận của bạn...", Toast.LENGTH_SHORT).show();
            }else{
                alertDialog.dismiss();
                addComment();
            }
        });
    }

    private void addComment() {
        //show progress
        progress.setMessage("Đang Thêm Bình Luân...");
        progress.show();
        // timestamp for comment id ,cooment time
        String timestamp = "" +System.currentTimeMillis();
        //setup data to add in db for comment
        HashMap<String,Object>  hasmap = new HashMap<>();
        hasmap.put("id",""+timestamp);
        hasmap.put("bookId",""+bookId);
        hasmap.put("timestamp",""+timestamp);
        hasmap.put("comment",""+comment);
        hasmap.put("uid",""+firebaseAuth.getUid());
        //Db path to add data into it
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId).child("Comments").child(timestamp)
                .setValue(hasmap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(Details_Book.this, "Thêm Thành Công", Toast.LENGTH_SHORT).show();
                        progress.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Details_Book.this, "Không thành công"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }



    //request Storage permission
    private ActivityResultLauncher<String> request =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),isGranted ->{
                if(isGranted){
                    Log.d(TAG_DOWNLOAD, "Premission Granted ");
                    MyApplication.downLoadBook(this,""+bookId,""+bookTitle,""+bookUrl);
                }else{
                    Log.d(TAG_DOWNLOAD, "Premission was denied ...");
                    Toast.makeText(this, "Premission was denied ...", Toast.LENGTH_SHORT).show();
                }
            });
    private void loadBookDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                         bookTitle = "" + snapshot.child("title").getValue();
                        String description = "" + snapshot.child("description").getValue();
                        String categoryId = "" + snapshot.child("categoryId").getValue();
                        String viewCount = "" + snapshot.child("viewCount").getValue();
                        String downloadCount = "" + snapshot.child("downloadCount").getValue();
                        bookUrl = "" + snapshot.child("url").getValue();
                        String timetamp = "" + snapshot.child("timestamp").getValue();
                        //required data is loaded ,show downloadButton
                        detailsbin.btnDownload.setVisibility(View.VISIBLE);
                        //format date
//                        String date = MyApplication.formatTimetamp(Long.parseLong(timetamp));
//                        Log.d(">>", "onDataChange: "+date);


                        MyApplication.loadCategory(
                                "" + categoryId,
                                detailsbin.categoryTv
                        );
                        MyApplication.loadFilePdfUrl(
                                "" + bookUrl,
                                "" + bookTitle,
                                detailsbin.pdfView,
                                detailsbin.progressBar,
                                detailsbin.pageTv


                        );
                        MyApplication.loadSizePdf(
                                "" + bookUrl,
                                "" + bookTitle,
                                detailsbin.sizeTv

                        );

//                        MyApplication.loadPdfPageCount(
//                                Details_Book.this,
//                                ""+bookUrl,
//                                detailsbin.pageTv);
                        //set data
                        detailsbin.titleTv.setText(bookTitle);
                        detailsbin.descriptionBook.setText(description);
                        detailsbin.viewTv.setText(viewCount.replace("null", "N/A"));
                        detailsbin.downloadTv.setText(downloadCount.replace("null", "N/A"));
//                        detailsbin.dateTv.setText(date);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
    private  void checkIsFavorite(){
        //logn in check if íts in favorite list or not
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
        ref.child(firebaseAuth.getUid()).child("Favorite").child(bookId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isInMyFavorite = snapshot.exists();//true : if exit , false not exit
                        if(isInMyFavorite){
                            //exit in favorite
                            detailsbin.btnLike.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.drawable.ic_baseline_favorite_24,0,0);
//                            detailsbin.btnLike.setText("Huỷ yêu thích");
                        }else{
                            detailsbin.btnLike.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.drawable.ic_baseline_favorite_border_24,0,0);
//                            detailsbin.btnLike.setText(" yêu thích");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }
}