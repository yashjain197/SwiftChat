package com.yashjain.swiftchat.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yashjain.swiftchat.Adapters.messageAdapter;
import com.yashjain.swiftchat.Models.Message;
import com.yashjain.swiftchat.R;
import com.yashjain.swiftchat.databinding.ActivityChatBinding;
import com.yashjain.swiftchat.databinding.ActivityCommunityChatBinding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class chatActivity extends AppCompatActivity {
    ActivityChatBinding binding;
    String senderUid, receiverUid, name, profileUrl;
    String senderRoom,receiverRoom;
    messageAdapter adapter;
    private byte encryptionKey[]={32,12,115,86,90,4,-34,-98,-57,20,53,123,23,48,20,23};
    private Cipher cipher, decipher;
    private SecretKeySpec secretKeySpec;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ArrayList<Message> messages=new ArrayList<Message>();
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //getting extra string from Main activity
        name=getIntent().getStringExtra("Name");
        profileUrl=getIntent().getStringExtra("ProfileUrl");
        receiverUid= getIntent().getStringExtra("uid");

        setDialog();

        senderUid= FirebaseAuth.getInstance().getUid();


        setCipher();

        //Blocking Screenshot
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);



        //unique room for sender and receiver only
        senderRoom= senderUid+receiverUid;
        receiverRoom= receiverUid+senderUid;

        //setting adapter
        adapter=new messageAdapter(this,messages,senderRoom,receiverRoom);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarDetails();
        binding.back.setOnClickListener(view ->finish());
        binding.dp.setOnClickListener(view -> finish());

        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();

        //it will Receive message from firebase
        receiveMessage();

        //online or offline indicator
        database.getReference().child("presence").child(receiverUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String status = snapshot.getValue(String.class);
                    if(!status.isEmpty()){
                        if(status.equals("offline")){
                            binding.status.setVisibility(View.GONE);
                        }else {
                            binding.status.setText(status);
                            binding.status.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.send.setOnClickListener(view -> {
            if (binding.messageBox.getText().toString().trim().isEmpty()){
                Toast.makeText(getApplicationContext(), "Type message", Toast.LENGTH_SHORT).show();
                binding.messageBox.setText("");
            }else {
                sendMessage();
            }
        });

        binding.attachment.setOnClickListener(view -> {
           openGallery();
        });

        binding.camera.setOnClickListener(view -> {
            openCamera();
        });


        //Typing status check
        final Handler handler=new Handler();
        binding.messageBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                database.getReference().child("presence").child(senderUid).setValue("typing...");
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(userStoppedTyping,1000);
            }
            Runnable userStoppedTyping= new Runnable() {
                @Override
                public void run() {
                    database.getReference().child("presence").child(senderUid).setValue("online");

                }
            };
        });
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(chatActivity.this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(chatActivity.this,new String[]{
                    Manifest.permission.CAMERA
            },100);
        }else{
            Intent intent= new Intent();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent,100);
        }
    }


    private void toolbarDetails() {
        binding.username.setText(name);
        Glide.with(chatActivity.this).load(profileUrl).placeholder(R.drawable.avatar).into(binding.dp);

    }

    private void openGallery() {
        Intent intent =new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,25);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==25){
            if(data!=null){
                if(data.getData()!=null){
                    Uri selectedImage= data.getData();
                    saveImageInDatabase(selectedImage);

                }
            }
        }else if(requestCode==100){

                    Bitmap bitmap=(Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream bytes=new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes);
                    String path= MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(),bitmap,"val",null);
                    Uri uri=Uri.parse(path);
                    saveImageInDatabase(uri);
        }
    }

    private void saveImageInDatabase(Uri selectedImage){

        Calendar calendar=Calendar.getInstance();
        StorageReference reference= storage.getReference().child("chats").child(calendar.getTimeInMillis()+"");
        dialog.show();
        reference.putFile(selectedImage).addOnCompleteListener(task -> {

            if(task.isSuccessful()){
                reference.getDownloadUrl().addOnSuccessListener(uri -> {

                    String filePath= uri.toString();
                    sendImage(filePath);
                    dialog.dismiss();
                });
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void sendImage(String filePath){
        String messageTxt=AESEncryptionMethod(binding.messageBox.getText().toString());
        Date date= new Date();

        binding.messageBox.setText("");
        Message message=new Message(messageTxt,senderUid,date.getTime());
        message.setImageUrl(filePath);
        message.setMessage("arXeermmOOrTTbbPPoNHG");
        String randomKey=database.getReference().push().getKey();

        database.getReference()
                .child("chats")
                .child(senderRoom)
                .child(randomKey)
                .setValue(message).addOnSuccessListener(unused -> database.getReference()
                .child("chats")
                .child(receiverRoom)
                .child(randomKey)
                .setValue(message)
                .addOnSuccessListener(unused1 -> {
                }));

    }
    private void receiveMessage(){
        database.getReference()
                .child("chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();
                        for(DataSnapshot snapshot1 : snapshot.getChildren()){
                            Message message=snapshot1.getValue(Message.class);
                           String msg= message.getMessage();
                           msg.trim();
                          msg= AESDecryptionMethod(msg);
                          message.setMessage(msg);
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

    private void sendMessage(){
            String messageTxt=AESEncryptionMethod(binding.messageBox.getText().toString());
            Date date= new Date();

            binding.messageBox.setText("");
            Message message=new Message(messageTxt,senderUid,date.getTime());
            String randomKey=database.getReference().push().getKey();

            database.getReference()
                    .child("chats")
                    .child(senderRoom)
                    .child(randomKey)
                    .setValue(message).addOnSuccessListener(unused -> database.getReference()
                    .child("chats")
                    .child(receiverRoom)
                    .child(randomKey)
                    .setValue(message)
                    .addOnSuccessListener(unused1 -> {
                    }));

    }
    private void setCipher(){
        try {
            cipher=Cipher.getInstance("AES");
            decipher=Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        secretKeySpec =new SecretKeySpec(encryptionKey,"AES");
    }

    private String AESEncryptionMethod(String string){
    byte[] stringByte= string.getBytes(StandardCharsets.ISO_8859_1);
    byte[] encryptedByte= new byte[stringByte.length];
        try {
            cipher.init(Cipher.ENCRYPT_MODE,secretKeySpec);
            encryptedByte=cipher.doFinal(stringByte);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        String returnVariable=null;
        try {
             returnVariable=new String(encryptedByte,"ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return returnVariable;
    }

    private String AESDecryptionMethod(String string){
        byte[] EncryptedByte= string.getBytes(StandardCharsets.ISO_8859_1);
        String decryptedString = string;
        byte[] decryption;
        try {
            decipher.init(cipher.DECRYPT_MODE,secretKeySpec);
            decryption= decipher.doFinal(EncryptedByte);
            decryptedString=new String(decryption);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return decryptedString;
    }

    public void setDialog(){
        dialog =new ProgressDialog(this);
        dialog.setMessage("Uploading image...");
        dialog.setCancelable(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String currentId= FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("Online");
    }
    @Override
    protected void onPause() {
        super.onPause();
        String currentId= FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("offline");
    }

}