package com.yashjain.swiftchat.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yashjain.swiftchat.Models.User;
import com.yashjain.swiftchat.R;
import com.yashjain.swiftchat.databinding.ActivitySetupProfileBinding;
import com.yashjain.swiftchat.progressBar.loadingdialog;

public class setupProfileActivity extends AppCompatActivity {
    //Initializing variables
    ActivitySetupProfileBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseStorage storage;
    Uri selectedImage;
    //This is a custom dialog box from progressBar file
    final loadingdialog dialog= new loadingdialog(setupProfileActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySetupProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        //Making Instance
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();
        auth=FirebaseAuth.getInstance();


        //Taking image from our storage
        binding.profileImage.setOnClickListener(view -> {
            setProfilePhoto();
        });

        //On click button and saving the data in firebase
        binding.setupProfile.setOnClickListener(view -> {
            //This is function that will add data in firebase
            setDataInFirebase();
        });

    }

    private void setProfilePhoto() {

        Intent intent= new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,25);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            if(data.getData()!=null){
                binding.profileImage.setImageURI(data.getData());
                selectedImage=data.getData();
            }
        }
    }

    private  void setDataInFirebase(){
        if(binding.name.getText().toString().isEmpty()){
            binding.name.setError("Please enter your name");
        }else{
            dialog.startLoadingDialog();
            if(selectedImage!=null){

                StorageReference reference= storage.getReference().child("users").child(auth.getUid());
                reference.putFile(selectedImage).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        reference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl= uri.toString();
                            String phoneNumber = auth.getCurrentUser().getPhoneNumber();
                            String name= binding.name.getText().toString();
                            String uid= auth.getUid();
                            User user=new User(name,uid,phoneNumber,imageUrl);

                            database.getReference()
                                    .child("users")
                                    .child(uid)
                                    .setValue(user)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(getApplicationContext(),"Logged in!!",Toast.LENGTH_SHORT);
                                        Intent intent=new Intent(setupProfileActivity.this,MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    });
                        });
                    }
                });
            }else{

                String phoneNumber = auth.getCurrentUser().getPhoneNumber();
                String name= binding.name.getText().toString();
                String uid= auth.getUid();
                User user=new User(name,uid,phoneNumber,"No Image");

                database.getReference()
                        .child("users")
                        .child(uid)
                        .setValue(user)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(getApplicationContext(),"Logged in!!",Toast.LENGTH_SHORT);
                            Intent intent=new Intent(setupProfileActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        });
            }
        }
    }
}