package com.example.fishclassification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingsFragment extends Fragment {

    private EditText editTextName, editTextAge, editTextCity, editTextFavoriteFish;
    private Button buttonUpdateProfile, buttonViewProfile;
    private FirebaseFirestore firestore;

    @Override


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        editTextName = view.findViewById(R.id.editTextName);
        editTextAge = view.findViewById(R.id.editTextAge);
        editTextCity = view.findViewById(R.id.editTextCity);
        editTextFavoriteFish = view.findViewById(R.id.editTextFavoriteFish);
        buttonUpdateProfile = view.findViewById(R.id.buttonUpdateProfile);
        buttonViewProfile = view.findViewById(R.id.buttonViewProfile);
        buttonViewProfile.setOnClickListener(v -> navigateToViewProfileFragment());

        firestore = FirebaseFirestore.getInstance();

        buttonUpdateProfile.setOnClickListener(v -> updateProfile());

        return view;
    }

    private void updateProfile() {
        String name = editTextName.getText().toString();
        String age = editTextAge.getText().toString();
        String city = editTextCity.getText().toString();
        String favoriteFish = editTextFavoriteFish.getText().toString();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userID = currentUser.getUid();

            firestore.collection("Profile").document(userID).set(new Profile(name, age, city, favoriteFish))
                    .addOnSuccessListener(aVoid -> {

                    })
                    .addOnFailureListener(e -> {

                    });
        }
    }


    private void navigateToViewProfileFragment() {
        // Assuming you want to show the bottom navigation in the MainActivity
        ((MainActivity) getActivity()).showBottomNavigation();

        // Create an instance of the ViewProfileFragment
        ViewProfileFragment viewProfileFragment = new ViewProfileFragment();

        // Replace the current fragment with the ViewProfileFragment
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, viewProfileFragment)
                .addToBackStack(null)
                .commit();
    }

}