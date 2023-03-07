package com.example.readbook.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readbook.Activity.Admin.MyApplication;
import com.example.readbook.Activity.Admin.modeladmin.ModelPdfFile;
import com.example.readbook.Activity.Details_Book;
import com.example.readbook.databinding.BookItemViewReadBinding;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdapterUserViewCount extends RecyclerView.Adapter<AdapterUserViewCount.holderPdfViewRead> {

    private Context mContext;
    private List<ModelPdfFile> pdfFileArrayList;
    private BookItemViewReadBinding viewreadBinding;
    private static final String TAG = "FAV_BOOK_TAG";

    public AdapterUserViewCount(Context mContext, List<ModelPdfFile> pdfFileArrayList) {
        this.mContext = mContext;
        this.pdfFileArrayList = pdfFileArrayList;
    }
    public void setData(List<ModelPdfFile> pdfFileArrayList){
        this.pdfFileArrayList=pdfFileArrayList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public holderPdfViewRead onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        viewreadBinding = BookItemViewReadBinding.inflate(LayoutInflater.from(mContext),parent,false);
        return new holderPdfViewRead(viewreadBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull holderPdfViewRead holder, int position) {
        ModelPdfFile model = pdfFileArrayList.get(position);

        // loadDetails
        loadBookDetails(model,holder);
        holder.itemView.setOnClickListener(v->{
            Intent item = new Intent(mContext, Details_Book.class);
            item.putExtra("bookId",model.getId());
            mContext.startActivity(item);

        });
        Log.d("title", "onBindViewHolder: "+model);

    }

    private void loadBookDetails(ModelPdfFile model, holderPdfViewRead holder) {
        String bookId = model.getId();
        Log.d(TAG, "loadBookDetails: Chi tiết sách cho mã sách" + bookId);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get book Info
                        String bookTitle  = ""+snapshot.child("title").getValue();
                        String description  = ""+snapshot.child("desription").getValue();
                        String categoryId  = ""+snapshot.child("categoryId").getValue();
                        String bookUrl  = ""+snapshot.child("url").getValue();
                        String timestamp  = ""+snapshot.child("timestamp").getValue();
                        String uid  = ""+snapshot.child("uid").getValue();

                        //set to model
                        model.setRead(true);
                        model.setTitle(bookTitle);
                        model.setDescription(description);
                        model.setTimestamp(Long.parseLong(timestamp));
                        model.setCategoryId(categoryId);
                        model.setUid(uid);
                        model.setUrl(bookUrl);

                        MyApplication.loadCategory(categoryId,holder.CattegoryTv);
                        MyApplication.loadFilePdfUrl(""+bookUrl,"" +bookTitle,holder.pdfView,holder.progressBar,null);
                        MyApplication.loadSizePdf( "" + bookUrl,"" + bookTitle,holder.sizeTv);
                        //setData to View
                        holder.titleTv.setText(bookTitle);
                        holder.descriptionTv.setText(description);
                        holder.CattegoryTv.setText(categoryId);

                        Log.d("title", "onDataChange: "+model);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return pdfFileArrayList.size();
    }

    class holderPdfViewRead extends RecyclerView.ViewHolder {
        PDFView pdfView;
        ProgressBar progressBar;
        TextView titleTv,descriptionTv,CattegoryTv,sizeTv;


        public holderPdfViewRead(@NonNull View itemView) {
            super(itemView);
            //init ui view of book_item_view_read.xml
            pdfView = viewreadBinding.pdfView;
            progressBar = viewreadBinding.progressBar;
            titleTv = viewreadBinding.titleTv;
            descriptionTv = viewreadBinding.desriptionTv;
            CattegoryTv = viewreadBinding.CategoriesTv;
            sizeTv = viewreadBinding.sizeTv;



        }

}
}
