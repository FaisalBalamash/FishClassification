package com.example.fishclassification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.fishclassification.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewProfileFragment extends Fragment {

    private TextView textViewProfileInfo;
    private FirebaseFirestore firestore;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_profile, container, false);
        textViewProfileInfo = view.findViewById(R.id.textViewProfileInfo);
        firestore = FirebaseFirestore.getInstance();
        fetchProfileInfo();
        return view;
    }

    private void fetchProfileInfo() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userID = currentUser.getUid();

            firestore.collection("Profile").document(userID).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Profile profile = documentSnapshot.toObject(Profile.class);
                            if (profile != null) {
                                String info = "Name: " + profile.getName() + "\nAge: " + profile.getAge() +
                                        "\nCity: " + profile.getCity() + "\nFavorite Fish: " + profile.getFavoriteFish();
                                textViewProfileInfo.setText(info);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                    });
        }
    }


}