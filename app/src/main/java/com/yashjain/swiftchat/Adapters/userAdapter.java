package com.yashjain.swiftchat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.yashjain.swiftchat.Activities.chatActivity;
import com.yashjain.swiftchat.Models.User;
import com.yashjain.swiftchat.R;
import com.yashjain.swiftchat.databinding.RecyclerMainLayoutBinding;

import java.util.ArrayList;

public class userAdapter extends  RecyclerView.Adapter<userAdapter.UserViewHolder>{

        Context context;
        ArrayList<User> userArrayList;

        public userAdapter(Context context,ArrayList<User> user) {
        this.context=context;
        this.userArrayList=user;
    }

        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(context).inflate(R.layout.recycler_main_layout,parent,false);
            return new UserViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            //This is the object from User class.

            User users = userArrayList.get(position);
            holder.binding.sampleName.setText(users.getName());
            Glide.with(context).load(users.getProfileImageUrl())
                    .placeholder(R.drawable.avatar).into(holder.binding.circleImageView);


            holder.binding.itemView.setOnClickListener(view -> {
                Intent intent= new Intent(context, chatActivity.class);
                intent.putExtra("Name",users.getName());
                intent.putExtra("ProfileUrl",users.getProfileImageUrl());
                intent.putExtra("uid",users.getUid());
                context.startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {return userArrayList.size();}


        public static class UserViewHolder extends RecyclerView.ViewHolder{
        RecyclerMainLayoutBinding binding;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            binding=RecyclerMainLayoutBinding.bind(itemView);
        }
    }
}








