package com.buc.gradution.View.Fragment.User;

import static android.app.Activity.RESULT_OK;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.buc.gradution.Constant.Constant;
import com.buc.gradution.Model.ScanOutputModel;
import com.buc.gradution.R;
import com.buc.gradution.Service.FirebaseService;
import com.buc.gradution.Service.NetworkService;
import com.buc.gradution.Interface.ScanInterface;
import com.buc.gradution.Service.RetrofitService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserScanFragment extends Fragment {
    private ScanOutputModel scanXray,scanPhoto;
    private ImageView beforeImg, afterImg;
    private MaterialButton submitBtn,captureBtn;
    private TextView resultTxt,noImageTxt;
    private CircularProgressIndicator progress;
    private Context context;
    private AtomicReference<Uri> uri = new AtomicReference<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_scan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initComponents(view);
        context = view.getContext();
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == RESULT_OK && result.getData() != null){
                uri.set(result.getData().getData());
                beforeImg.setImageURI(uri.get());
                if(NetworkService.isConnected(context)){
                    progress.setVisibility(View.VISIBLE);
                    progress.setProgress(15,true);
                    noImageTxt.setVisibility(View.INVISIBLE);
                    uploadXRayImage();
                }
                else{
                    NetworkService.connectionFailed(context);
                }
            }else {
                noImageTxt.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Please select an image", Toast.LENGTH_SHORT).show();
            }
        });
        ActivityResultLauncher<Intent> captureLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result ->{
            if(result.getResultCode() == RESULT_OK && result.getData() != null){
                Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                Uri myUri = getImageUri(getActivity().getApplicationContext(),bitmap);
                uri.set(myUri);
                beforeImg.setImageURI(uri.get());
                if(NetworkService.isConnected(context)){
                    progress.setVisibility(View.VISIBLE);
                    progress.setProgress(15,true);
                    noImageTxt.setVisibility(View.INVISIBLE);
                    uploadPhotoImage();
                }
                else{
                    NetworkService.connectionFailed(context);
                }
            }else {
                noImageTxt.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Please select an image", Toast.LENGTH_SHORT).show();
            }
        });
        captureBtn.setOnClickListener(v ->{
            progress.setProgress(0,true);
            afterImg.setImageBitmap(null);
            resultTxt.setText(null);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            captureLauncher.launch(intent);
        });
        submitBtn.setOnClickListener(v -> {
            progress.setProgress(0,true);
            afterImg.setImageBitmap(null);
            resultTxt.setText(null);
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            launcher.launch(intent);
        });
    }
    private void initComponents(View view) {
        beforeImg = view.findViewById(R.id.before_img);
        submitBtn = view.findViewById(R.id.submit_img_button);
        captureBtn = view.findViewById(R.id.capture_img_button);
        resultTxt = view.findViewById(R.id.result);
        afterImg = view.findViewById(R.id.after_img);
        progress = view.findViewById(R.id.progress);
        noImageTxt = view.findViewById(R.id.no_image_text);
    }
    private void uploadXRayImage() {
        String id = FirebaseService.getFirebaseAuth().getCurrentUser().getUid();
        FirebaseService.getFirebaseStorage().getReference(id)
                .child("Xray")
                .putFile(uri.get())
                .addOnSuccessListener(v -> {
                    progress.setProgress(25,true);
                    v.getStorage().getStorage()
                            .getReference(id)
                            .child("Xray")
                            .getDownloadUrl()
                            .addOnSuccessListener(x -> {
                                progress.setProgress(55,true);
                                String path = x.toString().split("&token")[0];
                                getXRayInference(path);
                            }).addOnFailureListener(e -> {
                                progress.setIndicatorColor(getContext().getColor(R.color.error_color));
                                progress.setProgress(100,true);
                                progress.setVisibility(View.INVISIBLE);
                                progress.setIndicatorColor(getContext().getColor(R.color.main_color));
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e0 -> Toast.makeText(getContext(), e0.getMessage(), Toast.LENGTH_SHORT).show());
    }
    private void uploadPhotoImage() {
        String id = FirebaseService.getFirebaseAuth().getCurrentUser().getUid();
        FirebaseService.getFirebaseStorage().getReference(id)
                .child("Real Photo")
                .putFile(uri.get())
                .addOnSuccessListener(v -> {
                    progress.setProgress(25,true);
                    v.getStorage().getStorage()
                            .getReference(id)
                            .child("Real Photo")
                            .getDownloadUrl()
                            .addOnSuccessListener(x -> {
                                progress.setProgress(55,true);
                                String path = x.toString().split("&token")[0];
                                getPhotoInference(path);
                            }).addOnFailureListener(e -> {
                                progress.setIndicatorColor(getContext().getColor(R.color.error_color));
                                progress.setProgress(100,true);
                                progress.setVisibility(View.INVISIBLE);
                                progress.setIndicatorColor(getContext().getColor(R.color.main_color));
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e0 -> Toast.makeText(getContext(), e0.getMessage(), Toast.LENGTH_SHORT).show());
    }
    private void getPhotoInference(String url){
        try {
            progress.setProgress(75,true);
            ScanInterface scan = RetrofitService.getRetrofit("https://detect.roboflow.com/")
                    .create(ScanInterface.class);
            scan.postCapturedImage(Constant.apiScanKey,url)
                    .enqueue(new Callback<ScanOutputModel>() {
                        @Override
                        public void onResponse(Call<ScanOutputModel> call, Response<ScanOutputModel> response) {
                            scanPhoto = response.body();
                            getTotalData(scanPhoto);
                        }

                        @Override
                        public void onFailure(Call<ScanOutputModel> call, Throwable t) {
                            Toast.makeText(getActivity().getApplicationContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }catch (Exception e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void getXRayInference(String url) {
        try {
            progress.setProgress(75,true);
            ScanInterface scan = RetrofitService.getRetrofit("https://detect.roboflow.com/")
                    .create(ScanInterface.class);
            scan.postImage(Constant.apiScanKey, url)
                    .enqueue(new Callback<ScanOutputModel>() {
                        @Override
                        public void onResponse(Call<ScanOutputModel> call, Response<ScanOutputModel> response) {
                            scanXray = response.body();
                            getTotalData(scanXray);
                        }
                        @Override
                        public void onFailure(Call<ScanOutputModel> call, Throwable t) {
                            Toast.makeText(getActivity().getApplicationContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void getTotalData(ScanOutputModel scanModel){
                        try {
                            progress.setProgress(85, true);
                            Bitmap bitmapWithText = createBitmapFromUri(uri.get()).copy(Bitmap.Config.ARGB_8888, true);
                            createScanOutput(bitmapWithText,scanModel);
                        }catch (Exception e){
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
    }
    private void createScanOutput(Bitmap image,ScanOutputModel scanModel) {
        try {
            String x = "";
            Canvas canvas = new Canvas(image);
            Paint paintTxt = new Paint();
            Paint paintRect = new Paint();
            Paint background = new Paint();
            background.setColor(getContext().getColor(R.color.prediction_background));
            paintTxt.setColor(getContext().getColor(R.color.black));
            paintTxt.setTextSize(18);
            paintTxt.setAntiAlias(true);
            paintTxt.setStyle(Paint.Style.FILL);
            paintRect.setColor(getContext().getColor(R.color.dark_red));
            paintRect.setAntiAlias(true);
            DecimalFormat format = (DecimalFormat) DecimalFormat.getNumberInstance(Locale.US);
            format.applyPattern("###.##");
            for (int i = 0; i < scanModel.getPredictions().size(); i++) {
                float xPos = scanModel.getPredictions().get(i).getX();
                float yPos = scanModel.getPredictions().get(i).getY();
                String txt = scanModel.getPredictions().get(i).getClassType();
                double confidence = scanModel.getPredictions().get(i).getConfidence();
                String confidenceTxt = format.format(confidence);
                int conPer = (int) (Double.parseDouble(confidenceTxt) * 100);
                x += txt + " " + conPer +"%" + "\n";
                String resTxt = txt + " " + conPer + "%";
                float textWidth = paintTxt.measureText(resTxt);
                Rect r = new Rect((int) xPos, (int) yPos, (int) (xPos + 10), (int) (yPos + 10));
                float x0 = xPos - 10;
                float y0 = yPos - 10;
                canvas.drawRect(r, paintRect);
                canvas.drawRect(x0, y0 - paintTxt.getTextSize(), x0 + textWidth, y0, background);
                canvas.drawText(resTxt, x0, y0, paintTxt);
            }
            progress.setProgress(100, true);
            progress.setVisibility(View.INVISIBLE);
            resultTxt.setText(x);
            afterImg.setImageBitmap(image);
        }
        catch(Exception e){
            progress.setProgress(100, true);
            progress.setVisibility(View.INVISIBLE);
            afterImg.setImageBitmap(null);
            resultTxt.setText(getString(R.string.error));
        }
    }
    private Bitmap createBitmapFromUri(Uri uri) {
        try {
            ContentResolver contentResolver = getContext().getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (inputStream != null) {
                inputStream.close();
            }
            return bitmap;
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }
}