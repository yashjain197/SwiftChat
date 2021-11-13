package com.yashjain.swiftchat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yashjain.swiftchat.Adapters.userAdapter;
import com.yashjain.swiftchat.Models.User;
import com.yashjain.swiftchat.R;
import com.yashjain.swiftchat.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    FirebaseDatabase database;
    ActivityMainBinding binding;
    ArrayList<User> users=new ArrayList<>();
    userAdapter adapter;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        adapter=new userAdapter(this ,users);
        binding.recyclerViewMain.setAdapter(adapter);

        database.getReference().child("users").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                binding.shimmer.stopShimmer();
                binding.shimmer.setVisibility(View.GONE);
              for(DataSnapshot snapshot1: snapshot.getChildren()){
                  User user1 = snapshot1.getValue(User.class);
                  if(!user1.getUid().equals(FirebaseAuth.getInstance().getUid()))
                  users.add(user1);
              }
              adapter.notifyDataSetChanged();
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        SharedPreferences sp=getSharedPreferences("SwiftChat",MODE_PRIVATE);
        boolean bln=sp.getBoolean("ONCE",true);
        if(bln){
            Load_dialog();
            SharedPreferences sps=getSharedPreferences("SwiftChat",MODE_PRIVATE);
            SharedPreferences.Editor editor=sps.edit();
            editor.putBoolean("ONCE",false);
            editor.apply();

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        String currentId= FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        String currentId= FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("offline");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.search:
            case R.id.calls:
            case R.id.invite:
                Toast.makeText(this, "Not available right now", Toast.LENGTH_SHORT).show();
                break;
            case R.id.groups:
                startActivity(new Intent(MainActivity.this,communityChat.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void Load_dialog(){
        AlertDialog.Builder ad=new AlertDialog.Builder(this);
        ad.setTitle("Notice!");
        ad.setMessage("This app does not support dark mode right now as This is a early version of this application.So some features are not available right now");


        ad.setPositiveButton("Ok", (dialogInterface, i) -> dialogInterface.dismiss());
        ad.show();
    }
}