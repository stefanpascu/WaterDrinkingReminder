package com.example.waterdrinkingreminder;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
    RelativeLayout weightWrapper;
    RelativeLayout ageWrapper;
    RelativeLayout sexWrapper;
    RelativeLayout unitWrapper;

    private Dialog dialog;
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

        weightWrapper = getView().findViewById(R.id.weightWrapper);
        ageWrapper = getView().findViewById(R.id.ageWrapper);
        sexWrapper = getView().findViewById(R.id.sexWrapper);
        unitWrapper = getView().findViewById(R.id.unitWrapper);

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

        dialog = new Dialog(this.getActivity());
        dialog.setContentView(R.layout.dialog_edit_weight);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(this.getActivity().getDrawable(R.drawable.input));
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation; //Setting the animations to dialog

        Button okBtn = dialog.findViewById(R.id.okBtn);
        Button cancelBtn = dialog.findViewById(R.id.cancelBtn);

        logoutBtn = getView().findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(v -> logout());

        weightWrapper.setOnClickListener(v -> {
            dialog.show();
            EditText input = dialog.findViewById(R.id.inputWeight);
            input.setText(weightValue.getText());
        }); // Showing the dialog here);
        okBtn.setOnClickListener(v -> updateWeight());
        cancelBtn.setOnClickListener(v -> dialog.dismiss());
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();//logout
        Intent i = new Intent(getActivity(), MainActivity.class);
        startActivity(i);
        ((Activity) getActivity()).overridePendingTransition(0, 0);
    }

    private void updateWeight(){
        EditText input = dialog.findViewById(R.id.inputWeight);
        String updatedWeight = input.getText().toString();
        DocumentReference documentReference = firebaseStore.collection("users").document(userId);
        documentReference.update("weight", updatedWeight);
        weightValue.setText(updatedWeight);
        dialog.dismiss();
    }

}
