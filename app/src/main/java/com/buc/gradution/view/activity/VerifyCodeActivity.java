package com.buc.gradution.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.buc.gradution.R;
import com.buc.gradution.service.FirebaseService;
import com.buc.gradution.service.NetworkService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyCodeActivity extends AppCompatActivity {

    private String email,phoneNum,finalPhoneNum,id;
    private ImageView back;
    private TextView text1,resendBtn;
    private TextInputLayout t1,t2,t3,t4,t5,t6;
    private MaterialButton verify;
    private Intent mainIntent;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);
        mainIntent = getIntent();
        context = getApplicationContext();
        initComponents();
        getDataFromResetScreen();
        getNextText(t1,t2);
        getNextText(t2,t3);
        getNextText(t3,t4);
        getNextText(t4,t5);
        getNextText(t5,t6);
        getPreviousText(t1,t2);
        getPreviousText(t2,t3);
        getPreviousText(t3,t4);
        getPreviousText(t4,t5);
        getPreviousText(t5,t6);
        back.setOnClickListener(v -> finish());
        verify.setOnClickListener(v -> {
            boolean isEmail = mainIntent.getExtras().getBoolean("isEmail");
            String code = t1.getEditText().getText().toString()+
                    t2.getEditText().getText().toString()+
                    t3.getEditText().getText().toString()+
                    t4.getEditText().getText().toString()+
                    t5.getEditText().getText().toString()+
                    t6.getEditText().getText().toString();
            if(NetworkService.isConnected(context)){
                if(isEmail){
                    Intent intent = new Intent(VerifyCodeActivity.this, CreateNewPasswordActivity.class);
                    intent.putExtra("code",code);
                    startActivity(intent);
                }else{
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id,code);
                    FirebaseService.getFirebaseAuth()
                            .signInWithCredential(credential)
                            .addOnSuccessListener(command -> {
                                Intent intent = new Intent(VerifyCodeActivity.this, CreateNewPasswordActivity.class);
                                startActivity(intent);
                            })
                            .addOnFailureListener(e-> Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show());
                }
            } else{
                NetworkService.connectionFailed(context);
            }
        });
        resendBtn.setOnClickListener(v -> {
            if (NetworkService.isConnected(context)){
                resendOTP(finalPhoneNum);
            }else{
                NetworkService.connectionFailed(context);
            }
        });
    }
    private void initComponents(){
        back = findViewById(R.id.back_button);
        t1 = findViewById(R.id.t1);
        t2 = findViewById(R.id.t2);
        t3 = findViewById(R.id.t3);
        t4 = findViewById(R.id.t4);
        t5 = findViewById(R.id.t5);
        t6 = findViewById(R.id.t6);
        text1 = findViewById(R.id.txt1);
        verify = findViewById(R.id.verify_button);
        resendBtn = findViewById(R.id.resend_btn);
    }
    private void getDataFromResetScreen(){
        finalPhoneNum = phoneNum;
        if(mainIntent.getExtras() != null && !mainIntent.getExtras().getBoolean("isEmail")){
            phoneNum = mainIntent.getExtras().getString("phone");
            id = mainIntent.getExtras().getString("id");
            text1.setText(text1.getText()+phoneNum);
        }
        else if (mainIntent.getExtras() != null && mainIntent.getExtras().getBoolean("isEmail")){
            email = mainIntent.getExtras().getString("email");
            text1.setText(text1.getText()+email);
        }
    }
    private void getNextText(TextInputLayout t1 , TextInputLayout t2){
        t1.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length() == 1){
                    t2.requestFocus();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    private void getPreviousText(TextInputLayout t1 , TextInputLayout t2){
        t2.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length() == 0){
                    t1.requestFocus();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    private void resendOTP(String phoneNum){
        PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                .setPhoneNumber(phoneNum)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(VerifyCodeActivity.this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        Intent intent = new Intent(VerifyCodeActivity.this, VerifyCodeActivity.class);
                        intent.putExtra("id",s);
                        startActivity(intent);
                        finish();
                    }
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        Toast.makeText(VerifyCodeActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(VerifyCodeActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
    }
}