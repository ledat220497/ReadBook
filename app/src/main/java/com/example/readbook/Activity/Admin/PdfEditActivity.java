package com.example.readbook.Activity.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.readbook.R;
import com.example.readbook.databinding.ActivityPdfEditBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class PdfEditActivity extends AppCompatActivity {
    ActivityPdfEditBinding bindingedit;
    //book id get from intent started from AdapterPdfAdmin
    private String bookId;

    //progress dialog
    ProgressDialog progressDialog;

    private ArrayList<String> categoryTitleArraylist, categoryIdArraylist;
    private static final String TAG = "BOOK_EDIT";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingedit = ActivityPdfEditBinding.inflate(getLayoutInflater());
        View view = bindingedit.getRoot();
        setContentView(view);
        bookId = getIntent().getStringExtra("bookId");
        ;
        //set up progressdialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Vui lòng chờ...");
        progressDialog.setCanceledOnTouchOutside(false);

        //load category from db
        loadCategories();
        loadBookInfo();

        //handle click out screen
        bindingedit.backBtn.setOnClickListener(v -> {
            onBackPressed();
        });

        //handle click category]
        bindingedit.categoryTv.setOnClickListener(v -> {
            categoryDialog();
        });
        //handle click beagin upload
        bindingedit.submitbookBtn.setOnClickListener(v -> {
            validateData();
        });

    }

    private String title = "", description = "";

    private void validateData() {
        //get data
        title = bindingedit.titleBookEt.getText().toString().trim();
        description = bindingedit.titleDesEt.getText().toString().trim();
        //validate data
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Nhập tên...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Nhập nội dung...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(selectCategoryId)) {
            Toast.makeText(this, "Chọn thể loại...", Toast.LENGTH_SHORT).show();
        } else {
            updatePdf();
        }

    }

    private void updatePdf() {
        Log.d(TAG, "updatePdf: Starting updatePdf info to db");
        progressDialog.setMessage("Đang cập nhật...");
        progressDialog.show();
        //setup data to update
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("title", "" + title);
        hashMap.put("description", "" + description);
        hashMap.put("categoryId", "" + selectCategoryId);
        //start updatting
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Update book successfully..");
                        progressDialog.dismiss();
                        Toast.makeText(PdfEditActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: failed to update due to" + e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(PdfEditActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadBookInfo() {
        Log.d(TAG, "loadBookInfo: Loading books info");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        //get book info
                        selectCategoryId = "" + snapshot.child("categoryId").getValue();
                        String description = "" + snapshot.child("description").getValue();
                        String title = "" + snapshot.child("title").getValue();
                        //set to view
                        bindingedit.titleBookEt.setText(title);
                        bindingedit.titleDesEt.setText(description);
                        Log.d(TAG, "onDataChange: Loading book category info");
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
                        ref.child(selectCategoryId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        //get category
                                        String category = "" + snapshot.child("category").getValue();
                                        //set category to text view
                                        bindingedit.categoryTv.setText(category);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



    }
    private String selectCategoryId = "", SelectCategoryTitle = "";
    private void categoryDialog () {
        String[] categoryArray = new String[categoryTitleArraylist.size()];
        for (int i = 0; i < categoryTitleArraylist.size(); i++) {
            categoryArray[i] = categoryTitleArraylist.get(i);
        }
        //Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn Thể Loại")
                .setItems(categoryArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectCategoryId = categoryIdArraylist.get(i);
                        SelectCategoryTitle = categoryTitleArraylist.get(i);

                        //setting to textview
                        bindingedit.categoryTv.setText(SelectCategoryTitle);

                    }
                }).show();
    }
    private void loadCategories () {
        Log.d(TAG, "loadCategories: Loading categories...");
        categoryTitleArraylist = new ArrayList<>();
        categoryIdArraylist = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryIdArraylist.clear();
                categoryTitleArraylist.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String id = "" + ds.child("id").getValue();
                    String category = "" + ds.child("category").getValue();
                    categoryIdArraylist.add(id);
                    categoryTitleArraylist.add(category);


                    Log.d(TAG, "onDataChange: ID:" + id);
                    Log.d(TAG, "onDataChange: Category:" + category);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}