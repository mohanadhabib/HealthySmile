package com.buc.gradution.View.Activity.User;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.buc.gradution.Adapter.User.UserHistoryRecyclerAdapter;
import com.buc.gradution.Constant.Constant;
import com.buc.gradution.Model.HistoryModel;
import com.buc.gradution.Model.UserModel;
import com.buc.gradution.R;
import com.buc.gradution.Service.FirebaseSecurity;
import com.buc.gradution.Service.FirebaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserHistoryActivity extends AppCompatActivity {
    private final FirebaseSecurity security = new FirebaseSecurity();
    private String id;
    private UserHistoryRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private TextView yourHistory;
    private ImageView back;
    private TextView noData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_history);
        initComponents();
        if(getIntent().getSerializableExtra("user") != null){
            UserModel user = (UserModel)getIntent().getSerializableExtra("user");
            yourHistory.setText(user.getName());
            id = user.getId();
        }
        else{
            id = FirebaseService.getFirebaseAuth().getCurrentUser().getUid().toString();
        }
        adapter = new UserHistoryRecyclerAdapter();
        back.setOnClickListener(v -> finish());
        getHistory();
    }
    private void initComponents(){
        back = findViewById(R.id.back);
        yourHistory = findViewById(R.id.your_history);
        noData = findViewById(R.id.no_data);
        recyclerView = findViewById(R.id.history_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));
    }
    private void getHistory(){
        FirebaseService.getFirebaseDatabase()
                .getReference(Constant.HISTORY)
                .child(id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<HistoryModel> historyList = new ArrayList<>();
                        for(DataSnapshot snapshot0 : snapshot.getChildren()){
                            HistoryModel history;
                            try {
                                history = security.decryptHistory(snapshot0.getValue().toString());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            historyList.add(history);
                        }
                        if (!historyList.isEmpty()){
                            noData.setVisibility(View.INVISIBLE);
                        }
                        adapter.setHistory(historyList);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        noData.setVisibility(View.VISIBLE);
                        Toast.makeText(UserHistoryActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}