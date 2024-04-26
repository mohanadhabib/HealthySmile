package com.buc.gradution.View.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.buc.gradution.R;
import com.buc.gradution.Service.FirebaseService;
import com.buc.gradution.Service.NetworkService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ResetPasswordActivity extends AppCompatActivity {

    private AtomicBoolean isEmailMethod;
    private ImageView back;
    private TextView emailBtn,phoneBtn;
    private TextInputLayout email,phone,code;
    private MaterialButton resetPassword;
    private CircularProgressIndicator progressIndicator;
    private LinearLayout phoneCode;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        initComponents();
        context = getApplicationContext();
        isEmailMethod = new AtomicBoolean(true);
        if(isEmailMethod.get()){
            email.setVisibility(View.VISIBLE);
            phoneCode.setVisibility(View.INVISIBLE);
        }else{
            email.setVisibility(View.INVISIBLE);
            phoneCode.setVisibility(View.VISIBLE);
        }
        back.setOnClickListener(v -> finish());
        emailBtn.setOnClickListener(v -> {
            isEmailMethod.set(true);
            email.setVisibility(View.VISIBLE);
            phoneCode.setVisibility(View.INVISIBLE);
            phoneBtn.setBackground(null);
            phoneBtn.setTextColor(getColor(R.color.grey));
            emailBtn.setBackground(AppCompatResources.getDrawable(ResetPasswordActivity.this,R.drawable.background_forget_button));
            emailBtn.setTextColor(getColor(R.color.main_color));
        });
        phoneBtn.setOnClickListener(v -> {
            isEmailMethod.set(false);
            email.setVisibility(View.INVISIBLE);
            phoneCode.setVisibility(View.VISIBLE);
            phoneBtn.setBackground(AppCompatResources.getDrawable(ResetPasswordActivity.this,R.drawable.background_forget_button));
            phoneBtn.setTextColor(getColor(R.color.main_color));
            emailBtn.setBackground(null);
            emailBtn.setTextColor(getColor(R.color.grey));
        });
        resetPassword.setOnClickListener(v ->{
            if(isEmailMethod.get()){
                if(NetworkService.isConnected(context)){
                    String email = this.email.getEditText().getText().toString();
                    sendResetPasswordEmail(email);
                }
                else{
                    NetworkService.connectionFailed(context);
                }
            }
            else{
                if(NetworkService.isConnected(context)){
                    String codePhoneTxt = code.getEditText().getText().toString() + phone.getEditText().getText().toString();
                    progressIndicator.setVisibility(View.VISIBLE);
                    progressIndicator.setProgress(50,true);
                    sendOTP(codePhoneTxt);
                }else{
                    NetworkService.connectionFailed(context);
                }
            }
        }
        );
    }
    private void initComponents(){
        back  = findViewById(R.id.back_button);
        emailBtn = findViewById(R.id.email_btn);
        phoneBtn = findViewById(R.id.phone_btn);
        email = findViewById(R.id.email_layout);
        resetPassword = findViewById(R.id.reset_password_btn);
        progressIndicator = findViewById(R.id.progress);
        phone = findViewById(R.id.phone_layout);
        code = findViewById(R.id.code_layout);
        phoneCode = findViewById(R.id.phone_code_layout);
    }
    private void sendResetPasswordEmail(String email){
        FirebaseService.getFirebaseAuth().sendPasswordResetEmail(email)
                .addOnSuccessListener(command -> {
                    progressIndicator.setProgress(100,true);
                    progressIndicator.setVisibility(View.INVISIBLE);
                    LayoutInflater inflater = LayoutInflater.from(ResetPasswordActivity.this);
                    View view = inflater.inflate(R.layout.alert_dialog_email_reset_password,null);
                    AlertDialog alertDialog = new MaterialAlertDialogBuilder(ResetPasswordActivity.this).create();
                    MaterialButton goToLoginPage = view.findViewById(R.id.go_to_login);
                    alertDialog.setView(view);
                    alertDialog.show();
                    goToLoginPage.setOnClickListener(v ->{
                        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    });
                })
                .addOnFailureListener(e -> Toast.makeText(ResetPasswordActivity.this, "Sorry,Couldn't send email\nplease try again", Toast.LENGTH_SHORT).show());
    }
    private void sendOTP(String phoneNum){
        PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                .setPhoneNumber(phoneNum)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(ResetPasswordActivity.this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        progressIndicator.setProgress(70,true);
                        Intent intent = new Intent(ResetPasswordActivity.this, VerifyCodeActivity.class);
                        intent.putExtra("id",s);
                        intent.putExtra("isEmail",false);
                        intent.putExtra("phone",phoneNum);
                        startActivity(intent);
                    }
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        progressIndicator.setProgress(100,true);
                        progressIndicator.setVisibility(View.INVISIBLE);
                        Toast.makeText(ResetPasswordActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        progressIndicator.setVisibility(View.INVISIBLE);
                        Toast.makeText(ResetPasswordActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
    }
}