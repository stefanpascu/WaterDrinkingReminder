package com.example.waterdrinkingreminder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.Executor;

public class ProfileFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseStore;
    String userId;

    TextView weightValue;
    TextView ageValue;
    TextView sexValue;
    TextView unitValue;

    private Button logoutBtn;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        weightValue = getView().findViewById(R.id.weightValue);
        unitValue = getView().findViewById(R.id.unitValue);
        sexValue = getView().findViewById(R.id.sexValue);
        ageValue = getView().findViewById(R.id.ageValue);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStore = FirebaseFirestore.getInstance();

        userId = firebaseAuth.getCurrentUser().getUid();

        DocumentReference documentReference = firebaseStore.collection("users").document(userId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                weightValue.setText(documentSnapshot.getString("weight"));
                unitValue.setText(documentSnapshot.getString("height"));
                sexValue.setText(documentSnapshot.getString("sex"));
                ageValue.setText(documentSnapshot.getString("birthDate"));
            }
        });


        logoutBtn = getView().findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(v -> logout());
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();//logout
        Intent i = new Intent(getActivity(), MainActivity.class);
        startActivity(i);
        ((Activity) getActivity()).overridePendingTransition(0, 0);
    }
}
