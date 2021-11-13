package com.yashjain.swiftchat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.yashjain.swiftchat.databinding.ActivityOtpBinding;
import com.yashjain.swiftchat.progressBar.loadingdialog;

public class otpActivity extends AppCompatActivity {
    ActivityOtpBinding binding;

    String verificationId;
    final loadingdialog dialog= new loadingdialog(otpActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.in1.requestFocus();

        FirebaseApp.initializeApp(otpActivity.this);

        getSupportActionBar().hide();

        //Taking information from the last activity(phoneNumberActivity.java)
        verificationId = getIntent().getStringExtra("OTP");
        String phoneNumber = getIntent().getStringExtra("phoneNumber");

        //Setting the number to Text view
        binding.verifyPhone.setText("Verify +91-"+phoneNumber);


        binding.continueBtn.setOnClickListener(view -> {

            //To match the otp from the firebase.

            verifyOtpFromFirebase(verificationId);
        });


        //this is to move the first edit text to another
        EditTextMove();
    }

    private void verifyOtpFromFirebase(String verificationId) {
        //checking if any edit text box is empty
        dialog.startLoadingDialog();
        if(!binding.in1.getText().toString().trim().isEmpty()
                && !binding.in2.getText().toString().trim().isEmpty()
                && !binding.in3.getText().toString().trim().isEmpty()
                && !binding.in4.getText().toString().trim().isEmpty()
                && !binding.in5.getText().toString().trim().isEmpty()
                && !binding.in6.getText().toString().trim().isEmpty()) {

            //Taking all the inputs from Edit text and combine it as one.
            String enteredOTP= binding.in1.getText().toString()+
                    binding.in2.getText().toString()+
                    binding.in3.getText().toString()+
                    binding.in4.getText().toString()+
                    binding.in5.getText().toString()+
                    binding.in6.getText().toString();

            if(verificationId!=null){
                //This is checking the OTP
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, enteredOTP);
                signInWithPhoneAuthCredential(credential);

            }else{
                //If verificationId id null
                dialog.dismissDialog();
                Toast.makeText(getApplicationContext(),"Please check internet connection",Toast.LENGTH_SHORT).show();


            }
        }else{
            //If user not entered all the numbers
            dialog.dismissDialog();
            Toast.makeText(getApplicationContext(),"Please enter all number",Toast.LENGTH_SHORT).show();


        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){

                        Intent intent = new Intent(otpActivity.this, setupProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }else{
                        dialog.dismissDialog();
                        Toast.makeText(getApplicationContext(),"Incorrect OTP!",Toast.LENGTH_SHORT).show();


                    }
                });
    }

    private void EditTextMove() {
        binding.in1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.in2.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.in2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.in3.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.in3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.in4.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.in4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.in5.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.in5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.in6.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.in6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                verifyOtpFromFirebase(verificationId);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



    }



}