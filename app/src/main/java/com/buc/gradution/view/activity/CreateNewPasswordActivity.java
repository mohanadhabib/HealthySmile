package com.buc.gradution.view.activity;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.buc.gradution.R;
import com.buc.gradution.service.FirebaseService;
import com.buc.gradution.service.NetworkService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

public class CreateNewPasswordActivity extends AppCompatActivity {

    private Intent mainIntent;
    private ImageView back;
    private TextInputLayout password,confirmPassword;
    private MaterialButton createPasswordBtn;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_password);
        initComponents();
        context = getApplicationContext();
        mainIntent = getIntent();
        back.setOnClickListener(v -> finish());
        password.setEndIconOnClickListener(v -> showAndHidePassword(password));
        confirmPassword.setEndIconOnClickListener(v -> showAndHidePassword(confirmPassword));
        createPasswordBtn.setOnClickListener(v -> {
            boolean isPasswordValid = passwordValidation(password);
            boolean isConfirmPasswordValid = passwordValidation(confirmPassword);
            if(!password.getEditText().getText().toString().equals(confirmPassword.getEditText().getText().toString())){
                Toast.makeText(CreateNewPasswordActivity.this, "The entered passwords doesn't match", Toast.LENGTH_SHORT).show();
            }
            else if(isPasswordValid && isConfirmPasswordValid && password.getEditText().getText().toString().equals(confirmPassword.getEditText().getText().toString())){
                if(NetworkService.isConnected(context)) {
                    updatePassword(password.getEditText().getText().toString());
                }
                else{
                    NetworkService.connectionFailed(context);
                }
            }
        });
    }
    private void initComponents(){
        back = findViewById(R.id.back_button);
        password = findViewById(R.id.password_layout);
        confirmPassword = findViewById(R.id.confirm_password_layout);
        createPasswordBtn = findViewById(R.id.create_password_button);
    }
    private boolean passwordValidation(TextInputLayout password){
        boolean isValid = false;
        if(password.getEditText().getText().toString().isEmpty()){
            password.setError("Password is mandatory");
        }
        else if(password.getEditText().getText().toString().length() < 8){
            password.setError("Weak password, Password must be at least 8 letters");
        }
        password.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty()){
                    password.setError(null);
                }
                else if(s.toString().length() > 8){
                    password.setError(null);
                }
            }
        });
        if(password.getError() == null){
            isValid = true;
        }
        return isValid;
    }
    private void showAndHidePassword(TextInputLayout password){
        int visible = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
        int invisible = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
        int position = password.getEditText().getText().toString().length();
        if(password.getEditText().getInputType() == invisible){
            password.getEditText().setInputType(visible);
            password.getEditText().setSelection(position);
        }
        else if (password.getEditText().getInputType() == visible){
            password.getEditText().setInputType(invisible);
            password.getEditText().setSelection(position);
        }
    }
    private void goToLoginScreen(){
        LayoutInflater inflater = LayoutInflater.from(CreateNewPasswordActivity.this);
        View view = inflater.inflate(R.layout.alert_dialog_new_password, null);
        AlertDialog alertDialog = new MaterialAlertDialogBuilder(CreateNewPasswordActivity.this).create();
        MaterialButton login = view.findViewById(R.id.login);
        login.setOnClickListener(
                v1 -> {
                    startActivity(new Intent(CreateNewPasswordActivity.this, LoginActivity.class));
                    finish();
                }
        );
        alertDialog.setView(view);
        alertDialog.show();
    }
    private void updatePassword(String password){
        if(mainIntent.getExtras() != null){
            String code = mainIntent.getExtras().getString("code");
            FirebaseService.getFirebaseAuth()
                    .confirmPasswordReset(code,password)
                    .addOnSuccessListener(v -> goToLoginScreen())
                    .addOnFailureListener(e -> Toast.makeText(this, "Sorry, Something went wrong Please try again", Toast.LENGTH_SHORT).show());
        }
        else {
            FirebaseService.getFirebaseAuth()
                    .getCurrentUser()
                    .updatePassword(password)
                    .addOnSuccessListener(v -> goToLoginScreen())
                    .addOnFailureListener(e -> Toast.makeText(this, "Sorry, Something went wrong Please try again", Toast.LENGTH_SHORT).show());
        }
    }
}