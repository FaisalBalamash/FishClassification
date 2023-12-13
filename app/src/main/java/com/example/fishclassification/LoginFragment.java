package com.example.fishclassification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {
    private EditText emailEditText;
    private EditText passwordEditText;
    private FirebaseAuth mAuth;

    // Called when the fragment is first created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    // Called to create the view hierarchy associated with the fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Hide bottom navigation bar in the MainActivity
        ((MainActivity) getActivity()).hideBottomNavigation();

        // Bind the EditText fields for email and password
        emailEditText = view.findViewById(R.id.username);
        passwordEditText = view.findViewById(R.id.password);

        // Bind the login button and set its click listener
        Button loginButton = view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> attemptLogin());

        return view;
    }

    // Attempts to log in the user
    private void attemptLogin() {
        // Retrieve text from EditText fields and trim any extra spaces
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Check if email or password fields are empty
        if (email.isEmpty() || password.isEmpty()) {
            // Show a message if fields are empty
            Toast.makeText(getActivity(), "Email and password must not be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use FirebaseAuth to sign in with email and password
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), task -> {
                    // Log email and password for debugging
                    System.out.println(email + password);

                    if (task.isSuccessful()) {
                        // If login is successful, show bottom navigation and navigate to DetectorFragment
                        MainActivity.showBottomNavigation();
                        navigateToDetectorFragment();
                    } else {
                        // If login fails, show a toast message
                        Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Navigates to the DetectorFragment
    private void navigateToDetectorFragment() {
        // Show bottom navigation bar in the MainActivity
        ((MainActivity) getActivity()).showBottomNavigation();

        // Replace the current fragment with DetectorFragment
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new DetectorFragment())
                .commit();
    }
}
