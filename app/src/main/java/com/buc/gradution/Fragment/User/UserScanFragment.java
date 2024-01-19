package com.buc.gradution.Fragment.User;

import static android.app.Activity.RESULT_OK;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
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
import com.buc.gradution.Service.ScanInterface;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicReference;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserScanFragment extends Fragment {
    private ImageView beforeImg, afterImg;
    private MaterialButton submitBtn;
    private TextView resultTxt;
    private CircularProgressIndicator progress;
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
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == RESULT_OK && result.getData() != null){
                uri.set(result.getData().getData());
                beforeImg.setImageURI(uri.get());
                progress.setVisibility(View.VISIBLE);
                progress.setProgress(15,true);
                uploadImage();
            }else {
                Toast.makeText(getContext(), "Please select an image", Toast.LENGTH_SHORT).show();
            }
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
        resultTxt = view.findViewById(R.id.result);
        afterImg = view.findViewById(R.id.after_img);
        progress = view.findViewById(R.id.progress);
    }
    private void uploadImage() {
        String id = FirebaseService.getFirebaseAuth().getCurrentUser().getUid();
        FirebaseService.getFirebaseStorage().getReference(id)
                .child("Scan")
                .putFile(uri.get())
                .addOnSuccessListener(v -> {
                    progress.setProgress(25,true);
                    v.getStorage().getStorage()
                            .getReference(id)
                            .child("Scan")
                            .getDownloadUrl()
                            .addOnSuccessListener(x -> {
                                progress.setProgress(55,true);
                                String path = x.toString().split("&token")[0];
                                getInference(path);
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

    private void getInference(String url) {
        try {
            progress.setProgress(75,true);
            ScanInterface scan = new Retrofit.Builder()
                    .baseUrl("https://outline.roboflow.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ScanInterface.class);
            scan.postImage(Constant.apiScanKey, url)
                    .enqueue(new Callback<ScanOutputModel>() {
                        @Override
                        public void onResponse(Call<ScanOutputModel> call, Response<ScanOutputModel> response) {
                            if(response.isSuccessful() && response.body().getPredictions().size() != 0){
                                try {
                                    progress.setProgress(85,true);
                                    String x = "";
                                    Bitmap bitmapWithText = createBitmapFromUri(uri.get()).copy(Bitmap.Config.ARGB_8888, true);
                                    Canvas canvas = new Canvas(bitmapWithText);
                                    Paint paintTxt = new Paint();
                                    Paint paintRect = new Paint();
                                    Paint background = new Paint();
                                    background.setColor(getContext().getColor(R.color.prediction_background));
                                    paintTxt.setColor(getContext().getColor(R.color.black));
                                    paintTxt.setTextSize(45);
                                    paintTxt.setAntiAlias(true);
                                    paintTxt.setStyle(Paint.Style.FILL);
                                    paintRect.setColor(getContext().getColor(R.color.dark_red));
                                    paintRect.setAntiAlias(true);
                                    DecimalFormat format = new DecimalFormat("0.00");
                                    for (int i = 0; i < response.body().getPredictions().size(); i++) {
                                        String txt = response.body().getPredictions().get(i).getClassType();
                                        double confidence = response.body().getPredictions().get(i).getConfidence();
                                        String confidenceTxt = format.format(confidence);
                                        x += txt + "\t" + confidenceTxt + "\n";
                                        int conPer = (int) (Double.parseDouble(confidenceTxt) * 100);
                                        float xPos = response.body().getPredictions().get(i).getX();
                                        float yPos = response.body().getPredictions().get(i).getY();
                                        String resTxt = txt+" "+conPer+"%";
                                        float textWidth = paintTxt.measureText(resTxt);
                                        Rect r = new Rect((int)xPos,(int)yPos,(int)(xPos+20),(int)(yPos+20));
                                        float x0 = xPos-10;
                                        float y0 = yPos-10;
                                        canvas.drawRect(r,paintRect);
                                        canvas.drawRect(x0,y0-paintTxt.getTextSize(),x0+textWidth,y0,background);
                                        canvas.drawText(resTxt, x0, y0, paintTxt);
                                        progress.setProgress(100,true);
                                        progress.setVisibility(View.INVISIBLE);
                                    }
                                    resultTxt.setText(x);
                                    afterImg.setImageBitmap(bitmapWithText);
                                } catch (Exception e) {
                                    progress.setProgress(100,true);
                                    progress.setVisibility(View.INVISIBLE);
                                    afterImg.setImageBitmap(null);
                                    resultTxt.setText(getString(R.string.error));
                                }
                            }
                            else if (response.body().getPredictions().size() == 0){
                                progress.setProgress(100,true);
                                progress.setVisibility(View.INVISIBLE);
                                afterImg.setImageBitmap(null);
                                resultTxt.setText(getString(R.string.sorry_it_is_not_an_xray_image));
                            }else{
                                progress.setProgress(100,true);
                                progress.setVisibility(View.INVISIBLE);
                                afterImg.setImageBitmap(null);
                                resultTxt.setText(getString(R.string.error));
                            }
                        }
                        @Override
                        public void onFailure(Call<ScanOutputModel> call, Throwable t) {
                            progress.setIndicatorColor(getContext().getColor(R.color.error_color));
                            progress.setProgress(100,true);
                            progress.setVisibility(View.INVISIBLE);
                            progress.setIndicatorColor(getContext().getColor(R.color.main_color));
                            Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
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
}