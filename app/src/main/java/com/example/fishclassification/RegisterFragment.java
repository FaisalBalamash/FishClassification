package com.example.fishclassification;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class RegisterFragment extends Fragment {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflating the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Hiding the bottom navigation bar in MainActivity
        ((MainActivity) getActivity()).hideBottomNavigation();

        // Initializing Firebase Authentication instance
        mAuth = FirebaseAuth.getInstance();

        // Binding the EditText for username and password
        usernameEditText = view.findViewById(R.id.username);
        passwordEditText = view.findViewById(R.id.password);

        // Setting up the registration button
        Button registerButton = view.findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> attemptRegistration());

        return view;
    }

    private void attemptRegistration() {
        // Extracting email and password from EditText fields
        String email = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validating the form before proceeding with registration
        if (!validateForm()) {
            return;
        }

        // Creating a new user with email and password in Firebase
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // On successful registration, navigating to DetectorFragment
                        MainActivity.showBottomNavigation();
                        navigateToDetectorFragment();
                    } else {
                        // Handling failures such as email collisions
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(getActivity(), "Email already exists", Toast.LENGTH_SHORT).show();
                        } else {
                            // Displaying other registration errors
                            Toast.makeText(getActivity(), "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private boolean validateForm() {
        // Getting username and password from EditText fields
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validating the username and password
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(getActivity(), "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            Toast.makeText(getActivity(), "Invalid email format", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getActivity(), "Password cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 6) {
            Toast.makeText(getActivity(), "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void navigateToDetectorFragment() {
        // Navigating to DetectorFragment on successful registration
        ((MainActivity) getActivity()).showBottomNavigation();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new DetectorFragment())
                .commit();
    }
}
