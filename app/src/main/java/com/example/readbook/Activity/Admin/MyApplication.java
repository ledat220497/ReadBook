package com.example.readbook.Activity.Admin;

import static com.example.readbook.Activity.Contanst.MAX_BYTES_PDF;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.readbook.Activity.Admin.adapterAdmin.AdapterPdfAdmin;
import com.example.readbook.Activity.Admin.modeladmin.ModelPdfFile;
import com.example.readbook.Activity.Contanst;
import com.example.readbook.Activity.Details_Book;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class MyApplication extends Application  {

    private static final String TAG_DOWNLOAD = "DOWNLOAD_TAG";
    @Override
    public void onCreate() {
        super.onCreate();
    }
    public static final String formatTimetamp(long timetamp){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(timetamp);
        //format timetamp to dd/MM/yyyy
        String date = DateFormat.format("dd/MM/yyyy",calendar).toString();
        return date;
    }
    public static void deleteBook(Context context, String bookId,String bookUrl,String bookTitle) {
        String TAG ="DELETE_BOOK";


        Log.d(TAG, "deleteBook: Deleting...");
        ProgressDialog progress = new ProgressDialog(context);
        progress.setTitle("Vui lòng chờ...");
        progress.setMessage("Đang xoá..."+bookTitle);
        progress.show();

        Log.d(TAG, "deleteBook: Deleting from storage...");
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(bookUrl);
        storageReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Deleted from storage...");

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
                        ref.child(bookId)
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "onSuccess: Delete from db to");
                                        progress.dismiss();
                                        Toast.makeText(context, "Xoá Sách Thành Công", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: Failed to delete from db"+e.getMessage());
                                        progress.dismiss();
                                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Failed to delete from to storage"+e.getMessage());
                progress.dismiss();
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public static void loadSizePdf(String pdfUrl, String pdfTitle, TextView sizeTv) {
        String TAG ="PDF_SIZE";
        //using url we can get file and its metadata from firebase storage

        StorageReference  reference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        reference.getMetadata()
                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        //get size byte data

                        double bytes = storageMetadata.getSizeBytes();
                        Log.d(TAG, "onSuccess: "+pdfTitle+""+bytes);
                        //convert  holder.bytes KB,MB
                        double kb = bytes/1024;
                        double mb = kb/1024;
                        if(mb >= 1){
                            sizeTv.setText(String.format("%.2f",mb)+"MB");
                        }else if(kb >= 1){
                            sizeTv.setText(String.format("%.2f",kb)+"KB");
                        }else{
                           sizeTv.setText(String.format("%.2f",bytes)+"bytes");

                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed getting metadata
                        Log.d(TAG, "onFailure: "+e.getMessage());
                    }
                });
    }
    public static void loadFilePdfUrl(String pdfUrl, String pdfTitle, PDFView pdfView,ProgressBar progressBar,TextView pagesTv) {
        //using url we can get file and its metadata from firebase storage
        String TAG ="PDF_LOAD_SINGLE";
        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        reference.getBytes(MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Log.d(TAG, "onSuccess: "+pdfTitle+"Successfully got the file");

                        //set to pdf view
                       pdfView.fromBytes(bytes)
                                .pages(0)
                                .spacing(0)
                                .swipeHorizontal(false)
                                .enableSwipe(false)
                                .onError(new OnErrorListener() {
                                    @Override
                                    public void onError(Throwable t) {
                                        Log.d(TAG, "onError: "+t.getMessage());
                                      progressBar.setVisibility(View.INVISIBLE);
                                    }
                                })
                                .onPageError(new OnPageErrorListener() {
                                    @Override
                                    public void onPageError(int page, Throwable t) {
                                        Log.d(TAG, "onPageError: "+t.getMessage());
                                        progressBar.setVisibility(View.INVISIBLE);

                                    }
                                })
                                .onLoad(new OnLoadCompleteListener() {
                                    @Override
                                    public void loadComplete(int nbPages) {
                                        //pdf loaded

                                        //hide progressbar
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Log.d(TAG, "loadComplete: pdf loaded");
                                        if(pagesTv != null){
                                            pagesTv.setText(""+nbPages);
                                        }

                                    }
                                })
                                .load();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);

                        Log.d(TAG, "onFailure: failed getting file from url due to"+e.getMessage());
                    }
                });
    }

    public static void loadCategory(String categoryId,TextView categoriesTv) {
        //get category using categoryId
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(categoryId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String category =""+ snapshot.child("category").getValue();


                        //set to category text view
                       categoriesTv.setText(category);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
    public static void incrementBookViewCount(String bookId){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // get view count
                        String viewCount =""+ snapshot.child("viewCount").getValue();
                        // in case of null
                        if(viewCount.equals("")||viewCount.equals("null")){
                            viewCount = "0";
                        }

                        // increment view count
                        long newViewCount = Long.parseLong(viewCount)+1;
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("viewCount",newViewCount);
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
                        ref.child(bookId)
                                .updateChildren(hashMap);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    public static void downLoadBook(Context context,String bookId,String bookTitle,String bookUrl){
        Log.d(TAG_DOWNLOAD, "downLoadBook: downloading book....");
        String nameWithEtention = bookTitle + ".pdf";
        Log.d(TAG_DOWNLOAD, "downLoadBook: name"+nameWithEtention);

        //progressDialog
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Vui lòng chờ...");
        progressDialog.setMessage("Downloading"+nameWithEtention+"...");//eg. Downloading Book pdf
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        //Download from firebase Storage
        StorageReference sRef = FirebaseStorage.getInstance().getReferenceFromUrl(bookUrl);
        sRef.getBytes(MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Log.d(TAG_DOWNLOAD, "onSuccess: Book Download");
                        Log.d(TAG_DOWNLOAD, "onSuccess: Saving Book...");
                        saveDownloadBook(context,progressDialog,bytes,nameWithEtention,bookId);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG_DOWNLOAD, "onFailure: failed to download due to"+e.getMessage());
                        progressDialog.dismiss();
                    }
                });

    }



    private static void saveDownloadBook(Context context, ProgressDialog progressDialog, byte[] bytes, String nameWithEtention, String bookId) {
        Log.d(TAG_DOWNLOAD, "saveDownloadBook: Saving download book");
        try {
            File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            downloadFolder.mkdirs();
            String filePath = downloadFolder.getPath()+"/"+nameWithEtention;
            FileOutputStream out = new FileOutputStream(filePath);
            out.write(bytes);
            out.close();
            Toast.makeText(context, "Lưu sách thành công", Toast.LENGTH_SHORT).show();
            Log.d(TAG_DOWNLOAD, "saveDownloadBook:Save to Download Forlder ");
            progressDialog.dismiss();

            incrementBookDownloadCount(bookId);
            }catch (Exception ex){
            Log.d(TAG_DOWNLOAD, "saveDownloadBook:Failed saving to Download Folder due to "+ ex.getMessage());
            progressDialog.dismiss();
        }
    }

    private static void incrementBookDownloadCount(String bookId) {
        Log.d(TAG_DOWNLOAD, "incrementBookDownloadCount: Incrementing Book Download Count");
        //Step 1 get previous download count
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String downloadCount = "" +snapshot.child("downloadCount").getValue();
                        Log.d(TAG_DOWNLOAD, "onDataChange: Download Count "+downloadCount);
                        
                        if(downloadCount.equals("")|| downloadCount.equals("null")){
                            downloadCount = "0";

                        }
                        //convert to long inCrement
                        long newDownloadCount = Long.parseLong(downloadCount)+1;
                        Log.d(TAG_DOWNLOAD, "onDataChange:New Download Count "+newDownloadCount);
                        //set up to data update
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("downloadCount",newDownloadCount);
                        //Step 2 Update new increment download count to db
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
                        ref.child(bookId).updateChildren(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG_DOWNLOAD, "onSuccess:DownloadCount Update... ");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG_DOWNLOAD, "onFailure: Failed to update Download Count due to");
                                    }
                                });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    public static void addToFactorite(Context context,String bookId){
        //we can add only if use is login in
        // 1) Check if user login
        FirebaseAuth firebaseauth = FirebaseAuth.getInstance();
        if(firebaseauth.getCurrentUser() == null){
            Toast.makeText(context, "Bạn cần đăng nhập tài khoản", Toast.LENGTH_SHORT).show();
        }else{
            long timetamp = System.currentTimeMillis();
            //set up data add in firebase of curren user for favarite
            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("bookId",""+bookId);
            hashMap.put("timetamp",""+timetamp);

            //Save to db
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
            ref.child(firebaseauth.getUid()).child("Favorite").child(bookId)
                    .setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context, "Yêu thích thành công", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Yêu thích không thành công", Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    public static void RemoveFavorite(Context context ,String bookId){
        //we can add only remove if use is login in
        // 1) Check if user login
        FirebaseAuth firebaseauth = FirebaseAuth.getInstance();
        if(firebaseauth.getCurrentUser() == null){
            //not logged in ,cant remove to favorite
            Toast.makeText(context, "Bạn cần đăng nhập tài khoản", Toast.LENGTH_SHORT).show();
        }else{

            //remove to db
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
            ref.child(firebaseauth.getUid()).child("Favorite").child(bookId)
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context, "Huỷ yêu thích thành công", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Huỷ Yêu thích không thành công", Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

}
