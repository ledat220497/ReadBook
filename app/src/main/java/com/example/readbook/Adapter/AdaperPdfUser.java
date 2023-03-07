package com.example.readbook.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readbook.Activity.Admin.MyApplication;
import com.example.readbook.Activity.Admin.filtersAdmin.filterPdfUser;
import com.example.readbook.Activity.Admin.modeladmin.ModelPdfFile;
import com.example.readbook.Activity.Details_Book;
import com.example.readbook.databinding.RowPdfUserBinding;
import com.github.barteksc.pdfviewer.PDFView;

import java.util.ArrayList;

public class AdaperPdfUser extends RecyclerView.Adapter<AdaperPdfUser.holderPdfUser> implements Filterable {
    private Context mContext;
    public  ArrayList<ModelPdfFile> pdfFileArrayList,filterlist;
    private filterPdfUser filter;
    private RowPdfUserBinding rowPdfUserBinding;
    private static final  String TAG ="ADAPTER_PDF_USER";

    public AdaperPdfUser(Context mContext, ArrayList<ModelPdfFile> pdfFileArrayList) {
        this.mContext = mContext;
        this.pdfFileArrayList = pdfFileArrayList;
        this.filterlist = pdfFileArrayList;
    }

    @NonNull
    @Override
    public holderPdfUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        rowPdfUserBinding = RowPdfUserBinding.inflate(LayoutInflater.from(mContext),parent,false);
        return new holderPdfUser(rowPdfUserBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull holderPdfUser holder, int position) {
        ModelPdfFile model = pdfFileArrayList.get(position);
        String title = model.getTitle();
        String bookId =model.getId();
        String description = model.getDescription();
        String pdfUrl = model.getUrl();
        String categoryId = model.getCategoryId();
        long timetamp = model.getTimestamp();
        //Convert time
        String date = MyApplication.formatTimetamp(timetamp);
        //setData
        holder.titleTv.setText(title);
        holder.desriptionTv.setText(description);
        holder.dateTv.setText(date);

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
                holder.CategoriesTv);
        MyApplication.loadSizePdf(
                ""+pdfUrl,
                ""+title,
                holder.sizeTv);
        holder.itemView.setOnClickListener(v->{

            try {
                Intent intent = new Intent(mContext, Details_Book.class);
                intent.putExtra("bookId", bookId);
                intent.putExtra("categoryId",categoryId);
                mContext.startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return pdfFileArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new filterPdfUser(filterlist,this);

        }
        return filter;
    }

    public class holderPdfUser extends RecyclerView.ViewHolder {

        TextView titleTv,desriptionTv,CategoriesTv,dateTv,sizeTv;
        PDFView pdfView;
        ProgressBar progressBar;
        public holderPdfUser(@NonNull View itemView) {
            super(itemView);
            titleTv= rowPdfUserBinding.titleTv;
            desriptionTv = rowPdfUserBinding.desriptionTv;
            CategoriesTv = rowPdfUserBinding.CategoriesTv;
            sizeTv = rowPdfUserBinding.sizeTv;
            dateTv = rowPdfUserBinding.dateTv;
            pdfView = rowPdfUserBinding.pdfView;
            progressBar = rowPdfUserBinding.progressBar;
        }
    }
}
