package com.buc.gradution.Fragment.User;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.buc.gradution.Adapter.TopDoctorsRecyclerAdapter;
import com.buc.gradution.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class UserHomeFragment extends Fragment {
    private ImageView notification;
    private TextInputLayout doctorSearch;
    private MaterialButton btn;
    private TextView seeAll;
    private RecyclerView recyclerView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_home,container,false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initComponents(view);
        btn.setOnClickListener(v -> {
            String url = "https://sites.google.com/view/healthy-smile-tech-p/home";
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });
    }
    private void initComponents(View view){
        notification = view.findViewById(R.id.notification);
        doctorSearch = view.findViewById(R.id.search_doctor);
        btn = view.findViewById(R.id.btn);
        seeAll = view.findViewById(R.id.see_more_doctors);
        recyclerView = view.findViewById(R.id.top_doctors_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setAdapter(new TopDoctorsRecyclerAdapter());
    }
}
