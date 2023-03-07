package com.example.readbook.Activity.LoginAndRegister;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.readbook.Adapter.MyPdfLogin;
import com.example.readbook.Model.PDFItem;
import com.example.readbook.R;
import com.example.readbook.databinding.ActivityLoginBinding;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class Login extends AppCompatActivity {
    ActivityLoginBinding loginbin;
    private TextView btnLogin,btnCreateAcc;

    private RecyclerView rcvBook;
    private MyPdfLogin bookAdapter;

    ArrayList<PDFItem> pdfItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginbin = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = loginbin.getRoot();
        setContentView(view);
        runtimePermission();

        btnLogin =view.findViewById(R.id.txtsignin);
        btnCreateAcc= view.findViewById(R.id.txtcreateacc);

//        File docdir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
//        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//        getPdfFile(docdir);
//        getPdfFile(downloadDir);
        btnLogin.setOnClickListener(v ->{
            startActivity(new Intent(Login.this,Login_Form.class));
            finish();
        });
        btnCreateAcc.setOnClickListener(v ->{
            startActivity(new Intent(Login.this,Register_Form.class));
            finish();
        });
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 999);
//        } else {
//        }


    }
    private void runtimePermission(){
        Dexter.withContext(Login.this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
//                        showPdf();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }
    private void getPdfFile(File pdfDir){

        PDFItem items ;
        String file_ext = ".pdf";

        try {
            File listPdfFile[] = pdfDir.listFiles();

            if(listPdfFile !=null){
                for(int i =0 ; i<listPdfFile.length;i++){
                    File pdf_file = listPdfFile[i];
                    if(pdf_file.getName().endsWith(file_ext)){
                        items = new PDFItem();
                        items.setFileName(pdf_file.getName());
//                        items.setFileName(pdf_file.getAbsolutePath());
                        items.setDate(new Date(pdf_file.lastModified()));
                        pdfItems.add(items);
                        Log.d("listbook", "showPdf: " +pdfItems);
                    }

                }

            }

        }catch(Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    public void showPdf(){
//        rcvBook = loginbin.bookRcv;
        rcvBook.setHasFixedSize(true);
        rcvBook.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        bookAdapter = new MyPdfLogin(this,pdfItems);
        rcvBook.setAdapter(bookAdapter);

    }

}