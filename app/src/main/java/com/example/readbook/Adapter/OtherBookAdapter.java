package com.example.readbook.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readbook.Activity.Admin.MyApplication;
import com.example.readbook.Activity.Admin.modeladmin.ModelPdfFile;
import com.example.readbook.Activity.Details_Book;
import com.example.readbook.databinding.OrtherBookItemBinding;
import com.github.barteksc.pdfviewer.PDFView;

import java.util.ArrayList;

public class OtherBookAdapter extends RecyclerView.Adapter<OtherBookAdapter.OtherBookViewHolder>{
    private Context context;
    private ArrayList<ModelPdfFile> ortherlistpdf;
    private OrtherBookItemBinding ortherbookbin;
    public OtherBookAdapter(Context context, ArrayList<ModelPdfFile> ortherlistpdf) {
        this.context = context;
        this.ortherlistpdf = ortherlistpdf;
    }
    public void setData(ArrayList<ModelPdfFile> ortherlistpdf){
        this.ortherlistpdf= ortherlistpdf;
    }

    @NonNull
    @Override
    public OtherBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ortherbookbin= OrtherBookItemBinding.inflate(LayoutInflater.from(context),parent,false);
        return new OtherBookViewHolder(ortherbookbin.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull OtherBookViewHolder holder, int position) {
        ModelPdfFile model = ortherlistpdf.get(position);
        String title = model.getTitle();
        String bookId =model.getId();
        String description = model.getDescription();
        String pdfUrl = model.getUrl();
        String categoryId = model.getCategoryId();
        //setData
        holder.titleBook.setText(title);
        holder.description.setText(description);
        //we dont need  page  number  here ,pass null
        MyApplication.loadFilePdfUrl(
                ""+pdfUrl,
                ""+title,
                holder.pdfView,
                holder.progressBar,
                null
        );
        MyApplication.loadCategory(
                ""+categoryId,
                holder.categoryBook);
        MyApplication.loadSizePdf(
                ""+pdfUrl,
                ""+title,
                holder.sizeBook);
        holder.itemView.setOnClickListener(v->{

            try {
                Intent intent = new Intent(context, Details_Book.class);
                intent.putExtra("bookId", bookId);
                intent.putExtra("categoryId",categoryId );
                context.startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return ortherlistpdf.size();
    }

    class OtherBookViewHolder extends RecyclerView.ViewHolder {
        PDFView pdfView;
        ProgressBar progressBar;
        TextView titleBook,description,sizeBook,categoryBook;
        public OtherBookViewHolder(@NonNull View itemView) {
            super(itemView);
            titleBook = ortherbookbin.titleTv;
            description= ortherbookbin.desriptionTv;
            sizeBook=ortherbookbin.sizeTv;
            categoryBook= ortherbookbin.CategoriesTv;
            pdfView=ortherbookbin.pdfView;
            progressBar= ortherbookbin.progressBar;
        }
    }
}
