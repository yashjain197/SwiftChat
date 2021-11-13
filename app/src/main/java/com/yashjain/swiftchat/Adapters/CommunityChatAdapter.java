package com.yashjain.swiftchat.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yashjain.swiftchat.Models.Message;
import com.yashjain.swiftchat.Models.User;
import com.yashjain.swiftchat.R;
import com.yashjain.swiftchat.databinding.ItemRecieveBinding;
import com.yashjain.swiftchat.databinding.ItemRecieveCommunityBinding;
import com.yashjain.swiftchat.databinding.ItemSendBinding;
import com.yashjain.swiftchat.databinding.ItemSentCommunityBinding;

import java.util.ArrayList;

public class CommunityChatAdapter extends RecyclerView.Adapter{

    Context context;
    ArrayList<Message> messages;

    final int ITEM_SENT=1;
    final int ITEM_RECEIVE=2;



    public CommunityChatAdapter(Context context, ArrayList<Message> messages){
        this.context=context;
        this.messages=messages;


    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType==ITEM_SENT){
            View view = LayoutInflater.from(context).inflate(R.layout.item_sent_community,parent,false);
            return new sentViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.item_recieve_community,parent,false);
            return new reciverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message =messages.get(position);


       if(holder.getClass()==sentViewHolder.class){
           FirebaseDatabase.getInstance()
                   .getReference()
                   .child("users")
                   .child(message.getSenderId())
                   .addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot snapshot) {
                           if(snapshot.exists()){
                               User user = snapshot.getValue(User.class);
                               ((sentViewHolder) holder).binding.UserName.setText("@"+user.getName()+" ");
                           }
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError error) {

                       }
                   });
           ((sentViewHolder) holder).binding.sentMessage.setText(message.getMessage());
       }else{
           reciverViewHolder viewHolder= (reciverViewHolder) holder;
           FirebaseDatabase.getInstance()
                   .getReference()
                   .child("users")
                   .child(message.getSenderId())
                   .addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot snapshot) {
                           if(snapshot.exists()){
                               User user = snapshot.getValue(User.class);
                               ((reciverViewHolder) holder).binding.userName.setText("@"+user.getName()+" ");
                           }
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError error) {

                       }
                   });
           viewHolder.binding.message.setText(message.getMessage());
       } 
    }

    @Override
    public int getItemViewType(int position) {

        Message message= messages.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(message.getSenderId())){
            return ITEM_SENT;
        }else {
            return ITEM_RECEIVE;
        }
    }

    @Override
    public int getItemCount() {
      return messages.size();
    }


    public class sentViewHolder extends RecyclerView.ViewHolder{
        ItemSentCommunityBinding binding;
        public sentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSentCommunityBinding.bind(itemView);
        }
    }

    public class reciverViewHolder extends RecyclerView.ViewHolder{
        ItemRecieveCommunityBinding binding;
        public reciverViewHolder(@NonNull View itemView){
            super(itemView);
            binding= ItemRecieveCommunityBinding.bind(itemView);
        }
    }
}
