package com.buc.gradution.View.Activity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.buc.gradution.Constant.Constant;
import com.buc.gradution.Model.DoctorModel;
import com.buc.gradution.Model.UserModel;
import com.buc.gradution.R;
import com.buc.gradution.Service.FirebaseService;
import com.buc.gradution.Service.NetworkService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.MultiFactorSession;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class SignupActivity extends AppCompatActivity {
    private ImageView back;
    private ShapeableImageView profileImg;
    private TextInputLayout name , email ,password ,phone;
    private MaterialCheckBox checkBox,doctorCheck;
    private MaterialButton signupBtn;
    private CircularProgressIndicator progressIndicator;
    private AtomicReference<Uri> uri;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initComponents();
        context = getApplicationContext();
        uri = new AtomicReference<>();
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result -> {
            if(result.getResultCode() == RESULT_OK && result.getData() != null){
                profileImg.setImageURI(result.getData().getData());
                profileImg.setImageTintMode(null);
                uri.set(result.getData().getData());
            }else{
                Toast.makeText(SignupActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
            }
        });
        back.setOnClickListener(v -> finish());
        profileImg.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            launcher.launch(intent);
        });
        password.setEndIconOnClickListener(v -> showAndHidePassword(password));
        signupBtn.setOnClickListener(v -> {
            progressIndicator.setProgress(0);
            boolean isNameValid = nameValidation(name);
            boolean isEmailValid = emailValidation(email);
            boolean isPasswordValid = passwordValidation(password);
            boolean isCheckBoxChecked = isCheckboxChecked(checkBox);
            boolean isPhoneValid = phoneValidation(phone);
            boolean isProfileImageSelected = uri.get() != null;
            if(!isProfileImageSelected){
                Toast.makeText(SignupActivity.this, "Please select an image first.", Toast.LENGTH_SHORT).show();
            }
            if(isNameValid && isEmailValid && isPasswordValid && isPhoneValid && isCheckBoxChecked && isProfileImageSelected){
                if(NetworkService.isConnected(context)){
                    String nameTxt = name.getEditText().getText().toString();
                    String sanitizedNameTxt = sanitizeName(nameTxt);
                    String emailTxt = email.getEditText().getText().toString();
                    String passwordTxt = password.getEditText().getText().toString();
                    String phoneTxt = "+2" + phone.getEditText().getText().toString();
                    signup(sanitizedNameTxt,emailTxt,passwordTxt,phoneTxt);
                }
                else{
                    NetworkService.connectionFailed(context);
                }
            }
        });
    }
    private void initComponents(){
        back = findViewById(R.id.back_button);
        profileImg = findViewById(R.id.profile_img);
        name = findViewById(R.id.name_layout);
        email = findViewById(R.id.email_layout);
        password = findViewById(R.id.password_layout);
        phone = findViewById(R.id.phone_layout);
        doctorCheck = findViewById(R.id.doctor);
        checkBox = findViewById(R.id.checkbox);
        signupBtn = findViewById(R.id.signup_button);
        progressIndicator = findViewById(R.id.progress);
    }
    private boolean nameValidation(TextInputLayout name){
        boolean isValid = false;
        if(name.getEditText().getText().toString().isEmpty()){
            name.setError(getString(R.string.name_is_mandatory));
        }
        else if(name.getEditText().getText().toString().charAt(0) < 65 ||
                name.getEditText().getText().toString().charAt(0) > 122 ||
                (name.getEditText().getText().toString().charAt(0) > 90 && name.getEditText().getText().toString().charAt(0) < 97 )) {
            name.setError(getString(R.string.name_must_start_with_letter));
        }
        name.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty()){
                    name.setError(null);
                }
                else if((s.toString().charAt(0) >= 65 && s.toString().charAt(0) <= 90 ) ||
                        (s.toString().charAt(0) >= 97 && s.toString().charAt(0) <= 122 )){
                    name.setError(null);
                }
            }
        });
        if(name.getError() == null){
            isValid = true;
        }
        return isValid;
    }
    private String sanitizeName(String name){
        StringBuilder sanitized = new StringBuilder();
        for(int i = 0 ; i < name.length(); i++){
            char ch = name.charAt(i);
            if(Character.isLetter(ch)|| ch ==' '){
                sanitized.append(ch);
            }
        }
        return sanitized.toString();
    }
    private boolean emailValidation(TextInputLayout email){
        boolean isValid = false;
        if(email.getEditText().getText().toString().isEmpty()){
            email.setError(getString(R.string.email_is_mandatory));
        }
        else if(!email.getEditText().getText().toString().contains("@")){
            email.setError(getString(R.string.wrong_email_format));
        }
        email.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty()){
                    email.setError(null);
                }
                else if(s.toString().contains("@")){
                    email.setError(null);
                }
            }
        });
        if(email.getError() == null){
            isValid = true;
        }
        return isValid;
    }
    private boolean phoneValidation(TextInputLayout phone){
        boolean isValid = false;
        if (phone.getEditText().getText().toString().isEmpty()){
            phone.setError("Phone is mandatory");
        }
        else if(phone.getEditText().getText().toString().length() != 11){
            phone.setError("Wrong phone number");
        }
        phone.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty()){
                    phone.setError(null);
                }
                else if(s.toString().length() == 11){
                    phone.setError(null);
                }
            }
        });
        if(phone.getError() == null){
            isValid = true;
        }
        return isValid;
    }
    private boolean passwordValidation(TextInputLayout password){
        boolean isValid = false;
        if(password.getEditText().getText().toString().isEmpty()){
            password.setError(getString(R.string.password_is_mandatory));
        }
        else if(password.getEditText().getText().toString().length() < 8){
            password.setError(getString(R.string.weak_password_password_must_be_at_least_8_letters));
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
    private boolean isCheckboxChecked(MaterialCheckBox checkBox){
        boolean isValid = checkBox.isChecked();
        if(!isValid){
            checkBox.setError(getString(R.string.please_check_this_first));
        }
        else{
            checkBox.setError(null);
        }
        return isValid;
    }
    private String getUserType(MaterialCheckBox checkBox){
        if(checkBox.isChecked()){
            return Constant.DOCTOR_TYPE;
        }
        else{
            return Constant.PATIENT_TYPE;
        }
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
    private void createUser(AuthResult authResult ,String name ,String email ,String password ,Uri uri,String phone){
        String id = authResult.getUser().getUid();
        String type = getUserType(doctorCheck);
        UserModel user = new UserModel();
        if(type.equals(Constant.PATIENT_TYPE)){
            user = new UserModel(id,name,email,type,uri.toString(),phone);
        }else if (type.equals(Constant.DOCTOR_TYPE)){
            user = new DoctorModel(id,name,email,type,uri.toString(),phone,"Dentist","4,5","750m","");
        }
        UserModel finalUser = user;
        FirebaseService.getFirebaseDatabase()
                .getReference(getUserType(doctorCheck))
                .child(id)
                .setValue(user)
                .addOnSuccessListener(
                        e -> {
                            LayoutInflater layoutInflater = LayoutInflater.from(SignupActivity.this);
                            View layout = layoutInflater.inflate(R.layout.alert_dialog_signup,null);
                            MaterialButton login = layout.findViewById(R.id.login_button);
                            AlertDialog dialog = new MaterialAlertDialogBuilder(SignupActivity.this).create();
                            login.setOnClickListener( v -> {
                                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                intent.putExtra("email", email);
                                intent.putExtra("password",password);
                                startActivity(intent);
                                finish();
                            });
                            dialog.setView(layout);
                            dialog.show();
                            //progressIndicator.setProgress(100,true);
                            //progressIndicator.setVisibility(View.INVISIBLE);
                            //secondFactorVerify(authResult,phone,email,password);
                        }
                )
                .addOnFailureListener(e -> {
                    progressIndicator.setIndicatorColor(getColor(R.color.error_color));
                    progressIndicator.setProgress(100,true);
                    progressIndicator.setVisibility(View.INVISIBLE);
                    progressIndicator.setIndicatorColor(getColor(R.color.main_color));
                    Toast.makeText(SignupActivity.this, "Couldn't save user info", Toast.LENGTH_SHORT).show();
                });
    }
    private void signup(String name , String email , String password,String phone){
        progressIndicator.setVisibility(View.VISIBLE);
        progressIndicator.setProgress(10,true);
        FirebaseService.getFirebaseAuth()
                .createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener(authResult -> {
                    progressIndicator.setProgress(30,true);
                    uploadProfileImg(authResult,name,email,password,uri.get(),phone);
                })
                .addOnFailureListener(e -> {
                    progressIndicator.setIndicatorColor(getColor(R.color.error_color));
                    progressIndicator.setProgress(100,true);
                    progressIndicator.setVisibility(View.INVISIBLE);
                    progressIndicator.setIndicatorColor(getColor(R.color.main_color));
                    Toast.makeText(SignupActivity.this, "Sorry, Couldn't create account\nPlease try again", Toast.LENGTH_SHORT).show();
                });
    }
    private void uploadProfileImg(AuthResult authResult,String name,String email,String password,Uri imageUri,String phone){
        FirebaseService.getFirebaseStorage()
                .getReference(authResult.getUser().getUid())
                .child("profile image")
                .putFile(imageUri)
                .addOnSuccessListener(v -> {
                    progressIndicator.setProgress(50,true);
                    v.getStorage()
                            .getStorage()
                            .getReference(authResult.getUser().getUid())
                            .child("profile image")
                            .getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                progressIndicator.setProgress(70,true);
                                createUser(authResult,name,email,password,uri,phone);
                            })
                            .addOnFailureListener(e -> {
                                progressIndicator.setIndicatorColor(getColor(R.color.error_color));
                                progressIndicator.setProgress(100,true);
                                progressIndicator.setVisibility(View.INVISIBLE);
                                progressIndicator.setIndicatorColor(getColor(R.color.main_color));
                                Toast.makeText(SignupActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    progressIndicator.setIndicatorColor(getColor(R.color.error_color));
                    progressIndicator.setProgress(100,true);
                    progressIndicator.setVisibility(View.INVISIBLE);
                    progressIndicator.setIndicatorColor(getColor(R.color.main_color));
                    Toast.makeText(SignupActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void secondFactorVerify(AuthResult authResult,String phone,String email,String password){
        PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                        super.onCodeSent(verificationId, token);
                        progressIndicator.setProgress(70,true);
                        Intent intent = new Intent(getApplicationContext(), VerifySignUpCodeActivity.class);
                        intent.putExtra("id",verificationId);
                        intent.putExtra("phone",phone);
                        intent.putExtra("email",email);
                        intent.putExtra("password",password);
                        startActivity(intent);
                    }
                };
        authResult.getUser().getMultiFactor().getSession()
                .addOnCompleteListener(
                        new OnCompleteListener<MultiFactorSession>() {
                            @Override
                            public void onComplete(Task<MultiFactorSession> task) {
                                if (task.isSuccessful()) {
                                    MultiFactorSession multiFactorSession = task.getResult();
                                    PhoneAuthOptions phoneAuthOptions =
                                            PhoneAuthOptions.newBuilder()
                                                    .setPhoneNumber(phone)
                                                    .setTimeout(30L, TimeUnit.SECONDS)
                                                    .setMultiFactorSession(multiFactorSession)
                                                    .setCallbacks(callbacks)
                                                    .build();
                                    PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
                                }
                            }
                        });
    }
}