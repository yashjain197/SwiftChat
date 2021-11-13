package com.yashjain.swiftchat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.yashjain.swiftchat.Adapters.CommunityChatAdapter;
import com.yashjain.swiftchat.Adapters.messageAdapter;
import com.yashjain.swiftchat.Models.Message;
import com.yashjain.swiftchat.R;
import com.yashjain.swiftchat.databinding.ActivityCommunityChatBinding;

import java.util.ArrayList;
import java.util.Date;

public class communityChat extends AppCompatActivity {
    FirebaseDatabase database;
    FirebaseStorage storage;
    ArrayList<Message> messages=new ArrayList<>();
    String senderUid;
    ActivityCommunityChatBinding binding;
    CommunityChatAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityCommunityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();
        messages=new ArrayList<>();
        getSupportActionBar().setTitle("Community chat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter=new CommunityChatAdapter(this,messages);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        receiveChat();

        binding.send.setOnClickListener(view -> {
            if(binding.messageBox.getText().toString().isEmpty()){
                Toast.makeText(this,"Type Message",Toast.LENGTH_SHORT).show();
            }else
            sendMessage();
        });
    }

    private void sendMessage() {
        String messageTxt= binding.messageBox.getText().toString();
        Date date= new Date();
        senderUid=FirebaseAuth.getInstance().getUid();
        binding.messageBox.setText("");
        Message message=new Message(messageTxt,senderUid,date.getTime());

        database.getReference().child("public")
                .push()
                .setValue(message);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    public void receiveChat(){
        database.getReference()
                .child("public")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();
                        for(DataSnapshot snapshot1 : snapshot.getChildren()){
                            Message message=snapshot1.getValue(Message.class);
                            message.setMessageId(snapshot1.getKey());
                            messages.add(message);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
}