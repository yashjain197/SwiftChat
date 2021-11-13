package com.yashjain.swiftchat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.yashjain.swiftchat.databinding.ActivityPhoneNumberBinding;
import com.yashjain.swiftchat.progressBar.loadingdialog;

import java.util.concurrent.TimeUnit;

public class phoneNumberActivity extends AppCompatActivity {
    ActivityPhoneNumberBinding binding;
    private FirebaseAuth auth;
    //making an object of loading dialog
    final loadingdialog dialog= new loadingdialog(phoneNumberActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //Initializing to firebase
        FirebaseApp.initializeApp(phoneNumberActivity.this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();

        //SafetyNet checking
        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());

        auth = FirebaseAuth.getInstance();

        //If current uer is already logged in we don't need this Activity to run.
        if(auth.getCurrentUser()!=null){
            Intent intent =new Intent(phoneNumberActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        getSupportActionBar().hide();
        binding.phoneNumber.requestFocus();

        binding.continueBtn.setOnClickListener(view -> {

            verifyPhoneNumber(auth);//verify phone number from firebase

        });

    }


    //To verify phone number from firebase we use this function.
    public void verifyPhoneNumber(FirebaseAuth auth){

       if(!binding.phoneNumber.getText().toString().isEmpty()){
           if((binding.phoneNumber.getText().toString().trim()).length()==10){
               dialog.startLoadingDialog();
               PhoneAuthOptions options=PhoneAuthOptions.newBuilder(auth)
                       .setPhoneNumber("+91"+binding.phoneNumber.getText().toString())
                       .setTimeout(60L, TimeUnit.SECONDS)
                       .setActivity(phoneNumberActivity.this)
                       .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                           @Override
                           public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                           }

                           @Override
                           public void onVerificationFailed(@NonNull FirebaseException e) {
                                dialog.dismissDialog();
                               Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();

                           }

                           @Override
                           public void onCodeSent(@NonNull String verifyId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                               super.onCodeSent(verifyId, forceResendingToken);
                               Toast.makeText(getApplicationContext(),"Enter OTP!",Toast.LENGTH_SHORT).show();
                               Intent intent=new Intent(phoneNumberActivity.this, otpActivity.class);
                               intent.putExtra("phoneNumber",binding.phoneNumber.getText().toString());
                               intent.putExtra("OTP",verifyId);
                               startActivity(intent);


                           }
                       }).build();
               PhoneAuthProvider.verifyPhoneNumber(options);//This is the firebase object's function
           }else{


               Toast.makeText(getApplicationContext(),"Enter 10-Digit phone number",Toast.LENGTH_SHORT).show();

           }
       }else{

           Toast.makeText(getApplicationContext(),"Enter a valid phoneNumber",Toast.LENGTH_SHORT).show();

       }

   }

}