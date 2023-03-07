package com.example.readbook.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.readbook.R;
import com.example.readbook.databinding.ActivityPdfviewBinding;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PDFViewActivity extends AppCompatActivity {
   ActivityPdfviewBinding pdfViewBinding;

   private String bookId;
   private static final String TAG = "PDF_VIEW";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pdfViewBinding = ActivityPdfviewBinding.inflate(getLayoutInflater());
        setContentView(pdfViewBinding.getRoot());

        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");
        Log.d(TAG, "onCreate: "+bookId);

        loadBookDetails();

        //handle click go back
        pdfViewBinding.btnBack.setOnClickListener(v->{

            onBackPressed();
        });


    }

    private void loadBookDetails() {

        Log.d(TAG, "loadBookDetails: Get pdf url ...");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get book url
                        String pdfurl = "" +snapshot.child("url").getValue();
                        Log.d(TAG, "onDataChange: PDF URL" +pdfurl);
                        //load book from url Storage
                        loadBookfromUrl(pdfurl);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadBookfromUrl(String pdfurl) {
        Log.d(TAG, "loadBookfromUrl: Get PDF from Storage");
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfurl);
        ref.getBytes(Contanst.MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {

                        //load pdf byte
                        pdfViewBinding.pdfView.fromBytes(bytes)
                                .swipeHorizontal(false)
                                .onPageChange(new OnPageChangeListener() {
                                    @Override
                                    public void onPageChanged(int page, int pageCount) {
                                        int currentPage = (page + 1);
                                        pdfViewBinding.toolBarSubTitleTv.setText(currentPage +""+pageCount);
                                    }
                                })
                                .onError(new OnErrorListener() {
                                    @Override
                                    public void onError(Throwable t) {
                                        Toast.makeText(PDFViewActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .onPageError(new OnPageErrorListener() {
                                    @Override
                                    public void onPageError(int page, Throwable t) {
                                        Toast.makeText(PDFViewActivity.this, ""+page+t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .load();
                        pdfViewBinding.progressBar.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //load to pdf book failed
                        pdfViewBinding.progressBar.setVisibility(View.GONE);

                    }
                });

    }
}