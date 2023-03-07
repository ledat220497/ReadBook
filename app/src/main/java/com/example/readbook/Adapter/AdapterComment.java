package com.example.readbook.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.readbook.Activity.Admin.MyApplication;
import com.example.readbook.Model.ModelComment;
import com.example.readbook.R;
import com.example.readbook.databinding.RowCommentBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.HolderComment> {

    private Context mContext;
    //arraylist to hold Comment
    private ArrayList<ModelComment>  commentArrayList;
    private RowCommentBinding commentBinding;
    //init firebase
    private FirebaseAuth mFirebaseAuth;
    //Contructor

    public AdapterComment(Context mContext, ArrayList<ModelComment> commentArrayList) {
        this.mContext = mContext;
        this.commentArrayList = commentArrayList;
        mFirebaseAuth =  FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderComment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        commentBinding = RowCommentBinding.inflate(LayoutInflater.from(mContext),parent,false);
        return new HolderComment(commentBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderComment holder, int position) {

        /*Get data from specific position of list set data handle Click item*/

        //get data
        ModelComment modelComment = commentArrayList.get(position);
        String id = modelComment.getId();
        String bookId = modelComment.getBookId();
        String comment = modelComment.getComment();
        String uid = modelComment.getUid();
        String timestamp = modelComment.getTimestamp();

        /*Format date alrealdy made function in MyApplication class*/
        String date = MyApplication.formatTimetamp(Long.parseLong(timestamp));

        //set data
        holder.dateTv.setText(date);
        holder.commentTv.setText(comment);

        //we don't  have users name ,profile ,so we will load it using uid we stored in each comment
        loadUserDetails(modelComment,holder);

        //handle Click show comment
        holder.itemView.setOnClickListener(v->{

            if(mFirebaseAuth.getCurrentUser() != null && uid.equals(mFirebaseAuth.getUid())){
                deleteComment(modelComment,holder);

            }
        });


    }

    private void deleteComment(ModelComment modelComment, HolderComment holder) {
        //show confilm  dialog before deleting comment
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Xoá Bình Luận")
                .setMessage("Bạn có chắc muón xoá bình luận không?")
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Delete  from dialog click , begin delete
                        DatabaseReference ref =  FirebaseDatabase.getInstance().getReference("Books");
                        ref.child(modelComment.getBookId())
                                .child("Comments").child(modelComment.getId())
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(mContext, "Xoá Thành Công", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(mContext, "Xoá không thành công", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                
                    }
                })
                .setNegativeButton("Huỷ Bỏ", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                .show();
    }

    private void loadUserDetails(ModelComment modelComment, HolderComment holder) {
        String uid  = modelComment.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
        ref.child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //set Data
                        String name = "" +snapshot.child("name").getValue();
                        String profileimage = ""+snapshot.child("profileimage").getValue();
                        //setData
                        holder.nameTv.setText(name);
                        try {
                            Glide.with(mContext)
                                    .load(profileimage)
                                    .placeholder(R.drawable.ic_person_24)
                                    .into(holder.profileTv);
                        }catch(Exception e){
                            holder.profileTv.setImageResource(R.drawable.ic_person_24);
                        }
                        Log.d("anh", "onDataChange: "+profileimage);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public int getItemCount() {
        return commentArrayList.size();
    }

    //View holder class for row_cooment.xml
    class HolderComment extends RecyclerView.ViewHolder {
        ImageView profileTv;
        TextView nameTv,dateTv,commentTv;
        public HolderComment(@NonNull View itemView) {
            super(itemView);
            //ui view of row comment .xml
            profileTv = commentBinding.profileIv;
            nameTv = commentBinding.nameTv;
            dateTv = commentBinding.dateTv;
            commentTv = commentBinding.commentTv;




        }
    }
}
