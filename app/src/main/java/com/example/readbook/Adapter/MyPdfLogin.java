package com.example.readbook.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.readbook.Activity.Admin.MyApplication;
import com.example.readbook.Activity.Details_Book;
import com.example.readbook.Model.PDFItem;
import com.example.readbook.R;
import com.example.readbook.databinding.BookItemBinding;
import com.github.barteksc.pdfviewer.PDFView;

import java.util.ArrayList;

public class MyPdfLogin extends RecyclerView.Adapter<MyPdfLogin.PdfLoginHolder> {

    private Context context;
    private ArrayList<PDFItem> pdfItemArrayList;

    //Contructor params

    public MyPdfLogin(Context context, ArrayList<PDFItem> pdfItemArrayList) {
        this.context = context;
        this.pdfItemArrayList = pdfItemArrayList;
    }

    //init ui from book_item.xml
    private BookItemBinding bookItemBinding;

    @NonNull
    @Override
    public PdfLoginHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        bookItemBinding = BookItemBinding.inflate(LayoutInflater.from(context),parent,false);
        return new PdfLoginHolder(bookItemBinding.getRoot());
    }

    @Override
    @SuppressLint("RecyclerView")
    public void onBindViewHolder(@NonNull PdfLoginHolder holder, int position) {
        PDFItem item = pdfItemArrayList.get(position);

        String title = item.fileName;
        String date = item.date.toString();
        String pdfUrl = item.getFilePath();
        //set data
        holder.title.setText(title);
        holder.date.setText(date);
        holder.title.setSelected(true);

        //hanlde OnClick Item
        holder.itemView.setOnClickListener(v ->{
//            Intent items = new Intent(context, Details_Book.class);
////            items.putExtra("bookId",item.getId());
//            context.startActivity(items);

        } );
    }

    @Override
    public int getItemCount() {
        return pdfItemArrayList.size();
    }

    //class holder file pdf from storage gallery
    class PdfLoginHolder extends RecyclerView.ViewHolder {
        TextView title,date;
        PDFView pdfView;
        ProgressBar progress;
        public PdfLoginHolder(@NonNull View itemView) {
            super(itemView);
            title = bookItemBinding.titleTv;
            pdfView = bookItemBinding.pdfView;
            date = bookItemBinding.dateTv;
            progress= bookItemBinding.progressBar;
        }
    }
}
