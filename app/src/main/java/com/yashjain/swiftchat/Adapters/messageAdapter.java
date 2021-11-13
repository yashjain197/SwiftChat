package com.yashjain.swiftchat.Adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.yashjain.swiftchat.Models.Message;
import com.yashjain.swiftchat.R;
import com.yashjain.swiftchat.databinding.ItemRecieveBinding;
import com.yashjain.swiftchat.databinding.ItemSendBinding;

import java.util.ArrayList;

public class messageAdapter extends RecyclerView.Adapter{

    Context context;
    ArrayList<Message> messages;

    final int ITEM_SENT=1;
    final int ITEM_RECEIVE=2;
    String senderRoom;
    String receiverRoom;
    
    
    public messageAdapter(Context context, ArrayList<Message> messages, String senderRoom,String receiverRoom){
        this.context=context;
        this.messages=messages;
        this.senderRoom=senderRoom;
        this.receiverRoom=receiverRoom;

    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType==ITEM_SENT){
            View view = LayoutInflater.from(context).inflate(R.layout.item_send,parent,false);
            return new sentViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.item_recieve,parent,false);
            return new reciverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message =messages.get(position);

        int[] reactions=new int[]{
                R.drawable.ic_fb_like,
                R.drawable.ic_fb_love,
                R.drawable.ic_fb_laugh,
                R.drawable.ic_fb_wow,
                R.drawable.ic_fb_sad,
                R.drawable.ic_fb_angry
        };

        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();
        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            if(holder.getClass()==sentViewHolder.class){
                sentViewHolder viewHolder =(sentViewHolder)holder;
                viewHolder.binding.feeling.setImageResource(reactions[pos]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);

            }else{
                reciverViewHolder viewHolder= (reciverViewHolder) holder;
                viewHolder.binding.feelingRecieve.setImageResource(reactions[pos]);
                viewHolder.binding.feelingRecieve.setVisibility(View.VISIBLE);
            }
            message.setFeeling(pos);
            FirebaseDatabase.getInstance().getReference().child("chats")
                    .child(senderRoom)
                    .child(message.getMessageId())
                    .setValue(message);

            FirebaseDatabase.getInstance().getReference().child("chats")
                    .child(receiverRoom)
                    .child(message.getMessageId())
                    .setValue(message);
            return true; // true is closing popup, false is requesting a new selection
        });

       if(holder.getClass()==sentViewHolder.class){

           if(message.getMessage().equals("arXeermmOOrTTbbPPoNHG")){
               ((sentViewHolder) holder).binding.image.setVisibility(View.VISIBLE);
               ((sentViewHolder) holder).binding.sentMessage.setVisibility(View.GONE);
               Glide.with(context).load(message.getImageUrl()).placeholder(R.drawable.image_placeholder).into(((sentViewHolder) holder).binding.image);
           }

           ((sentViewHolder) holder).binding.sentMessage.setText(message.getMessage());
           if(message.getFeeling()>=0){
               ((sentViewHolder) holder).binding.feeling.setImageResource(reactions[(int) message.getFeeling()]);
               ((sentViewHolder) holder).binding.feeling.setVisibility(View.VISIBLE);
           }else{
               ((sentViewHolder) holder).binding.feeling.setVisibility(View.GONE);

           }
           ((sentViewHolder) holder).binding.sentMessage.setOnTouchListener((view, motionEvent) -> {
               popup.onTouch(view,motionEvent);
               return false;
           });
           ((sentViewHolder) holder).binding.image.setOnTouchListener((view, motionEvent) -> {
               popup.onTouch(view,motionEvent);
               return false;
           });
       }else{
           if(message.getMessage().equals("arXeermmOOrTTbbPPoNHG")){
               ((reciverViewHolder) holder).binding.imageReciever.setVisibility(View.VISIBLE);
               ((reciverViewHolder) holder).binding.message.setVisibility(View.GONE);
               Glide.with(context).load(message.getImageUrl()).placeholder(R.drawable.image_placeholder).into(((reciverViewHolder) holder).binding.imageReciever);
           }
           reciverViewHolder viewHolder= (reciverViewHolder) holder;
           viewHolder.binding.message.setText(message.getMessage());
           if(message.getFeeling()>=0){
               ((reciverViewHolder) holder).binding.feelingRecieve.setImageResource(reactions[(int) message.getFeeling()]);
                   ((reciverViewHolder) holder).binding.feelingRecieve.setVisibility(View.VISIBLE);
           }else{
               ((reciverViewHolder) holder).binding.feelingRecieve.setVisibility(View.GONE);

           }
           ((reciverViewHolder) holder).binding.message.setOnTouchListener((view, motionEvent) -> {
               popup.onTouch(view,motionEvent);
               return false;
           });

           ((reciverViewHolder) holder).binding.imageReciever.setOnTouchListener((view, motionEvent) -> {
               popup.onTouch(view,motionEvent);
               return false;
           });
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
        ItemSendBinding binding;
        public sentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSendBinding.bind(itemView);
        }
    }

    public class reciverViewHolder extends RecyclerView.ViewHolder{
        ItemRecieveBinding binding;
        public reciverViewHolder(@NonNull View itemView){
            super(itemView);
            binding= ItemRecieveBinding.bind(itemView);
        }
    }
}
