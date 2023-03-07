package com.example.readbook.Activity.Admin.adapterAdmin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.readbook.Activity.Admin.modeladmin.ModelUser;
import com.example.readbook.databinding.UserItemBinding;

import java.util.ArrayList;
import java.util.List;

public class AdapterUserAdmin extends  RecyclerView.Adapter<AdapterUserAdmin.USerViewHolder>{
    private Context context;
    private List<ModelUser> arraylistuser;
    private UserItemBinding bindinguser;
    public AdapterUserAdmin(Context context, List<ModelUser> arraylistuser) {
        this.context = context;
        this.arraylistuser = arraylistuser;
    }
    public void setData(List<ModelUser> arraylistuser){
        this.arraylistuser=arraylistuser;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public USerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        bindinguser = UserItemBinding.inflate(LayoutInflater.from(context),parent,false);
        return new USerViewHolder(bindinguser.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull USerViewHolder holder, int position) {
        ModelUser model = arraylistuser.get(position);
        loadDetailUser(holder,model);
    }

    private void loadDetailUser(USerViewHolder holder, ModelUser model) {
        holder.name.setText(model.getName());
        Glide.with(context)
                .load(model.getProfileimage())
                .into(holder.imgProfileName);
    }

    @Override
    public int getItemCount() {
        return arraylistuser.size();
    }

    class USerViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProfileName;
        TextView name;
        public USerViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfileName=bindinguser.profileIv;
            name= bindinguser.nameTv;
        }
    }
}
