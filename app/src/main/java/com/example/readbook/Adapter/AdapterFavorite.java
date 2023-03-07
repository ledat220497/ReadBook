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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readbook.Activity.Admin.MyApplication;
import com.example.readbook.Activity.Admin.modeladmin.ModelPdfFile;
import com.example.readbook.Activity.Details_Book;
import com.example.readbook.databinding.RowPdfFavoriteBinding;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdapterFavorite extends RecyclerView.Adapter<AdapterFavorite.holderPdfFavorite> {
    private Context mContext;
    private ArrayList<ModelPdfFile> pdfFileArrayList;

    private RowPdfFavoriteBinding favoriteBinding;
    private static final String TAG = "FAV_BOOK_TAG";




    //contructor all params


    //Contructor

    public AdapterFavorite(Context mContext, ArrayList<ModelPdfFile> pdfFileArrayList) {
        this.mContext = mContext;
        this.pdfFileArrayList = pdfFileArrayList;
    }

    @NonNull
    @Override
    public holderPdfFavorite onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        favoriteBinding = RowPdfFavoriteBinding.inflate(LayoutInflater.from(mContext),parent,false);
        return new holderPdfFavorite(favoriteBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull holderPdfFavorite holder, int position) {
        //getData setData and Handle Click View Item
        ModelPdfFile model = pdfFileArrayList.get(position);

        // loadDetails
        loadBookDetails(model,holder);

        holder.itemView.setOnClickListener(v->{
            Intent item = new Intent(mContext, Details_Book.class);
            item.putExtra("bookId",model.getId());
            mContext.startActivity(item);

        });
    //Handle Click Favorite
        holder.removeFavorite.setOnClickListener(v->{
            MyApplication.RemoveFavorite(mContext,model.getId());
        });
    }

    private void loadBookDetails(ModelPdfFile model, holderPdfFavorite holder) {
        String bookId = model.getId();
        Log.d(TAG, "loadBookDetails: Chi tiết sách cho mã sách" + bookId);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get book Info
                        String bookTitle  = ""+snapshot.child("title").getValue();
                        String description  = ""+snapshot.child("description").getValue();
                        String categoryId  = ""+snapshot.child("categoryId").getValue();
                        String bookUrl  = ""+snapshot.child("url").getValue();
                        String timestamp  = ""+snapshot.child("timestamp").getValue();
                        String uid  = ""+snapshot.child("uid").getValue();
                        String viewCount  = ""+snapshot.child("viewCount").getValue();
                        String downloadCount  = ""+snapshot.child("downloadCount").getValue();
                        //set to model
                        model.setFavorite(true);
                        model.setTitle(bookTitle);
                        model.setDescription(description);
//                        model.setTimestamp(Long.parseLong(timestamp));
                        model.setCategoryId(categoryId);
                        model.setUid(uid);
                        model.setUrl(bookUrl);


                        //formattedate
//                        String date = MyApplication.formatTimetamp(Long.parseLong(timestamp));


                        MyApplication.loadCategory(categoryId,holder.CattegoryTv);
                        MyApplication.loadFilePdfUrl(""+bookUrl,"" +bookTitle,holder.pdfView,holder.progressBar,null);
                        MyApplication.loadSizePdf( "" + bookUrl,"" + bookTitle,holder.sizeTv);
                        //setData to View
                        holder.titleTv.setText(bookTitle);
                        holder.descriptionTv.setText(description);
                        holder.CattegoryTv.setText(categoryId);
//                        holder.dateTv.setText(date);

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

    class holderPdfFavorite extends RecyclerView.ViewHolder {
        PDFView pdfView;
        ProgressBar progressBar;
        TextView titleTv,descriptionTv,CattegoryTv,sizeTv,dateTv;
        ImageButton removeFavorite;

        public holderPdfFavorite(@NonNull View itemView) {
            super(itemView);
            //init ui view of rowpdffavorite.xml
            pdfView = favoriteBinding.pdfView;
            progressBar = favoriteBinding.progressBar;
            titleTv = favoriteBinding.titleTv;
            removeFavorite = favoriteBinding.removeFavorite;
            descriptionTv = favoriteBinding.descriptionTv;
            CattegoryTv = favoriteBinding.categoryTv;
            sizeTv = favoriteBinding.sizeTv;
            dateTv = favoriteBinding.dateTv;



        }
    }
}
